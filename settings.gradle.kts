pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
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
    ":Core:AppVersionChecker",
    ":Core:InAppUpdate",
    ":Core:Legacy",
    ":Core:Network",
    ":Core:Network:Models",
    ":Core:Sentry",
    ":Core:Ui:Compose:BasicButton",
    ":Core:Ui:Compose:BottomStickyButtonScaffolds",
    ":Core:Ui:Compose:Margin",
    ":Core:Ui:Compose:Preview",
    ":Core:Ui:View:EdgeToEdge"
)
