#!/usr/bin/env python3
"""Check that distributed binary and source artifacts retain the upstream notice."""
from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
license_text = (ROOT/'tokens/LICENSE.tailwind').read_bytes()
notice = 'META-INF/tailwind-native/LICENSE.tailwind'
import argparse
parser = argparse.ArgumentParser()
parser.add_argument('--platform', choices=['all', 'android', 'swift'], default='all')
platform = parser.parse_args().platform
if platform in ('all', 'android'):
    aar_path = ROOT/'jetpackcompose/build/outputs/aar/tailwind-jetpackcompose-release.aar'
    with ZipFile(aar_path) as aar:
        with ZipFile(BytesIO(aar.read('classes.jar'))) as classes:
            assert classes.read(notice) == license_text, 'AAR notice differs from upstream'
    sources = ROOT/'jetpackcompose/build/verification-maven/dev/tailwind/tailwind-jetpackcompose/0.1.0/tailwind-jetpackcompose-0.1.0-sources.jar'
    with ZipFile(sources) as archive:
        assert archive.read(notice) == license_text, 'Source archive notice differs from upstream'
if platform in ('all', 'swift'):
    bundles = list((ROOT/'.build').rglob('TailwindNative_swiftui.bundle/Contents/Resources/LICENSE.tailwind'))
    assert bundles, 'Swift resource bundle is missing'
    assert all(bundle.read_bytes() == license_text for bundle in bundles), 'Swift resource notice differs from upstream'
print(f'{platform}: distributed upstream notices verified.')
