# Shared tokens

`theme.css` is copied from Tailwind CSS v4.3.3:
https://github.com/tailwindlabs/tailwindcss/blob/v4.3.3/packages/tailwindcss/theme.css

Tailwind CSS is Copyright Tailwind Labs, Inc., distributed under the MIT license.
The upstream license must accompany redistributed theme data (see LICENSE.tailwind).

Regenerate both native palettes, numeric scales and JSON fixtures with:

```sh
python3 tools/generate_tokens.py
```

The generator converts OKLCH to 8-bit sRGB, clipping out-of-gamut channels.
