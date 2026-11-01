import SwiftUI

private struct TWBoxShadow: ViewModifier {
    let layers: [TW.ShadowLayer]
    let color: Color
    let radius: TW.Radius

    private var padding: CGFloat {
        var extent: Double = 0
        for layer in layers {
            let offset = max(abs(layer.x), abs(layer.y))
            let margin = offset + layer.blur * 2 + abs(layer.spread) + 1
            extent = max(extent, margin)
        }
        return CGFloat(extent)
    }

    private func draw(context: GraphicsContext, box: CGRect) {
        let outline = TWRoundedShape(radius: radius).path(in: box)
        for layer in layers.reversed() {
            let spreadBox = box.insetBy(dx: -layer.spread, dy: -layer.spread)
            guard spreadBox.width > 0 && spreadBox.height > 0 else { continue }
            let corner: CGFloat = radius == .full ? min(spreadBox.width, spreadBox.height) / 2
                : CGFloat(twShadowCornerRadius(radius: radius.rawValue, spread: layer.spread))
            let path = Path(roundedRect: spreadBox, cornerSize: CGSize(width: corner, height: corner))
            var layerContext = context
            layerContext.clip(to: outline, options: .inverse)
            // GraphicsContext blur radius is Gaussian sigma; CSS specifies half its blur token.
            layerContext.addFilter(.blur(radius: layer.blur / 2))
            let shiftedPath = path.applying(CGAffineTransform(translationX: layer.x, y: layer.y))
            layerContext.fill(shiftedPath, with: .color(color.opacity(layer.opacity)))
        }
    }

    func body(content: Content) -> some View {
        content.background {
            GeometryReader { geometry in
                Canvas { context, _ in
                    draw(context: context, box: CGRect(x: padding, y: padding,
                        width: geometry.size.width, height: geometry.size.height))
                }
                .frame(width: geometry.size.width + padding * 2, height: geometry.size.height + padding * 2)
                .offset(x: -padding, y: -padding)
            }
            .allowsHitTesting(false)
            .accessibilityHidden(true)
        }
    }
}

public extension View {
    /// Tailwind outer box shadows, including layered opacity, offset and spread.
    /// Set the radius explicitly to match rounded corners. Shadow paint does not change layout.
    func shadow(_ token: TW.Shadow, color: Color = .black, radius: TW.Radius = .none) -> some View {
        shadow(layers: token.layers, color: color, radius: radius)
    }
    func shadow(layers: [TW.ShadowLayer], color: Color = .black, radius: TW.Radius = .none) -> some View {
        modifier(TWBoxShadow(layers: layers, color: color, radius: radius))
    }

}

// CSS Backgrounds 3, Shadow Shape, Spread, and Knockout.
func twShadowCornerRadius(radius: Double, spread: Double) -> Double {
    let ratio = spread > 0 && radius < spread ? radius / spread - 1 : 0
    let adjusted = spread > 0 && radius < spread ? spread * (1 + ratio * ratio * ratio) : spread
    return max(0, radius + adjusted)
}
