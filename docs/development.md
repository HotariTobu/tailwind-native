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

Run from the repository root:

```sh
./tools/verify.sh
```

This checks token values and reproducibility, Swift tests, Android tests on API 31 and 37, Android Lint, release packaging, consumer builds, and distributed license notices. Individual checks are:

```sh
python3 tools/test_tokens.py
./tools/swift.sh test --package-path swiftui
./tools/verify-android.sh
./tools/verify-apple.sh
```

`tools/swift.sh` selects Xcode's Swift when `DEVELOPER_DIR` is set, otherwise the mise toolchain or installed Swift. Apple device verification requires full Xcode with iOS, tvOS and watchOS SDKs and an installed iPhone simulator. It builds device targets and runs iOS tests, including Dynamic Type.

Compose pixel assertions draw the View tree to a native Canvas because Robolectric/API 37 window capture can return stale buffers. Swift pixel tests use explicit sRGB fills and cleared buffers.

## Consumer examples

The examples compile against public library products:

```sh
./tools/swift.sh build --package-path examples/swiftui-app
cd jetpackcompose
mise exec -- ./gradlew -p ../examples/compose assembleDebug
```

Launch the Swift executable in a desktop session to use its text field and button. The same screen can be hosted in an iOS app. The Compose example uses a composite build by default; `-PtailwindNativeRepository=/path/to/maven/repository` selects a published AAR, POM and Gradle module metadata instead. `tools/verify-android.sh` checks both modes.
