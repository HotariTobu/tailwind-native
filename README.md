# Tailwind Native

Tailwind-style utility chains for SwiftUI and Jetpack Compose, with plain native controls and no CSS runtime or cross-platform renderer.

## SwiftUI

Clone this repository and add its `swiftui/` directory as a local Swift Package dependency. Link the `swiftui` product. Requires Swift 6.4 and iOS 16 / macOS 14 / tvOS 16 / watchOS 9 or later.

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

## Jetpack Compose

Requires JDK 25 and Android SDK 37, with minimum Android API 31. Add a composite build in your app's `settings.gradle.kts`:

```kotlin
includeBuild("../tailwind-native/jetpackcompose")
```

Add `implementation("dev.tailwind:tailwind-jetpackcompose:0.1.0")` to your dependencies. To use a local Maven artifact instead, run `publishToMavenLocal` in `jetpackcompose/` and add `mavenLocal()` to the consumer's repositories.

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

## Utilities

One spacing unit is 4 points / dp: `p(4)` is 16 and `w(64)` is 256. Fractional units are supported; negative spacing is rejected, while translations accept signed values. Numeric inputs must be finite. Start/end padding follows layout direction; left/right padding and translations use physical directions.

| Family | API |
| --- | --- |
| Padding | `p`, `px`, `py`, `pt`, `pb`, `pl`, `pr`, `ps`, `pe` |
| Sizing | `w`, `h`, `size`, `wFull`, `hFull`, `minW`, `maxW`, `minH`, `maxH`, `aspect` |
| Decoration | `bg`, `rounded`, `border`, `opacityPercent`, `overflowHidden` |
| Transforms | `rotate` (degrees), `scale` (percent), `translateX`, `translateY` (spacing units) |
| Typography | `text` (size or color), `font`, `italicText`, `underlineText`, `lineThrough`, `tracking`, `textStart`, `textCenter`, `textEnd` |
| Layout | `TWRow`, `TWColumn`, `TWStack`, `TWGrid`, `TWScroll`; explicit zero default gap |
| Variants | `variant(condition)`, `dark`, `at(breakpoint, width)` |
| Colors | Swift `TW.Palette.blue500`, Kotlin `Palette.blue500`; all 26 Tailwind palettes plus black, white, transparent |

`maxW` accepts a `TW.Container` token. `TW.Radius.x4l` / `TW.Radius.X4l` is 32 points / dp; `full` is a capsule at any view size.

Modifier order follows the native framework. Compose decorations usually precede padding; SwiftUI backgrounds usually follow it. Utilities do not emulate a CSS cascade, DOM selectors, pseudo-elements or browser layout modes.

## Controls and state

`TWPreflight` supplies a base text environment with opaque black/white foregrounds. SwiftUI `preflight()` applies plain control styles and a base font. `TWButton` and `TWInput` expose undecorated controls; Compose uses Foundation primitives without Material or a default ripple. Its `TWInput` accepts `TextFieldState`, with input/output transformations and a cursor matching the text color.

Use `variant(condition)` with native state for focus, disabled and application-specific styles. SwiftUI `TWButton.pressed { label in ... }` uses native pressed state; apply it before generic view modifiers. SwiftUI also provides `hover`. In Compose, share a `MutableInteractionSource` between a control and `Modifier.pressed(source)`, `focus(source)` or `hover(source)`.

`dark` follows the local theme; Compose `TWPreflight(dark = ...)` can override it. `at(breakpoint, width)` accepts a logical viewport width from the host, such as `GeometryReader` or `BoxWithConstraints`. Breakpoints are 640, 768, 1024, 1280 and 1536.

SwiftUI conditional variants change view structure; keep persistent model/editing state above their boundary. Compose variants retain the surrounding composition. Native accessibility, focus and input handling apply on both platforms.

## Typography and effects

Typography modifies SwiftUI views or Compose `TextStyle`. Text sizes use points / sp and retain native line metrics. SwiftUI uses `ScaledMetric` for Dynamic Type; macOS retains [native fixed text sizes](https://developer.apple.com/documentation/swiftui/environmentvalues/dynamictypesize). Reference line heights are exposed as tokens. Compose `TextStyle.leading` accepts a multiplier or `TW.Leading`; SwiftUI uses native `lineSpacing` and `Font.leading`. Truncation uses SwiftUI `truncate()` / `lineClamp()` or Compose `TWText(maxLines = ..., overflow = TextOverflow.Ellipsis)`.

`blur` accepts a `TW.Blur` token or nonnegative point/dp radius. Outer shadows use `TW.Shadow` or custom `TW.ShadowLayer` values and an explicit corner radius. Place shadows outside clipping:

- SwiftUI: `.rounded(.lg).shadow(.md, radius: .lg)`
- Compose: `Modifier.shadow(TW.Shadow.Md, radius = TW.Radius.Lg).rounded(TW.Radius.Lg)`

See [effect semantics](docs/effects.md) for layer composition and rendering limits.

## Development

See [development tools, verification and examples](docs/development.md).

Regenerate native colors, numeric scales and JSON fixtures with `python3 tools/generate_tokens.py`. The source is the vendored [Tailwind CSS v4.3.3 theme](https://github.com/tailwindlabs/tailwindcss/blob/v4.3.3/packages/tailwindcss/theme.css). OKLCH colors are converted to 8-bit sRGB with channel clipping. Preserve the [upstream MIT license](tokens/LICENSE.tailwind) when redistributing derived code or artifacts; binary and source packages include it.
