#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"
python3 tools/test_tokens.py
./tools/verify-swift.sh
./tools/swift.sh build --package-path examples/swiftui
./tools/verify-android.sh
python3 tools/verify_artifacts.py
