#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
# Apple CLT distributions expose Swift Testing as a framework. Xcode needs no override.
DEV_PATH=$(xcode-select -p)
if [ -d "$DEV_PATH/Library/Developer/Frameworks/Testing.framework" ]; then
    swift test --package-path "$ROOT" \
        -Xswiftc -F -Xswiftc "$DEV_PATH/Library/Developer/Frameworks" \
        -Xlinker -rpath -Xlinker "$DEV_PATH/Library/Developer/Frameworks" \
        -Xlinker -rpath -Xlinker "$DEV_PATH/Library/Developer/usr/lib" "$@"
else
    swift test --package-path "$ROOT" "$@"
fi
