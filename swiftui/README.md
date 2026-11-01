# Tailwind Native for SwiftUI

Requires Swift 6.4 and iOS 16 / macOS 14 / tvOS 16 / watchOS 9 or later.

Configure the registry in your consuming project:

```sh
swift package-registry set --scope hotaritobu https://swift.cloudsmith.io/hotaritobu/tailwind-native/
```

Add the package dependency to `Package.swift`:

```swift
.package(id: "hotaritobu.tailwind-native", from: "0.1.0")
```

Add the `swiftui` product to your target's dependencies:

```swift
.product(name: "swiftui", package: "hotaritobu.tailwind-native")
```

```swift
import SwiftUI
import swiftui

TWColumn(gap: 4) {
    Text("Welcome").text(.xl).font(TW.Weight.bold)
    Button("Continue", action: continueAction)
        .preflight()
        .px(4).py(2)
        .bg(TW.Palette.blue500).text(.white)
        .rounded(.lg)
}
.p(6)
```

## Development

Install [mise](https://mise.jdx.dev/) and run the following in this directory:

```sh
mise install
mise run test
mise run build
```

Full Xcode is required for iOS, tvOS and watchOS device builds and iOS Simulator tests.
