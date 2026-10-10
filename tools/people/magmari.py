"""Magmari: the lava-sea folk. About 2.5 blocks tall, hunched, with long backward-bending legs made for
wading lava and a short heavy tail. Their skin is magma crust, dark plates with glowing seams, and a
crest of basalt columns runs over the head like a ridge of the delta. Their heat is their health: out
of lava and away from fire they cool, the seams go dark and the crust turns ash-violet, the way a
strider does (that texture is magmari_cold).

  magmari          a male resident of a Mooring: twisting-vine rope at the waist, a woven hip cloth,
                   an iron chain sash (they are chainwrights) and wrist chains
  magmari_female   a female: twin crest ridges hung with chain loops, a woven chest wrap, a longer
                   hip cloth (magmari_female_cold when cooled)
  magmari_cold     the same Magmari, cooled: the texture swaps, the model does not
  magmari_ferrier  a ferrier who drives strider barges: the long lure-pole with a warped fungus
  magmari_child    a young Magmari, about 1.3 blocks: round, stubby-legged, crest still three nubs,
                   running hotter than an adult (magmari_child_cold when cooled)

  python tools/people/magmari.py            all of them
"""
from common import (BASALT_DARK, IRON, IRON_DARK, MAGMA_CRUST, MAGMA_CRUST_LIGHT, MAGMA_DEEP, MAGMA_GLOW,
                    MAGMA_HOT, VINE_RED, VINE_RED_DARK, VINE_TEAL, VINE_TEAL_DARK, basalt_px, crack_paths,
                    glow_as, painter, ring_u, shade, tone)
from mobkit import Model, Pack, faces

NAME = "Magmari"

COLD = ["#4a3f4f", "#544858", "#3f3544", "#4f4353"]
COLD_LIGHT = "#6a5d6e"
COLD_SEAM = "#6b2a28"
COLD_NEAR = "#57404b"
EYE_HOT = "#fff3b0"
WARPED = ["#167e86", "#14757c", "#1b8c94"]
WARPED_STEM = ["#3a3a6b", "#433f7a", "#35345f"]


class Heat:
    """Which texture is being painted: warm (glowing seams) or cold (cooled, ash-violet)."""
    cold = False


# -- skin --------------------------------------------------------------------------------------------
def crust(c, x, y):
    if Heat.cold:
        col = c.pick(COLD)
        if c.rng.random() < 0.06:
            col = COLD_LIGHT
    else:
        col = c.pick(MAGMA_CRUST)
        if c.rng.random() < 0.06:
            col = MAGMA_CRUST_LIGHT
    if c.face == "bottom":
        return shade(col, 0.8)
    return tone(c, y, col, 0.1)


def magma(density=34, length=4, hot_every=5):
    """Crust with a net of seams. Warm: the seams glow orange with hot yellow points (one in hot_every;
    the young run hotter). Cold: dull red."""
    def run(c):
        seams = crack_paths(c, max(1, c.w * c.h // density), length)
        near = {(x + dx, y + dy) for (x, y) in seams for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))} - seams
        for y in range(c.h):
            for x in range(c.w):
                if (x, y) in seams:
                    if Heat.cold:
                        c.set(x, y, COLD_SEAM)
                    else:
                        hot = (x * 3 + y * 7) % hot_every == 0
                        glow_as(c, x, y, MAGMA_HOT if hot else MAGMA_GLOW, "#a85a10" if hot else "#7a2a08")
                elif (x, y) in near and c.rng.random() < 0.4:
                    c.set(x, y, COLD_NEAR if Heat.cold else MAGMA_DEEP)
                else:
                    c.set(x, y, crust(c, x, y))
    return run


def face(c):
    magma(48, 3)(c)
    if c.face != "front":
        return
    for x in range(c.w):
        c.set(x, 0, crust(c, x, 0))
        c.set(x, 4, crust(c, x, 4))
        c.set(x, 6, shade(crust(c, x, 6), 0.8))
    eyes = ((1, 2), (c.w - 3, c.w - 2))              # 9 wide: 1-2 and 6-7; a child's 7: 1-2 and 4-5
    for a, b in eyes:                                # narrow eye slits under the brow
        for x in (a, b):
            inner = x in (2, c.w - 3)
            if Heat.cold:
                c.set(x, 3, "#c9702e" if inner else "#8a3f1c")
            else:
                glow_as(c, x, 3, EYE_HOT if inner else MAGMA_HOT, "#b0802a" if inner else "#8a4a10")
    for x in range(1, c.w - 1):                      # the mouth: a long seam
        if Heat.cold:
            c.set(x, 5, COLD_SEAM if x % 2 else "#2a2026")
        else:
            glow_as(c, x, 5, MAGMA_GLOW if x % 2 else MAGMA_DEEP, "#7a2a08" if x % 2 else "#3a1004")


def column(c):
    """A basalt column; its top is a small vent, glowing when warm."""
    for y in range(c.h):
        for x in range(c.w):
            c.set(x, y, basalt_px(c, x, y))
    if c.face == "top":
        for y in range(c.h):
            for x in range(c.w):
                if 0 < x < c.w - 1 and 0 < y < c.h - 1 or (c.w <= 2 and c.h <= 2 and (x + y) % 2 == 0):
                    if Heat.cold:
                        c.set(x, y, BASALT_DARK)
                    else:
                        glow_as(c, x, y, MAGMA_GLOW, "#6a2406")


# -- clothes -----------------------------------------------------------------------------------------
def rope(c, x, y):
    """Twisting-vine rope: teal twists."""
    if c.face in ("top", "bottom"):
        return VINE_TEAL_DARK
    return VINE_TEAL[0] if (x + ring_u(c, c.w, c.w) + y) % 3 else VINE_TEAL_DARK


def weave(c, x, y):
    """Woven weeping-vine cloth, deep red with a teal border."""
    if c.face in ("top", "bottom"):
        return VINE_RED_DARK
    if y == c.h - 1:
        return VINE_TEAL[1] if x % 2 else VINE_TEAL_DARK
    if y == c.h - 2:
        return VINE_TEAL_DARK
    col = c.pick(VINE_RED)
    if (x + y) % 4 == 0:
        col = VINE_RED_DARK
    return col


def chained(skin_painter, rows=(), diagonal=False, w=1):
    """Skin with iron chain links painted over it: as bands on given rows, or a sash shoulder to hip."""
    def run(c):
        skin_painter(c)
        if c.face in ("top", "bottom"):
            return
        for y in range(c.h):
            for x in range(c.w):
                link = None
                if y in rows:
                    link = (x + ring_u(c, w, w)) % 2
                elif diagonal and c.face in ("front", "back"):
                    t = x if c.face == "front" else c.w - 1 - x
                    if abs(t - (c.w - 1) + y * 0.95) < 0.8:
                        link = y % 2
                if link is not None:
                    c.set(x, y, IRON[0] if link else IRON_DARK)
    return run


# -- the body -----------------------------------------------------------------------------------------
def body(m, P, female=False):
    """female: twin crest ridges instead of one, chain loops hanging from the crest, a woven chest wrap instead of
    the chain sash, a longer hip cloth, lighter shoulder plates."""
    skin = magma()
    torso = m.part("body")
    P.add(torso, (-4, 2.5, -2.5), (8, 5, 5), skin)                                             # pelvis
    P.add(torso, (-3.5, -0.5, -2.25), (7, 3, 4.5), skin)                                       # waist
    P.add(torso, (-4.75, -9, -3), (9.5, 8.5, 6), skin if female else chained(skin, diagonal=True))   # chest (+ chain sash)
    if female:
        P.add(torso, (-4.95, -7.6, -3.2), (9.9, 3, 6.4), faces(painter(weave)))                 # woven chest wrap
    P.add(torso, (-4.2, -1, -2.7), (8.4, 1.6, 5.4), faces(painter(rope)))                      # vine rope belt
    cloth = (5.5, 11, 0.4) if female else (5, 8, 0.4)
    P.add(torso, (-cloth[0] / 2, 0.5, -2.9), cloth, faces(painter(weave)), share="cloth")     # hip cloth, front
    P.add(torso, (-cloth[0] / 2, 0.5, 2.5), cloth, faces(painter(weave)), share="cloth")      # and back

    head = m.part("head", (0, -9, -1.5), (0.1, 0, 0))
    P.add(head, (-4.5, -7, -5), (9, 7, 8), faces(magma(48, 3), front=face))
    P.add(head, (-4.6, -5.2, -5.9), (9.2, 1.2, 1.2), magma(30, 3))                              # heavy brow
    P.add(head, (-3.5, -1.2, -5.4), (7, 1.6, 1), magma(30, 3))                                  # jutting jaw
    crest = head.part("crest", (0, -7, 0))
    if female:
        for sx in (-1, 1):                                                                       # twin ridges
            for z, h in ((-4, 2), (-2, 4), (0, 5), (2, 4)):
                P.add(crest, (sx * 1.8 - 0.75, -h, z), (1.5, h, 1.5), column, share=f"twin{h}")
        for sx in (-1, 1):                                                                       # chain loops
            P.add(head, (sx * 4.75 - 0.25, -3.5, -1.5), (0.5, 3, 0.5), faces(painter(lambda c, x, y: IRON[0] if y % 2 else IRON_DARK)), share="loop")
    else:
        for z, h in ((-4, 2), (-2, 4), (0, 6), (2, 5)):
            P.add(crest, (-1, -h, z), (2, h, 2), column, share=f"col{h}")
        for sx in (-1, 1):
            for z, h in ((-3, 2), (-1, 3), (1, 3)):
                P.add(crest, (sx * 2.6 - 0.75, -h, z), (1.5, h, 1.5), column, share=f"side{h}")

    tail = torso.part("tail", (0, 5, 2.2), (-0.95, 0, 0))                                        # short, heavy, drooping
    P.add(tail, (-2, -2, 0), (4, 4, 4), skin)
    tip = tail.part("tail_tip", (0, 0, 3.5), (-0.35, 0, 0))
    P.add(tip, (-1.25, -1.25, 0), (2.5, 2.5, 3.5), skin)
    P.add(tip, (-0.5, -2.5, 0.5), (1, 1.5, 1), column)                                          # a spur on the tip

    arms, legs = {}, {}
    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        arm = m.part(f"{side}_arm", (5.6 * sx, -7.5, -0.5))
        P.add(arm, (-1.5, -1.5, -1.5), (3, 10, 3), skin, mir, "upper")
        if female:
            P.add(arm, (-2.25, -2.25, -2.25), (4, 2.5, 4.5), magma(22, 4), mir, "shoulder")     # lighter crust plates
        else:
            P.add(arm, (-2.5, -2.5, -2.5), (4.5, 3, 5), magma(22, 4), mir, "shoulder")          # crust plates
        P.add(arm, (-1.5 if sx < 0 else -0.5, -4.5, -0.5), (1, 2, 1), column, mir, "spur")
        fore = arm.part(f"{side}_forearm", (0, 8.5, 0), (-0.25, 0, 0))
        P.add(fore, (-1.5, 0, -1.5), (3, 8, 3), chained(skin, rows=(5, 6), w=3), mir, "fore")
        P.add(fore, (-1.75, 8, -1.75), (3.5, 3, 3.5), skin, mir, "hand")
        leg = m.part(f"{side}_leg", (2.6 * sx, 6.7, 0.3), (-0.55, 0, 0))
        P.add(leg, (-1.75, -0.5, -1.75), (3.5, 9.5, 3.5), skin, mir, "thigh")
        shin = leg.part(f"{side}_shin", (0, 9, 0), (1.1, 0, 0))
        P.add(shin, (-1.25, 0, -1.25), (2.5, 9.5, 2.5), skin, mir, "shin")
        foot = shin.part(f"{side}_foot", (0, 9, 0), (-0.55, 0, 0))
        P.add(foot, (-1.75, 0, -4.5), (3.5, 2, 5.5), skin, mir, "foot")
        P.add(foot, (-0.5, -1, 0.5), (1, 2, 1.5), column, mir, "heel")                          # heel spur
        arms[side], legs[side] = arm, leg
    return torso, head, arms, legs


def resident(cold=False, female=False):
    Heat.cold = cold
    mob = "magmari_female" if female else "magmari"
    m = Model(mob, 128, 128, seed=131 if female else 101, texture_id=mob + "_cold" if cold else None)
    m.preview = {"right_arm": [-0.05, 0, 0.12], "left_arm": [0.05, 0, -0.12]}
    P = Pack(m)
    body(m, P, female)
    P.apply()
    m.save(geometry=not cold)
    Heat.cold = False


def ferrier():
    m = Model("magmari_ferrier", 128, 128, seed=103)
    m.preview = {"right_arm": [-0.7, 0, 0.12], "right_forearm": [-0.5, 0, 0], "left_arm": [0.05, 0, -0.12]}
    P = Pack(m)
    torso, head, arms, legs = body(m, P)
    pole = m.get("right_forearm").part("pole", (0, 9.5, 0), (1.2, 0, 0))
    def stem(c, x, y):
        return c.pick(WARPED_STEM) if y % 5 else "#5a4e9a"
    P.add(pole, (-0.5, -30, -0.5), (1, 40, 1), faces(painter(stem)))
    P.add(pole, (-0.5, -31, -0.5), (1, 1, 4), faces(painter(lambda c, x, y: IRON_DARK)))      # hook
    lure = pole.part("lure", (0, -31, 3), (-1.2, 0, 0))
    P.add(lure, (-0.25, 0, -0.25), (0.5, 4, 0.5), faces(painter(lambda c, x, y: VINE_TEAL_DARK)))
    def fungus(c, x, y):
        if c.face == "bottom":
            return "#0f5a55"
        return c.pick(WARPED) if (x + y) % 3 else "#ff7a1a"             # warped fungus with orange spots
    P.add(lure, (-1.5, 4, -1.5), (3, 2, 3), faces(painter(fungus)))
    P.add(lure, (-0.5, 6, -0.5), (1, 1, 1), faces(painter(lambda c, x, y: "#d8c7a0")))
    P.apply()
    m.save()


def child(cold=False):
    """Round belly, short digitigrade legs, a big head with three crest nubs, a stub of a tail; hotter seams."""
    Heat.cold = cold
    m = Model("magmari_child", 64, 64, seed=107, texture_id="magmari_child_cold" if cold else None)
    m.preview = {"right_arm": [-0.2, 0, 0.18], "left_arm": [0.1, 0, -0.18]}
    P = Pack(m)
    skin = magma(20, 3, hot_every=2)
    torso = m.part("body")
    P.add(torso, (-3.5, 9.5, -2.75), (7, 6.5, 5.5), skin)                                      # round belly
    P.add(torso, (-3.7, 13, -2.95), (7.4, 1.2, 5.9), faces(painter(rope)))                     # a little vine sash
    head = m.part("head", (0, 9.5, -1), (0.08, 0, 0))
    P.add(head, (-3.5, -5.5, -4), (7, 5.5, 6.5), faces(magma(30, 3, hot_every=2), front=face))
    P.add(head, (-3.6, -4.2, -4.6), (7.2, 1, 1), magma(30, 3, hot_every=2))                     # soft brow
    crest = head.part("crest", (0, -5.5, 0))
    for z, h in ((-2.5, 1), (-0.5, 2), (1.5, 1.5)):
        P.add(crest, (-0.75, -h, z), (1.5, h, 1.5), column, share=f"nub{h}")
    tail = torso.part("tail", (0, 14.5, 2.4), (-0.8, 0, 0))
    P.add(tail, (-1.25, -1.25, 0), (2.5, 2.5, 3.5), skin)
    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        arm = m.part(f"{side}_arm", (4.3 * sx, 10.5, -0.3))
        P.add(arm, (-1, -0.5, -1), (2, 5, 2), skin, mir, "arm")
        P.add(arm, (-1.25, 4.5, -1.25), (2.5, 2, 2.5), skin, mir, "hand")
        leg = m.part(f"{side}_leg", (1.8 * sx, 15.5, 0.2), (-0.45, 0, 0))
        P.add(leg, (-1.25, -0.5, -1.25), (2.5, 4.5, 2.5), skin, mir, "thigh")
        shin = leg.part(f"{side}_shin", (0, 4, 0), (0.9, 0, 0))
        P.add(shin, (-1, 0, -1), (2, 4.5, 2), skin, mir, "shin")
        foot = shin.part(f"{side}_foot", (0, 4, 0), (-0.45, 0, 0))
        P.add(foot, (-1.25, 0, -3), (2.5, 1.5, 3.5), skin, mir, "foot")
    P.apply()
    m.save(geometry=not cold)
    Heat.cold = False


def build():
    resident(); resident(cold=True); resident(female=True); resident(cold=True, female=True)
    ferrier(); child(); child(cold=True)


if __name__ == "__main__":
    build()
