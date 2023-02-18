plugins {
    id("com.android.library")
    id("org.mozilla.rust-android-gradle.rust-android")
}

val androidMinSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra

val versionName: String by rootProject.extra
val versionCode: Int by rootProject.extra

android {
    namespace = "xyz.mufanc.amic"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
    }
}

cargo {
    module = "."
    libname = "amic"
    targets = listOf("arm64")
    targetIncludes = arrayOf("amic")
    val isDebug = gradle.startParameter.taskNames.any { it.toLowerCase().contains("debug") }
    profile = if (isDebug) "debug" else "release"
    exec = { spec, _ ->
        spec.environment("PROFILE", profile)
        spec.environment("ANDROID_NDK_HOME", android.ndkDirectory.path)
        spec.environment("VERSION_NAME", versionName)
        spec.environment("VERSION_CODE", versionCode)
    }
}

androidComponents.onVariants { variant ->
    val variantLowered = variant.name.toLowerCase()
    val variantCapped = variant.name.capitalize()

    val outputDir = "$buildDir/intermediates/apk/$variantLowered"

    val syncApkTask = task<Sync>("syncApk$variantCapped") {
        dependsOn(":app:assemble$variantCapped")
        from("${project(":app").buildDir}/outputs/apk/$variantLowered/app-$variantLowered.apk")
        into(outputDir)
        rename { "amic.apk" }
    }

    val buildAmicTask = task("buildAmic$variantCapped") {
        dependsOn(
            syncApkTask,
            ":wrapper:cargoBuild"
        )
    }

    val deployToAndroidTask = task<Exec>("deployToAndroid$variantCapped") {
        dependsOn(buildAmicTask)
        commandLine("adb", "push", "$buildDir/rustJniLibs/android/arm64-v8a/amic", "/data/local/tmp")
        doLast {
            exec { commandLine("adb", "shell", "chmod", "+x", "/data/local/tmp/amic") }
        }
    }

    task<Exec>("runAmic$variantCapped") {
        dependsOn(deployToAndroidTask)
//        commandLine("adb", "shell", "su", "-c", "/data/local/tmp/amic", "--help")
        commandLine("adb", "shell", "/data/local/tmp/amic", "--help")
    }
}
