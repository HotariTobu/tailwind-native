# Development

Tool versions are defined in the platform-specific mise files, Swift manifest, Gradle version catalog and wrapper, and [CI workflow](../.github/workflows/verify.yml). AGP uses built-in Kotlin with an explicitly loaded Kotlin Gradle plugin; the Compose compiler uses the same Kotlin version.

## SwiftUI

```sh
cd swiftui
mise run test
mise run build
```

Full Xcode is required for iOS, tvOS and watchOS device builds and iOS Simulator tests. CI selects it through `DEVELOPER_DIR`.

## Jetpack Compose

Set `ANDROID_HOME` or an untracked `jetpackcompose/local.properties` to an SDK containing API 37, then run:

```sh
cd jetpackcompose
mise run test
mise run build
mise run lint
```

Robolectric, Espresso and JUnit are test-only dependencies. The test JVM opens `jdk.internal.access` and enables native access for test libraries. Pixel assertions draw the View tree to a native Canvas because Robolectric/API 37 window capture can return stale buffers.

[Kotlin's compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html) covers Gradle through 9.7.0 and AGP through 9.3.1 for Kotlin 2.4.21. The configured Gradle 9.8.2 / AGP 9.5.0 combination needs project-level verification beyond that range. The `xcode-27` runner image is preview infrastructure with a stable Xcode release.
