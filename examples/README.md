# Consumer examples

These projects depend on the public library products. They do not import internal source files.

## SwiftUI

`swift build --package-path examples/swiftui` compiles a macOS SwiftUI example app. Launch the executable from a desktop session to edit the text field and activate the plain native button. The same screen can be hosted in an iOS SwiftUI app.

## Compose

`examples/compose` is a reusable composable screen and an Android consumer compilation fixture. It uses a composite build by default:

```sh
cd jetpackcompose
mise exec -- ./gradlew -p ../examples/compose assembleDebug
```

Set `ANDROID_HOME` to an SDK containing API 35. You can instead verify a published artifact with `-PtailwindNativeRepository=/path/to/maven/repository`; this disables composite substitution and resolves the Maven AAR, POM and Gradle module metadata.
