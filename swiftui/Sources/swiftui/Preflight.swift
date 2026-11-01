import SwiftUI

struct TWPreflightModifier: ViewModifier {
    @Environment(\.colorScheme) private var scheme
    func body(content: Content) -> some View {
        let foreground = scheme == .dark ? TW.Palette.white : TW.Palette.black
        content.buttonStyle(.plain).textFieldStyle(.plain).text(TW.TextSize.base)
            .foregroundStyle(foreground).tint(foreground)
    }
}
