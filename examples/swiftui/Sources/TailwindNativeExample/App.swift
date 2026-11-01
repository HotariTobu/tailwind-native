import SwiftUI
import swiftui

@main
struct TailwindNativeExample: App {
    var body: some Scene {
        WindowGroup { ExampleScreen().frame(minWidth: 320, minHeight: 320) }
    }
}

struct ExampleScreen: View {
    @State private var name = ""
    @State private var saved = false
    var body: some View {
        TWPreflight {
            TWColumn(gap: 4) {
                Text("Tailwind Native").text(.xxl).font(TW.Weight.bold)
                Text("Native controls. Utility method chains.").text(TW.Palette.slate500)
                TWInput("Your name", text: $name)
                    .p(3).border(TW.Palette.slate300, radius: .md)
                TWButton(action: { saved = true }) {
                    Text(saved ? "Saved" : "Save").px(4).py(2)
                        .bg(TW.Palette.blue500).text(.white).rounded(.lg)
                }
                .disabled(name.isEmpty)
                .opacityPercent(name.isEmpty ? 50 : 100)
                .hover { $0.scale(102) }
            }.p(6).maxW(TW.Container.md)
        }
    }
}
