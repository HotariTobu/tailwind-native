pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
val publishedRepository = providers.gradleProperty("tailwindNativeRepository").orNull
if (publishedRepository == null) includeBuild("../../jetpackcompose")
dependencyResolutionManagement {
    repositories {
        if (publishedRepository != null) maven { url = uri(publishedRepository) }
        google()
        mavenCentral()
    }
    versionCatalogs { create("libs") { from(files("../../jetpackcompose/gradle/libs.versions.toml")) } }
}
rootProject.name = "tailwind-native-compose-example"
