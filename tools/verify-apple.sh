#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"

# Device SDK builds check the declared deployment versions, without app signing.
while read -r apple_sdk apple_triple; do
    apple_sdk_path=$(xcrun --sdk "$apple_sdk" --show-sdk-path)
    swift build --package-path "$ROOT" --sdk "$apple_sdk_path" --triple "$apple_triple" \
        --scratch-path "$ROOT/.build/apple/$apple_sdk"
done <<'PLATFORMS'
iphoneos arm64-apple-ios16.0
appletvos arm64-apple-tvos16.0
watchos arm64_32-apple-watchos9.0
PLATFORMS

mkdir -p "$ROOT/.build/apple"
xcrun simctl list devices available --json > "$ROOT/.build/apple/simulators.json"
ios_simulator_uuid=$(python3 -c '
import json, sys
with open(sys.argv[1]) as f:
    devices = json.load(f)["devices"]
for runtime, entries in devices.items():
    if ".iOS-" in runtime:
        for device in entries:
            if device.get("isAvailable") and "iPhone" in device["name"]:
                print(device["udid"])
                raise SystemExit(0)
raise SystemExit("No available iPhone simulator. Install an iOS simulator runtime in Xcode.")
' "$ROOT/.build/apple/simulators.json")
xcodebuild -list -json > "$ROOT/.build/apple/schemes.json"
ios_scheme=$(python3 -c '
import json, sys
with open(sys.argv[1]) as f:
    listing = json.load(f)
schemes = []
for section in listing.values():
    if isinstance(section, dict):
        schemes.extend(section.get("schemes", []))
for preferred in ("TailwindNative-Package", "TailwindNative", "swiftui"):
    if preferred in schemes:
        print(preferred)
        raise SystemExit(0)
if len(schemes) == 1:
    print(schemes[0])
else:
    raise SystemExit("Cannot identify the package test scheme: " + repr(schemes))
' "$ROOT/.build/apple/schemes.json")
ios_result_bundle="$ROOT/.build/apple/iOS-$(date +%s).xcresult"
printf '%s\n' "Testing iOS scheme: $ios_scheme"
xcodebuild test -scheme "$ios_scheme" -destination "platform=iOS Simulator,id=$ios_simulator_uuid" \
    -derivedDataPath "$ROOT/.build/apple/DerivedData" -resultBundlePath "$ios_result_bundle" \
    CODE_SIGNING_ALLOWED=NO
