// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
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

val amicVersionName by extra("$versionNamePrefix.r$commitCount.$commitHash")
val amicVersionCode by extra(commitCount)

val androidMinSdkVersion by extra(29)
val androidTargetSdkVersion by extra(33)
val androidCompileSdkVersion by extra(33)

val androidSourceCompatibility by extra(JavaVersion.VERSION_11)
val androidTargetCompatibility by extra(JavaVersion.VERSION_11)
val androidKotlinJvmTarget by extra("11")
