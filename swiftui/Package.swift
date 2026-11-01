// swift-tools-version: 6.4
import PackageDescription

let package = Package(
    name: "TailwindNative",
    platforms: [.iOS(.v16), .macOS(.v14), .tvOS(.v16), .watchOS(.v9)],
    products: [.library(name: "swiftui", targets: ["swiftui"])],
    targets: [
        .target(name: "swiftui", resources: [.copy("Resources/LICENSE.tailwind")]),
        .testTarget(name: "swiftuiTests", dependencies: ["swiftui"])
    ],
    swiftLanguageModes: [.v6]
)
