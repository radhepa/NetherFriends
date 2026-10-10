"""Withren everyday idles: quiet, grave, formal, and now and then a dry graveyard joke."""
import math

from kit import clip

# poses worked out on the sheet (straight arms, no elbows)
FOLD = (-30, -45, 0)          # hands folded at the belly (both arms)
CHEST = (-60, -50, 0)         # hand over the heart (where the kept name lives)
CHIN = (-100, -55, 0)         # hand to the jaw
POINT = (-92, -12, 0)         # arm out before them, tracing
BEHIND = (25, 35, 0)          # hands clasped behind the back (both arms)

# The Remembrancer's staff and the Ashwarden's glaive hang from the right hand, and neither look has the hip
# lantern: clips that swing the right arm (or lift the lantern) would swing the staff through the skull.
BEARERS = ["job:remembrancer", "job:ashwarden"]


def hold_lantern(pitch, yaw_out, roll_out=0.0):
    """The left arm at this pose with the hip lantern hanging from its fist (the grip the hand-tuned lantern clips
    use: the ring at the end of the arm, a little to the inside)."""
    x, y, z = (math.radians(a) for a in (pitch, -yaw_out, -roll_out))
    gx, gy, gz = -0.6, 10.2, -0.4
    gy, gz = gy * math.cos(x) - gz * math.sin(x), gy * math.sin(x) + gz * math.cos(x)
    gx, gz = gx * math.cos(y) + gz * math.sin(y), -gx * math.sin(y) + gz * math.cos(y)
    gx, gy = gx * math.cos(z) - gy * math.sin(z), gx * math.sin(z) + gy * math.cos(z)
    return dict(la=(pitch, yaw_out, roll_out),
                lantern_pos=(round(5 + gx - 3.2, 2), round(2 + gy - 10, 2), round(gz + 2.2, 2)))


@clip("fold_hands", "Folds the hands", length=4.5, weight=2, avoid=BEARERS)
def _(c):
    c.key(0.9, ra=(-33, -48, 0), la=(-33, -48, 0), head=(4, 0, 0))
    c.key(1.3, ra=FOLD, la=FOLD, head=(9, 0, 0), waist=(3, 0, 0))
    c.key(2.4, head=(11, 0, 2))
    c.key(3.4, ra=FOLD, la=FOLD, head=(8, 0, -1), waist=(3, 0, 0))
    c.key(4.5)


@clip("trace_name", "Traces a name in the air", length=5.6, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.8, ra=(-80, -22, 0), head=(-4, -6, 0))
    c.key(1.1, ra=(-96, -24, 0), head=(-6, -8, 0))
    strokes = [(-100, -25), (-86, -21), (-99, -15), (-86, -10), (-101, -5), (-90, 0), (-84, 5), (-98, 10),
               (-87, 15)]
    t = 1.35
    for pitch, yaw in strokes:
        c.key(t, ra=(pitch, yaw, 0), head=(-5 - (pitch + 92) * 0.25, yaw * 0.45, 0))
        t += 0.28
    c.key(t + 0.4, ra=(-58, -46, 0), head=(6, 0, 0), waist=(2, 0, 0))
    c.key(t + 0.7, ra=CHEST, head=(9, 0, 0), waist=(3, 0, 0))
    c.key(t + 1.4, ra=CHEST, head=(8, 0, 0), waist=(3, 0, 0))
    c.key(5.6)


@clip("lantern_read_names", "Reads old names by lantern light", length=6.2, weight=2, mirror="never",
      avoid=BEARERS)
def _(c):
    # the lantern held out at shoulder height and carried slowly along a wall of names off to their left
    c.key(0.8, **hold_lantern(-62, 14), lantern=(0, 0, 6), head=(6, -10, 0))
    c.key(1.4, **hold_lantern(-96, 30), lantern=(0, 0, -5), head=(-4, -26, 0), waist=(-2, -5, 0))
    c.key(2.0, lantern=(0, 0, 2), head=(-5, -27, -3))
    c.key(3.6, **hold_lantern(-98, 12), lantern=(0, 0, -3), head=(-6, -12, -3), waist=(-2, -2, 0))
    c.key(4.0, lantern=(0, 0, 1), head=(4, -10, 0))          # a name they knew: a small nod
    c.key(4.5, **hold_lantern(-96, 12), head=(-4, -11, 0), waist=(-2, -2, 0))
    c.key(5.3, **hold_lantern(-40, 8), lantern=(0, 0, 6), head=(4, 0, 0), waist=(0, 0, 0))
    c.key(6.2)


@clip("three_nods", "Three bows for the Three", length=6.0, weight=1.5, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.8, head=(-16, 0, 0), waist=(-2, 0, 0))
    c.key(1.3, head=(-14, 0, 0), waist=(-2, 0, 0))
    t = 1.3
    for _ in range(3):
        c.key(t + 0.45, head=(14, 0, 0), waist=(5, 0, 0))
        c.key(t + 0.75, head=(12, 0, 0), waist=(4, 0, 0))
        c.key(t + 1.2, head=(-6, 0, 0), waist=(0, 0, 0))
        t += 1.2
    c.key(t + 0.5, ra=CHEST, head=(6, 0, 0))
    c.key(t + 1.1, ra=CHEST, head=(8, 0, 0))
    c.key(6.0)


@clip("murmur_name", "Murmurs the kept name", length=4.2, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.8, ra=(-58, -48, 0), head=(10, 0, 0))
    c.key(1.1, ra=CHEST, head=(13, 0, -2))
    c.wobble(1.2, 2.9, 2.5, "head", 3, base=(13, 0, -2), axis=0)
    c.key(3.3, ra=CHEST, head=(4, 0, 0))
    c.key(4.2)


@clip("settle_bones", "Settles the bones", length=3.8, weight=1.5)
def _(c):
    c.key(0.7, ra=(-6, 0, 14), head=(0, 0, -6))
    c.key(1.2, ra=(0, 0, 0), la=(-6, 0, 14), head=(0, 0, 8))
    c.key(1.55, la=(0, 0, 2), head=(0, 0, 17))
    c.key(1.75, head=(0, 0, 13))
    c.key(2.2, head=(0, 0, -5), waist=(0, 0, -2))
    c.wobble(2.3, 2.8, 9, "head", 2.5, base=(0, 0, -3), axis=2)
    c.key(3.8)


@clip("skull_knock", "Knocks on the skull", length=3.6, weight=1, require=["adult"], avoid=BEARERS)
def _(c):
    # the fist comes down on the top corner of the skull (no outward roll, the skull leaning into it)
    c.key(0.7, ra=(-150, 2, 0), head=(0, 0, -2))
    c.key(0.95, ra=(-160, 0, 0), head=(0, 0, -4))
    c.wobble(1.0, 1.8, 4, "right_arm", 5, base=(-160, 0, 0), axis=0)
    c.key(1.0, head=(0, 0, -4))
    c.key(1.8, head=(0, 0, -3))
    c.key(2.2, head=(-4, 8, 12))                               # and listens to the hollow
    c.key(2.6, ra=(-30, 0, 4), head=(-2, 6, 6))
    c.key(3.6)


@clip("pulse_check", "Checks for a pulse", length=4.8, weight=1, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.8, la=(-62, -22, 0), head=(14, 0, 0))
    c.key(1.2, la=(-68, -26, 0), ra=(-58, -40, 0), head=(24, -4, 0))
    c.key(1.4, ra=(-60, -44, 0))
    c.hold(1.6, 2.7, la=(-68, -26, 0), ra=(-60, -44, 0), head=(26, -5, 3))
    c.key(3.2, la=(-20, 0, 9), ra=(-15, 0, 9), head=(6, 0, -9), root_pos=(0, -0.6, 0))
    c.key(3.6, la=(-6, 0, 2), ra=(-4, 0, 2), head=(4, 0, -6), root_pos=(0, 0, 0))
    c.key(4.8)


@clip("gaze_up", "Gazes up into the dark", length=4.6, weight=1.5)
def _(c):
    c.key(1.2, head=(-24, 4, 0), waist=(-3, 0, 0), ra=(4, 0, 3), la=(4, 0, 3))
    c.key(1.6, head=(-27, 5, -2), waist=(-4, 0, 0))
    c.key(3.0, head=(-25, -6, 3), waist=(-3, 0, 0), ra=(4, 0, 3), la=(4, 0, 3))
    c.key(3.6, head=(-6, -2, 0), waist=(0, 0, 0))
    c.key(4.6)


@clip("hands_behind", "Waits with hands behind", length=5.4, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.9, ra=(22, 32, 0), la=(22, 32, 0), root=(0, 0, 0))
    c.key(1.3, ra=BEHIND, la=BEHIND, root=(0, 0, 2), root_pos=(-0.4, 0, 0), head=(0, 6, 0))
    c.key(2.6, root=(0, 0, 2.4), root_pos=(-0.5, 0, 0), head=(2, 16, 0))
    c.key(3.6, root=(0, 0, -1.5), root_pos=(0.3, 0, 0), head=(2, -10, 0))
    c.key(4.4, ra=BEHIND, la=BEHIND, root=(0, 0, 0), root_pos=(0, 0, 0), head=(0, 0, 0))
    c.key(5.4)


@clip("count_ribs", "Counts the remembered days on its ribs", length=5.4, weight=1.5, require=["adult"],
      avoid=BEARERS)
def _(c):
    # the left arm eases out of the way and the right hand taps down the ribs, one day to a rib
    c.key(0.7, ra=(-66, -56, 0), la=(-4, 0, 12), head=(16, -8, 0))
    t = 1.0
    for i in range(6):
        pitch = -66 + i * 5
        c.key(t, ra=(pitch, -60, 0), head=(16 + i * 2, -8, 0))
        c.key(t + 0.17, ra=(pitch - 4, -50, 0))
        t += 0.42
    c.key(t + 0.1, ra=(-38, -58, 0), head=(26, -8, 0))
    c.key(t + 0.7, head=(8, -4, 9))                            # lost count? a tilt, and the last rib again
    c.key(t + 1.1, ra=(-40, -54, 0), la=(-4, 0, 10), head=(10, -2, 7))
    c.key(t + 1.6, ra=(-20, -20, 0), la=(0, 0, 3), head=(4, 0, 0))
    c.key(5.4)


@clip("brush_ash", "Brushes the ash away", length=3.8, weight=1.5, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-95, -62, 0), head=(6, -14, -6))
    c.cycle(0.8, 1.9, 0.36, dict(ra=(-102, -70, 0)), dict(ra=(-82, -56, 0)))
    c.key(1.9, head=(8, -16, -6))
    c.key(2.3, ra=(-10, 0, 0), la=(-90, -60, 0), head=(6, 12, 6))
    c.cycle(2.4, 3.2, 0.34, dict(la=(-96, -66, 0)), dict(la=(-80, -54, 0)))
    c.key(3.2, head=(4, 10, 4))
    c.key(3.8)


@clip("slow_look", "Slow look around", length=5.6, weight=2)
def _(c):
    c.key(1.3, head=(2, -34, 0), root=(0, -5, 0))
    c.key(2.2, head=(4, -36, 2), root=(0, -6, 0))
    c.key(3.4, head=(0, 28, 0), root=(0, 4, 0))
    c.key(4.2, head=(2, 31, -2), root=(0, 5, 0))
    c.key(5.6)


@clip("ponder", "Ponders with a hand to the jaw", length=4.8, weight=1.5, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.9, ra=(-96, -52, 0), la=(-26, -40, 0), head=(4, 0, 4), waist=(3, 0, 0))
    c.key(1.2, ra=CHIN, la=(-28, -44, 0), head=(6, 0, 8), waist=(4, 0, 0))
    c.wobble(1.6, 2.6, 3, "right_arm", 3, base=CHIN, axis=0)
    c.key(1.6, head=(6, 0, 8))
    c.key(3.2, head=(2, 6, 10))
    c.key(3.8, ra=CHIN, la=(-28, -44, 0), head=(3, 2, 4), waist=(4, 0, 0))
    c.key(4.8)


@clip("dry_chuckle", "Dry chuckle", length=3.2, weight=1)
def _(c):
    c.key(0.5, head=(-8, 0, 3), waist=(-3, 0, 0))
    c.wobble(0.6, 1.9, 6, "head", 4, base=(-10, 0, 3), axis=0, decay=0.5)
    c.wobble(0.6, 1.9, 6, "waist", 1.5, base=(-3, 0, 0), axis=0, decay=0.5)
    c.wobble(0.6, 1.9, 6, "right_arm", 3, base=(0, 0, 4), axis=2, decay=0.5)
    c.wobble(0.6, 1.9, 6, "left_arm", 3, base=(0, 0, 4), axis=2, decay=0.5)
    c.key(2.4, head=(6, 0, 0), waist=(1, 0, 0))
    c.key(3.2)


@clip("dirge_sway", "Sways to an old dirge", length=6.4, weight=1.5, mirror="never")
def _(c):
    # unlike fold_hands the arms hang loose and the whole body rocks to the tune, the arms swinging a beat behind
    # (small enough that a Remembrancer's staff or an Ashwarden's glaive only sways with them)
    c.key(0.8, head=(8, 0, 0), ra=(0, 0, 2), la=(0, 0, 2))
    c.wave(0.8, 5.6, 2.4, "root", 4, axis=2)
    c.wave(0.8, 5.6, 2.4, "head", 8, base=(8, 0, 0), axis=2, phase=0.12)
    c.wave(0.8, 5.6, 2.4, "right_arm", 5, base=(0, 0, 2), axis=2, phase=-0.1)
    c.wave(0.8, 5.6, 2.4, "left_arm", -5, base=(0, 0, 2), axis=2, phase=-0.1)
    c.wave(0.8, 5.6, 1.2, "waist", 2, base=(0, 0, 0), axis=0, phase=0.25)    # a slow nod on every beat
    c.key(6.4)
