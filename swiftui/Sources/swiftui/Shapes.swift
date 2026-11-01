import SwiftUI

/// A full radius is half the shorter edge, including views larger than 19,998 points.
struct TWRoundedShape: InsettableShape {
    let radius: TW.Radius
    var insetAmount: CGFloat = 0
    func path(in rect: CGRect) -> Path {
        let value = radius == .full ? min(rect.width, rect.height) / 2 : radius.rawValue
        return RoundedRectangle(cornerRadius: value).inset(by: insetAmount).path(in: rect)
    }
    func inset(by amount: CGFloat) -> some InsettableShape {
        var shape = self
        shape.insetAmount += amount
        return shape
    }
}
