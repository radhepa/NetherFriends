"""Silkgrub: the Weft's beast. A fat teal caterpillar of the warped canopy that lives in the wart and spins silk;
the Weft farm them on their decks, and every thread of the Long Cloth began in a Silkgrub's tufts. Shear the
tufts for string (they grow back); it climbs like the Weft do. About 0.6 blocks tall, 1.3 long.

  python tools/creatures/silkgrub.py
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(os.path.dirname(HERE), "people"))

from common import glow_as, painter, shade, tone  # noqa: E402
from mobkit import Model, Pack, faces  # noqa: E402

NAME = "Silkgrub"
TEAL = ["#178c86", "#14807b", "#1c9890", "#15867f"]
TEAL_LIGHT = "#3fb8ad"
TEAL_DARK = "#0d5c58"
INDIGO = ["#2a2d5a", "#262852", "#2f3263"]
INDIGO_DARK = "#191a3a"
SPOT = "#ff9a3c"            # shroomlight orange, the Weft's stitching colour
SPOT_GLOW = "#a8521a"
SILK = ["#d9f2ee", "#cdebe6", "#e6f8f5", "#c3e3de"]
SILK_SHADE = "#9fcfc8"


def hide(c, x, y):
    """Teal back and sides, an indigo underside; a pale ring at each segment's front and back edge."""
    if c.face == "bottom":
        return c.pick(INDIGO)
    if c.face == "top":
        return TEAL_LIGHT if (x + y) % 6 == 0 else c.pick(TEAL)
    if y >= c.h - 2:
        return shade(INDIGO[0], 1.1)
    col = c.pick(TEAL)
    if c.face in ("left", "right") and x in (0, c.w - 1):
        col = TEAL_DARK
    return tone(c, y, col, 0.18)


def segment(spots):
    """A body segment; {spots} glowing orange dots along each flank, like the Weft's stitching."""
    def run(c):
        painter(hide)(c)
        if c.face in ("left", "right") and spots:
            y = c.h // 2
            for i in range(spots):
                x = 1 + i * max(1, (c.w - 2) // max(1, spots))
                if x < c.w - 1:
                    glow_as(c, x, y, SPOT, SPOT_GLOW)
    return run


def head(c):
    for y in range(c.h):
        for x in range(c.w):
            col = c.pick(INDIGO)
            if c.face == "top" and (x + y) % 4 == 0:
                col = shade(TEAL_DARK, 1.0)
            c.set(x, y, tone(c, y, col, 0.12))
    if c.face == "front":
        for ex in (1, c.w - 2):                    # two small hot eyes, like a Weft's
            glow_as(c, ex, 2, SPOT, SPOT_GLOW)
        c.set(c.w // 2, c.h - 2, INDIGO_DARK)       # mouth


def mandible(c, x, y):
    return INDIGO_DARK if y == c.h - 1 else "#3a3d6e"


def leg(c, x, y):
    return INDIGO_DARK if y == c.h - 1 else c.pick(INDIGO)


def silk(c, x, y):
    """A soft tuft of silk: pale, uneven, a little see-through at the edges."""
    if c.face == "bottom":
        return SILK_SHADE
    edge = min(x, c.w - 1 - x)
    if c.face != "top" and y == 0 and edge == 0 and c.rng.random() < 0.6:
        return None
    col = c.pick(SILK)
    if c.rng.random() < 0.15:
        col = SILK_SHADE
    return col


SEGMENTS = [      # z of the pivot, width, height, depth, spots per flank
    (-4.0, 8, 7, 4, 1),
    (0.0, 9, 8, 4, 2),
    (4.0, 8, 7, 4, 1),
    (7.5, 6, 6, 3, 1),
    (10.0, 4, 4, 2, 0),
]


def build():
    m = Model("silkgrub", 64, 64, seed=223)
    P = Pack(m)
    for i, (z, w, h, d, spots) in enumerate(SEGMENTS):
        seg = m.part(f"segment_{i}", (0, 23, z))
        P.add(seg, (-w / 2, -h, -d / 2), (w, h, d), faces(segment(spots)))
        if i < 3:
            for sx in (-1, 1):
                x = -w / 2 + 0.5 if sx < 0 else w / 2 - 2.5
                P.add(seg, (x, 0, -1), (2, 1, 2), faces(painter(leg)), sx > 0, "leg")
            # a soft fringe of silk along the top of the segment, a little wider than the back
            tuft = seg.part(f"silk_{i}", (0, -h, 0))
            P.add(tuft, (-w / 2 + 0.5, -2, -d / 2 + 0.5), (w - 1, 2, d - 1), faces(painter(silk)), share=f"silk{i}", inflate=0.25)
    hd = m.part("head", (0, 23, -6.5))
    P.add(hd, (-3.5, -6, -3), (7, 6, 3), faces(head))
    for sx in (-1, 1):
        P.add(hd, (-2.5 if sx < 0 else 1.5, -2, -4), (1, 2, 1), faces(painter(mandible)), sx > 0, "mandible")
    P.apply()
    m.save()


if __name__ == "__main__":
    build()
