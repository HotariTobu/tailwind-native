import SwiftUI

struct TWTextSizeModifier: ViewModifier {
    @ScaledMetric private var points: Double
    init(_ size: TW.TextSize) { self._points = ScaledMetric(wrappedValue: size.rawValue, relativeTo: .body) }
    func body(content: Content) -> some View { content.font(.system(size: points)) }
}

public extension Text {
    func italicText() -> Text { italic() }
    func underlineText() -> Text { underline() }
    func lineThrough() -> Text { strikethrough() }
    /// Tailwind tracking values are ems; supply the text size for conversion to native points.
    func tracking(_ em: Double, size: TW.TextSize) -> Text { tracking(TW.finite(TW.finite(em) * size.rawValue)) }
    func uppercase() -> some View { textCase(.uppercase) }
    func lowercase() -> some View { textCase(.lowercase) }
}
public extension View {
    func textCenter() -> some View { multilineTextAlignment(.center) }
    func textStart() -> some View { multilineTextAlignment(.leading) }
    func textEnd() -> some View { multilineTextAlignment(.trailing) }
    func truncate() -> some View { lineLimit(1).truncationMode(.tail) }
    func lineClamp(_ lines: Int) -> some View {
        precondition(lines > 0, "Line clamp must be positive")
        return lineLimit(lines)
    }
    func blur(_ points: CGFloat) -> some View {
        precondition(points.isFinite && points >= 0, "Blur radius must be finite and nonnegative")
        return blur(radius: points)
    }
}

public extension Text {
    func tracking(_ token: TW.Tracking, size: TW.TextSize) -> Text { tracking(token.rawValue, size: size) }
}
public extension View {
    func maxW(_ token: TW.Container) -> some View { frame(maxWidth: token.rawValue) }
    func blur(_ token: TW.Blur) -> some View { blur(radius: token.rawValue) }
}
