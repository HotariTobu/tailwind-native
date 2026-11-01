import SwiftUI
import Testing
@testable import swiftui

@Test func spacingUsesFourPointsPerUnit() {
    #expect(TW.spacing(0) == 0)
    #expect(TW.spacing(0.5) == 2)
    #expect(TW.spacing(4) == 16)
    #expect(TW.spacing(96) == 384)
}
@Test func defaultTokensMatchTailwind() {
    #expect(TW.Radius.sm.rawValue == 4)
    #expect(TW.Radius.lg.rawValue == 8)
    #expect(TW.TextSize.base.rawValue == 16)
    #expect(TW.TextSize.xxxl.rawValue == 30)
}
@MainActor @Test func utilityChainsCompile() {
    let content = TWColumn(gap: 4) {
        Text("Tailwind Native").text(.xl).font(TW.Weight.bold)
        Button("Continue") {}.preflight().px(4).py(2)
            .bg(.blue).text(.white).rounded(.lg)
    }.p(6).w(80).minH(20).border(.gray, radius: .lg)
        .opacityPercent(90).translateX(-1).variant(true) { $0.rotate(5) }
    #expect(String(describing: type(of: content)).contains("ModifiedContent"))
}

#if os(macOS)
import AppKit

@MainActor @Test func spacingAndSizingAffectRenderedBounds() throws {
    let renderer = ImageRenderer(content: Color.red.w(10).h(5).p(2))
    let image = try #require(renderer.cgImage)
    #expect(image.width == 56)
    #expect(image.height == 36)
}

@MainActor @Test func rowGapAffectsRenderedBounds() throws {
    let renderer = ImageRenderer(content: TWRow(gap: 3) {
        Color.red.size(5)
        Color.blue.size(5)
    })
    let image = try #require(renderer.cgImage)
    #expect(image.width == 52)
    #expect(image.height == 20)
}

@MainActor @Test func backgroundUsesGeneratedColor() throws {
    let renderer = ImageRenderer(content: Color.clear.size(5).bg(TW.Palette.blue500))
    let image = try #require(renderer.cgImage)
    let space = try #require(CGColorSpace(name: CGColorSpace.sRGB))
    let context = try #require(CGContext(data: nil, width: image.width, height: image.height,
        bitsPerComponent: 8, bytesPerRow: image.width * 4, space: space,
        bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
    context.clear(CGRect(x: 0, y: 0, width: image.width, height: image.height))
    context.draw(image, in: CGRect(x: 0, y: 0, width: image.width, height: image.height))
    let pixels = try #require(context.data?.assumingMemoryBound(to: UInt8.self))
    let index = (10 * image.width + 10) * 4
    let color = NSColor(srgbRed: Double(pixels[index])/255, green: Double(pixels[index + 1])/255,
                        blue: Double(pixels[index + 2])/255, alpha: 1)
    #expect(abs(color.redComponent - 43.0/255) < 0.01)
    #expect(abs(color.greenComponent - 127.0/255) < 0.01)
    #expect(abs(color.blueComponent - 1) < 0.01)
}
#endif

@Test func breakpointBoundariesAreInclusive() {
    #expect(!TW.Breakpoint.md.matches(width: 767))
    #expect(TW.Breakpoint.md.matches(width: 768))
}

@MainActor @Test func controlsAndVariantsCompile() {
    _ = TWPreflight {
        TWColumn {
            TWInput("Name", text: .constant(""))
            TWButton(action: {}) { Text("Save") }.pressed { $0.opacityPercent(80) }
                .dark { $0.bg(TW.Palette.slate900) }
                .hover { $0.opacityPercent(80) }
                .at(.md, width: 800) { $0.p(8) }
            Text("Heading").tracking(0.025, size: .xl).underlineText()
                .text(.xl).textCenter().lineClamp(2)
        }
    }
}
