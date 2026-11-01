# Tailwind Native

Strip default styling from native UI frameworks, and write Tailwind CSS-style utility modifiers as native method chains. The libraries use SwiftUI and Jetpack Compose directly, without a CSS runtime or a cross-platform rendering layer.

## SwiftUI

Add this repository as a Swift Package dependency and link the `swiftui` product. The root package manifest supports Git-based installation; `swiftui/` also remains usable as a local package. Requires Swift 6.4, iOS 16 / macOS 14 / tvOS 16 / watchOS 9 or later.

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

Requires JDK 25 and Android SDK 37 (minimum Android API 31). Use a composite build in your app's `settings.gradle.kts`:

```kotlin
includeBuild("../tailwind-native/jetpackcompose")
```

Then add `implementation("dev.tailwind:tailwind-jetpackcompose:0.1.0")` to your app's dependencies. This preserves the library's own plugin management and version catalog. Alternatively, run `publishToMavenLocal` in `jetpackcompose/` and add `mavenLocal()` to the consumer's repositories. The library exposes Foundation components, without depending on Material.

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

## Utility conventions

One spacing unit is 4 points / dp. `p(4)` is 16, `w(64)` is 256; fractional units are supported. `maxW` also accepts a `TW.Container` token. `TW.Radius.x4l` / `TW.Radius.X4l` is 32 points / dp; `full` remains a capsule at any view size. Negative spacing is rejected; translations accept negative units. Text sizes use native points / sp.

| Family | API |
| --- | --- |
| Padding | `p`, `px`, `py`, `pt`, `pb`, `pl`, `pr`, `ps`, `pe` |
| Sizing | `w`, `h`, `size`, `wFull`, `hFull`, `minW`, `maxW`, `minH`, `maxH`, `aspect` |
| Decoration | `bg`, `rounded`, `border`, `opacityPercent`, `overflowHidden` |
| Transforms | `rotate` (degrees), `scale` (percent), `translateX`, `translateY` (spacing units) |
| Typography | `text` (size or color), `font`, `italicText`, `underlineText`, `lineThrough`, `tracking`, `textStart`, `textCenter`, `textEnd` |
| Layout | `TWRow`, `TWColumn`, `TWStack`, `TWGrid`, `TWScroll`; explicit zero default gap |
| Variants | `variant(condition)`, `dark`, `at(breakpoint, width)` |
| Colors | Swift `TW.Palette.blue500`, Kotlin `Palette.blue500`; all 22 Tailwind palettes plus black, white, transparent |

Typography modifies a SwiftUI view or Compose `TextStyle`, according to the native framework. `at` uses an explicit logical viewport width supplied by the host (for example, `GeometryReader` or `BoxWithConstraints`). Breakpoints are 640, 768, 1024, 1280 and 1536. Focus, pressed, disabled and application-specific states can use `variant` with native state. SwiftUI also exposes `hover`.

Call order follows native layout semantics. These are typed native utilities, not a CSS cascade: later calls do not universally override earlier calls. Compose decorations are typically placed before padding so they enclose it; SwiftUI backgrounds typically follow padding. Retain native accessibility labels, focus, semantics and input handling.

Wrap a subtree in `TWPreflight` for base text styles (including system light/dark foreground on Compose). SwiftUI `preflight()` applies plain button/input styles and a base font. `TWButton` and `TWInput` also expose undecorated native controls on SwiftUI. Compose `TWButton`, `TWInput`, and `TWText` use undecorated Foundation primitives. Resets apply explicitly to the components where they are used.

CSS DOM selectors, pseudo-elements, browser display modes, CSS positioning and the CSS box model are not emulated. Use native layout containers and scoped modifiers for parent-controlled alignment, sizing and scrolling. Platform differences and completion gates are tracked in [docs/implementation.md](docs/implementation.md).

## Development

```sh
python3 tools/test_tokens.py
./tools/verify-swift.sh
cd jetpackcompose
mise exec -- ./gradlew testDebugUnitTest assembleRelease
```

The palettes are generated offline with `python3 tools/generate_tokens.py` from the pinned [Tailwind v4.1.18 theme](https://github.com/tailwindlabs/tailwindcss/blob/v4.1.18/packages/tailwindcss/theme.css). OKLCH colors are converted to 8-bit sRGB with channel clipping, so wide-gamut browser rendering may differ. See `tokens/LICENSE.tailwind` for the upstream license.

For interaction variants in Compose, pass a shared `MutableInteractionSource` to `TWButton` and use `Modifier.pressed(source)`, `focus(source)`, or `hover(source)` in a composable. Native state drives these styles; the reset button adds no default ripple. `TWPreflight(dark = ...)` can override the system theme for both text defaults and `dark` variants.


The generated numeric scales include radii, text sizes and reference line heights, font weights, tracking, leading, container widths, breakpoints, and blur. Both native sources come from `tokens/theme.css`; fixtures are in `tokens/scales.json`. Text-size utilities set the font size while retaining native line metrics. Compose `TextStyle.leading` accepts a multiplier or a `TW.Leading` token; SwiftUI exposes its native `lineSpacing` and `Font.leading` APIs rather than claiming CSS line-box equivalence. Compose text truncation uses `TWText(maxLines = ..., overflow = TextOverflow.Ellipsis)`; SwiftUI provides `truncate()` and `lineClamp()`.

Native blur accepts a `TW.Blur` token or a nonnegative point/dp radius. Android API 31 is the minimum supported version, so native blur is available throughout the supported range. `shadow(TW.Shadow, color:, radius:)` / `Modifier.shadow(TW.Shadow, color, radius)` use the official outer box-shadow layers, including spread, opacity, and offsets. Custom layers use `TW.ShadowLayer`. Shadows paint outside the box without changing layout or filling its transparent interior; native rasterization can differ at antialiased or blurred edges.

SwiftUI text uses `ScaledMetric` for platforms that support Dynamic Type. macOS retains its native fixed text sizes, as specified by [Apple's dynamicTypeSize documentation](https://developer.apple.com/documentation/swiftui/environmentvalues/dynamictypesize). SwiftUI conditional variants change view structure like native conditional views; keep persistent model/editing state above a variant boundary. Compose variants retain their surrounding composition and native interaction state.

See [consumer examples](examples/README.md) for projects that compile against the public library products.

`./tools/verify.sh` runs the token checks, Swift tests and consumer example, Android tests, Android Lint, release assembly, and both Maven and composite-build consumer verification. It requires the configured Swift toolchain, JDK and Android SDK. Each binary distribution includes the upstream Tailwind theme license; preserve that notice when redistributing derived code or artifacts.


SwiftUI `TWButton.pressed { label in ... }` applies a style using the native button's pressed state. Apply this control-specific method before generic view modifiers. Use native `@FocusState` with `variant` for focus styling. Both preflight implementations use opaque black/white foregrounds, and the Compose input cursor follows its resolved text color.

Place the shadow outside a clipping modifier: SwiftUI commonly uses `.rounded(.lg).shadow(.md, radius: .lg)`, while Compose uses `Modifier.shadow(TW.Shadow.Md, radius = TW.Radius.Lg).rounded(TW.Radius.Lg)`. The shadow's `radius` is explicit because native method chains do not share a CSS cascade.

`./tools/verify-apple.sh` additionally requires full Xcode with iOS, tvOS and watchOS SDKs and an installed iPhone simulator. It builds the declared device targets and runs the Swift package tests on iOS Simulator, including Dynamic Type. GitHub CI uses Xcode 26.5 on `macos-26`; the Android job verifies Linux consumers independently. See [effect semantics](docs/effects.md) for rendering details and reference sources.

Dependency versions, update rationale, and verification limits are recorded in [the dependency audit](docs/dependencies.md). Compose `TWInput` accepts a Foundation `TextFieldState` (for example, `rememberTextFieldState()`), with native input/output transformations.
