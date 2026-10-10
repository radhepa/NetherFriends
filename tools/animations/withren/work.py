"""Withren at their trades. The Remembrancer's staff and the Ashwarden's glaive are in the right hand, so those
clips never mirror. At rest the staff leans 31 degrees forward and the glaive 20: an arm pitch of -31 (-20) holds
it upright, and a staff/glaive pitch adds to the lean (staff lean = 31 + arm pitch + staff pitch)."""
from kit import clip

CHEST_L = (-60, -50, 0)       # left hand over the heart
FOLD = (-30, -45, 0)


def job(name):
    return ["adult", "job:" + name]


# -- Remembrancer: keeper of the Name Wall, staff with a soul lantern ------------------------------------------------
@clip("staff_remembrance", "Raises the staff in remembrance", length=6.6, weight=4, require=job("remembrancer"),
      mirror="never", blend=(.4, .5))
def _(c):
    c.key(0.9, ra=(6, 0, 2), staff=(-4, 0, 0), head=(10, 0, 0), waist=(3, 0, 0))
    c.key(2.1, ra=(-148, 0, 6), staff=(120, 0, 0), la=CHEST_L, head=(-22, 0, 0), waist=(-4, 0, 0))
    c.key(2.45, ra=(-154, 0, 6), staff=(124, 0, 0), head=(-25, 0, 0), waist=(-5, 0, 0))
    c.key(2.8, ra=(-150, 0, 6), staff=(121, 0, 0), staff_lantern=(0, 0, 10))
    c.key(3.4, staff_lantern=(0, 0, -7), head=(-24, 0, 2))
    c.key(4.0, staff_lantern=(0, 0, 4))
    c.key(4.5, ra=(-150, 0, 6), staff=(121, 0, 0), la=CHEST_L, staff_lantern=(0, 0, -2), head=(-23, 0, 0),
          waist=(-4, 0, 0))
    c.key(5.4, ra=(-38, 0, 2), staff=(18, 0, 0), la=(-40, -40, 0), staff_lantern=(0, 0, 6), head=(12, 0, 0),
          waist=(4, 0, 0))
    c.key(6.6)


@clip("recite_names", "Recites the names from the Wall", length=6.4, weight=4, require=job("remembrancer"),
      mirror="never")
def _(c):
    c.key(0.8, ra=(-31, 0, 0), la=(-70, -8, 0), head=(-6, -10, 0))
    t = 1.1
    for i in range(5):
        lift = dict(ra=(-37, 0, 0), la=(-82 + (i % 2) * 8, -14 + i * 7, 0), head=(-8, -10 + i * 5, 0))
        plant = dict(ra=(-30, 0, 0), la=(-74 - (i % 2) * 6, -10 + i * 7, 0), head=(-2, -10 + i * 5, 0),
                     root_pos=(0, 0.25, 0))
        c.key(t, **lift, root_pos=(0, 0, 0))
        c.key(t + 0.4, **plant)
        c.key(t + 0.55, root_pos=(0, 0, 0))
        t += 0.9
    c.key(t + 0.3, ra=(-31, 0, 0), la=CHEST_L, head=(10, 0, 0))
    c.key(6.4)


@clip("tend_staff_lantern", "Tends the lantern on the staff", length=5.2, weight=3, require=job("remembrancer"),
      mirror="never")
def _(c):
    c.key(1.0, ra=(-58, -24, 0), staff=(10, 0, 0), head=(-18, 4, 0))
    c.key(1.5, la=(-132, -28, 0), head=(-22, 6, 0), staff_lantern=(0, 0, 8))
    c.wobble(1.6, 3.2, 3, "staff_lantern", 5, axis=2, decay=0.6)
    c.key(1.6, la=(-136, -30, 0))
    c.key(2.4, la=(-128, -26, 0), head=(-24, 4, -3))
    c.key(3.2, la=(-134, -30, 0), ra=(-60, -24, 0), staff=(10, 0, 0), head=(-20, 4, 0))
    c.key(4.0, la=(-10, 0, 0), ra=(-35, -6, 0), staff=(4, 0, 0), head=(4, 0, 0))
    c.key(5.2)


# -- Ashwarden: guard of the ash and the dead, a glaive ---------------------------------------------------------------
@clip("glaive_drill", "Glaive drill", length=7.4, weight=4, require=job("ashwarden"), mirror="never")
def _(c):
    guard = dict(ra=(-50, -25, 0), la=(-45, -30, 0), glaive=(40, 0, -25), rl=(-8, 0, 0), ll=(6, 0, 0),
                 waist=(4, 0, 0), root=(0, 0, 0), root_pos=(0, 0.6, 0), head=(0, 0, 0))
    c.key(0.9, **guard)
    c.key(1.6, ra=(-28, -8, 0), la=(-20, -10, 0), glaive=(110, 0, 0), root=(0, -12, 0), waist=(0, -6, 0),
          head=(0, 12, 0))
    c.key(2.0, ra=(-84, -4, 0), la=(-10, 0, 6), glaive=(150, 0, 0), rl=(-24, 0, 0), ll=(16, 0, 0),
          root=(0, 4, 0), root_pos=(0, 0.8, -1.2), waist=(8, 2, 0), head=(-4, -4, 0))
    c.key(2.3, ra=(-88, -4, 0), glaive=(152, 0, 0), waist=(9, 2, 0))
    c.key(3.0, ra=(-40, -15, 0), la=(-30, -20, 0), glaive=(100, 0, 0), rl=(-8, 0, 0), ll=(6, 0, 0),
          root=(0, 0, 0), root_pos=(0, 0.6, 0), waist=(2, 0, 0), head=(0, 0, 0))
    c.key(3.7, ra=(-150, 0, 0), la=(-20, 0, 10), glaive=(130, 0, 0), head=(-14, 0, 0), waist=(-3, 0, 0))
    c.key(4.3, ra=(-82, 34, 0), glaive=(150, 0, 0), root=(0, 10, 0), waist=(4, 8, 0), head=(0, 6, 0))
    c.key(4.8, ra=(-80, -44, 0), glaive=(150, 0, 0), root=(0, -12, 0), waist=(6, -8, 0), head=(0, -6, 0))
    c.key(5.0, ra=(-80, -48, 0), root=(0, -13, 0))
    c.key(5.8, **guard)
    c.key(6.4, **guard)
    c.key(7.4)


@clip("stand_watch", "Stands the watch", length=6.4, weight=4, require=job("ashwarden"), mirror="never")
def _(c):
    hold = dict(ra=(-20, 0, 0), la=(-34, -42, 0))
    c.key(0.9, **hold, head=(0, 0, 0))
    c.key(1.1, root_pos=(0, 0.4, 0))
    c.key(1.3, root_pos=(0, 0, 0))
    c.key(2.4, **hold, head=(-2, -32, 0), root=(0, -3, 0))
    c.key(3.2, head=(-2, -34, 0))
    c.key(4.4, **hold, head=(-2, 30, 0), root=(0, 3, 0))
    c.key(5.0, head=(0, 28, 0))
    c.key(5.6, **hold, head=(2, 0, 0), root=(0, 0, 0))
    c.key(6.4)


@clip("glaive_salute", "Salutes with the glaive", length=4.6, weight=3, require=job("ashwarden"),
      mirror="never")
def _(c):
    c.key(0.5, ra=(-10, 0, 0), head=(-4, 0, 0))
    c.key(1.2, ra=(-78, -32, 0), glaive=(56, 0, 0), la=(-20, 0, 0), head=(-2, 0, 0))
    c.key(1.4, ra=(-82, -34, 0), glaive=(60, 0, 0))
    c.key(2.0, head=(14, 0, 0), waist=(6, 0, 0))
    c.key(2.9, ra=(-80, -33, 0), glaive=(58, 0, 0), head=(14, 0, 0), waist=(6, 0, 0))
    c.key(3.6, ra=(-14, 0, 0), glaive=(4, 0, 0), head=(0, 0, 0), waist=(0, 0, 0))
    c.key(4.6)


# -- Lantern-keeper: tends the soul-fire lanterns -------------------------------------------------------------------
LIFT_LA = (-55, 10, 0)
LIFT_LANTERN = (2.8, -3, -6.5)


@clip("trim_flame", "Trims the soul flame", length=5.6, weight=4, require=job("lantern_keeper"), mirror="never")
def _(c):
    c.key(0.9, la=(-58, 12, 0), lantern_pos=LIFT_LANTERN, lantern=(0, 0, 6), head=(16, -10, 0))
    c.key(1.3, la=LIFT_LA, lantern_pos=LIFT_LANTERN, lantern=(0, 0, -3), ra=(-62, -34, 0), head=(24, -14, 0),
          waist=(5, -3, 0))
    t = 1.6
    for i in range(5):
        c.key(t, ra=(-70, -38, 0), lantern=(0, 0, 1 if i % 2 else -1))
        c.key(t + 0.25, ra=(-64, -34, 0))
        t += 0.5
    c.key(t + 0.2, ra=(-50, -30, 0), head=(18, -12, 3))
    c.key(t + 0.7, la=LIFT_LA, lantern_pos=LIFT_LANTERN, ra=(-20, -10, 0), head=(12, -8, 0), waist=(4, -3, 0))
    c.key(5.6)


@clip("light_high_lantern", "Lights a lantern on a high post", length=6.0, weight=4,
      require=job("lantern_keeper"), mirror="hand")
def _(c):
    c.key(0.8, ra=(-20, 0, 0), head=(-14, 4, 0), root_pos=(0, 0.4, 0))
    c.key(1.6, ra=(-168, 6, 0), la=(-20, 0, 8), head=(-34, 4, 0), waist=(-5, 0, 0), root_pos=(0, -1.2, 0))
    c.key(1.9, ra=(-172, 8, 0), root_pos=(0, -1.4, 0))
    c.wobble(2.0, 3.2, 3, "right_arm", 4, base=(-172, 8, 0), axis=1)
    c.key(2.0, head=(-36, 4, 0), root_pos=(0, -1.4, 0))
    c.key(3.2, head=(-34, 2, 0), root_pos=(0, -1.3, 0))
    c.key(3.8, ra=(-40, -10, 0), la=(0, 0, 0), head=(-26, 0, 0), waist=(-2, 0, 0), root_pos=(0, 0, 0))
    c.key(4.6, ra=(-30, -20, 0), head=(-24, 0, -4))
    c.key(6.0)


# -- Wart-grower: nether wart in soul sand -------------------------------------------------------------------------
@clip("tend_wart", "Tends the nether wart", length=6.4, weight=4, require=job("wart_grower"), mirror="free")
def _(c):
    bent = dict(waist=(36, 0, 0), head=(14, 0, 0), rl=(-4, 0, 0), ll=(-4, 0, 0))
    c.key(1.0, **bent, ra=(-40, -8, 0), la=(-36, -8, 0))
    c.cycle(1.2, 4.4, 0.8, dict(ra=(-52, -14, 0), la=(-34, -6, 0), head=(16, -5, 0)),
            dict(ra=(-36, -6, 0), la=(-50, -14, 0), head=(16, 5, 0)))
    c.key(4.4, **bent)
    c.key(5.2, waist=(-6, 0, 0), head=(-8, 0, 0), ra=(18, 24, 0), la=(-4, 0, 0))
    c.key(5.6, waist=(-4, 0, 0), ra=(16, 22, 0))
    c.key(6.4)


@clip("sow_wart", "Scatters wart seed in an arc", length=5.2, weight=3, require=job("wart_grower"))
def _(c):
    # upright, unlike tend_wart: a hand into the pouch at the belt, then a flat cast out over the soul sand
    t = 0.5
    for fling, look in ((46, 22), (54, 30), (38, 14)):
        c.key(t, ra=(-32, -45, 0), head=(18, -8, 0), waist=(3, -5, 0), la=(-8, 0, 6))
        c.key(t + 0.3, ra=(-34, -42, 0))
        c.key(t + 0.55, ra=(-66, 14, 8), head=(8, look * 0.6, 0), waist=(2, 4, 0))
        c.key(t + 0.75, ra=(-52, fling, 24), head=(10, look, 0), waist=(1, 7, 0))
        c.key(t + 1.0, ra=(-48, fling + 2, 22), head=(14, look + 2, 0))
        t += 1.3
    c.key(t + 0.2, ra=(-10, 6, 6), head=(6, 10, 0), waist=(0, 0, 0), la=(0, 0, 2))
    c.wobble(t + 0.2, t + 0.7, 8, "right_arm", 3, base=(-10, 6, 6), axis=2)   # shakes off the last grains
    c.key(5.2)


# -- Bonecarver: carves bone into small keepsakes -------------------------------------------------------------------
@clip("carve_bone", "Carves a bone keepsake", length=6.4, weight=4, require=job("bonecarver"))
def _(c):
    hold = dict(la=(-52, -28, 0), head=(26, -4, 0), waist=(6, 0, 0))
    c.key(0.9, **hold, ra=(-50, -20, 0))
    c.cycle(1.1, 4.1, 0.5, dict(ra=(-62, -26, 0)), dict(ra=(-48, -16, 0)))
    c.key(4.1, **hold)
    c.key(4.6, la=(-62, -26, 0), head=(16, -6, 0), waist=(3, 0, 0), ra=(-30, -10, 0))
    c.key(4.9, head=(12, -6, 0), la=(-64, -26, 0))
    c.key(5.2, head=(18, -6, 0))
    c.key(6.4)


@clip("show_carving", "Holds a carving to the light", length=5.2, weight=3, require=job("bonecarver"))
def _(c):
    c.key(1.0, la=(-112, -16, 0), ra=(-84, -30, 0), head=(-18, -6, 0))
    c.wobble(1.2, 2.8, 1.5, "left_arm", 8, base=(-112, -16, 0), axis=1)
    c.key(1.2, head=(-20, -6, 4))
    c.key(2.8, head=(-18, -4, -4))
    c.key(3.4, la=(-60, -24, 0), ra=(-30, 0, 0), head=(16, -6, 0))
    c.key(4.0, head=(18, -6, 0), la=(-58, -24, 0))
    c.key(5.2)


# -- Brewer: soul brews and ash tea --------------------------------------------------------------------------------
@clip("stir_brew", "Stirs the brew", length=6.6, weight=4, require=job("brewer"))
def _(c):
    c.key(0.9, ra=(-52, -14, 0), la=(-38, -12, 0), head=(22, 0, 0), waist=(8, 0, 0))
    circle = [(-56, -20, 0), (-48, -26, 0), (-42, -16, 0), (-50, -8, 0)]
    t = 1.1
    for i in range(12):
        c.key(t, ra=circle[i % 4])
        t += 0.36
    c.key(t, ra=(-52, -14, 0), head=(22, 0, 0))
    c.key(5.7, ra=(-30, -8, 0), la=(-30, -10, 0), head=(8, 0, 0), waist=(2, 0, 0))
    c.key(6.6)


@clip("sniff_brew", "Sniffs the brew without a nose", length=5.0, weight=3, require=job("brewer"))
def _(c):
    c.key(0.9, la=(-40, -12, 0), ra=(-40, -10, 0), waist=(16, 0, 0), head=(22, 0, 0))
    c.key(1.3, waist=(18, 0, 0), head=(26, 0, 0))
    c.key(1.8, waist=(16, 0, 0), head=(18, 0, 0))
    c.key(2.1, head=(24, 0, 0))
    c.key(2.7, waist=(2, 0, 0), head=(-2, 0, 10), la=(-10, 0, 9), ra=(-10, 0, 9), root_pos=(0, -0.5, 0))
    c.key(3.1, la=(-4, 0, 2), ra=(-4, 0, 2), head=(-2, 0, 6), root_pos=(0, 0, 0))
    c.key(3.6, head=(8, 0, 2))
    c.key(4.0, head=(0, 0, 0))
    c.key(5.0)


# -- the bearers' manners --------------------------------------------------------------------------------------------
# Remembrancers and Ashwardens never let go of the staff or glaive, so the everyday, social and reaction clips that
# swing the right arm avoid them (it would sweep the shaft through the skull). These give them every moment back
# with the shaft planted upright and only the free left hand speaking.
def bearer():
    return dict(require=["adult", "job:remembrancer|job:ashwarden"], mirror="never")


def shaft_up(arm=-26.0, waist=0.0):
    """The right arm at this pitch with the staff or glaive standing upright (the waist bent forward by `waist`)."""
    return dict(ra=(arm, 0, 0), staff=(-31 - arm - waist, 0, 0), glaive=(-20 - arm - waist, 0, 0))


PLANT = shaft_up()             # the foot of the shaft on the ground beside them
LIFT = shaft_up(-40)           # lifted a hand's breadth, to thump it down again
PALM_L = (-132, 14, 4)         # the free hand raised at head height


@clip("bearer_bow", "Bows over the planted shaft", trigger="greet", length=3.6, weight=3, **bearer())
def _(c):
    c.key(0.6, **PLANT, la=(-56, -46, 0), head=(4, 0, 0))
    c.key(0.9, la=CHEST_L, head=(6, 0, 0))
    c.key(1.5, **shaft_up(-44, 18), la=CHEST_L, waist=(18, 0, 0), head=(12, 0, 0))
    c.key(2.2, **shaft_up(-44, 18), la=CHEST_L, waist=(18, 0, 0), head=(13, 0, 0))
    c.key(2.8, **PLANT, la=(-30, -30, 0), waist=(0, 0, 0), head=(2, 0, 0))
    c.key(3.6)


@clip("bearer_farewell", "Thumps the shaft and lifts the free hand", trigger="farewell", length=3.6, weight=2,
      **bearer())
def _(c):
    c.key(0.5, **PLANT, head=(2, 0, 0))
    c.key(0.8, **LIFT, head=(-2, 0, 0), root_pos=(0, 0, 0))
    c.rest(1.1, "la")
    c.key(0.95, **PLANT, root_pos=(0, 0.3, 0), head=(6, 0, 0))
    c.key(1.15, root_pos=(0, 0, 0))
    c.key(1.7, **PLANT, la=PALM_L, head=(2, 0, 0))
    c.key(2.4, la=PALM_L, head=(10, 0, 0))
    c.key(3.0, **PLANT, la=(-36, -10, 0), head=(4, 0, 0))
    c.key(3.6)


@clip("bearer_talk", "Makes the point with a thump of the shaft", trigger="talk", length=3.4, weight=2, **bearer())
def _(c):
    c.key(0.5, **PLANT, la=(-36, 6, 6), head=(2, 0, 0))
    c.key(1.0, la=(-54, 20, 10), head=(-2, -4, 0))
    c.key(1.5, **PLANT, la=(-50, 16, 8), head=(4, -2, 0))
    c.key(1.75, **LIFT, root_pos=(0, 0, 0))
    c.key(1.9, **PLANT, root_pos=(0, 0.3, 0), head=(8, 0, 0), la=(-44, 12, 6))
    c.key(2.1, root_pos=(0, 0, 0))
    c.key(2.6, **PLANT, la=(-28, 4, 2), head=(3, 0, 0))
    c.key(3.4)


@clip("bearer_chat_lean", "Leans on the shaft to talk", trigger="chat_speak", length=4.6, weight=2, **bearer())
def _(c):
    lean = dict(root=(0, 0, -3), root_pos=(-0.3, 0, 0))
    c.key(0.8, **PLANT, **lean, head=(2, 4, -3), la=(-20, 0, 8))
    c.key(1.6, la=(-48, 18, 10), head=(-2, 0, -2))
    c.key(2.2, la=(-44, 12, 8), head=(4, -6, -2))
    c.key(2.9, la=(-52, 22, 12), head=(-2, 4, -3))
    c.key(3.7, **PLANT, **lean, la=(-20, 0, 8), head=(6, 2, -2))
    c.key(4.6)


@clip("bearer_thanks", "Hand to the heart and a thump of the shaft", trigger="thanks", length=3.8, weight=2,
      **bearer())
def _(c):
    c.key(0.5, **PLANT, la=(-56, -46, 0), head=(2, 0, 0))
    c.key(0.8, la=CHEST_L, head=(6, 0, 0))
    c.key(1.3, **shaft_up(-32, 6), la=CHEST_L, head=(20, 0, 0), waist=(6, 0, 0))
    c.key(2.0, **shaft_up(-32, 6), la=CHEST_L, head=(19, 0, 0), waist=(6, 0, 0))
    c.key(2.6, **PLANT, la=(-40, -30, 0), head=(4, 0, 0), waist=(0, 0, 0))
    c.key(2.85, **LIFT, root_pos=(0, 0, 0))
    c.key(3.0, **PLANT, root_pos=(0, 0.3, 0))
    c.key(3.2, root_pos=(0, 0, 0))
    c.key(3.8)
