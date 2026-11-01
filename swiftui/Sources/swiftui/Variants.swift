import SwiftUI

private struct DarkVariant<Base: View, Content: View>: View {
    @Environment(\.colorScheme) private var scheme
    let base: Base
    let transform: (Base) -> Content
    var body: some View { base.variant(scheme == .dark, transform: transform) }
}
private struct HoverVariant<Base: View, Content: View>: View {
    @State private var hovered = false
    let base: Base
    let transform: (Base) -> Content
    var body: some View {
        #if os(macOS) || os(iOS)
        base.variant(hovered, transform: transform).onHover { hovered = $0 }
        #else
        base
        #endif
    }
}
public extension View {
    func dark<Content: View>(@ViewBuilder _ transform: @escaping (Self) -> Content) -> some View {
        DarkVariant(base: self, transform: transform)
    }
    /// Hover applies on platforms with pointing devices. Use variant for focus/pressed state owned by controls.
    func hover<Content: View>(@ViewBuilder _ transform: @escaping (Self) -> Content) -> some View {
        HoverVariant(base: self, transform: transform)
    }
    func at<Content: View>(_ breakpoint: TW.Breakpoint, width: Double,
                           @ViewBuilder _ transform: (Self) -> Content) -> some View {
        variant(breakpoint.matches(width: width), transform: transform)
    }
}
