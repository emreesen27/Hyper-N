import org.jetbrains.kotlin.fir.declarations.builder.buildField

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
}

apply("dependencies.gradle")

android {
    namespace = "com.snstudio.hyper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.snstudio.hyper"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    val myChannelValue = "nativeBridgeChannel"
    buildTypes {
        getByName("debug") {
            buildConfigField(
                "String", "MY_CHANNEL",
                "\"$myChannelValue\""
            )
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String", "MY_CHANNEL",
                "\"$myChannelValue\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

}