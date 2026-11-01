# Tailwind Native for Jetpack Compose

Requires JDK 25 and Android SDK 37, with minimum Android API 31.

Add the repository to your app's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://dl.cloudsmith.io/public/hotaritobu/tailwind-native/maven/") }
    }
}
```

Add the dependency to your app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("dev.tailwind:tailwind-jetpackcompose:0.1.0")
}
```

```kotlin
import dev.tailwind.nativeui.*

TWColumn(modifier = Modifier.p(6), gap = 4) {
    TWText("Welcome", style = TextStyle.Default.text(TW.TextSize.Xl).font(FontWeight.Bold))
    TWButton(onClick = ::continueAction,
        modifier = Modifier.rounded(TW.Radius.Lg).bg(Palette.blue500).px(4).py(2)) {
        TWText("Continue", style = TextStyle.Default.text(Palette.white))
    }
}
```

## Development

Install [mise](https://mise.jdx.dev/) and Android SDK API 37. Set `ANDROID_HOME` to the SDK directory, or set `sdk.dir` in an untracked `local.properties` in this directory.

Run the following in this directory:

```sh
mise install
mise run test
mise run build
mise run lint
```
