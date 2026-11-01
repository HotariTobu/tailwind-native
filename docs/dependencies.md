# Dependencies and toolchains

Dependency versions are pinned in the package manifests, Gradle version catalog, wrapper properties, mise configuration and CI workflow.

| Area | Version / requirement |
| --- | --- |
| Swift development / manifest | Swift 6.4.2 / tools 6.4 |
| Apple platforms | iOS 16, macOS 14, tvOS 16, watchOS 9 or later |
| Apple CI | Xcode 27.1 on `xcode-27` |
| Android build | AGP 9.5.0, Gradle 9.8.2, Kotlin and Compose compiler 2.4.21 |
| Android UI | Compose BOM 2026.09.00 (Foundation/UI 1.12.1) |
| Java | Temurin 25.0.5+8 LTS; emitted JVM target 21 |
| Android SDK | compile 37, minimum 31; build tools 36.0.0; Command-line Tools 22.0 |
| Android tests | Robolectric 4.17, Espresso 3.7.0, JUnit 4.13.2 |
| CI actions | checkout 7.0.1, setup-java 6.1.0; immutable commit pins, Node 24 |
| Linux CI | Ubuntu 26.04 |
| Token source | Vendored Tailwind CSS 4.3.3 |

## Build configuration

AGP uses built-in Kotlin with Kotlin Gradle plugin 2.4.21 loaded explicitly. The Compose compiler uses the same Kotlin version. The libraries expose native SwiftUI and Compose Foundation/UI APIs; Robolectric, Espresso and JUnit are test-only dependencies. JUnit 4 is required by Robolectric's runner.

The Gradle distribution and Android Command-line Tools archive have SHA-256 checksums pinned in the wrapper properties and CI workflow. Gradle's published wrapper JAR checksum is available alongside its [distributions](https://services.gradle.org/distributions/). The vendored Tailwind theme includes its upstream MIT license, which is also included in binary and source artifacts.

## Tool constraints

[Kotlin's compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html) lists Gradle through 9.7.0 and AGP through 9.3.1 for Kotlin 2.4.21. The configured Gradle 9.8.2 / AGP 9.5.0 combination requires project-level verification beyond that vendor-tested range. See [verification commands](implementation.md#verification).

[Robolectric on JDK 17+](https://robolectric.org/getting-started/#running-with-java-17-and-higher) requires reflection access to JDK internals. Test JVM arguments open `jdk.internal.access` and enable native access for test libraries. These settings do not enter the published AAR or consumer runtime. Compose pixel-test behavior is described in [native semantics](implementation.md#verification).

The `xcode-27` runner image is preview infrastructure; the selected Xcode release is stable. Full Xcode provides the Apple device SDKs and simulator runtimes. Swift Testing requires the macOS 14 deployment baseline.

## Upstream references

- [Swift releases](https://www.swift.org/download/)
- [AGP releases](https://developer.android.com/build/releases/about-agp)
- [Gradle releases](https://gradle.org/releases/) and [security advisories](https://github.com/gradle/gradle/security/advisories)
- [Compose Foundation releases](https://developer.android.com/jetpack/androidx/releases/compose-foundation)
- [AndroidX Test releases](https://developer.android.com/jetpack/androidx/releases/test)
- [Android CLI](https://developer.android.com/tools/agents/android-cli)
- [checkout releases](https://github.com/actions/checkout/releases) and [setup-java releases](https://github.com/actions/setup-java/releases)
- [Xcode runner image inventory](https://github.com/actions/runner-images/blob/main/images/macos/xcode-27-arm64-Readme.md)
