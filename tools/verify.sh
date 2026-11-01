#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"
python3 tools/test_tokens.py
./tools/swift.sh test --package-path "$ROOT/swiftui"
./tools/swift.sh build --package-path examples/swiftui-app
./tools/verify-android.sh
python3 tools/verify_artifacts.py
