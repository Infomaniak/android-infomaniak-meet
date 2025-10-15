plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

val javaVersion: JavaVersion by rootProject.extra

android {
    namespace = "com.infomaniak.meet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.infomaniak.meet"
        minSdk = 27
        targetSdk = 35
        versionCode = 26
        versionName = "2.6.6"

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

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.preference.ktx)

    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.progressbutton)

    implementation(libs.jitsi.meet.sdk) {
        exclude(group = "com.google.firebase")
        exclude(group = "com.android.installreferrer")
    }
}
