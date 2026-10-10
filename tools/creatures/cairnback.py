"""Cairnback: the Withren's beast. A slow, low tortoise of the soul sand valleys whose high, stepped shell is
plated in bone like a cairn of stacked stones, with a bone bowl on top where a small soul fire always burns.
The Withren walk them in their remembrance processions, and the fire never goes out while the beast lives.
About 1.1 blocks to the top of the shell (the flame stands above that), 1.4 long.

  python tools/creatures/cairnback.py
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(os.path.dirname(HERE), "people"))

from common import (BASALT, BASALT_DARK, BASALT_LIGHT, BONE, BONE_DARK, BONE_DEEP, SOUL_DEEP, SOUL_FIRE,  # noqa: E402
                    SOUL_HOT, SOUL_SOIL, glow_as, painter, shade, tone)
from mobkit import Model, Pack, faces  # noqa: E402

NAME = "Cairnback"
SKIN = ["#5a4d45", "#524640", "#62554c"]        # soul-soil grey-brown, scaly
SKIN_DARK = "#3b322d"
SKIN_LIGHT = "#76685d"
SOCKET = "#1d1a19"


def skin(c, k=0.16):
    for y in range(c.h):
        for x in range(c.w):
            col = c.pick(SKIN)
            if (x + 2 * y) % 5 == 0:
                col = SKIN_DARK                      # scales
            elif (x * 3 + y) % 11 == 0:
                col = SKIN_LIGHT
            c.set(x, y, tone(c, y, col, k))
    if c.face == "bottom":
        c.fill(shade(SKIN_DARK, 0.9))


def plates(c, x, y):
    """Bone plates laid like the stones of a cairn: offset courses with dark basalt seams between them."""
    if c.face == "bottom":
        return BASALT_DARK
    if c.face == "top":
        if x % 5 == 0 or y % 4 == 0:
            return shade(BONE_DEEP, 0.8)
        return c.pick(BONE)
    course = y // 3
    seam_y = y % 3 == 2
    seam_x = (x + course * 2) % 5 == 4
    if seam_y or seam_x:
        return shade(BONE_DEEP, 0.72)
    col = c.pick(BONE)
    if c.rng.random() < 0.1:
        col = BONE_DARK
    return tone(c, y % 3, col, 0.08)


def rim(c, x, y):
    """The shell's lower rim: dark basalt with a pale bone edge along the top."""
    if c.face == "top":
        return BONE_DARK if min(x, y, c.w - 1 - x, c.h - 1 - y) == 0 else BASALT_DARK
    if c.face == "bottom":
        return BASALT_DARK
    if y == 0:
        return BONE_DARK
    col = c.pick(BASALT)
    if (x + y) % 4 == 0:
        col = BASALT_LIGHT
    return tone(c, y, col, 0.15)


def belly(c, x, y):
    if c.face == "bottom":
        return shade(BONE_DEEP, 0.9) if (x // 3 + y // 3) % 2 else BONE_DEEP
    return shade(c.pick(SKIN), 0.9)


def bowl(c, x, y):
    """The bone bowl the soul fire sits in."""
    if c.face == "top":
        edge = min(x, y, c.w - 1 - x, c.h - 1 - y)
        return BONE[0] if edge == 0 else shade(SOUL_SOIL[0], 0.8)
    if c.face == "bottom":
        return BONE_DEEP
    return BONE_DARK if y == c.h - 1 else c.pick(BONE)


def flame(hot=False):
    """Soul fire: lit, so it glows in the dark (a darker emissive keeps its colour)."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                base = SOUL_HOT if hot or (y >= c.h - 2 and 0 < x < c.w - 1) else SOUL_FIRE
                if not hot and y == 0 and x in (0, c.w - 1):
                    c.clear(x, y)                     # a ragged top
                    continue
                glow_as(c, x, y, base, SOUL_DEEP if base == SOUL_FIRE else "#7fd6e0")
    return run


def skull(c):
    """The head: a long, bony face, pale as the plates, with deep sockets and a soul-lit glint in each."""
    for y in range(c.h):
        for x in range(c.w):
            col = c.pick(BONE)
            if y >= c.h - 1:
                col = BONE_DARK
            c.set(x, y, tone(c, y, col, 0.1))
    if c.face == "front":
        for ex in (1, c.w - 2):
            c.set(ex, 1, SOCKET)
            c.set(ex, 2, SOCKET)
            glow_as(c, ex, 2, SOUL_FIRE, SOUL_DEEP)
        for x in range(1, c.w - 1):
            c.set(x, c.h - 2, BONE_DEEP if x % 2 else BONE_DARK)    # a beak-edge of a mouth
    if c.face in ("left", "right"):
        c.set(1, 1, SOCKET)
        glow_as(c, 1, 2, SOUL_FIRE, SOUL_DEEP)


def leg(c):
    skin(c, 0.22)
    if c.face in ("front", "back", "left", "right"):
        for x in range(c.w):
            c.set(x, c.h - 1, BONE_DARK if x % 2 == 0 else BONE_DEEP)     # blunt bone claws


def tail(c):
    skin(c, 0.1)
    if c.face == "top":
        for y in range(0, c.h, 2):
            for x in range(c.w):
                c.set(x, y, BONE_DARK)


def build(child=False):
    m = Model("cairnback", 128, 64, seed=211)
    m.preview = {"head": [0.15, 0.2, 0]}
    P = Pack(m)
    body = m.part("body", (0, 17, 0))
    P.add(body, (-8, -3, -9), (16, 4, 18), faces(painter(rim)))                   # the rim of the shell
    P.add(body, (-7, -7, -8), (14, 4, 16), faces(painter(plates)))                # first course of plates
    P.add(body, (-5, -10, -6), (10, 3, 12), faces(painter(plates)))               # the cairn's crown
    P.add(body, (-6, 1, -7), (12, 2, 14), faces(painter(belly)))                  # belly
    brazier = body.part("brazier", (0, -10, 0))
    P.add(brazier, (-2.5, -2, -2.5), (5, 2, 5), faces(painter(bowl)))
    for sx in (-1, 1):
        P.add(brazier, (sx * 2.5 - 0.5, -3, -0.5), (1, 1, 1), faces(painter(bowl)), sx > 0, "lug")
    fire = brazier.part("flame", (0, -2, 0))
    P.add(fire, (-1.5, -5, -1.5), (3, 5, 3), faces(flame()))
    P.add(fire, (-0.5, -7, -0.5), (1, 2, 1), faces(flame(hot=True)))
    head = m.part("head", (0, 18, -9))
    P.add(head, (-2, -2, -4), (4, 4, 4), faces(lambda c: skin(c, 0.1)))           # neck
    P.add(head, (-3, -4, -9), (6, 5, 5), faces(skull))                            # skull
    for i, z in enumerate((-5, 5)):
        for side, sx in (("right", -1), ("left", 1)):
            lg = m.part(f"{side}_leg_{i}", (6 * sx, 19, z))
            P.add(lg, (-2, 0, -2), (4, 5, 4), faces(leg), side == "left", "leg")
    tl = body.part("tail", (0, 1, 9), (0.35, 0, 0))
    P.add(tl, (-1, -1, 0), (2, 2, 4), faces(tail))
    P.apply()
    m.save()


if __name__ == "__main__":
    build()
