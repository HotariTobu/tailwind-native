#!/usr/bin/env python3
"""Independent fixture and generation checks, run with python3 tools/test_tokens.py."""
import json
import subprocess
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

class TokenTests(unittest.TestCase):
    def test_palette_cardinality_and_anchors(self):
        palette = json.loads((ROOT/'tokens/colors.json').read_text())
        self.assertEqual(len(palette), 288)
        self.assertEqual(palette['white'], [255, 255, 255])
        self.assertEqual(palette['black'], [0, 0, 0])
        self.assertEqual(palette['red-500'], [251, 44, 54])
        self.assertEqual(palette['blue-500'], [43, 127, 255])
        self.assertEqual(palette['neutral-50'], [250, 250, 250])
        for family in ['mauve', 'olive', 'mist', 'taupe']:
            for shade in [50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 950]:
                self.assertIn(f'{family}-{shade}', palette)
        for color in palette.values():
            self.assertTrue(all(isinstance(c, int) and 0 <= c <= 255 for c in color))

    def test_numeric_scales_match_reference(self):
        scales = json.loads((ROOT/'tokens/scales.json').read_text())
        self.assertEqual(scales['SpacingUnit'], 4)
        self.assertEqual(list(scales['Radius'].values()), [0, 2, 4, 6, 8, 12, 16, 24, 32, 9999])
        self.assertEqual(list(scales['TextSize'].values()), [12, 14, 16, 18, 20, 24, 30, 36, 48, 60, 72, 96, 128])
        self.assertEqual(list(scales['TextLineHeight'].values()), [16, 20, 24, 28, 28, 32, 36, 40, 48, 60, 72, 96, 128])
        self.assertEqual(list(scales['Weight'].values()), list(range(100, 1000, 100)))
        self.assertEqual(list(scales['Breakpoint'].values()), [640, 768, 1024, 1280, 1536])
        self.assertEqual(list(scales['Tracking'].values()), [-0.05, -0.025, 0, 0.025, 0.05, 0.1])
        self.assertEqual(scales['Shadow']['md'], [
            dict(x=0, y=4, blur=6, spread=-1, opacity=0.1),
            dict(x=0, y=2, blur=4, spread=-2, opacity=0.1)])
        self.assertEqual(scales['Shadow']['none'], [])

    def test_upstream_license_copies_are_identical(self):
        notice = (ROOT/'tokens/LICENSE.tailwind').read_bytes()
        for name in ['swiftui/Sources/swiftui/Resources/LICENSE.tailwind',
                     'jetpackcompose/src/main/resources/META-INF/tailwind-native/LICENSE.tailwind']:
            self.assertEqual((ROOT/name).read_bytes(), notice)

    def test_generated_sources_are_reproducible(self):
        paths = [ROOT/'tokens/colors.json', ROOT/'swiftui/Sources/swiftui/Palette.swift',
                 ROOT/'jetpackcompose/src/main/kotlin/dev/tailwind/nativeui/Palette.kt',
                 ROOT/'tokens/scales.json', ROOT/'swiftui/Sources/swiftui/ThemeTokens.swift',
                 ROOT/'jetpackcompose/src/main/kotlin/dev/tailwind/nativeui/Tokens.kt']
        before = [p.read_bytes() for p in paths]
        subprocess.run([sys.executable, str(ROOT/'tools/generate_tokens.py')], check=True)
        self.assertEqual(before, [p.read_bytes() for p in paths])

if __name__ == '__main__':
    unittest.main()
