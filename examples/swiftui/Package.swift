// swift-tools-version: 6.4
import PackageDescription

let package = Package(
    name: "TailwindNativeExample",
    platforms: [.macOS(.v14)],
    dependencies: [.package(name: "TailwindNative", path: "../..")],
    targets: [.executableTarget(name: "TailwindNativeExample", dependencies: [
        .product(name: "swiftui", package: "TailwindNative")
    ])]
)
