buildscript { dependencies { classpath(libs.kotlin.gradle.plugin) } }

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}
group = "dev.tailwind"
version = providers.gradleProperty("releaseVersion").getOrElse("0.1.0")
android {
    namespace = "dev.tailwind.nativeui"
    compileSdk = 37
    defaultConfig { minSdk = 31 }
    buildFeatures { compose = true }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        // Robolectric's documented JDK 17+ reflection requirement, scoped to tests.
        unitTests.all {
            it.jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
                "--enable-native-access=ALL-UNNAMED")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    publishing { singleVariant("release") { withSourcesJar() } }
}
kotlin {
    jvmToolchain(25)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) }
}
dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.foundation)
    api(libs.compose.ui)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.espresso.core)
    testImplementation(libs.compose.test.junit4)
    debugImplementation(libs.compose.test.manifest)
}
afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "Cloudsmith"
                url = uri("https://maven.cloudsmith.io/hotaritobu/tailwind-native/")
                credentials {
                    username = System.getenv("REGISTRY_USERNAME")
                    password = System.getenv("REGISTRY_PASSWORD")
                }
            }
        }
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
