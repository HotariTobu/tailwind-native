#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT/jetpackcompose"
run_gradle() {
    if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        ./gradlew "$@"
    elif command -v mise >/dev/null 2>&1; then
        mise exec -- ./gradlew "$@"
    else
        ./gradlew "$@"
    fi
}
verification_repository="$ROOT/jetpackcompose/build/verification-maven"
run_gradle testDebugUnitTest assembleRelease lintRelease publishToMavenLocal "-Dmaven.repo.local=$verification_repository"
run_gradle -p "$ROOT/examples/compose" "-PtailwindNativeRepository=$verification_repository" assembleDebug
run_gradle -p "$ROOT/examples/compose" assembleDebug
