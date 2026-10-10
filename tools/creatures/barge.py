"""Strider barge: the Magmari ferry. A flat raft of basalt slabs lashed together with iron chain, low chain rails
along its sides, a bench for the passenger, a lantern on a post at the bow and a magma brazier at the stern (no
Magmari sets out without a fire). Two chains run forward from the bow to the strider that tows it; the strider is
the game's own, drawn by the renderer. Three blocks wide, five long.

  python tools/creatures/barge.py
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(os.path.dirname(HERE), "people"))

from common import (BASALT, BASALT_DARK, BASALT_LIGHT, IRON, IRON_DARK, MAGMA_CRUST, MAGMA_GLOW, MAGMA_HOT,  # noqa: E402
                    glow_as, painter, shade)
from mobkit import Model, Pack, faces  # noqa: E402

NAME = "Strider barge"
LANTERN = "#ffd27a"
LANTERN_GLOW = "#ff9a3c"


def slabs(c, x, y):
    """The deck: basalt slabs a block square, with dark seams, lashed across with chain every two blocks."""
    if c.face == "bottom":
        return BASALT_DARK
    if c.face == "top":
        if y % 32 in (15, 16):
            return IRON_DARK if x % 4 < 2 else c.pick(IRON)       # the chain lashings
        if x % 16 == 0 or y % 16 == 0:
            return shade(BASALT_DARK, 0.9)
        col = c.pick(BASALT)
        if (x * 7 + y * 3) % 23 == 0:
            col = BASALT_LIGHT
        return col
    # the hull's sides: basalt, darker toward the waterline, scorched along the bottom
    if y >= c.h - 2:
        return shade(BASALT_DARK, 0.7)
    col = c.pick(BASALT)
    return shade(col, 1.0 - 0.08 * y)


def chain(c, x, y):
    """Iron chain: links alternating face-on and edge-on along the length."""
    along = x if c.face in ("top", "bottom", "front", "back") and c.w > c.h else y
    if along % 4 == 3:
        return IRON_DARK
    if along % 8 < 4:
        return c.pick(IRON)
    return shade(c.pick(IRON), 0.78)


def post(c, x, y):
    if c.face in ("top", "bottom"):
        return BASALT_LIGHT if c.face == "top" else BASALT_DARK
    col = c.pick(BASALT)
    return BASALT_LIGHT if x == 0 else col


def bench(c, x, y):
    if c.face == "top":
        return shade(c.pick(BASALT), 1.1) if y % 6 else BASALT_DARK
    return c.pick(BASALT) if y < c.h - 1 else BASALT_DARK


def lantern(c, x, y):
    if c.face in ("top", "bottom"):
        return IRON_DARK
    if x in (0, c.w - 1) or y in (0, c.h - 1):
        return IRON_DARK
    glow_as(c, x, y, LANTERN, LANTERN_GLOW)
    return None


def brazier(c, x, y):
    """A basalt bowl of glowing magma: no Magmari sets out without a fire."""
    if c.face == "top":
        edge = min(x, y, c.w - 1 - x, c.h - 1 - y)
        if edge == 0:
            return BASALT_LIGHT
        glow_as(c, x, y, MAGMA_HOT if (x + y) % 3 else MAGMA_GLOW, MAGMA_GLOW)
        return None
    if c.face == "bottom":
        return BASALT_DARK
    if y == 0:
        glow_as(c, x, y, MAGMA_GLOW, MAGMA_GLOW)
        return None
    return c.pick(MAGMA_CRUST) if y < 2 else c.pick(BASALT)


def build():
    m = Model("barge", 256, 256, seed=313)
    P = Pack(m)
    deck = m.part("deck", (0, 24, 0))
    P.add(deck, (-24, -6, -40), (48, 6, 80), faces(painter(slabs)))
    rails = m.part("rails", (0, 24, 0))
    P.add(rails, (-23, -9, -36), (2, 3, 72), faces(painter(chain)), share="rail")
    P.add(rails, (21, -9, -36), (2, 3, 72), faces(painter(chain)), share="rail")
    for i, (x, z) in enumerate(((-23, -39), (20, -39), (-23, 36), (20, 36))):
        P.add(rails, (x, -11, z), (3, 5, 3), faces(painter(post)), share="post")
    seat = m.part("bench", (0, 24, 0))
    P.add(seat, (-18, -13, 8), (36, 7, 7), faces(painter(bench)))
    bow = m.part("bow", (0, 24, -38))
    P.add(bow, (-1.5, -20, -1.5), (3, 14, 3), faces(painter(post)))
    P.add(bow, (-2.5, -26, -2.5), (5, 6, 5), faces(lantern_faces()))
    stern = m.part("stern", (0, 24, 30))
    P.add(stern, (-5, -11, -5), (10, 5, 10), faces(painter(brazier)))
    tow = m.part("tow", (0, 24, -40))
    P.add(tow, (-6, -10, -24), (1, 1, 24), faces(painter(chain)), share="tow")
    P.add(tow, (5, -10, -24), (1, 1, 24), faces(painter(chain)), share="tow")
    P.apply()
    m.save()


def lantern_faces():
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                col = lantern(c, x, y)
                if col is not None:
                    c.set(x, y, col)
    return run


if __name__ == "__main__":
    build()
