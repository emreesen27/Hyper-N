pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
var storageUrl: String =
    if (System.getenv("FLUTTER_STORAGE_BASE_URL") != null)
        System.getenv("FLUTTER_STORAGE_BASE_URL")
    else "https://storage.googleapis.com"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()

        maven(url = "${rootProject.projectDir}/explode/build/host/outputs/repo")
        maven(url = "$storageUrl/download.flutter.io")
    }
}

rootProject.name = "Hyper"
include(":app")