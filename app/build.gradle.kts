plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
}

apply("dependencies.gradle")

android {
    namespace = "com.snstudio.hyper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.snstudio.hyper"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    val myChannelValue = "nativeBridgeChannel"
    val releaseVersionUrl = "https://api.github.com/repos/emreesen27/Hyper-N/releases/latest"
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "MY_CHANNEL", "\"$myChannelValue\"")
            buildConfigField("String", "RELEASE_VERSION", "\"$releaseVersionUrl\"")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "MY_CHANNEL", "\"$myChannelValue\"")
            buildConfigField("String", "RELEASE_VERSION", "\"$releaseVersionUrl\"")
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