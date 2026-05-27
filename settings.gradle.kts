pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("Core/build-logic")
}

plugins {
    id("com.infomaniak.core.composite")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://github.com/jitsi/jitsi-maven-repository/raw/master/releases") }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
    }
    versionCatalogs {
        create("core") {
            from(files("Core/gradle/core.versions.toml"))
        }
    }
}

rootProject.name = "kMeet"

include(
    ":app",
    ":Core:Legacy",
)
