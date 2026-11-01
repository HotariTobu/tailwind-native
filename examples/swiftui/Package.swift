// swift-tools-version: 6.3
import PackageDescription

let package = Package(
    name: "TailwindNativeExample",
    platforms: [.macOS(.v13)],
    dependencies: [.package(name: "TailwindNative", path: "../..")],
    targets: [.executableTarget(name: "TailwindNativeExample", dependencies: [
        .product(name: "swiftui", package: "TailwindNative")
    ])]
)
