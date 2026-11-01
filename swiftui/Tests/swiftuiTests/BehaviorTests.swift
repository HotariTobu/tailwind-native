import SwiftUI
import Testing
@testable import swiftui

@Test func completeNumericScalesMatchReference() {
    #expect(TW.Radius.allCases.map(\.rawValue) == [0, 2, 4, 6, 8, 12, 16, 24, 32, 9999])
    #expect(TW.TextSize.allCases.map(\.lineHeight) == [16, 20, 24, 28, 28, 32, 36, 40, 48, 60, 72, 96, 128])
    #expect(TW.Weight.allCases.map(\.rawValue) == Array(stride(from: 100.0, through: 900.0, by: 100)))
    #expect(TW.Tracking.wide.rawValue == 0.025)
    #expect(TW.Container.xs.rawValue == 320)
}

@Test func fullRadiusScalesBeyondFixedRadiusSentinel() {
    let path = TWRoundedShape(radius: .full).path(in: CGRect(x: 0, y: 0, width: 50_000, height: 50_000))
    #expect(path.contains(CGPoint(x: 25_000, y: 25_000)))
    #expect(!path.contains(CGPoint(x: 4_000, y: 4_000)))
}

@MainActor @Test func layoutContainersCompile() {
    _ = TWStack {
        TWScroll { TWColumn(gap: 2) { Text("One"); Text("Two") } }
        TWGrid(columns: 2, gap: 2) { ForEach(0..<4) { Text("\($0)") } }
    }.pl(2).pr(4).maxW(TW.Container.sm).blur(TW.Blur.none)
}

#if os(macOS)
@MainActor private func pixel<V: View>(_ view: V, x: Int, y: Int) throws -> [UInt8] {
    let image = try #require(ImageRenderer(content: view).cgImage)
    let colorSpace = try #require(CGColorSpace(name: CGColorSpace.sRGB))
    let context = try #require(CGContext(data: nil, width: image.width, height: image.height,
        bitsPerComponent: 8, bytesPerRow: image.width * 4, space: colorSpace,
        bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
    context.clear(CGRect(x: 0, y: 0, width: image.width, height: image.height))
    context.draw(image, in: CGRect(x: 0, y: 0, width: image.width, height: image.height))
    let data = try #require(context.data?.assumingMemoryBound(to: UInt8.self))
    let index = (y * image.width + x) * 4
    return Array(UnsafeBufferPointer(start: data + index, count: 4))
}

@MainActor @Test func physicalLeftPaddingStaysLeftInRTL() throws {
    let view = Color.red.size(5).pl(3).environment(\.layoutDirection, .rightToLeft)
    #expect(try pixel(view, x: 2, y: 10)[3] == 0)
    #expect(try pixel(view, x: 20, y: 10)[3] == 255)
}

@MainActor @Test func startPaddingFollowsRTL() throws {
    let view = Color.red.size(5).ps(3).environment(\.layoutDirection, .rightToLeft)
    #expect(try pixel(view, x: 2, y: 10)[3] == 255)
    #expect(try pixel(view, x: 30, y: 10)[3] == 0)
}

@MainActor @Test func darkVariantUsesLocalColorScheme() throws {
    let dark = Color.clear.size(5).dark { $0.bg(.white) }.bg(TW.Palette.blue500).environment(\.colorScheme, .dark)
    let light = Color.clear.size(5).dark { $0.bg(.white) }.bg(TW.Palette.blue500).environment(\.colorScheme, .light)
    #expect(try pixel(dark, x: 10, y: 10) == [255, 255, 255, 255])
    let lightPixel = try pixel(light, x: 10, y: 10)
    #expect(lightPixel == [43, 127, 255, 255])
}

@MainActor @Test func textSizeFollowsMacOSDynamicTypePolicy() throws {
    let small = try #require(ImageRenderer(content: Text("Aa").text(.base)
        .environment(\.dynamicTypeSize, .large)).cgImage)
    let large = try #require(ImageRenderer(content: Text("Aa").text(.base)
        .environment(\.dynamicTypeSize, .accessibility3)).cgImage)
    // Apple's dynamicTypeSize contract does not scale macOS text.
    #expect(large.height == small.height)
    #expect(large.width == small.width)
}
#endif

#if os(iOS)
@MainActor @Test func textSizeRespectsIOSDynamicType() throws {
    let small = try #require(ImageRenderer(content: Text("Aa").text(.base)
        .environment(\.dynamicTypeSize, .large)).cgImage)
    let large = try #require(ImageRenderer(content: Text("Aa").text(.base)
        .environment(\.dynamicTypeSize, .accessibility3)).cgImage)
    #expect(large.height > small.height)
    #expect(large.width > small.width)
}
#endif

@Test func shadowLayersMatchTailwindReference() {
    #expect(TW.Shadow.md.layers == [
        .init(x: 0, y: 4, blur: 6, spread: -1, opacity: 0.1),
        .init(x: 0, y: 2, blur: 4, spread: -2, opacity: 0.1)
    ])
    #expect(TW.Shadow.xxs.layers.first?.blur == 0)
    #expect(TW.Shadow.none.layers.isEmpty)
}

#if os(macOS)
@MainActor @Test func boxShadowPaintsOutsideWithoutFillingInterior() throws {
    let view = Color.clear.size(10).shadow(TW.Shadow.md).p(10).bg(TW.Palette.white)
    let outside = try pixel(view, x: 38, y: 60)
    #expect(outside[0] < 255)
    #expect(outside[0] == outside[1])
    #expect(try pixel(view, x: 60, y: 60) == [255, 255, 255, 255])
    let image = try #require(ImageRenderer(content: view).cgImage)
    #expect(image.width == 120)
    #expect(image.height == 120)
}

@MainActor @Test func shadowNoneAddsNoPaint() throws {
    let view = Color.clear.size(10).shadow(TW.Shadow.none).p(10).bg(TW.Palette.white)
    #expect(try pixel(view, x: 38, y: 60) == [255, 255, 255, 255])
}
#endif

#if os(macOS)
@MainActor @Test func hardShadowUsesPhysicalOffsetAndExplicitColor() throws {
    let layer = TW.ShadowLayer(x: 4, y: 0, blur: 0, spread: 0, opacity: 1)
    let view = Color.clear.size(10).shadow(layers: [layer], color: TW.Palette.blue500)
        .p(10).bg(TW.Palette.white)
    #expect(try pixel(view, x: 82, y: 60) == [43, 127, 255, 255])
    #expect(try pixel(view, x: 60, y: 60) == [255, 255, 255, 255])
    #expect(try pixel(view, x: 38, y: 60) == [255, 255, 255, 255])
}
#endif

@Test func signedSpacingPreservesNegativeValues() {
    #expect(TW.spacingUnit == 4)
    #expect(TW.signedSpacing(-4) == -16)
}

#if os(macOS)
@Test func invalidSpacingIsRejected() async {
    await #expect(processExitsWith: .failure) { _ = TW.spacing(-1) }
}
@Test func nonfiniteTranslationIsRejected() async {
    await #expect(processExitsWith: .failure) { await MainActor.run { _ = Color.clear.translateX(.infinity) } }
}
@Test func invalidAspectRatioIsRejected() async {
    await #expect(processExitsWith: .failure) { await MainActor.run { _ = Color.clear.aspect(0) } }
}
#endif

@Test func shadowCornerSpreadMatchesSpecificationExamples() {
    #expect(twShadowCornerRadius(radius: 10, spread: 20) == 27.5)
    #expect(twShadowCornerRadius(radius: 0, spread: 5) == 0)
    #expect(twShadowCornerRadius(radius: 10, spread: -2) == 8)
}

#if os(macOS)
@MainActor @Test func preflightUsesSharedNeutralForegroundColors() throws {
    let light = Rectangle().fill(.foreground).size(5).preflight().environment(\.colorScheme, .light)
    let dark = Rectangle().fill(.foreground).size(5).preflight().environment(\.colorScheme, .dark)
    #expect(try pixel(light, x: 10, y: 10) == [0, 0, 0, 255])
    #expect(try pixel(dark, x: 10, y: 10) == [255, 255, 255, 255])
}
#endif
