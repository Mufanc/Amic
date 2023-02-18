plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.mozilla.rust-android-gradle:plugin:0.9.3")
    }
}

fun String.execute(): String {
    return Runtime.getRuntime()
        .exec(this.split(" ").toTypedArray())
        .apply { waitFor() }
        .inputStream.bufferedReader().readText().trim()
}

val versionNamePrefix = "v1.0.0"

val commitHash = "git rev-parse --short HEAD".execute()
val commitCount = "git rev-list --count HEAD".execute().toInt()

val androidMinSdkVersion by extra(29)
val androidTargetSdkVersion by extra(33)
val androidCompileSdkVersion by extra(33)

val versionName by extra("$versionNamePrefix.r$commitCount.$commitHash")
val versionCode by extra(commitCount)
