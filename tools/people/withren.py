"""Withren: the Wither's own people. A player's body and height (1.8 blocks) with the Wither's head at
normal head size. The head is not painted here: it is the Wither's own center head, drawn from the game's
wither texture (entity/wither/wither, UV 0,0, 8x8x8), so it matches the Wither exactly and follows resource
packs. Children keep that same full-size head on a half-size body. Ash-dark, bony-knuckled skin,
soul-cloth clothes in slate and soul-soil brown, bone toggles, soul lanterns.

  withren               a villager of a Vigil: soul-cloth tunic, soul-soil sash, a soul lantern at
                        the hip, bone bands on bare forearms
  withren_remembrancer  an elder who keeps the names of the dead: hood, long robe, a stole stitched
                        with tally marks, the Threefold pendant, a lantern staff
  withren_ashwarden     a guard of the Vigil: basalt cuirass carved with bone ribs (the Wither's
                        ribcage), bone pauldrons, a circlet with three empty sockets, a soul-edged glaive
  withren_child         a child, the classic Minecraft baby: the villager's body at half size and the
                        same full-size Wither head, about 1.25 blocks; shares the withren texture

  python tools/people/withren.py            all four
"""
from common import (BASALT_DARK, BASALT_LIGHT, BONE, BONE_DARK, BONE_DEEP, IRON_DARK,
                    SOUL_FIRE, SOUL_HOT, SOUL_SOIL, basalt_px, bone_px, clear_all, glow_as, painter,
                    ring_u, shade, tone)
from mobkit import Model, Pack, babify, faces

NAME = "Withren"
WITHER = "entity/wither/wither"     # borrowed at runtime, never copied into the mod

ASH = ["#3a3a3c", "#353537", "#3f3f41", "#38383a"]
ASH_LIGHT = "#56565a"
ASH_DARK = "#262628"
CLOTH = ["#2f3d3f", "#2a3739", "#344446"]
CLOTH_LIGHT = "#46595b"
CLOTH_DARK = "#1e2829"
STITCH = "#4fb9c6"


# -- body and clothes --------------------------------------------------------------------------------
def ash(c, x, y):
    if c.face == "bottom":
        return ASH_DARK
    col = c.pick(ASH)
    if c.rng.random() < 0.05:
        col = ASH_LIGHT
    return tone(c, y, col, 0.12)


def cloth(c, x, y, w=8, d=4):
    if c.face == "top":
        return CLOTH_DARK
    if c.face == "bottom":
        return shade(CLOTH_DARK, 0.8)
    col = c.pick(CLOTH)
    u = x + ring_u(c, w, d)
    if u % 4 == 0:
        col = CLOTH_LIGHT if y % 3 else col          # weave
    return tone(c, y, col, 0.14)


def tunic(c):
    """Commoner's tunic: sleeveless soul-cloth over ash skin, a V of bone toggles, a soul-soil sash."""
    for y in range(c.h):
        for x in range(c.w):
            col = cloth(c, x, y)
            if c.face == "front" and y < 4 and abs(x - 3.5) < (4 - y) * 0.5:
                col = ash(c, x, y)                   # the V neck
                if y == 0 and x in (3, 4):
                    col = ASH_LIGHT                  # collarbones
            if c.face in ("front", "back", "left", "right") and y in (7, 8):
                col = c.pick(SOUL_SOIL) if y == 7 else shade(SOUL_SOIL[0], 0.8)
            if c.face == "front" and x == 4 and y in (4, 6):
                col = BONE[0]                        # bone toggles
            if c.face in ("front", "back") and y >= 10 and x in (0, c.w - 1):
                col = STITCH                         # cyan stitching down the hem
            c.set(x, y, col)


def sleeve_arm(c):
    """Upper arm in soul-cloth, bare ash forearm with bone bands, bony knuckles."""
    for y in range(c.h):
        for x in range(c.w):
            if y < 5:
                col = cloth(c, x, y, 4, 4)
                if y == 4:
                    col = CLOTH_DARK
            else:
                col = ash(c, x, y)
                if y in (6, 8) and c.face in ("front", "back", "left", "right"):
                    col = BONE_DARK if (x + y) % 2 else BONE[0]      # bone bands
                if y == c.h - 1 and c.face in ("front", "back", "left", "right") and x % 2 == 0:
                    col = ASH_LIGHT                  # knuckles
            if c.face == "bottom":
                col = ASH_DARK
            c.set(x, y, col)


def trouser(c):
    for y in range(c.h):
        for x in range(c.w):
            col = cloth(c, x, y, 4, 4)
            if y >= 7:
                col = c.pick(SOUL_SOIL) if (y + x // 2) % 3 else shade(SOUL_SOIL[1], 0.8)   # leg wraps
            if y == c.h - 1:
                col = ASH_DARK
            if c.face == "bottom":
                col = ASH_DARK
            c.set(x, y, col)


def lantern_body(c):
    """A small soul lantern: dark iron frame, a soul flame burning inside."""
    for y in range(c.h):
        for x in range(c.w):
            frame = c.face in ("top", "bottom") or y in (0, c.h - 1) or (c.w > 2 and x in (0, c.w - 1))
            if frame:
                c.set(x, y, "#3b3b40" if (x + y) % 2 else IRON_DARK)
            else:
                glow_as(c, x, y, SOUL_HOT if y == c.h // 2 else SOUL_FIRE, "#2b8c9a")


# -- shared body --------------------------------------------------------------------------------------
def body(m, P, top=tunic, arm=sleeve_arm, leg=trouser):
    head = m.part("head")
    head.cube((0, 0), (-4, -8, -4), (8, 8, 8), texture=WITHER)                               # the Wither's center head
    torso = m.part("body")
    P.add(torso, (-4, 0, -2), (8, 12, 4), top)
    arms, legs = {}, {}
    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        a = m.part(f"{side}_arm", (5 * sx, 2, 0))
        P.add(a, (-3 if sx < 0 else -1, -2, -2), (4, 12, 4), arm, mir, "arm")
        l = m.part(f"{side}_leg", (1.9 * sx, 12, 0))
        P.add(l, (-2, 0, -2), (4, 12, 4), leg, mir, "leg")
        arms[side], legs[side] = a, l
    return head, torso, arms, legs


# -- the three studies -------------------------------------------------------------------------------
def villager(child=False):
    """child=True: the classic Minecraft baby. Same parts and the same texture as the adult, the body at half
    size, the head left at full size, so a Withren child is mostly Wither skull."""
    m = Model("withren_child" if child else "withren", 128, 64, seed=83, texture_id="withren")
    m.preview = {"right_arm": [-0.1, 0, 0.06], "left_arm": [0.08, 0, -0.06]}
    P = Pack(m)
    head, torso, arms, legs = body(m, P)
    lamp = torso.part("lantern", (3.2, 10, -2.2), (0, 0, 0.12))
    P.add(lamp, (-0.5, 0, -0.5), (1, 1, 1), faces(painter(lambda c, x, y: IRON_DARK)))      # hook
    P.add(lamp, (-1, 1, -1), (2, 3, 2), lantern_body)
    P.apply()
    if child:
        babify(m, body_scale=0.5, head_scale=1.0)
    m.save()


def remembrancer():
    m = Model("withren_remembrancer", 128, 128, seed=89)
    m.preview = {"right_arm": [-0.55, 0, 0.1], "left_arm": [0.06, 0, -0.05]}
    P = Pack(m)

    def robe_top(c):
        for y in range(c.h):
            for x in range(c.w):
                col = cloth(c, x, y)
                if c.face == "front" and y < 3 and 3 <= x <= 4:
                    col = ash(c, x, y)
                c.set(x, y, col)
    def robe_arm(c):
        for y in range(c.h):
            for x in range(c.w):
                col = cloth(c, x, y, 4, 4)
                if y >= c.h - 3:
                    col = ash(c, x, y) if y == c.h - 1 else CLOTH_DARK      # wide cuff, bony hand
                c.set(x, y, col)
    head, torso, arms, legs = body(m, P, top=robe_top, arm=robe_arm)

    def hood(c):
        for y in range(c.h):
            for x in range(c.w):
                if c.face == "front" and 0 < x < c.w - 1 and y > 0:
                    c.clear(x, y)                    # the hood is open at the front
                    continue
                if c.face == "bottom":
                    c.clear(x, y)
                    continue
                col = cloth(c, x, y, c.w, c.w)
                if c.face == "front":
                    col = CLOTH_LIGHT
                c.set(x, y, col)
    P.add(head, (-4.5, -8.6, -4.5), (9, 9.4, 9.6), hood)
    P.add(head, (-3, -9.4, -1), (6, 1, 5), faces(painter(lambda c, x, y: c.pick(["#2a3739", "#2f3d3f"]))))  # hood peak

    def skirt(c):
        for y in range(c.h):
            for x in range(c.w):
                col = cloth(c, x, y, 9, 5)
                if c.face == "front" and x == c.w // 2:
                    col = CLOTH_DARK
                if y >= c.h - 1:
                    col = c.pick(SOUL_SOIL)
                if c.face in ("front", "back") and y == c.h - 3:
                    col = STITCH if x % 2 else CLOTH_DARK
                c.set(x, y, col)
    P.add(torso, (-4.5, 10, -2.5), (9, 14, 5), skirt)                                         # long robe

    def stole(c):
        for y in range(c.h):
            for x in range(c.w):
                if c.face in ("front", "back"):
                    col = BONE[0] if y % 3 == 1 else "#5a4733"       # tally marks: one for each name kept
                    if y % 3 == 1 and c.w > 1 and x % 2:
                        col = "#5a4733"
                else:
                    col = "#4a3a2a"
                c.set(x, y, col)
    for sx in (-1, 1):
        P.add(torso, (sx * 2.5 - 1, -0.2, -2.6), (2, 18, 0.5), stole, share="stole")
    def threefold(c):
        for y in range(c.h):
            for x in range(c.w):
                c.set(x, y, BONE[0] if c.face == "front" and x % 2 == 0 else BONE_DEEP)
    P.add(torso, (-1.5, 4, -2.8), (3, 1, 0.6), threefold)                                    # three bone beads

    staff = arms["right"].part("staff", (-1, 10, 0), (0.55, 0, 0))
    P.add(staff, (-0.5, -24, -0.5), (1, 36, 1), faces(painter(bone_px)))
    P.add(staff, (-0.5, -25, -3.5), (1, 1, 4), faces(painter(bone_px)))                        # the crook
    lamp = staff.part("staff_lantern", (0, -25, -3), (0, 0, 0))
    P.add(lamp, (-0.5, 0, -0.5), (1, 1, 1), faces(painter(lambda c, x, y: IRON_DARK)))
    P.add(lamp, (-1.5, 1, -1.5), (3, 4, 3), lantern_body)
    P.apply()
    m.save()


def ashwarden():
    m = Model("withren_ashwarden", 128, 128, seed=97)
    m.preview = {"right_arm": [-0.35, 0, 0.1], "left_arm": [0.1, 0, -0.08]}
    P = Pack(m)
    head, torso, arms, legs = body(m, P)

    def ribplate(c):
        """Basalt carved with the Wither's ribcage in bone."""
        for y in range(c.h):
            for x in range(c.w):
                col = basalt_px(c, x, y)
                if c.face in ("front", "back") and c.h > 4:
                    mid = (c.w - 1) / 2
                    if abs(x - mid) < 0.6:
                        col = BONE[0] if y % 2 else BONE_DARK          # the spine / sternum
                    elif y % 2 == 1 and y < c.h - 1 and abs(x - mid) < 1 + (c.h - y) * 0.6:
                        col = BONE[1] if abs(x - mid) < 3 else BONE_DARK   # ribs
                if c.face == "top":
                    col = BASALT_LIGHT
                c.set(x, y, col)
    P.add(torso, (-4.5, -0.5, -2.5), (9, 10, 5), ribplate)
    def scales(c, x, y):
        if c.face == "bottom":
            return BONE_DEEP
        if c.face == "top":
            return c.pick(BONE)
        return BONE_DARK if (y % 2 == 0 and (x + y // 2) % 2 == 0) else c.pick(BONE)
    for side in ("right", "left"):
        mir = side == "left"
        P.add(arms[side], (-2.5 if side == "left" else -3.5, -3, -2.5), (5, 4, 5), faces(painter(scales)), mir, "pauldron")
        P.add(legs[side], (-2.3, 1, -2.3), (4.6, 5, 4.6), faces(painter(basalt_px)), mir, "tasset")
    def circlet(c):
        for y in range(c.h):
            for x in range(c.w):
                if c.face in ("top", "bottom"):
                    c.clear(x, y) if min(x, y, c.w - 1 - x, c.h - 1 - y) else c.set(x, y, BASALT_DARK)
                    continue
                c.set(x, y, basalt_px(c, x, y))
        if c.face == "front":
            for x in (2, 4, 6):                      # three empty sockets: the Three
                c.set(x, 0, "#0a0a0a")
    P.add(head, (-4.4, -7.3, -4.4), (8.8, 1, 8.8), circlet)

    glaive = arms["right"].part("glaive", (-1, 10, 0), (0.35, 0, 0))
    P.add(glaive, (-0.5, -22, -0.5), (1, 34, 1), faces(painter(bone_px)))
    def blade(c):
        clear_all(c)
        for y in range(c.h):
            for x in range(c.w):
                if c.face in ("left", "right"):
                    half = (y + 1) / c.h * 2.2
                    mid = (c.w - 1) / 2
                    if abs(x - mid) > half:
                        continue
                    if abs(abs(x - mid) - half) < 0.8:
                        glow_as(c, x, y, SOUL_FIRE, "#1f6f7d")     # soul-tempered edge
                    else:
                        c.set(x, y, BASALT_DARK if (x + y) % 3 else BASALT_LIGHT)
                else:
                    c.set(x, y, BASALT_DARK)
    P.add(glaive, (-0.5, -31, -2.5), (1, 9, 5), blade)
    P.add(glaive, (-1.5, -23, -1.5), (3, 1, 3), faces(painter(bone_px)))
    P.apply()
    m.save()


def build():
    villager(); villager(child=True); remembrancer(); ashwarden()


if __name__ == "__main__":
    build()
