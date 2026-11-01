# Implementation contract

Tailwind Native strips native control decoration and provides Tailwind-style utility method chains directly on SwiftUI views and Jetpack Compose modifiers. It uses the host frameworks, without a CSS runtime, embedded web engine, Material component dependency, or cross-platform renderer.

## Semantics

- Tailwind CSS v4.3.3 is the pinned token reference (`tokens/theme.css`). The generators emit matching native colors and numeric scales, with offline reproducibility checks.
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

## Historical verification before dependency migration — 2026-11-02

`tools/verify.sh` completed successfully using Apple Swift 6.3.3, JDK 21.0.2, Gradle 8.11.1, Android SDK 35 and build-tools 35.0.0.

- Python: 4 checks passed (palette anchors, full numeric scales, source reproducibility, license consistency).
- Swift Testing on macOS: 25 tests passed, including rendered dimensions/colors, RTL padding, local dark variants, full-radius geometry, and macOS text-scaling policy.
- Android: 26 tests passed (11 token/validation tests, 15 UI behavior/rendering tests). They cover button actions, disabled behavior, input editing, pressed-state rendering, RTL, grids and native scrolling.
- Android release AAR assembled. Android Lint reported `No issues found.`
- SwiftUI example app compiled as an independent package consumer.
- Compose example compiled against the published Maven AAR and against the composite source build. This verifies exported public dependencies and plugin/catalog isolation.
- AAR classes JAR, source JAR and Swift resource bundle contain the exact upstream license notice.
- `git diff --check` passed.

Pixel assertions render the actual View tree to a native Canvas; Robolectric/API 37 window capture can return stale buffers. Swift pixel tests clear their destination buffers and use explicit sRGB fills for conditional-style comparisons.

## Coverage audit

- Spacing/sizing: shared generated spacing unit, signed translations, logical and physical padding, min/max dimensions, container tokens and aspect ratios. Finite/range validation prevents invalid graphics values.
- Typography: font-size/weight/tracking tokens and native text styles; platform-specific line metrics are documented. macOS Dynamic Type behavior and an iOS scaling test are explicit.
- Decoration/effects: shared sRGB palette, radius geometry (including full capsules), borders, opacity, clipping, native blur, and reference outer box-shadow layers. Shadows include spread/corner rules, color, layer order, transparent-interior knockout and geometry-independent paint. See `docs/effects.md`.
- Layout: row, column, stack, lazy grid and scrolling. Tests cover actual gaps, grid positions, scrolling and directionality.
- Reset/interaction: plain native controls, 16-point/sp base text, shared neutral foregrounds, themed cursor, native actions/editing, disabled behavior and pressed state. Model/focus state uses native APIs with conditional variants.
- Environments: explicit conditions, local dark themes, pointer hover and mobile-first breakpoints.
- Delivery: public package products, a Maven AAR with exported dependencies, source archives, consumer fixtures, reusable verification commands and upstream notices in distributed artifacts.

## Historical implementation audit — 2026-11-02

The implementation at commit `6f27b089e1704624ee56ecef71d941cd7cfa62dc` passed [GitHub Verify run 41324992656](https://github.com/HotariTobu/tailwind-native/actions/runs/41324992656). Both the Apple and Android jobs completed successfully.

| Gate | Verified evidence |
| --- | --- |
| Pinned builds | Apple Swift 6.3.3 and the pinned Swift 6.4.0 compiled the library locally; CI used full Xcode 26.5. Gradle, Kotlin, AGP, Compose BOM and JDK versions are pinned. |
| Common tokens and utilities | Generated native colors/numeric scales match the vendored theme; reproducibility, reference-value and validation checks passed. Native adapters and their differences are documented. |
| Public native APIs | Both libraries and independent consumer examples compile. Reset controls, layout, typography, decoration, effects, transforms and state/environment variants are implemented. |
| Runtime behavior | macOS: 25 tests passed. Android: 26 tests passed, including native actions, input editing, state styling, layout and pixels. iOS Simulator: 12 tests passed, explicitly including `textSizeRespectsIOSDynamicType`. Python: 4 checks passed. |
| Installation and documentation | README, effect semantics, native platform constraints and independent consumer projects are present. |
| Automated delivery verification | Android Lint reported no issues. Release AAR, source JAR, Maven and composite consumers, and distributed upstream notices were verified. |
| Apple platform claims | CI successfully built iOS 16, tvOS 16 and watchOS 9 device targets with their SDKs, then executed the iOS package tests on a simulator. Mac Catalyst also compiled locally with iOS 16 deployment settings. |

That run verified the earlier implementation, but did not establish dependency lifecycle or security suitability. The current dependency migration requires fresh verification; see [dependencies.md](dependencies.md).

The reusable `tools/verify-apple.sh` discovers the Xcode-generated scheme and an available iPhone simulator. Full Xcode is required for the device-target matrix; the local Command Line Tools alone are sufficient for the macOS checks.

## Local verification setup

The ignored `jetpackcompose/local.properties` points to `/tmp/tailwind-native-android-sdk`. Full verification was invoked with `ANDROID_HOME=/tmp/tailwind-native-android-sdk` and `GRADLE_USER_HOME=/tmp/tailwind-native-gradle`. Configure these paths for another machine. Build outputs, caches and local SDK paths are excluded from source control.

## Current dependency migration verification — 2026-11-02

Commit `ff37aa633fd1de3a7728289bdd41653d84cf8ce2` passed [Verify run 41330372229](https://github.com/HotariTobu/tailwind-native/actions/runs/41330372229) on the current dependency baseline. Both jobs succeeded with zero check annotations. Swift: 25 macOS tests, independent consumer, all declared device SDK targets and 12 iOS Simulator tests passed. Android: 41 tests across API 31 and 37, lint, release packaging, Maven and composite consumers passed. Python's four token checks and both platforms' distributed license checks passed. The latest Tailwind 4.3.3 reference includes all 26 color families. See [the dependency audit](dependencies.md) for exact versions and verification limits.

This supersedes the earlier completion evidence for the current code and toolchain. Android API 31 and macOS 14 are the supported minimums; Compose input uses `TextFieldState`. No legacy plugin opt-outs or older-Android blur no-op are retained. A following documentation-only commit records these results.
