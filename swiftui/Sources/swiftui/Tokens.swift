import SwiftUI

/// Shared Tailwind token namespace.
public enum TW {
    public static func spacing(_ value: Double) -> CGFloat {
        precondition(value >= 0, "Spacing must be nonnegative")
        return signedSpacing(value)
    }
    public static func signedSpacing(_ value: Double) -> CGFloat {
        let points = finite(value) * spacingUnit
        precondition(points.isFinite, "Spacing exceeds the native numeric range")
        return CGFloat(points)
    }
    static func finite(_ value: Double) -> Double {
        precondition(value.isFinite, "Utility values must be finite")
        return value
    }
}
