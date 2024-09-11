plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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
        versionName = "1.0.0-Beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    val myChannelValue = "nativeBridgeChannel"
    val releaseVersionUrl = "https://api.github.com/repos/emreesen27/Hyper-N/releases/latest"
    val releaseDownloadUrl = "https://github.com/emreesen27/Hyper-N/releases/tag/"
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "MY_CHANNEL", "\"$myChannelValue\"")
            buildConfigField("String", "RELEASE_VERSION", "\"$releaseVersionUrl\"")
            buildConfigField("String", "RELEASE_DOWNLOAD", "\"$releaseDownloadUrl\"")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "MY_CHANNEL", "\"$myChannelValue\"")
            buildConfigField("String", "RELEASE_VERSION", "\"$releaseVersionUrl\"")
            buildConfigField("String", "RELEASE_DOWNLOAD", "\"$releaseDownloadUrl\"")
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

tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}

tasks.register("installGitHook") {
    doLast {
        val gitHooksDir = file("$rootDir/.git/hooks")
        val preCommitHook = file("$rootDir/scripts/pre-commit")
        val commitMsgHook = file("$rootDir/scripts/commit-msg")

        if (!gitHooksDir.exists()) {
            println("Git hooks directory does not exist.")
            return@doLast
        }

        if (preCommitHook.exists()) {
            val destinationPreCommitHook = file("$gitHooksDir/pre-commit")
            preCommitHook.copyTo(destinationPreCommitHook, overwrite = true)
            destinationPreCommitHook.setExecutable(true)
            println("Pre-commit hook installed successfully.")
        } else {
            println("Pre-commit hook script does not exist.")
        }

        if (commitMsgHook.exists()) {
            val destinationCommitMsgHook = file("$gitHooksDir/commit-msg")
            commitMsgHook.copyTo(destinationCommitMsgHook, overwrite = true)
            destinationCommitMsgHook.setExecutable(true)
            println("Commit-msg hook installed successfully.")
        } else {
            println("Commit-msg hook script does not exist.")
        }
    }
}

tasks.named("build") {
    dependsOn("installGitHook")
}
