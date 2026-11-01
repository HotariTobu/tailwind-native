# Effects and native rendering

Outer shadow tokens come from Tailwind v4.1.18. Both implementations use every layer's offset, blur, spread and opacity; the first layer is painted last, above later layers. A shadow does not participate in layout or hit testing, and its paint is knocked out inside the original rounded box. Set the shadow radius to match the content's rounded corners.

SwiftUI draws the layers in a noninteractive background Canvas. Its Gaussian filter uses half the CSS blur value as sigma. Compose caches software-rendered masks per size/token, supporting the shared effect on the API 31+ baseline without introducing a rendering-library dependency. Android's legacy BlurMaskFilter radius is converted from the target sigma before rendering. Positive spread preserves sharp corners and adjusts small radii using the CSS corner-spread rule. Native antialiasing and Gaussian implementations can vary at edges; this is not a browser pixel renderer.

Use `shadow(layers:color:radius:)` on SwiftUI or `Modifier.shadow(layers, color, radius)` on Compose for custom layers. Each layer validates finite geometry, a nonnegative blur and opacity in 0...1. Colors tint the masks, including their alpha. The radius is explicit; native modifier chains do not have a CSS cascade.

The renderer handles outer box shadows on uniform rounded rectangles/capsules. Native clipping and chain order still apply. Inset shadows and shadows following an arbitrary image's alpha mask are distinct effects and are not represented by the outer-shadow API.

References:

- [CSS Backgrounds 3: shape, spread, knockout, blur and layer order](https://www.w3.org/TR/css-backgrounds-3/#shadow-shape)
- [SwiftUI Gaussian filter radius](https://developer.apple.com/documentation/swiftui/graphicscontext/filter/blur(radius:options:))
- [Android BlurMaskFilter's radius-to-sigma conversion](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/libs/hwui/jni/MaskFilter.cpp)

Tests check the reference layers, specification corner-spread examples, paint outside the box, an untouched transparent interior, a hard shadow's physical offset and explicit color, the no-shadow case, and unchanged measured dimensions.
