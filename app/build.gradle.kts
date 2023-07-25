plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.rikka.tools.refine") version "4.3.0"
}

val versionCode: Int by rootProject.extra
val versionName: String by rootProject.extra

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

        versionName = versionName
        versionCode = versionCode
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
        buildConfig = true
    }
}

dependencies {
    compileOnly(project(":api-stub"))
    implementation("info.picocli:picocli:4.7.4")
}
