"""Piglin reactions: quick to laugh, quick to snatch, quick to take offence."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


# -- happy: a gift they love, a new level of friendship ---------------------------------------------------------------
@clip("happy_jig", "A hopping victory jig", trigger="happy", length=3.2, weight=2, mirror="free")
def _(c):
    side = 1
    for t in (0.3, 0.9, 1.5, 2.1):
        c.key(t, root_pos=(0, 0.4, 0), root=(0, 0, 0))
        c.key(t + 0.18, root_pos=(0, -1.6, 0), root=(0, 0, 6 * side), head=(-6, 0, -12 * side),
              la=(-10, 0, 120 if side > 0 else 50), ra=(-10, 0, 50 if side > 0 else 120))
        c.key(t + 0.4, root_pos=(0, 0.3, 0))
        side = -side
    c.wobble(0.3, 2.6, 6, "left_ear", 22, base=(-6, 0, 20), axis=2)
    c.wobble(0.3, 2.6, 6, "right_ear", 22, base=(-6, 0, 20), axis=2)
    c.key(2.7, root_pos=(0, 0, 0), root=(0, 0, 0), head=(-4, 0, 0), la=(-6, 0, 20), ra=(-6, 0, 20))
    c.key(3.2)


@clip("happy_bellow", "Fists overhead and a roaring laugh", trigger="happy", length=3.2, weight=2,
      require=["adult"])
def _(c):
    c.key(0.25, waist=(4, 0, 0), la=(8, 0, 6), ra=(8, 0, 6))
    c.key(0.6, waist=(-9, 0, 0), head=(-30, 0, 0), la=(-24, 0, 150), ra=(-24, 0, 150))
    ears(c, 0.6, roll=36, pitch=-10)
    c.key(0.8, la=(-24, 0, 162), ra=(-24, 0, 162))
    c.wobble(0.85, 2.0, 7, "head", 6, base=(-30, 0, 0), decay=0.3)
    c.wobble(0.9, 2.0, 7, "left_ear", 14, base=(-10, 0, 34), axis=2)
    c.wobble(0.85, 2.0, 7, "right_ear", 14, base=(-10, 0, 34), axis=2)
    c.key(2.0, waist=(-8, 0, 0), la=(-24, 0, 154), ra=(-24, 0, 154))
    c.key(2.4, waist=(2, 0, 0), head=(-4, 0, 0), la=(-36, -26, 0), ra=(-10, 0, 10))
    c.key(2.55, la=(-18, -16, 4))
    c.key(3.2)


# -- thanks: a gift they like or don't mind ---------------------------------------------------------------------------
@clip("thanks_pocket", "Snatches it and pockets it with a glance about", trigger="thanks", length=2.8, weight=2,
      mirror="never")
def _(c):
    c.key(0.15, la=(-30, 0, 0))
    c.key(0.32, la=(-84, 0, 0), head=(4, 0, 0))
    c.key(0.6, la=(-28, -18, 0), head=(10, -6, 0))
    ears(c, 0.6, roll=-8, pitch=16)
    c.key(0.85, head=(2, 32, 0))
    c.key(1.15, head=(2, -30, 0))
    c.key(1.4, head=(8, -4, 0), la=(-18, -14, 0))
    c.key(1.55, la=(-32, -18, 0))
    c.key(1.7, la=(-18, -14, 0))
    c.key(2.0, head=(14, 0, 0))
    ears(c, 2.0, roll=18)
    c.key(2.2, head=(0, 0, 0))
    c.key(2.8)


@clip("thanks_thump", "One thump on the belly and a deep nod", trigger="thanks", length=2.6, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.3, la=(-8, 0, 6), head=(-6, 0, 0))
    c.key(0.48, la=(-42, -30, 0))
    c.key(0.62, la=(-32, -26, 0))
    flick(c, 0.48, amp=18)
    c.key(1.0, waist=(14, 0, 0), head=(16, 0, 0))
    ears(c, 1.0, roll=-10, pitch=10)
    c.hold(1.1, 1.5, waist=(14, 0, 0), head=(16, 0, 0), la=(-32, -26, 0))
    c.key(2.0, waist=(0, 0, 0), head=(-2, 0, 0), la=(-6, 0, 0))
    ears(c, 2.0, roll=10)
    c.key(2.6)


# -- decline: a gift they dislike, or they won't talk -----------------------------------------------------------------
@clip("decline_snort_away", "Snorts and brushes it away nose in the air", trigger="decline", length=2.6, weight=2,
      mirror="never")
def _(c):
    snort(c, 0.1, amp=9)
    c.key(0.55, head=(-14, 40, 0), la=(-58, -20, 0))
    ears(c, 0.55, roll=-12, pitch=24)
    c.key(0.8, la=(-52, 36, 30))
    c.key(0.95, la=(-54, 30, 24))
    c.hold(1.0, 1.6, head=(-16, 42, 4))
    c.key(2.0, la=(-10, 0, 4), head=(-4, 10, 0))
    c.key(2.6)


@clip("decline_spit", "Spits aside and waves it off", trigger="decline", length=2.8, weight=2, require=["adult"])
def _(c):
    c.key(0.2, head=(-10, 0, 0))
    c.key(0.38, head=(26, -20, 0), waist=(6, 0, 0))
    flick(c, 0.38, amp=24)
    c.key(0.6, head=(8, -10, 0), waist=(2, 0, 0))
    c.key(0.9, la=(-72, -32, 0), ra=(-72, -32, 0))
    c.key(1.2, la=(-70, 30, 22), ra=(-70, 30, 22))
    c.wobble(0.9, 1.8, 3, "head", 14, base=(6, 0, 0), axis=1)
    ears(c, 1.2, roll=-10, pitch=20)
    c.key(1.9, la=(-20, 6, 8), ra=(-20, 6, 8))
    c.key(2.8)


@clip("decline_child_pout", "Stamps a foot and turns away in a huff", trigger="decline", length=2.8, weight=2,
      require=["child"])
def _(c):
    c.key(0.25, rl=(-24, 0, 0), la=(12, 0, 12), ra=(12, 0, 12))
    c.key(0.4, rl=(4, 0, 0), root_pos=(0, 0.5, 0))
    c.key(0.5, rl=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(0.9, root=(0, -28, 0), head=(14, -16, 0), la=(16, 0, 14), ra=(16, 0, 14))
    ears(c, 0.9, roll=-16, pitch=10)
    c.hold(1.0, 2.1, root=(0, -30, 0), head=(14, -18, -6), la=(16, 0, 14), ra=(16, 0, 14))
    flick(c, 1.6, side="right", amp=16, base=(10, 0, -16))
    c.key(2.8)


# -- pleased / annoyed: a reply they liked, or one that stung ---------------------------------------------------------
@clip("pleased_smirk", "A smug tilt and nod with ears up", trigger="pleased", length=2.4, weight=2)
def _(c):
    c.key(0.4, head=(-6, 6, 12), waist=(-4, 0, 0))
    ears(c, 0.4, roll=28, pitch=-8)
    c.key(0.9, head=(10, 6, 10))
    c.key(1.15, head=(-2, 6, 8))
    c.hold(1.2, 1.6, waist=(-4, 0, 0))
    ears(c, 1.6, roll=26, pitch=-6)
    c.key(2.4)


@clip("pleased_belly_pat", "Pats the belly contentedly", trigger="pleased", length=2.6, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.35, la=(-30, -20, 0), waist=(-4, 0, 0), head=(-6, 0, 0))
    for t in (0.55, 0.9):
        c.key(t, la=(-16, -14, 4))
        c.key(t + 0.17, la=(-32, -20, 0))
    ears(c, 0.7, roll=22)
    c.key(1.5, head=(12, 0, 4))
    c.key(1.75, head=(-2, 0, 2))
    c.key(2.6)


@clip("annoyed_stomp", "Stomps and snorts with the ears pinned", trigger="annoyed", length=2.4, weight=2)
def _(c):
    c.key(0.25, rl=(-22, 0, 0), head=(6, 0, 0))
    ears(c, 0.25, roll=-14, pitch=26)
    c.key(0.42, rl=(4, 0, 0), root_pos=(0, 0.5, 0), waist=(6, 0, 0))
    c.key(0.55, rl=(0, 0, 0), root_pos=(0, 0, 0))
    snort(c, 0.5, base=(8, 0, 0), amp=10)
    c.hold(0.9, 1.6, head=(10, 0, 0), waist=(5, 0, 0))
    ears(c, 1.6, roll=-14, pitch=26)
    c.key(2.4)


@clip("annoyed_glare", "Leans in glaring with a clenched fist", trigger="annoyed", length=2.8, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.4, waist=(10, 0, 0), head=(-4, 0, 0), la=(-8, 0, 16))
    ears(c, 0.4, roll=-16, pitch=30)
    c.wobble(0.5, 1.9, 9, "la", 3, base=(-10, 0, 18))
    c.key(1.2, head=(-6, 0, -6))
    snort(c, 1.9, base=(-6, 0, -6), amp=7)
    c.key(2.8)


# -- hurt -------------------------------------------------------------------------------------------------------------
@clip("hurt_recoil", "Recoils from the blow", trigger="hurt", length=1.6, weight=2, blend=(0.05, 0.3), mirror="free")
def _(c):
    c.key(0.12, waist=(-14, 0, 6), head=(-22, 10, 10), root_pos=(0, 0, 1), la=(-30, 0, 22), ra=(-24, 0, 18))
    ears(c, 0.12, roll=-18, pitch=34)
    c.key(0.4, waist=(-8, 0, 3), head=(-8, 6, 6), root_pos=(0, 0, 0.6), la=(-16, 0, 14), ra=(-14, 0, 10))
    c.key(0.75, waist=(3, 0, 0), head=(6, 0, 0), root_pos=(0, 0, 0), la=(-4, 0, 4), ra=(-4, 0, 4))
    ears(c, 0.75, roll=-8, pitch=14)
    c.key(1.6)


@clip("hurt_snarl", "Doubles over then snarls back up", trigger="hurt", length=1.9, weight=2, blend=(0.05, 0.3),
      require=["adult"], mirror="never")
def _(c):
    c.key(0.14, waist=(20, 0, 0), head=(14, 0, 0), la=(-34, -20, 0))
    ears(c, 0.14, roll=-18, pitch=30)
    c.key(0.5, waist=(16, 0, 0), head=(10, 0, 0))
    c.key(0.85, waist=(6, 0, 0), head=(-14, 0, 0), la=(-10, 0, 14))
    snort(c, 0.9, base=(-14, 0, 0), amp=8)
    ears(c, 1.2, roll=-14, pitch=26)
    c.key(1.9)
