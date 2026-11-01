import SwiftUI

/// Overlapping children with no implicit padding or spacing.
public struct TWStack<Content: View>: View {
    private let alignment: Alignment
    private let content: Content
    public init(alignment: Alignment = .topLeading, @ViewBuilder content: () -> Content) {
        self.alignment = alignment; self.content = content()
    }
    public var body: some View { ZStack(alignment: alignment) { content } }
}

/// A native lazy grid with equal-width columns and explicit gaps.
public struct TWGrid<Content: View>: View {
    private let columns: Int
    private let gap: Double
    private let content: Content
    public init(columns: Int, gap: Double = 0, @ViewBuilder content: () -> Content) {
        precondition(columns > 0, "Grid columns must be positive")
        self.columns = columns; self.gap = gap; self.content = content()
    }
    public var body: some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible(minimum: 0), spacing: TW.spacing(gap), alignment: .topLeading), count: columns),
                  alignment: .leading, spacing: TW.spacing(gap)) { content }
    }
}

public extension TW {
    enum ScrollAxis: Sendable { case vertical, horizontal }
}

/// Native scrolling; direct children form a zero-gap row or column.
public struct TWScroll<Content: View>: View {
    private let axis: TW.ScrollAxis
    private let showsIndicators: Bool
    private let content: Content
    public init(axis: TW.ScrollAxis = .vertical, showsIndicators: Bool = false, @ViewBuilder content: () -> Content) {
        self.axis = axis; self.showsIndicators = showsIndicators; self.content = content()
    }
    public var body: some View {
        ScrollView(axis == .vertical ? .vertical : .horizontal, showsIndicators: showsIndicators) {
            if axis == .vertical { VStack(alignment: .leading, spacing: 0) { content } }
            else { HStack(alignment: .top, spacing: 0) { content } }
        }
    }
}
