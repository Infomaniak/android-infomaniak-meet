plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(core.plugins.kotlin.parcelize)
    alias(core.plugins.kotlin.serialization)
}

val javaVersion: JavaVersion by rootProject.extra

android {
    namespace = "com.infomaniak.meet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.infomaniak.meet"
        minSdk = 27
        targetSdk = 35
        versionCode = 27
        versionName = "2.6.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("distribution")
    productFlavors {
        create("standard") {
            isDefault = true
        }
        create("fdroid")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":Core"))
    implementation(project(":Core:Legacy"))
    implementation(project(":Core:Legacy:Stores"))
    implementation(project(":Core:Ui:View:EdgeToEdge"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(core.kotlinx.coroutines.android)

    implementation(core.appcompat)
    implementation(core.constraintlayout)
    implementation(core.androidx.core.ktx)
    implementation(core.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.preference.ktx)

    implementation(core.material)
    implementation(core.gson)
    implementation(core.progress.button)

    implementation(libs.jitsi.meet.sdk) {
        exclude(group = "com.google.firebase")
        exclude(group = "com.android.installreferrer")
    }
}
