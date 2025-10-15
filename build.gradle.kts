buildscript {
    rootProject.extra.apply {
        set("javaVersion", JavaVersion.VERSION_17)
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
}

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes the root project's build directory."

    delete(rootProject.layout.buildDirectory)
}
