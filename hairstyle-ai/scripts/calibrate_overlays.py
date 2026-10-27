#!/usr/bin/env python3
"""
Measure AR hair overlay images so they sit correctly on the head.

For each transparent overlay it reports, as fractions of the image size:
  hairlineAnchorY  front edge of the hair cap at the centre of the image (placed on the user's hairline)
  hairWidthRatio   widest part of the hair cap (scaled to the user's head width)

Copy the values into HairstyleOverlayEngine's asset registry whenever an overlay image changes.

Usage: pip install pillow && python scripts/calibrate_overlays.py app/src/main/assets/hairstyles/*/*.webp
"""
import sys

from PIL import Image

ALPHA_THRESHOLD = 40      # pixels more opaque than this count as hair
CAP_START_COVERAGE = 0.5  # centre band at least half covered: hair cap begins
CAP_END_COVERAGE = 0.3    # centre band below this for 3 rows: hair cap ends
MIN_CAP_THICKNESS = 0.06  # ignore thin stray strands above the real cap


def measure(path):
    image = Image.open(path).convert("RGBA")
    width, height = image.size
    alpha = image.getchannel("A").load()
    x0, x1 = int(width * 0.40), int(width * 0.60)
    coverage = [
        sum(1 for x in range(x0, x1) if alpha[x, y] > ALPHA_THRESHOLD) / (x1 - x0)
        for y in range(height)
    ]

    y = 0
    while y < height:
        start = next((r for r in range(y, height) if coverage[r] >= CAP_START_COVERAGE), None)
        if start is None:
            return None
        end, low_rows = start, 0
        for r in range(start, height):
            if coverage[r] < CAP_END_COVERAGE:
                low_rows += 1
                if low_rows >= 3:
                    break
            else:
                low_rows, end = 0, r
        if (end - start) / height >= MIN_CAP_THICKNESS:
            break
        y = end + 1
    else:
        return None

    cap_width = 0
    for r in range(start, end + 1, 2):
        xs = [x for x in range(width) if alpha[x, r] > ALPHA_THRESHOLD]
        if xs:
            cap_width = max(cap_width, xs[-1] - xs[0])
    return end / height, cap_width / width


def main(paths):
    for path in paths:
        result = measure(path)
        if result is None:
            print(f"{path}: no hair cap found")
        else:
            print(f"{path}: hairlineAnchorY = {result[0]:.3f}f, hairWidthRatio = {result[1]:.3f}f")


if __name__ == "__main__":
    main(sys.argv[1:])
