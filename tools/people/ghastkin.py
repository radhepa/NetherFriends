"""Ghastkin: the mourners of the Basalt Deltas, kin to the ghasts. A pale, slender upper body and a head like a
ghast's at normal head size (closed eyes, tear tracks, a small sad mouth); below the waist, six pale tentacles,
and they float a little off the ground. They live in Eaves hung under the delta cliffs. A Ghastkin who grieves too
long swells, loses arms and words, and drifts away as a ghast.

  ghastkin          a male: a hooded ash-silk mantle and a short cape, a tear-vial pendant
  ghastkin_female   a female: a long sheer veil from the crown, a circlet of tear beads, long sleeves
  ghastkin_child    a child, about 1.2 blocks: eyes still open (they have not learned to cry), four short tentacles

  python tools/people/ghastkin.py            all three
"""
from common import glow_as, painter, rim_only, ring_u, shade, tone
from mobkit import Model, Pack, faces

NAME = "Ghastkin"

PALE = ["#ece9e3", "#e4e1da", "#f2f0ea", "#e8e5de"]
PALE_SHADE = "#cfcac0"
PALE_DEEP = "#b4aea3"
LID = "#5d5a57"
MOUTH = "#4a4644"
TEAR = "#a9c4cf"
TEAR_HOT = "#d8f0f8"
ASH = ["#5e5b60", "#57545a", "#66636a"]
ASH_TRIM = "#8e8a92"
ASH_DARK = "#3a383d"
VEIL = ["#c9c6cc", "#bfbcc3", "#d2cfd5"]
BEAD = "#bfe9f5"


# -- skin and face --------------------------------------------------------------------------------------
def pale(c, x, y):
    if c.face == "bottom":
        return PALE_SHADE
    col = c.pick(PALE)
    if c.rng.random() < 0.05:
        col = PALE_SHADE
    return tone(c, y, col, 0.08)


def face(open_eyes=False):
    """A ghast's face at head size. Adults: closed eyes, tear tracks, a small down-turned mouth.
    Children: open eyes, no tears yet."""
    def run(c):
        for y in range(c.h):
            for x in range(c.w):
                c.set(x, y, pale(c, x, y))
        if c.face != "front":
            return
        w = c.w
        left, right = (1, 2), (w - 3, w - 2)
        if open_eyes:
            for x in left + right:
                c.set(x, 3, "#1c1a1a")
            c.set(left[1], 3, "#2a2a2a"); c.set(right[0], 3, "#2a2a2a")
            glow_as(c, left[0], 3, "#ffffff", "#303030")      # a catchlight in each eye
            glow_as(c, right[1], 3, "#ffffff", "#303030")
            for x in (left[1], right[0]):
                c.set(x, 2, PALE_SHADE)
            c.set(w // 2 - 1, 5, MOUTH); c.set(w // 2, 5, MOUTH)        # a small open mouth
            return
        c.set(left[1], 2, PALE_DEEP); c.set(right[0], 2, PALE_DEEP)     # brows raised at the middle: sorrow
        c.set(left[0], 2, PALE_SHADE); c.set(right[1], 2, PALE_SHADE)
        for x in left + right:
            c.set(x, 3, LID)                                             # closed eyes
        for x in (left[1], right[0]):                                    # tear tracks from the inner corners
            for y in range(4, c.h):
                if y == c.h - 2:
                    glow_as(c, x, y, TEAR_HOT, "#3e5860")                # a tear about to fall
                else:
                    c.set(x, y, TEAR)
        mid = w // 2
        c.set(mid - 1, 5, MOUTH); c.set(mid, 5, MOUTH)                   # down-turned mouth
        c.set(mid - 2, 6, MOUTH); c.set(mid + 1, 6, MOUTH)
    return run


def tentacle(c, x, y):
    if c.face == "top":
        return PALE_SHADE
    if c.face == "bottom":
        return TEAR
    col = pale(c, x, y)
    t = y / max(1, c.h - 1)
    if t > 0.6:
        col = shade(col, 1 - (t - 0.6) * 0.5)        # tips go grey
    if y == c.h - 1:
        col = TEAR                                    # a bead of tear at every tip
    return col


# -- cloth ----------------------------------------------------------------------------------------------
def ash_silk(c, x, y):
    if c.face in ("top", "bottom"):
        return ASH_DARK
    col = c.pick(ASH)
    if y == c.h - 1:
        col = ASH_TRIM
    elif (x + ring_u(c, c.w, c.w)) % 5 == 0:
        col = shade(col, 0.85)                        # soft folds
    return tone(c, y, col, 0.1)


def veil(c, x, y):
    if c.face in ("top", "bottom"):
        return VEIL[0]
    if y == c.h - 1 and x % 2:
        return None                                   # scalloped hem
    col = c.pick(VEIL)
    if (x + y) % 4 == 0:
        col = shade(col, 1.06)                        # lace
    return col


def vial(c):
    for y in range(c.h):
        for x in range(c.w):
            if y == 0:
                c.set(x, y, "#6e5a3a")                # cork
            else:
                glow_as(c, x, y, TEAR_HOT if y == c.h - 1 else TEAR, "#2e4a52")


# -- body -------------------------------------------------------------------------------------------------
TENTACLES = [(-1.6, -0.9, 12, 0.1, 0.06), (0, -0.9, 14, -0.05, 0), (1.6, -0.9, 12, 0.08, -0.07),
             (-1.6, 0.9, 13, -0.08, 0.05), (0, 0.9, 15, 0.06, 0), (1.6, 0.9, 11, -0.1, -0.06)]


def adult(female=False):
    m = Model("ghastkin_female" if female else "ghastkin", 128, 128, seed=157 if female else 151)
    m.preview = {"right_arm": [-0.2, 0, 0.12], "left_arm": [0.05, 0, -0.1]}
    P = Pack(m)
    skin = faces(painter(pale))
    head = m.part("head", (0, -4, 0))
    P.add(head, (-4, -8, -4), (8, 8, 8), faces(painter(pale), front=face()))
    torso = m.part("body")
    P.add(torso, (-3, -4, -2), (6, 8, 4), skin)
    P.add(torso, (-2.5, 4, -1.75), (5, 2, 3.5), skin)                                          # where the tentacles begin
    for i, (x, z, length, rx, rz) in enumerate(TENTACLES):
        t = m.part(f"tentacle_{i}", (x, 5.5, z), (rx, 0, rz))
        P.add(t, (-0.75, 0, -0.75), (1.5, length, 1.5), faces(painter(tentacle)), share=f"tentacle{length}")
    for side, sx in (("right", -1), ("left", 1)):
        mir = side == "left"
        arm = m.part(f"{side}_arm", (4 * sx, -3, 0))
        P.add(arm, (-1, -0.5, -1), (2, 10, 2), skin, mir, "arm")
        P.add(arm, (-1.1, 9.5, -1.1), (2.2, 2, 2.2), skin, mir, "hand")
        sleeve = (2.8, 8, 2.8) if female else (2.6, 4, 2.6)
        P.add(arm, (-1.4, -0.8, -1.4), sleeve, faces(painter(ash_silk)), mir, "sleeve")
    if female:
        P.add(head, (-4.3, -6.6, -4.3), (8.6, 1, 8.6), {"front": painter(lambda c, x, y: BEAD if x % 2 else ASH_TRIM),
              "back": painter(lambda c, x, y: BEAD if x % 2 else ASH_TRIM), "left": painter(lambda c, x, y: BEAD if x % 2 else ASH_TRIM),
              "right": painter(lambda c, x, y: BEAD if x % 2 else ASH_TRIM), "top": rim_only(lambda c, x, y: ASH_TRIM),
              "bottom": rim_only(lambda c, x, y: ASH_TRIM)})                                     # circlet of tear beads
        hang = head.part("veil", (0, -8.2, 4.2), (0.12, 0, 0))
        P.add(hang, (-4.4, 0, 0), (8.8, 19, 0.6), faces(painter(veil)))                          # the long veil
        P.add(torso, (-3.4, -4.4, -2.4), (6.8, 4, 4.8), faces(painter(ash_silk)))               # bodice
        P.add(torso, (-3.2, 2, -2.2), (6.4, 5, 4.4), faces(painter(ash_silk)))                  # sash and skirt-hem
    else:
        def hood(c):
            for y in range(c.h):
                for x in range(c.w):
                    if c.face == "front" and 0 < x < c.w - 1 and y > 0 or c.face == "bottom":
                        c.clear(x, y)                 # open at the face
                        continue
                    c.set(x, y, ash_silk(c, x, y))
        P.add(head, (-4.5, -8.6, -4.5), (9, 9, 9.4), hood)
        P.add(torso, (-3.6, -4.6, -2.6), (7.2, 4, 5.2), faces(painter(ash_silk)))               # mantle
        cape = torso.part("cape", (0, -4, 2.2), (0.1, 0, 0))
        P.add(cape, (-3.5, 0, 0), (7, 13, 0.6), faces(painter(ash_silk)))
        P.add(torso, (-3.2, 2, -2.2), (6.4, 2, 4.4), faces(painter(ash_silk)))                  # sash
    P.add(torso, (-0.5, -1.5, -2.9), (1, 1.5, 0.8), vial)                                       # a tear vial on a cord
    P.apply()
    m.save()


def child():
    m = Model("ghastkin_child", 64, 64, seed=163)
    m.preview = {"right_arm": [-0.3, 0, 0.2], "left_arm": [-0.1, 0, -0.2]}
    P = Pack(m)
    skin = faces(painter(pale))
    head = m.part("head", (0, 12, 0))
    P.add(head, (-3.5, -7, -3.5), (7, 7, 7), faces(painter(pale), front=face(open_eyes=True)))
    torso = m.part("body")
    P.add(torso, (-2, 12, -1.5), (4, 4.5, 3), skin)
    P.add(torso, (-2.2, 12, -1.7), (4.4, 2.2, 3.4), faces(painter(ash_silk)))                   # a little mantle
    for i, (x, z, length) in enumerate(((-1, -0.6, 4), (1, -0.6, 5), (-1, 0.6, 5), (1, 0.6, 4))):
        t = m.part(f"tentacle_{i}", (x, 16, z), (0.05 * (i - 1.5), 0, 0))
        P.add(t, (-0.6, 0, -0.6), (1.2, length, 1.2), faces(painter(tentacle)), share=f"t{length}")
    for side, sx in (("right", -1), ("left", 1)):
        P.add(m.part(f"{side}_arm", (2.7 * sx, 12.5, 0)), (-0.7, -0.3, -0.7), (1.4, 5, 1.4), skin, side == "left", "arm")
    P.apply()
    m.save()


def build():
    adult(); adult(female=True); child()


if __name__ == "__main__":
    build()
