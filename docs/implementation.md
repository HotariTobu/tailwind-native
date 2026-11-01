# Native semantics

Tailwind Native provides Tailwind-style utility method chains on SwiftUI views and Jetpack Compose modifiers. It uses the host frameworks directly, without a CSS runtime, embedded web engine, Material component dependency, or cross-platform renderer.

## Shared behavior

- Tailwind CSS v4.3.3 is the token reference (`tokens/theme.css`). Both generators use the same source for colors and numeric scales.
- One spacing unit is four logical Apple points / Android dp. Text sizes use native points / sp. Font-size utilities retain native line metrics; reference Tailwind line heights are exposed separately as tokens.
- Native chain order is significant. SwiftUI wraps views and Compose processes modifiers; neither API is a CSS cascade.
- Start/end padding follows layout direction; left/right padding and translations use physical directions.
- Row, column, stack, lazy equal-column grid, and scrolling containers have zero default gaps and padding.
- Preflight applies explicitly. It removes control decoration while preserving native interaction and accessibility. `TWPreflight` supplies a base text environment; Compose uses Foundation primitives and SwiftUI uses plain control styles.
- The palette contains 26 color families plus black, white and transparent. OKLCH colors are converted to 8-bit sRGB with channel clipping, giving both platforms the same RGB values.
- Numeric inputs validate finite values and applicable ranges before reaching native layout or graphics APIs.

## Platform behavior

Android API 31 is the minimum supported version, making native blur available throughout the supported range. Compose input uses Foundation `TextFieldState`, line limits, and input/output transformations. Interaction variants share a native interaction source with their control.

SwiftUI text scales with Dynamic Type on supported platforms; macOS retains native fixed text sizes. SwiftUI conditional variants have native conditional-view identity semantics, so persistent model/editing state belongs above a variant boundary. Compose variants retain their surrounding composition and native interaction state.

Outer box shadows include spread, corner rules, layer order and transparent-interior knockout. The radius is explicit and shadow paint does not affect layout. See [effect semantics](effects.md).

Browser DOM selectors, pseudo-elements, display modes, positioning and the CSS box model are outside the API. Native layout containers and scoped modifiers control alignment, sizing and scrolling. Native font metrics, antialiasing and Gaussian implementations can differ between platforms.

## Verification

Run `./tools/verify.sh` from the repository root with the [required tools](dependencies.md) installed and `ANDROID_HOME` pointing to the Android SDK. The script checks:

| Area | Checks |
| --- | --- |
| Tokens | Palette reference values, numeric scales, generated-source reproducibility and license consistency |
| SwiftUI | Validation, layout, colors, RTL, control interaction, variants, text scaling and shadow rendering |
| Compose | Token validation and UI behavior on API 31 and 37, including actions, editing, interaction state, layout and pixels |
| Consumers | Independent Swift package, published Maven AAR and Compose composite build |
| Distributions | Release AAR, source JAR, Swift resource bundle and upstream license notices |
| Android analysis | Android Lint |

`./tools/verify-apple.sh` requires full Xcode with iOS, tvOS and watchOS SDKs and an installed iPhone simulator. It builds the declared device targets, discovers the package test scheme and available simulator, and runs iOS tests including Dynamic Type.

Compose pixel assertions render the actual View tree to a native Canvas because Robolectric/API 37 window capture can return stale buffers. These assertions check native drawing rather than device screenshot infrastructure. Swift pixel tests use explicit sRGB fills and cleared destination buffers.

Build outputs, caches and local SDK paths are excluded from source control. Configure an SDK path through `ANDROID_HOME` or an untracked `jetpackcompose/local.properties`; `GRADLE_USER_HOME` can select a separate Gradle cache.
