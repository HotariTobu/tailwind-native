# Development

## Tools

| Configuration | Defines |
| --- | --- |
| [`swiftui/mise.toml`](../swiftui/mise.toml), [`jetpackcompose/mise.toml`](../jetpackcompose/mise.toml) | Local Swift and JDK versions |
| [`swiftui/Package.swift`](../swiftui/Package.swift) | Swift language and deployment requirements |
| [Gradle catalog](../jetpackcompose/gradle/libs.versions.toml), [wrapper](../jetpackcompose/gradle/wrapper/gradle-wrapper.properties) | Android dependencies and Gradle distribution |
| [Android build](../jetpackcompose/build.gradle.kts) | SDK and JVM targets, compilation and publication |
| [CI workflow](../.github/workflows/verify.yml) | Runner images, Xcode, JDK and SDK tools |

AGP uses built-in Kotlin with an explicitly loaded Kotlin Gradle plugin; the Compose compiler uses the same Kotlin version. Robolectric, Espresso and JUnit are test-only dependencies. JUnit 4 is required by Robolectric's runner. Test JVM arguments open `jdk.internal.access` and enable native access for test libraries.

[Kotlin's compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html) covers Gradle through 9.7.0 and AGP through 9.3.1 for Kotlin 2.4.21. The configured Gradle 9.8.2 / AGP 9.5.0 combination needs project-level verification beyond that range. The `xcode-27` runner image is preview infrastructure with a stable Xcode release.

Gradle and Android Command-line Tools downloads have SHA-256 checksums pinned in their configuration. Local SDK paths belong in `ANDROID_HOME` or an untracked `jetpackcompose/local.properties`. `GRADLE_USER_HOME` can select a separate cache.

## Verification

Run from the repository root with the configured Swift and JDK available:

```sh
python3 tools/test_tokens.py
swift test --package-path swiftui
swift build --package-path examples/swiftui-app
./tools/verify-android.sh
python3 tools/verify_artifacts.py
./tools/verify-apple.sh
```

Android verification runs tests on API 31 and 37, lint, release packaging and both Maven/composite consumers. Artifact verification checks distributed license notices. Apple verification builds iOS, tvOS and watchOS device targets and runs iOS Simulator tests including Dynamic Type; it requires full Xcode and an installed iPhone simulator.

For local Swift commands, use the pinned toolchain with `cd swiftui` followed by `mise exec -- swift test`. CI selects Swift through Xcode's `DEVELOPER_DIR`.

Compose pixel assertions draw the View tree to a native Canvas because Robolectric/API 37 window capture can return stale buffers. Swift pixel tests use explicit sRGB fills and cleared buffers.

## Consumer examples

The examples use public library products. Compile the Compose example independently with:

```sh
cd jetpackcompose
mise exec -- ./gradlew -p ../examples/compose assembleDebug
```

Launch the Swift executable in a desktop session to use its text field and button. The same screen can be hosted in an iOS app. The Compose example uses a composite build by default; `-PtailwindNativeRepository=/path/to/maven/repository` selects a published AAR, POM and Gradle module metadata instead. `tools/verify-android.sh` checks both modes.
