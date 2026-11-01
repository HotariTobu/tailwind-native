# Tailwind Native for SwiftUI

Clone this repository and add this directory as a local Swift Package dependency. Link the `swiftui` product. Requires Swift 6.4 and iOS 16 / macOS 14 / tvOS 16 / watchOS 9 or later.

```swift
.package(path: "../tailwind-native/swiftui")
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
