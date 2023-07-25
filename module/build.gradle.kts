plugins {
    id("com.android.library")
}

val amicVersionName: String by rootProject.extra

val androidMinSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra

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

androidComponents.onVariants { variant ->
    val nameCapped = variant.name.capitalize()
    val nameLowered = variant.name.toLowerCase()

    val moduleDir = "$buildDir/outputs/module/$nameLowered"

    val prepareModuleTask = task<Sync>("prepareModule$nameCapped") {
        dependsOn(":app:assemble$nameCapped")
        into(moduleDir)
        from("$projectDir/src")
        from("${project(":app").buildDir}/outputs/apk/$nameLowered") {
            include("app-*.apk")
            rename {
                "amic.apk"
            }
        }
    }

    task<Zip>("zipModule$nameCapped") {
        dependsOn(prepareModuleTask)
        from(moduleDir)
        archiveFileName.set("Amic-$amicVersionName-$nameLowered.zip")
        destinationDirectory.set(File("$buildDir/outputs/zip/$nameLowered"))
    }
}
