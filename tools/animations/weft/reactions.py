"""Weft reactions (grown-ups): delight is precise, thanks are formal, refusals are polite and a touch superior."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _weft import click, tap, twist, hang_all  # noqa: E402

# (the right forearm lies over the left: layered, not passing through each other)
UP_FOLD_R, UP_FOLD_L = twist(-42, -72), twist(-30, -88)
LO_FOLD_R, LO_FOLD_L = twist(-26, -64), twist(-16, -80)

UP_APART = dict(ra=(-60, 12, 10), rf=(-40, 0, 0), la=(-60, 12, 10), lf=(-40, 0, 0))
UP_CLAP = dict(ra=(-60, -16, 0), rf=(-40, 0, 0), la=(-60, -16, 0), lf=(-40, 0, 0))
LO_APART = dict(rla=(-55, 4, 12), rlf=(-20, 0, 0), lla=(-55, 4, 12), llf=(-20, 0, 0))
LO_CLAP = dict(rla=(-60, -25, 0), rlf=(-20, 0, 0), lla=(-60, -25, 0), llf=(-20, 0, 0))
# the upper hands palm to palm before the chest, one sliding up as the other slides down
RUB_A = dict(ra=(-36, -27, 16), rf=(-80, 0, 0), la=(-26, -27, 16), lf=(-80, 0, 0))
RUB_B = dict(ra=(-26, -27, 16), rf=(-80, 0, 0), la=(-36, -27, 16), lf=(-80, 0, 0))


# -- happy ------------------------------------------------------------------------------------------------------
@clip("happy_four_clap", "Four hand applause", trigger="happy", length=2.9, weight=3, require=["adult"])
def _(c):
    # the upper pair and the lower pair clap in turn, a quick double rhythm; up on the toes, head bobbing
    c.key(0.3, **UP_APART, **LO_APART, head=(-6, 0, 0), rft=(-8, 0, 0), lft=(-8, 0, 0), root_pos=(0, -0.5, 0))
    t = 0.3
    for i in range(4):
        c.key(t + 0.14, **UP_CLAP, head=(4, 0, 4 if i % 2 else -4))
        c.key(t + 0.28, **UP_APART, **LO_CLAP)
        c.key(t + 0.42, **LO_APART, head=(-6, 0, 0))
        t += 0.42
    c.hold(0.3, t, rft=(-8, 0, 0), lft=(-8, 0, 0), root_pos=(0, -0.5, 0))
    click(c, t, t + 0.5, base=(-8, 0, 6), rate=7, amp=3)
    c.key(2.9)


@clip("happy_flutter", "Delighted flutter", trigger="happy", length=3.0, weight=3, require=["adult"])
def _(c):
    # the upper arms rise in a V, every finger fluttering; the lower pair hugs the belly; knees bounce; clicks
    c.key(0.2, waist=(4, 0, 0), rs=(8, 0, 0), ls=(8, 0, 0), rl=(-4, 0, 0), ll=(-4, 0, 0), root_pos=(0, 0.4, 0))
    c.key(0.55, ra=(-14, 0, 150), la=(-14, 0, 150), rf=(-10, 0, 0), lf=(-10, 0, 0), waist=(-8, 0, 0),
          rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0), head=(-18, 0, 0),
          rs=(0, 0, 0), ls=(0, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0), root_pos=(0, 0, 0))
    c.wobble(0.55, 2.2, 6, "rf", 12, base=(-10, 0, 0), axis=2)
    c.wobble(0.6, 2.25, 6, "lf", 12, base=(-10, 0, 0), axis=2)
    c.hold(0.55, 2.25, ra=(-14, 0, 150), la=(-14, 0, 150), rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L,
           llf=(-30, 0, 0), waist=(-8, 0, 0))
    for t in (0.9, 1.5):
        c.key(t, rs=(10, 0, 0), ls=(10, 0, 0), rl=(-5, 0, 0), ll=(-5, 0, 0), root_pos=(0, 0.5, 0))
        c.key(t + 0.3, rs=(0, 0, 0), ls=(0, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0), root_pos=(0, 0, 0))
    click(c, 0.7, 2.2, base=(-18, 0, 0), rate=7, amp=4)
    c.key(3.0)


# -- thanks -----------------------------------------------------------------------------------------------------
@clip("thanks_crossed_bow", "Four hands to the heart", trigger="thanks", length=3.2, weight=3, require=["adult"])
def _(c):
    # all four hands cross over the chest, the upper pair high and the lower pair beneath, and a slow bow
    c.key(0.5, ra=twist(-48, -74), rf=(-96, 0, 0), la=twist(-36, -88), lf=(-84, 0, 0),
          rla=twist(-30, -72), rlf=(-40, 0, 0), lla=twist(-26, -76), llf=(-36, 0, 0), head=(-2, 0, 0))
    c.hold(0.5, 2.2, ra=twist(-48, -74), rf=(-96, 0, 0), la=twist(-36, -88), lf=(-84, 0, 0),
           rla=twist(-30, -72), rlf=(-40, 0, 0), lla=twist(-26, -76), llf=(-36, 0, 0))
    c.key(1.3, waist=(20, 0, 0), head=(12, 0, 0))
    c.key(1.55, waist=(22, 0, 0))
    c.key(2.2, waist=(0, 0, 0), head=(-2, 0, 4))
    c.key(3.2)


@clip("thanks_cradle_gift", "Cradles the gift", trigger="thanks", length=3.4, weight=3, require=["adult"])
def _(c):
    # the lower pair cradles the gift close; the upper right touches the chest and then opens toward the giver;
    # a nod and a soft click
    c.key(0.5, rla=twist(-42, -58), rlf=(-30, 0, 0), lla=twist(-36, -62), llf=(-30, 0, 0), head=(12, 0, 0))
    c.hold(0.5, 2.7, rla=twist(-42, -58), rlf=(-30, 0, 0), lla=twist(-36, -62), llf=(-30, 0, 0))
    c.key(0.9, ra=(-40, -30, 0), rf=(-96, 0, 0), head=(8, 0, 0))
    c.key(1.6, ra=(-46, 18, 16), rf=(-36, 0, 0), head=(-2, 0, 4))
    c.key(1.85, ra=(-48, 22, 18), head=(8, 0, 4))
    c.key(2.1, head=(2, 0, 6))
    click(c, 2.1, 2.6, base=(2, 0, 6), rate=6, amp=2.5)
    c.key(2.7, ra=(-20, 6, 6), rf=(-20, 0, 0))
    c.key(3.4)


# -- decline ----------------------------------------------------------------------------------------------------
@clip("decline_four_palms", "Four palms no", trigger="decline", length=2.8, weight=3, require=["adult"])
def _(c):
    # the upper hands come up palm-out, the lower pair turns outward too; the body leans away, head turned aside
    c.key(0.35, ra=(-70, 10, 10), rf=(-62, 0, 0), la=(-70, 10, 10), lf=(-62, 0, 0),
          rla=(-50, 10, 18), rlf=(-10, 0, 0), lla=(-50, 10, 18), llf=(-10, 0, 0), waist=(-8, 0, 0),
          head=(-6, -22, 0))
    c.key(0.5, ra=(-74, 10, 10), la=(-74, 10, 10), waist=(-9, 0, 0), head=(-6, -24, -4))
    c.hold(0.5, 1.6, ra=(-74, 10, 10), rf=(-62, 0, 0), la=(-74, 10, 10), lf=(-62, 0, 0),
           rla=(-50, 10, 18), rlf=(-10, 0, 0), lla=(-50, 10, 18), llf=(-10, 0, 0), waist=(-9, 0, 0))
    c.key(1.1, head=(-4, -18, 3))
    c.key(1.6, head=(-6, -24, -4))
    c.key(2.8)


@clip("decline_finger_wag", "Lower finger wag", trigger="decline", length=3.0, weight=3, require=["adult"])
def _(c):
    # arms folded above, and from below them one lower hand wags a long finger: no, no; the head shakes slowly
    c.key(0.4, ra=UP_FOLD_R, rf=(-90, 0, 0), la=UP_FOLD_L, lf=(-78, 0, 0), head=(-6, 0, 0),
          rla=(-60, -10, 0), rlf=(10, 0, 0))
    c.hold(0.4, 2.4, ra=UP_FOLD_R, rf=(-90, 0, 0), la=UP_FOLD_L, lf=(-78, 0, 0), rla=(-60, -10, 0))
    c.wobble(0.5, 2.3, 2.4, "rlf", 22, base=(10, 0, 0), axis=2)
    c.wobble(0.5, 2.3, 1.2, "head", 10, base=(-6, 0, 0), axis=1)
    c.key(2.4, rlf=(10, 0, 0))
    c.key(3.0)


# -- pleased / annoyed ------------------------------------------------------------------------------------------
@clip("pleased_smug_rub", "Smug little rub", trigger="pleased", length=2.8, weight=3, require=["adult"])
def _(c):
    # chin up, the upper hands rub together briskly before the chest while the lower pair hangs easy and open
    # (nothing like the tight fold of annoyance); a quick satisfied run of clicks
    c.key(0.4, **RUB_A, head=(-10, 4, -4), rla=(6, 0, 16), rlf=(-20, 0, 0), lla=(6, 0, 16), llf=(-20, 0, 0))
    c.hold(0.4, 2.1, rla=(6, 0, 16), rlf=(-20, 0, 0), lla=(6, 0, 16), llf=(-20, 0, 0))
    c.cycle(0.4, 1.8, 0.28, RUB_A, RUB_B)
    c.key(2.1, ra=(-30, -25, 16), rf=(-80, 0, 0), la=(-30, -25, 16), lf=(-80, 0, 0))
    click(c, 1.2, 2.0, base=(-12, 4, -6), rate=8, amp=3)
    c.key(2.8)


@clip("annoyed_drum", "Drums four sets of fingers", trigger="annoyed", length=3.4, weight=3, require=["adult"])
def _(c):
    # upper arms fold tight; the lower fingers drum on the belly, a foot taps, and one sharp click with a shake
    c.key(0.3, ra=UP_FOLD_R, rf=(-96, 0, 0), la=UP_FOLD_L, lf=(-80, 0, 0), head=(-4, 0, 0), waist=(-3, 0, 0),
          rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0))
    c.hold(0.3, 2.7, ra=UP_FOLD_R, rf=(-96, 0, 0), la=UP_FOLD_L, lf=(-80, 0, 0), waist=(-3, 0, 0),
           rla=LO_FOLD_R, lla=LO_FOLD_L)
    c.cycle(0.4, 2.0, 0.24, {"rlf": (-30, 0, 0), "llf": (-42, 0, 0)}, {"rlf": (-42, 0, 0), "llf": (-30, 0, 0)})
    tap(c, 0.5, 2.0, "rft", (0, 0, 0), -16, period=0.36)
    c.key(2.1, head=(6, 0, 0), rlf=(-30, 0, 0), llf=(-30, 0, 0))
    c.key(2.25, head=(8, -10, 0))
    c.key(2.45, head=(8, 10, 0))
    c.key(2.65, head=(6, 0, 0))
    c.key(3.4)


# -- hurt -------------------------------------------------------------------------------------------------------
@clip("hurt_curl", "Curls in", trigger="hurt", length=1.8, weight=3, require=["adult"], blend=(0.08, 0.35))
def _(c):
    # a blow: the body curls, all four arms snap in to cover, knees give; then it straightens, carefully
    c.key(0.12, waist=(22, 0, 4), head=(18, 0, 6), ra=UP_FOLD_R, rf=(-100, 0, 0), la=UP_FOLD_L,
          lf=(-80, 0, 0), rla=LO_FOLD_R, rlf=(-40, 0, 0), lla=LO_FOLD_L, llf=(-40, 0, 0),
          rl=(-10, 0, 0), rs=(20, 0, 0), ll=(-10, 0, 0), ls=(20, 0, 0), rft=(-10, 0, 0), lft=(-10, 0, 0),
          root_pos=(0, 0.3, 0.4))
    c.key(0.5, waist=(18, 0, 2), head=(14, 0, 4))
    c.key(1.1, waist=(8, 0, 0), head=(4, 0, 0), rl=(-4, 0, 0), rs=(8, 0, 0), ll=(-4, 0, 0), ls=(8, 0, 0),
          rft=(-4, 0, 0), lft=(-4, 0, 0), root_pos=(0, 0.1, 0.2))
    c.key(1.8)


@clip("hurt_recoil", "Recoils", trigger="hurt", length=1.6, weight=3, require=["adult"], blend=(0.06, 0.35))
def _(c):
    # head snaps back, all four arms fling out, one long step's worth of lean; then a wary settle
    c.key(0.1, waist=(-14, 0, -4), head=(-22, 0, -6), ra=(-30, 20, 50), rf=(-10, 0, 0), la=(-30, 20, 50),
          lf=(-10, 0, 0), rla=(-20, 10, 46), rlf=(20, 0, 0), lla=(-20, 10, 46), llf=(20, 0, 0),
          root=(-4, 0, 0), root_pos=(0, 0, 1.2))
    c.key(0.35, waist=(-10, 0, -2), head=(-14, 0, -4), root=(-3, 0, 0))
    c.key(0.8, waist=(4, 0, 0), head=(8, 0, 0), ra=(-20, -10, 6), rf=(-40, 0, 0), la=(-20, -10, 6),
          lf=(-40, 0, 0), rla=(-20, -10, 0), rlf=(-20, 0, 0), lla=(-20, -10, 0), llf=(-20, 0, 0),
          root=(0, 0, 0), root_pos=(0, 0, 0.6))
    c.key(1.6)


hang_all()
