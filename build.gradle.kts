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
    alias(core.plugins.kotlin.android) apply false
    alias(core.plugins.kotlin.serialization) apply false
    alias(core.plugins.ksp) apply false
    alias(core.plugins.navigation.safeargs) apply false
}

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes the root project's build directory."

    delete(rootProject.layout.buildDirectory)
}
