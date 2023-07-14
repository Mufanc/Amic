plugins {
    id("com.android.library")
}

val androidMinSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra

android {
    namespace = "hudden.api.stub"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
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
}

dependencies {
    compileOnly("dev.rikka.tools.refine:annotation:4.3.0")
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:4.3.0")
}
