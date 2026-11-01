#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
if [ -n "${DEVELOPER_DIR:-}" ]; then
    exec xcrun swift "$@"
elif command -v mise >/dev/null 2>&1; then
    swift_caller_dir=$PWD
    cd "$ROOT/swiftui"
    exec mise exec -- sh -c 'cd "$1"; shift; exec swift "$@"' sh "$swift_caller_dir" "$@"
else
    exec swift "$@"
fi
