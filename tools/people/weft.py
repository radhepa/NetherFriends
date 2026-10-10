"""Weft: the canopy weavers of the Warped Forest. About 2.3 blocks, slender, FOUR arms (a long upper pair to
climb, a short lower pair to weave), six small glowing eyes in a cluster on a smooth face plate, a long cranium
swept back, two dark mandibles at the jaw. Skin like warped-stem bark: dark blue-teal with cyan streaks. They
weave twisting vines into cloth, nets and bridges. Their saying: the forest is the warp, they are the weft.

  weft          a male: woven shoulder mantle, short kilt, a woven band round the head, a shuttle in a lower hand
  weft_female   a female: woven headwrap with two long tails, a long skirt, a shawl
  weft_child    a child, about 1.1 blocks: two arms, with nubs where the lower pair will grow

  python tools/people/weft.py               all three
"""
from common import glow_as, painter, rim_only, ring_u, shade, tone
from mobkit import Model, Pack, faces

NAME = "Weft"

BARK = ["#2a2f55", "#252a4d", "#2f355e", "#22284a"]     # warped-stem bark: deep indigo
BARK_STREAK = "#2d8a87"
BARK_DEEP = "#181c36"
PLATE = ["#141832", "#171b38", "#12162e"]
PLATE_RIM = "#3b4a7a"
CHITIN = "#0c0f22"
EYE = "#ffb84a"
EYE_HOT = "#fff0b0"
CLOTH = ["#178f87", "#13857e", "#1b9a91"]              # warped-wart teal weave
CLOTH_DARK = ["#0c4f4b", "#0e5550"]
VINE = "#0c4f4b"
VINE_LIGHT = "#3fd1c3"
DYE = "#e07a2a"                       # shroomlight dye
DYE_LIGHT = "#f2a24a"
SHUTTLE = ["#3a3a6b", "#433f7a"]      # warped planks


# -- skin --------------------------------------------------------------------------------------------
def bark(c, x, y):
    if c.face == "bottom":
        return BARK_DEEP
    if c.face == "top":
        return c.pick(BARK)
    u = x + ring_u(c, c.w, c.w)
    col = c.pick(BARK)
    if u % 4 == 0 and (y + u) % 5 and c.rng.random() < 0.6:
        col = BARK_STREAK if c.rng.random() < 0.5 else shade(BARK_STREAK, 0.75)  # long bark streaks
    return tone(c, y, col, 0.12)


def face_plate(eyes=6):
    """A smooth plate; a cluster of small glowing eyes, the hot pair in the middle row."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                c.set(x, y, bark(c, x, y))
        if c.face != "front":
            return
        for y in range(c.h):
            for x in range(c.w):
                edge = x in (0, c.w - 1)
                c.set(x, y, PLATE_RIM if edge and y < c.h - 2 else c.pick(PLATE))
        mid = c.w // 2                                # 6 wide: columns 0-5
        # two hot eyes, a small eye above each and one low at each side: six in all
        spots = [(1, 3, True), (c.w - 2, 3, True), (1, 2, False), (c.w - 2, 2, False)]
        if eyes == 6:
            spots += [(0, 4, False), (c.w - 1, 4, False)]
        for x, y, hot in spots:
            glow_as(c, x, y, EYE_HOT if hot else EYE, "#a86a18" if hot else "#7a4410")
        for x in range(1, c.w - 1):
            c.set(x, c.h - 1, CHITIN)                 # the jaw line
        if c.h >= 7:
            c.set(mid - 1, c.h - 2, CHITIN); c.set(mid, c.h - 2, CHITIN)    # a small mouth
    return run


# -- cloth --------------------------------------------------------------------------------------------
def weave(c, x, y):
    """Twisting-vine cloth: dark teal base, a teal chevron band and shroomlight-orange stitches."""
    if c.face in ("top", "bottom"):
        return CLOTH_DARK[0]
    u = x + ring_u(c, c.w, c.w)
    band = (y + abs((u % 6) - 3)) % 5
    if band == 0:
        return VINE_LIGHT
    if band == 1:
        return VINE
    if (u + y) % 7 == 0:
        return DYE
    return c.pick(CLOTH)


def fringed(fn):
    """Cloth with a fringe: the bottom row of the side faces is cut into tassels."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                if c.face in ("front", "back", "left", "right") and y == c.h - 1 and x % 2:
                    c.clear(x, y)
                    continue
                c.set(x, y, fn(c, x, y))
    return run


def shuttle(c, x, y):
    if c.face in ("left", "right") and 1 <= y <= c.h - 2:
        return VINE_LIGHT if x % 2 else DYE           # thread wound on the bobbin
    return c.pick(SHUTTLE)


# -- the body ------------------------------------------------------------------------------------------
def body(m, P, female=False):
    skin = faces(painter(bark))
    torso = m.part("body")
    P.add(torso, (-2.5, 6.5, -1.75), (5, 3, 3.5), skin)                                        # pelvis
    P.add(torso, (-2, 3, -1.5), (4, 4, 3), skin)                                               # narrow waist
    P.add(torso, (-3.5, -4.5, -2.25), (7, 8, 4.5), skin)                                       # chest
    P.add(torso, (-1, -6, -1.5), (2, 2, 2), skin)                                              # neck
    P.add(torso, (-2.7, 6.1, -1.95), (5.4, 1.2, 3.9), faces(painter(weave)))                   # belt
    for sx in (-1, 1):
        P.add(torso, (sx * 2.9 - 0.5, 6.4, -0.75), (1, 1.6, 1.5), faces(painter(shuttle)), share="spool")   # spools
    if female:
        P.add(torso, (-3, 7, -2.25), (6, 9.5, 4.5), faces(fringed(weave)))                     # long skirt
        P.add(torso, (-4.1, -5, -2.85), (8.2, 6, 5.7), faces(fringed(weave)))                  # shawl
    else:
        P.add(torso, (-2.8, 7, -2.05), (5.6, 4.5, 4.1), faces(fringed(weave)))                 # short kilt
        P.add(torso, (-4.1, -5, -2.85), (8.2, 3, 5.7), faces(fringed(weave)))                  # shoulder mantle

    head = m.part("head", (0, -5.5, -1))
    P.add(head, (-3, -7, -3.5), (6, 7, 6.5), faces(painter(bark), front=face_plate()))
    P.add(head, (-2.5, -6.5, 3), (5, 4.5, 3.5), skin)                                          # long cranium, swept back
    for sx in (-1, 1):
        P.add(head, (sx * 2.1 - 0.5, -1, -4.1), (1, 2.5, 1), faces(painter(lambda c, x, y: CHITIN)), share="mandible")
    if female:
        P.add(head, (-3.3, -7.4, -1.5), (6.6, 3.2, 8.5), {"front": rim_only(weave), "back": painter(weave),
              "left": painter(weave), "right": painter(weave), "top": painter(weave), "bottom": rim_only(weave)})   # headwrap
        tails = head.part("wrap_tails", (0, -5, 6.9), (0.25, 0, 0))
        for sx in (-1, 1):
            P.add(tails, (sx * 1.3 - 1, 0, 0), (2, 11, 0.5), faces(fringed(weave)), share="tail")
    else:
        P.add(head, (-3.3, -6.8, -3.8), (6.6, 1, 7), {"front": painter(weave), "back": painter(weave),
              "left": painter(weave), "right": painter(weave), "top": rim_only(weave), "bottom": rim_only(weave)})  # band

    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        arm = m.part(f"{side}_arm", (4.3 * sx, -3.5, 0))                                         # the climbing pair
        P.add(arm, (-1, -1, -1), (2, 8, 2), skin, mir, "upper")
        fore = arm.part(f"{side}_forearm", (0, 7, 0), (-0.3, 0, 0))
        P.add(fore, (-1, 0, -1), (2, 7, 2), skin, mir, "fore")
        P.add(fore, (-1.25, 6.5, -1.25), (2.5, 1.5, 2.5), skin, mir, "hand")
        for i, fx in enumerate((-0.9, 0, 0.9)):                                                 # three long fingers
            P.add(fore, (fx - 0.3, 8, -0.6), (0.6, 2.5, 0.6), faces(painter(lambda c, x, y: CHITIN if y == c.h - 1 else BARK_DEEP)), mir, "finger")
        low = m.part(f"{side}_lower_arm", (3.6 * sx, 1.5, -0.3))                                 # the weaving pair
        P.add(low, (-0.75, -0.5, -0.75), (1.5, 5, 1.5), skin, mir, "lower_upper")
        lfore = low.part(f"{side}_lower_forearm", (0, 4.5, 0), (-1.0, 0, 0))
        P.add(lfore, (-0.75, 0, -0.75), (1.5, 4, 1.5), skin, mir, "lower_fore")
        P.add(lfore, (-1, 3.8, -1), (2, 1.5, 2), skin, mir, "lower_hand")
        leg = m.part(f"{side}_leg", (1.5 * sx, 8.5, 0), (-0.15, 0, 0))
        P.add(leg, (-1, 0, -1), (2, 7.5, 2), skin, mir, "thigh")
        shin = leg.part(f"{side}_shin", (0, 7.4, 0), (0.3, 0, 0))
        P.add(shin, (-0.9, 0, -0.9), (1.8, 7, 1.8), skin, mir, "shin")
        foot = shin.part(f"{side}_foot", (0, 6.9, 0), (-0.15, 0, 0))
        P.add(foot, (-1.25, 0, -3), (2.5, 1.2, 4), skin, mir, "foot")
    held = m.get("right_lower_forearm").part("shuttle", (0, 5, 0), (1.0, 0, 0))
    P.add(held, (-0.75, -0.75, -2.5), (1.5, 1.5, 5), faces(painter(shuttle)))                  # a weaving shuttle
    return torso, head


def adult(female=False):
    m = Model("weft_female" if female else "weft", 128, 128, seed=139 if female else 113)
    m.preview = {"right_arm": [-0.15, 0, 0.1], "left_arm": [0.1, 0, -0.1],
                 "right_lower_arm": [-0.35, 0, 0.15], "left_lower_arm": [-0.45, 0, -0.15]}
    P = Pack(m)
    body(m, P, female)
    P.apply()
    m.save()


def child():
    """Big head, small body, two arms: the lower pair are only nubs until they grow in."""
    m = Model("weft_child", 64, 64, seed=149)
    m.preview = {"right_arm": [-0.2, 0, 0.12], "left_arm": [0.15, 0, -0.12]}
    P = Pack(m)
    skin = faces(painter(bark))
    torso = m.part("body")
    P.add(torso, (-2.25, 12.5, -1.5), (4.5, 5.5, 3), skin)
    P.add(torso, (-2.45, 14.5, -1.7), (4.9, 3, 3.4), faces(fringed(weave)))                    # a little woven tunic
    for sx in (-1, 1):
        P.add(torso, (sx * 2.6 - 0.5, 15.5, -0.5), (1, 1.5, 1), skin, share="nub")             # where the lower arms will grow
    head = m.part("head", (0, 12.5, -0.5))
    P.add(head, (-3, -6, -3), (6, 6, 6), faces(painter(bark), front=face_plate()))
    P.add(head, (-2, -5, 2.5), (4, 3, 2), skin)                                                # small cranium bump
    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        P.add(m.part(f"{side}_arm", (3 * sx, 13, 0)), (-0.75, -0.5, -0.75), (1.5, 6, 1.5), skin, mir, "arm")
        P.add(m.part(f"{side}_leg", (1.1 * sx, 18, 0)), (-0.8, 0, -0.8), (1.6, 6, 1.6), skin, mir, "leg")
    P.apply()
    m.save()


def build():
    adult(); adult(female=True); child()


if __name__ == "__main__":
    build()
