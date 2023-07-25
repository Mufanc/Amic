plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.rikka.tools.refine") version "4.3.0"
}

val amicVersionCode: Int by rootProject.extra
val amicVersionName: String by rootProject.extra

val androidMinSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra
val androidKotlinJvmTarget: String by rootProject.extra

android {
    namespace = "xyz.mufanc.amic"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        applicationId = "xyz.mufanc.amic"
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion

        versionName = amicVersionName
        versionCode = amicVersionCode

        buildConfigField("int", "VERSION_CODE", "$amicVersionCode")
        buildConfigField("String", "VERSION_NAME", "\"$amicVersionName\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = androidSourceCompatibility
        targetCompatibility = androidTargetCompatibility
    }

    kotlinOptions {
        jvmTarget = androidKotlinJvmTarget
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }
}

dependencies {
    compileOnly(project(":api-stub"))
    compileOnly("de.robv.android.xposed:api:82")
    implementation("info.picocli:picocli:4.7.4")
}
