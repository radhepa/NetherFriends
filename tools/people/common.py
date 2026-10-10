"""Shared painting helpers and Nether palettes for the people models."""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.dirname(HERE))

from mobkit import shade, mix, hexc  # noqa: E402,F401

SIDES = ("front", "back", "left", "right")

# Gold (piglin gold, nether gold ore)
GOLD = ["#f6cf4a", "#f0c23a", "#fbd95c"]
GOLD_LIGHT = "#fff1a8"
GOLD_DARK = "#c38a1d"
GOLD_DEEP = "#8a5a12"
# Blackstone
BLACKSTONE = ["#2a2429", "#302a2f", "#262126", "#2d272c"]
BLACKSTONE_LIGHT = "#463d44"
BLACKSTONE_DARK = "#1b171b"
# Crimson stem / hoglin hide
CRIMSON = ["#6a2b3a", "#5e2433", "#733142"]
CRIMSON_LIGHT = "#8f4356"
CRIMSON_DARK = "#431823"
HIDE = ["#8a5a3a", "#7d5034", "#956442"]
HIDE_LIGHT = "#b88358"
HIDE_DARK = "#5a3a24"
LEATHER = ["#6b4423", "#64401f", "#724a28"]
LEATHER_DARK = "#43290f"
LEATHER_LIGHT = "#8c5e34"
# Soul valley: soul sand, soul soil, soul fire, bone, basalt
SOUL_SAND = ["#51402f", "#4a3a2a", "#5a4734"]
SOUL_SOIL = ["#4b3a2c", "#433326", "#523f30"]
SOUL_FIRE = "#6fe3f0"
SOUL_HOT = "#d9fbff"
SOUL_DEEP = "#1f8fa3"
BONE = ["#e3dcc6", "#d9d1b9", "#ebe5d1"]
BONE_DARK = "#b3a989"
BONE_DEEP = "#857b5f"
BASALT = ["#4b4a50", "#45444a", "#525157", "#3f3e44"]
BASALT_LIGHT = "#6d6c73"
BASALT_DARK = "#2c2b30"
# Netherrack / magma
RACK = ["#6f2f2d", "#652a29", "#783432", "#5f2726"]
RACK_LIGHT = "#8f4a46"
RACK_DARK = "#4a1d1c"
MAGMA_CRUST = ["#3b1a14", "#45201a", "#341712", "#40211a"]
MAGMA_CRUST_LIGHT = "#5e2c20"
MAGMA_GLOW = "#ff7a1a"
MAGMA_HOT = "#ffc93d"
MAGMA_DEEP = "#c2400f"
# Twisting / weeping vines (rope)
VINE_TEAL = ["#1f8a83", "#178079", "#25968e"]
VINE_TEAL_DARK = "#0f5a55"
VINE_RED = ["#8e1f1f", "#7f1a1a", "#9b2626"]
VINE_RED_DARK = "#5a1010"
IRON = ["#b9b9bd", "#a9a9ae", "#c6c6ca"]
IRON_DARK = "#6e6e74"


def tone(c, y, col, k=0.14):
    """Banded darkening toward the bottom of side faces; undersides darker still."""
    if c.face in SIDES and c.h > 2:
        f = 1 - k * y / (c.h - 1)
    elif c.face == "bottom":
        f = 1 - k * 1.4
    else:
        f = 1.0
    return shade(col, round(f * 16) / 16)


def ring_u(c, w, d):
    """Column offset of a side face when walking round a box, so patterns continue round corners."""
    return {"front": 0, "left": w, "back": w + d, "right": w + d + w}.get(c.face, 0)


def glow_as(c, x, y, base, light):
    """A lit pixel whose emissive layer is a darker shade, so the additive glow keeps its hue."""
    if not c.inside(x, y):
        return
    c.set(x, y, base)
    c.model.glow_image().putpixel((c.x0 + x, c.y0 + y), hexc(light))


def painter(fn):
    """fn(c, x, y) -> colour or None, applied to every pixel of a face."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                col = fn(c, x, y)
                if col is not None:
                    c.set(x, y, col)
    return run


def clear_all(c):
    for y in range(c.h):
        for x in range(c.w):
            c.clear(x, y)


def rim_only(fn):
    """Paint only the outer ring of a face and leave the middle see-through (a hollow band)."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                if min(x, y, c.w - 1 - x, c.h - 1 - y) == 0:
                    col = fn(c, x, y)
                    if col is not None:
                        c.set(x, y, col)
                else:
                    c.clear(x, y)
    return run


def gold_px(c, x, y):
    if c.face == "top":
        return GOLD_LIGHT if (x + y) % 3 == 0 else c.pick(GOLD)
    if c.face == "bottom":
        return GOLD_DARK
    if y == 0:
        return GOLD_LIGHT
    if y == c.h - 1 and c.h > 1:
        return GOLD_DARK
    return c.pick(GOLD) if c.rng.random() > 0.12 else GOLD_LIGHT


def blackstone_px(c, x, y):
    if c.face == "top":
        return BLACKSTONE_LIGHT if (x * 3 + y) % 7 == 0 else c.pick(BLACKSTONE)
    if c.rng.random() < 0.06:
        return BLACKSTONE_LIGHT
    return tone(c, y, c.pick(BLACKSTONE), 0.1)


def basalt_px(c, x, y):
    """Basalt columns: vertical grain on the sides, a polished ring on top."""
    if c.face == "top":
        edge = min(x, y, c.w - 1 - x, c.h - 1 - y)
        return BASALT_LIGHT if edge == 0 else c.pick(BASALT)
    if c.face == "bottom":
        return BASALT_DARK
    col = c.pick(BASALT)
    if (x + ring_u(c, c.w, c.w)) % 3 == 0:
        col = shade(col, 0.82)
    if y == 0:
        col = BASALT_LIGHT
    return tone(c, y, col, 0.12)


def bone_px(c, x, y):
    if c.face == "bottom":
        return BONE_DARK
    if c.face == "top":
        return c.pick(BONE)
    if c.rng.random() < 0.08:
        return BONE_DARK
    return tone(c, y, c.pick(BONE), 0.12)


def crack_paths(c, n, length, seed_salt=0):
    """Random-walk crack lines on a face: returns a set of (x, y) pixels. Deterministic per face."""
    import random
    rng = random.Random((c.x0 * 7919 + c.y0 * 104729 + seed_salt) & 0xFFFFFFFF)
    out = set()
    for _ in range(n):
        x, y = rng.randrange(c.w), rng.randrange(c.h)
        dx, dy = rng.choice(((1, 0), (-1, 0), (0, 1), (0, -1)))
        for _ in range(length):
            out.add((x, y))
            if rng.random() < 0.45:
                dx, dy = rng.choice(((1, 0), (-1, 0), (0, 1), (0, -1)))
            x, y = x + dx, y + dy
            if not c.inside(x, y):
                break
    return out
