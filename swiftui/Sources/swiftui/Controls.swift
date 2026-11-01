import SwiftUI

/// An explicit base style environment for native views.
public struct TWPreflight<Content: View>: View {
    private let content: Content
    public init(@ViewBuilder content: () -> Content) { self.content = content() }
    public var body: some View { content.preflight() }
}

/// A plain native button; focus, keyboard activation, and accessibility remain native.
public struct TWButton<Label: View>: View {
    private let action: () -> Void
    private let label: Label
    public init(action: @escaping () -> Void, @ViewBuilder label: () -> Label) {
        self.action = action; self.label = label()
    }
    public var body: some View { Button(action: action) { label }.buttonStyle(.plain) }
    /// Native ButtonStyle drives pressed state, including keyboard activation.
    /// Apply this control-specific utility before generic view modifiers.
    public func pressed<PressedLabel: View>(@ViewBuilder _ transform: @escaping (ButtonStyleConfiguration.Label) -> PressedLabel) -> some View {
        Button(action: action) { label }.buttonStyle(TWPressedButtonStyle(transform: transform))
    }
}

private struct TWPressedButtonStyle<PressedLabel: View>: ButtonStyle {
    let transform: (ButtonStyleConfiguration.Label) -> PressedLabel
    func makeBody(configuration: Configuration) -> some View {
        configuration.label.variant(configuration.isPressed, transform: transform)
    }
}

/// A single-line native text field without a platform border or background.
public struct TWInput: View {
    private let title: String
    @Binding private var text: String
    public init(_ title: String, text: Binding<String>) {
        self.title = title; self._text = text
    }
    public var body: some View { TextField(title, text: $text).textFieldStyle(.plain) }
}
