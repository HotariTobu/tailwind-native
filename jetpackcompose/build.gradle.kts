plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}
group = "dev.tailwind"
version = "0.1.0"
android {
    namespace = "dev.tailwind.nativeui"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
    buildFeatures { compose = true }
    testOptions { unitTests.isIncludeAndroidResources = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing { singleVariant("release") { withSourcesJar() } }
}
kotlin {
    jvmToolchain(21)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
}
dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.foundation)
    api(libs.compose.ui)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.test.junit4)
    debugImplementation(libs.compose.test.manifest)
}
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                artifactId = "tailwind-jetpackcompose"
            }
        }
    }
}

// AGP's source archive omits Android resources; preserve the upstream notice there too.
tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
    if (name == "sourceReleaseJar") {
        from("src/main/resources/META-INF/tailwind-native/LICENSE.tailwind") {
            into("META-INF/tailwind-native")
        }
    }
}
