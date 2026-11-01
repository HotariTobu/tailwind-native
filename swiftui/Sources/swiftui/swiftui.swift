import SwiftUI

public extension View {
    func p(_ value: Double) -> some View { padding(TW.spacing(value)) }
    func px(_ value: Double) -> some View { padding(.horizontal, TW.spacing(value)) }
    func py(_ value: Double) -> some View { padding(.vertical, TW.spacing(value)) }
    func pt(_ value: Double) -> some View { padding(.top, TW.spacing(value)) }
    func pb(_ value: Double) -> some View { padding(.bottom, TW.spacing(value)) }
    func ps(_ value: Double) -> some View { padding(.leading, TW.spacing(value)) }
    func pe(_ value: Double) -> some View { padding(.trailing, TW.spacing(value)) }
    func w(_ value: Double) -> some View { frame(width: TW.spacing(value)) }
    func h(_ value: Double) -> some View { frame(height: TW.spacing(value)) }
    func size(_ value: Double) -> some View { frame(width: TW.spacing(value), height: TW.spacing(value)) }
    func wFull() -> some View { frame(maxWidth: .infinity) }
    func hFull() -> some View { frame(maxHeight: .infinity) }
    func minW(_ value: Double) -> some View { frame(minWidth: TW.spacing(value)) }
    func maxW(_ value: Double) -> some View { frame(maxWidth: TW.spacing(value)) }
    func minH(_ value: Double) -> some View { frame(minHeight: TW.spacing(value)) }
    func maxH(_ value: Double) -> some View { frame(maxHeight: TW.spacing(value)) }
    func bg(_ color: Color) -> some View { background(color) }
    func text(_ color: Color) -> some View { foregroundStyle(color) }
    func text(_ size: TW.TextSize) -> some View { modifier(TWTextSizeModifier(size)) }
    func font(_ weight: TW.Weight) -> some View { fontWeight(weight.native) }
    func rounded(_ radius: TW.Radius = .sm) -> some View {
        clipShape(TWRoundedShape(radius: radius))
    }
    func border(_ color: Color, width: CGFloat = 1, radius: TW.Radius = .none) -> some View {
        precondition(width.isFinite && width >= 0, "Border width must be finite and nonnegative")
        return overlay(TWRoundedShape(radius: radius).strokeBorder(color, lineWidth: width))
    }
    func opacityPercent(_ value: Double) -> some View {
        precondition((0...100).contains(value), "Opacity must be between 0 and 100")
        return opacity(value / 100)
    }
    func overflowHidden() -> some View { clipped() }
    func rotate(_ degrees: Double) -> some View { rotationEffect(.degrees(TW.finite(degrees).truncatingRemainder(dividingBy: 360))) }
    func scale(_ percent: Double) -> some View { scaleEffect(TW.finite(percent) / 100) }
    func translateX(_ value: Double) -> some View { offset(x: TW.signedSpacing(value)) }
    func translateY(_ value: Double) -> some View { offset(y: TW.signedSpacing(value)) }
    func aspect(_ ratio: CGFloat) -> some View {
        precondition(ratio.isFinite && ratio > 0, "Aspect ratio must be finite and positive")
        return aspectRatio(ratio, contentMode: .fit)
    }
    /// Removes framework control decoration. Does not remove accessibility or interaction.
    func preflight() -> some View {
        modifier(TWPreflightModifier())
    }
    @ViewBuilder func variant<Content: View>(_ condition: Bool, @ViewBuilder transform: (Self) -> Content) -> some View {
        if condition { transform(self) } else { self }
    }
}

/// Explicit zero-gap containers avoid platform-dependent default stack spacing.
public struct TWRow<Content: View>: View {
    let gap: Double
    let alignment: VerticalAlignment
    let content: Content
    public init(gap: Double = 0, alignment: VerticalAlignment = .center, @ViewBuilder content: () -> Content) {
        self.gap = gap; self.alignment = alignment; self.content = content()
    }
    public var body: some View { HStack(alignment: alignment, spacing: TW.spacing(gap)) { content } }
}
public struct TWColumn<Content: View>: View {
    let gap: Double
    let alignment: HorizontalAlignment
    let content: Content
    public init(gap: Double = 0, alignment: HorizontalAlignment = .leading, @ViewBuilder content: () -> Content) {
        self.gap = gap; self.alignment = alignment; self.content = content()
    }
    public var body: some View { VStack(alignment: alignment, spacing: TW.spacing(gap)) { content } }
}
