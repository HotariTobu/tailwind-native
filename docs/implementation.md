# Implementation contract

Tailwind Native strips native control decoration and provides Tailwind-style utility method chains directly on SwiftUI views and Jetpack Compose modifiers. It uses the host frameworks, without a CSS runtime, embedded web engine, Material component dependency, or cross-platform renderer.

## Semantics

- Tailwind CSS v4.1.18 is the pinned token reference (`tokens/theme.css`). The generators emit matching native colors and numeric scales, with offline reproducibility checks.
- One spacing unit is four logical Apple points / Android dp. Text sizes use native points / sp. Font-size utilities retain native line metrics; reference Tailwind line heights are also exposed as tokens.
- Native chain order is significant. SwiftUI wraps views and Compose processes modifiers; neither API is a CSS cascade.
- Start/end padding follows layout direction; left/right padding and translations use physical directions.
- Layout containers have zero default gaps and padding. Row, column, stack, lazy equal-column grid, and native scrolling are available on both platforms.
- Preflight applies explicitly. It removes control decoration while preserving native interaction and accessibility. Compose uses Foundation primitives; SwiftUI uses plain control styles. The root `TWPreflight` supplies a base text environment.
- The generated palette converts OKLCH to 8-bit sRGB with channel clipping. Both platforms receive the same RGB values. This intentionally cannot reproduce wide-gamut browser output.
- Native rendering differences remain explicit. Android blur requires API 31; macOS does not support Dynamic Type. SwiftUI conditional variants have native conditional-view identity semantics; persistent model/editing state should live above a variant boundary.

## Completion gates

1. Both libraries build using pinned dependencies and development tools.
2. Common tokens and native utility families cover the intended native equivalents and are documented on both platforms.
3. Reset controls, layout, typography, spacing/sizing, decoration, effects, transforms, and state/environment variants have usable public APIs.
4. Tests verify reference values, invalid inputs, layout, rendering, native actions, editing, directionality, and interaction variants.
5. Installation, consumer examples, platform constraints, and browser-only concepts are documented.
6. Automated verification runs successfully, and binary/source distributions include the required upstream notice.
7. Supported Apple platform claims are checked against their SDKs, rather than inferred from the macOS build.

## Verified evidence — 2026-11-02

`tools/verify.sh` completed successfully using Apple Swift 6.3.3, JDK 21.0.2, Gradle 8.11.1, Android SDK 35 and build-tools 35.0.0.

- Python: 4 checks passed (palette anchors, full numeric scales, source reproducibility, license consistency).
- Swift Testing on macOS: 25 tests passed, including rendered dimensions/colors, RTL padding, local dark variants, full-radius geometry, and macOS text-scaling policy.
- Android: 26 tests passed (11 token/validation tests, 15 UI behavior/rendering tests). They cover button actions, disabled behavior, input editing, pressed-state rendering, RTL, grids and native scrolling.
- Android release AAR assembled. Android Lint reported `No issues found.`
- SwiftUI example app compiled as an independent package consumer.
- Compose example compiled against the published Maven AAR and against the composite source build. This verifies exported public dependencies and plugin/catalog isolation.
- AAR classes JAR, source JAR and Swift resource bundle contain the exact upstream license notice.
- `git diff --check` passed.

Compose 1.8's standard screenshot helper waits for a hardware frame callback absent from Robolectric. Pixel tests draw the real view hierarchy to a native Canvas instead, retaining color assertions rather than dropping screenshot coverage. Swift pixel tests clear their destination buffers and use explicit sRGB fills for conditional-style comparisons.

## Coverage audit

- Spacing/sizing: shared generated spacing unit, signed translations, logical and physical padding, min/max dimensions, container tokens and aspect ratios. Finite/range validation prevents invalid graphics values.
- Typography: font-size/weight/tracking tokens and native text styles; platform-specific line metrics are documented. macOS Dynamic Type behavior and an iOS scaling test are explicit.
- Decoration/effects: shared sRGB palette, radius geometry (including full capsules), borders, opacity, clipping, native blur, and reference outer box-shadow layers. Shadows include spread/corner rules, color, layer order, transparent-interior knockout and geometry-independent paint. See `docs/effects.md`.
- Layout: row, column, stack, lazy grid and scrolling. Tests cover actual gaps, grid positions, scrolling and directionality.
- Reset/interaction: plain native controls, 16-point/sp base text, shared neutral foregrounds, themed cursor, native actions/editing, disabled behavior and pressed state. Model/focus state uses native APIs with conditional variants.
- Environments: explicit conditions, local dark themes, pointer hover and mobile-first breakpoints.
- Delivery: public package products, a Maven AAR with exported dependencies, source archives, consumer fixtures, reusable verification commands and upstream notices in distributed artifacts.

## Verification still required

The implementation and local verification are complete for macOS and Android. The Mac Catalyst build also succeeded with iOS 16 deployment settings and the macOS SDK's iOSSupport framework search path, exercising the iOS conditional code. A Catalyst build is not proof of iPhone/tvOS/watchOS builds or the iOS runtime test.

- The final source state also compiled successfully with the pinned Swift 6.4.0 development toolchain.
- GitHub CI verified the declared iOS/tvOS/watchOS device SDK builds at commit `49c93e4`, along with macOS tests/consumer compilation. Its Android job verified Linux compilation, tests, Lint, distributions and both consumer integration paths. Finish the iPhone simulator test run, including Dynamic Type, after discovering the generated Xcode package scheme from its actual list.
- Inspect workflow results, fix failures, and perform the final gate-by-gate audit. The goal remains active until those results prove completion.

The local Apple Command Line Tools do not contain the iPhoneOS/tvOS/watchOS SDKs. Remote CI uses full Xcode 26.5 on the documented GitHub `macos-26` image rather than treating absent local SDKs as successful checks.

## Local verification setup

The ignored `jetpackcompose/local.properties` points to `/tmp/tailwind-native-android-sdk`. Full verification was invoked with `ANDROID_HOME=/tmp/tailwind-native-android-sdk` and `GRADLE_USER_HOME=/tmp/tailwind-native-gradle`. Configure these paths for another machine. Build outputs, caches and local SDK paths are excluded from source control.
