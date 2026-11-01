# Dependency audit — 2026-11-02

The dependency baseline follows current stable releases. Older SDKs, compiler plugins, deprecated CI actions, and input API overloads are not retained to preserve compatibility. This audit supersedes the dependency assumptions in the historical implementation verification.

| Area | Selected baseline |
| --- | --- |
| Swift development / manifest | Swift 6.4.2 / tools 6.4; macOS 14 minimum |
| Apple CI | Xcode 27.1, explicit `xcode-27` image |
| Android build | AGP 9.5.0, Gradle 9.8.2, Kotlin and Compose compiler 2.4.21 |
| Android UI | Compose BOM 2026.09.00 (Foundation/UI 1.12.1) |
| Java | Temurin 25.0.5+8 LTS; emitted JVM target 21 |
| Android SDK | compile 37, minimum 31; build tools 36.0.0; Command-line Tools 22.0 (SHA-256 pinned) |
| Android tests | Robolectric 4.17, Espresso 3.7.0, JUnit 4.13.2 |
| CI actions | checkout 7.0.1, setup-java 6.1.0; immutable commit pins, Node 24 |
| Linux CI | explicit Ubuntu 26.04 |
| Token source | vendored Tailwind CSS 4.3.3, generated offline, upstream notice distributed |

## Decisions and evidence

- [Gradle releases](https://gradle.org/releases/) and [security advisories](https://github.com/gradle/gradle/security/advisories): the previous 8.11.1 baseline predates fixes for GHSA-j5m7-59rp-24f5, GHSA-xwqc-3h47-hg64, GHSA-mvvg-497x-hmj8, GHSA-mqwm-5m85-gmcv and GHSA-w78c-w6vf-rw82. Version 9.8.2 includes those fixes. The wrapper distribution has its published SHA-256 pinned. The regenerated wrapper JAR also matches the official 9.8.2 SHA-256 (`3b8a25775a69158b5ad2b1d17a80a88dc7b40a352a73eb27d06c612a7ce68e98`).
- [AGP releases](https://developer.android.com/build/releases/about-agp): use built-in Kotlin, with the current Kotlin Gradle plugin loaded explicitly. No legacy Kotlin Android plugin or built-in-Kotlin opt-out is used. Gradle `dependencyInsight` confirms the resolved `kotlin-compiler-embeddable` is 2.4.21.
- [Kotlin's tested compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html) currently lists Gradle through 9.7.0 and AGP through 9.3.1 for Kotlin 2.4.21. This project uses newer patched tools and must validate that combination itself; it does not claim the combination is covered by that vendor table.
- [Compose Foundation releases](https://developer.android.com/jetpack/androidx/releases/compose-foundation): use the state-based input API (`TextFieldState`, line limits and transformations). Minimum Android 31 makes native blur available across the supported range; there is no older-Android no-op policy.
- [AndroidX Test releases](https://developer.android.com/jetpack/androidx/releases/test): explicitly select Espresso 3.7.0 rather than inherit Compose's older transitive Espresso. JUnit 4.13.2 is retained because Robolectric uses its JUnit 4 runner; it is not a production dependency. Pixel assertions render the actual View tree to a native Canvas: Robolectric/API 37 PixelCopy can return stale window buffers. This checks native rendering, not device screenshot infrastructure.
- [Robolectric setup](https://robolectric.org/getting-started/#running-with-java-17-and-higher): test JVM reflection access is scoped to `jdk.internal.access`; native access is enabled for the test runner's native libraries. These settings do not enter the published AAR or consumer runtime.
- macOS 14 is the baseline required by the current Swift Testing runtime, avoiding a lower deployment target than its test runtime.
- [Swift releases](https://www.swift.org/download/), [checkout releases](https://github.com/actions/checkout/releases), [setup-java releases](https://github.com/actions/setup-java/releases) and [runner image inventory](https://github.com/actions/runner-images/blob/main/images/macos/xcode-27-arm64-Readme.md) establish the tool baseline. The Xcode image is preview infrastructure even though the selected Xcode is stable; macOS runner capacity notices are operational notices, not deprecated dependencies.

## Verification status

Swift 6.4.2 passed all 25 local tests and built the independent Swift consumer. Android passed all 41 local tests (30 UI tests across API 31 and 37, 11 token/validation tests), release assembly, lint, Maven and composite consumer builds, and distributed notice verification. [Verify run 41330372229](https://github.com/HotariTobu/tailwind-native/actions/runs/41330372229) passed for commit `ff37aa633fd1de3a7728289bdd41653d84cf8ce2`: both Apple and Android succeeded with zero check annotations. Apple CI passed 25 macOS tests, independent consumer build, license verification, iOS/tvOS/watchOS device SDK builds, and 12 iOS Simulator tests including Dynamic Type. Android CI passed tests, lint, release packaging, both consumers and notices. The Node 20/setup-java v4 deprecation and Ubuntu label migration annotations are gone.

Xcode emits an informational AppIntents metadata-extraction warning for the generated test host, which has no AppIntents dependency. The local standalone Swift distribution with Command Line Tools emits environment-specific scanner/framework search-path warnings; full-Xcode CI did not emit those warnings. No older toolchain is selected to work around them. The earlier green CI run used the former dependency baseline and is not evidence for this migration. This is a targeted lifecycle, known-advisory and build audit, not a claim of exhaustive vulnerability coverage for every transitive dependency.
