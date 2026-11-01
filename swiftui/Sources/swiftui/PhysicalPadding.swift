import SwiftUI

private struct TWPhysicalPadding: ViewModifier {
    @Environment(\.layoutDirection) private var direction
    let left: Bool
    let value: Double
    func body(content: Content) -> some View {
        content.padding((direction == .leftToRight) == left ? .leading : .trailing, TW.spacing(value))
    }
}
public extension View {
    /// Physical left padding; use ps/pe for layout-direction-aware padding.
    func pl(_ value: Double) -> some View { modifier(TWPhysicalPadding(left: true, value: value)) }
    func pr(_ value: Double) -> some View { modifier(TWPhysicalPadding(left: false, value: value)) }
}
