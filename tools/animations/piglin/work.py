"""Piglin trades: every job has its own moment, done with the free (left) hand where a hand is needed."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


@clip("barter_weigh", "Weighs gold in the palm", length=4.6, weight=4, require=["adult", "job:bartermaster"],
      mirror="never")
def _(c):
    c.key(0.45, la=(-62, -14, 0), head=(18, -8, 0))
    c.key(0.6, la=(-58, -14, 0))
    for i, t in enumerate((0.9, 1.45, 2.0)):
        c.key(t, la=(-50, -14, 0), head=(20, -8, 8 if i % 2 == 0 else -8))
        c.key(t + 0.22, la=(-66, -14, 0))
        c.key(t + 0.4, la=(-58, -14, 0))
    ears(c, 1.0, left=(-6, 0, 24), right=(4, 0, -10))
    ears(c, 1.8, left=(4, 0, -10), right=(-6, 0, 24))
    c.key(2.6, head=(16, -6, 0))
    c.key(2.85, head=(28, -6, 0))
    c.key(3.1, head=(4, -2, 0), la=(-56, -16, 0))
    ears(c, 3.1, roll=26, pitch=-6)
    c.key(3.5, la=(-26, -28, 0), head=(10, -8, 0))
    c.key(3.7, la=(-34, -30, 0))
    c.key(4.6)


@clip("barter_appraise", "Holds a trade up to the light", length=5.0, weight=4, require=["adult", "job:bartermaster"],
      mirror="never")
def _(c):
    c.key(0.3, la=(-20, -10, 0), head=(6, 0, 0))
    c.key(0.8, la=(-150, -24, 0), head=(-28, -10, 0))
    ears(c, 0.8, roll=20, pitch=-8)
    c.key(1.1, la=(-156, -26, 0), head=(-30, -12, -10))
    c.wobble(1.2, 2.4, 2.5, "la", 14, base=(-156, -26, 0), axis=1)
    c.key(1.8, head=(-30, -14, 10))
    c.key(2.4, head=(-28, -12, -6))
    c.key(2.9, la=(-104, -26, 0), head=(18, -8, 0), waist=(4, 0, 0))
    c.wobble(3.0, 3.4, 7, "head", 3, base=(18, -8, 0))
    snort(c, 3.5, base=(10, -6, 0), amp=8)
    ears(c, 3.6, left=(0, 0, 30), right=(6, 0, -8))
    c.key(4.1, la=(-30, -28, 0), head=(-2, 0, 4), waist=(0, 0, 0))
    ears(c, 4.4, roll=6)
    c.key(5.0)


@clip("hunter_scan", "Crouches and shades the eyes to scan for prey", length=5.2, weight=4,
      require=["adult", "job:hunter"], mirror="never")
def _(c):
    c.key(0.5, waist=(14, 0, 0), head=(-12, 0, 0), root_pos=(0, 0.8, 0), la=(-40, -10, 0))
    c.key(0.9, la=(-146, -16, -6), head=(-16, 0, 0))
    ears(c, 0.9, roll=24, pitch=-18)
    c.key(1.8, waist=(14, -16, 0), head=(-14, -22, 0), la=(-146, -16, -6))
    c.key(2.1, waist=(14, -18, 0), head=(-14, -26, 0))
    flick(c, 2.2, side="right", amp=22, base=(-18, 0, 24))
    c.key(3.3, waist=(14, 16, 0), head=(-14, 20, 0))
    c.key(3.6, waist=(15, 18, 0), head=(-18, 24, 0))
    c.hold(3.7, 4.2, waist=(15, 18, 0), head=(-18, 24, 0))
    ears(c, 4.2, roll=30, pitch=-22)
    c.key(4.5, la=(-30, -6, 0), waist=(6, 6, 0), head=(-6, 6, 0), root_pos=(0, 0.3, 0))
    c.key(5.2)


@clip("hunter_sniff_trail", "Sniffs out a trail and points the way", length=4.6, weight=3,
      require=["adult", "job:hunter"], mirror="never")
def _(c):
    c.key(0.6, waist=(30, 0, 0), head=(18, -10, 0), la=(-14, 0, 6), ra=(-14, 0, 6))
    ears(c, 0.6, roll=10, pitch=-20)
    c.wobble(0.7, 1.4, 7, "head", 4, base=(18, -12, 0))
    c.key(1.7, waist=(30, 10, 0), head=(18, 14, 0))
    c.wobble(1.75, 2.3, 7, "head", 4, base=(18, 14, 0))
    snort(c, 2.4, base=(14, 10, 0), amp=8)
    c.key(2.9, waist=(-2, 0, 0), head=(-10, 4, 0), la=(-88, 0, 0), ra=(0, 0, 0))
    ears(c, 2.9, roll=32, pitch=-14)
    c.key(3.1, la=(-92, 2, 0))
    c.hold(3.2, 3.8, la=(-90, 2, 0), head=(-8, 4, 0), waist=(-2, 0, 0))
    c.key(4.6)


@clip("forager_pluck", "Plucks a fungus and sniffs it", length=4.8, weight=4, require=["adult", "job:forager"],
      mirror="never")
def _(c):
    c.key(0.6, waist=(36, 0, 0), head=(14, 0, 0), la=(-28, -8, 0))
    ears(c, 0.6, roll=-6, pitch=-10)
    c.key(0.9, la=(-34, -8, 0))
    c.key(1.1, la=(-12, -8, 0), waist=(30, 0, 0))
    c.key(1.25, la=(-20, -8, 0), waist=(32, 0, 0))
    c.key(1.4, la=(-6, -8, 0), waist=(28, 0, 0))
    flick(c, 1.4, amp=18, base=(-10, 0, -6))
    c.key(2.0, waist=(2, 0, 0), head=(16, -6, 0), la=(-104, -26, 0))
    c.wobble(2.1, 2.7, 7, "head", 4, base=(16, -6, 0))
    c.key(3.0, head=(4, -2, 0))
    c.key(3.2, head=(16, -2, 0))
    ears(c, 3.2, roll=24)
    c.key(3.7, la=(-22, -26, 0), head=(14, -12, 0))
    c.key(3.9, la=(-30, -28, 0))
    c.key(4.8)


@clip("smelter_bellows", "Works the forge bellows", length=5.4, weight=4, require=["adult", "job:smelter"])
def _(c):
    c.key(0.4, la=(-56, -8, 0), ra=(-56, -8, 0), waist=(8, 0, 0), head=(-4, 26, 0))
    ears(c, 0.4, roll=-10, pitch=24)
    for i, t in enumerate((0.7, 1.6, 2.5, 3.4)):
        c.key(t, la=(-76, -8, 0), ra=(-76, -8, 0), waist=(2, 0, 0), head=(-6, 28, 0))
        c.key(t + 0.5, la=(-42, -8, 0), ra=(-42, -8, 0), waist=(12, 0, 0), head=(-2, 24, 0))
    c.key(4.3, la=(-56, -8, 0), ra=(-56, -8, 0), waist=(8, 0, 0), head=(-4, 26, 0))
    ears(c, 4.3, roll=-10, pitch=24)
    c.key(5.4)


@clip("smelter_brow_wipe", "Wipes the brow and fans off the heat", length=3.8, weight=3,
      require=["adult", "job:smelter"], mirror="never")
def _(c):
    # the hand crosses in front of the brow, chin up, rather than through the forehead
    c.key(0.45, la=(-140, -26, 0), head=(-8, 0, -4))
    c.key(0.95, la=(-146, 8, -6), head=(-4, 0, 10))
    ears(c, 0.95, roll=-16, pitch=10)
    c.key(1.2, la=(-110, -10, 0), head=(-14, 0, 0))
    c.cycle(1.3, 2.5, 0.3, {"la": (-120, -20, 0)}, {"la": (-96, -20, 0)})
    c.key(2.5, head=(-16, 0, 0))
    c.key(2.8, head=(10, 0, 0), waist=(4, 0, 0))
    snort(c, 2.85, base=(10, 0, 0), amp=6)
    ears(c, 3.0, roll=-18, pitch=12)
    c.key(3.8)


@clip("goldsmith_hammer", "Hammers gold on the anvil", length=5.2, weight=4, require=["adult", "job:goldsmith"],
      mirror="never")
def _(c):
    c.key(0.4, waist=(10, 0, 0), head=(22, -4, 0), ra=(-38, -12, 0), la=(-60, -6, 0))
    ears(c, 0.4, roll=-4, pitch=-6)
    strikes = (0.8, 1.55, 2.3, 3.05)
    for i, t in enumerate(strikes):
        last = i == len(strikes) - 1
        c.key(t, la=(-150 if not last else -168, -6, -14 if not last else -12), waist=(7, 0, 0), head=(20, -4, 0))
        c.key(t + 0.32, la=(-52, -6, 0), waist=(12 if not last else 15, 0, 0), head=(24, -4, 0))
        c.key(t + 0.45, la=(-62, -6, 0), waist=(10, 0, 0))
        flick(c, t + 0.32, amp=20 if not last else 30, base=(-6, 0, -4))
    c.key(3.8, la=(-60, -6, 0), ra=(-60, -14, 0), head=(16, -8, 0), waist=(8, 0, 0))
    c.key(4.3, ra=(-62, -16, 0), head=(12, -10, -6))
    ears(c, 4.3, roll=20)
    c.key(5.2)


@clip("goldsmith_inspect", "Squints at a ring and buffs it", length=4.8, weight=3, require=["adult", "job:goldsmith"],
      mirror="never")
def _(c):
    c.key(0.5, la=(-118, -40, 0), head=(4, -14, -8))
    ears(c, 0.5, left=(-10, 0, 20), right=(0, 0, 0))
    c.wobble(0.6, 1.6, 2.5, "la", 10, base=(-118, -40, 0), axis=1)
    c.key(1.6, head=(6, -16, -12))
    c.key(1.85, head=(14, -12, -4))
    c.key(1.98, head=(6, -12, -4))
    flick(c, 1.9, amp=14, base=(-5, 0, 10))
    c.key(2.3, la=(-34, -36, 0), head=(22, -8, 0))
    c.wobble(2.4, 3.2, 6, "la", 5, base=(-34, -36, 0), axis=1)
    c.key(3.6, la=(-128, -32, 0), head=(-10, -14, -8))
    ears(c, 3.6, roll=32, pitch=-8)
    c.key(4.0, la=(-130, -32, 0))
    c.key(4.8)


@clip("herder_call", "Bellows for the hoglin herd and waves it in", length=4.8, weight=4,
      require=["adult", "job:hoglin_herder"], mirror="never")
def _(c):
    c.key(0.4, la=(-112, -40, 0), head=(4, -6, 0), waist=(4, 0, 0))
    c.key(0.75, la=(-118, -42, 0), head=(-24, -6, 0), waist=(-7, 0, 0))
    ears(c, 0.75, roll=30, pitch=-8)
    c.wobble(0.8, 1.6, 6, "left_ear", 8, base=(-8, 0, 30), axis=2)
    c.wobble(0.8, 1.6, 6, "right_ear", 8, base=(-8, 0, 30), axis=2)
    c.key(1.6, head=(-20, -6, 0), waist=(-5, 0, 0))
    # waves overhead with a forward raise tipped out (a side raise this high cuts into the head)
    c.key(2.0, la=(-168, 0, -28), head=(-6, 10, 0), waist=(0, 6, 0))
    c.key(2.3, la=(-176, 0, -8))
    c.key(2.6, la=(-166, 0, -32))
    c.key(2.9, la=(-176, 0, -10))
    c.key(3.3, la=(-84, 0, 0), head=(-2, 0, 0), waist=(0, 0, 0))
    c.key(3.55, la=(-40, 4, 0), waist=(-3, 0, 0))
    c.key(3.8, la=(-84, 0, 0), waist=(0, 0, 0))
    c.key(4.05, la=(-40, 4, 0), waist=(-3, 0, 0))
    ears(c, 4.1, roll=12)
    c.key(4.8)


@clip("herder_pat", "Pats a hoglin's bristly back", length=4.4, weight=3, require=["adult", "job:hoglin_herder"],
      mirror="never")
def _(c):
    c.key(0.6, waist=(22, -8, 0), head=(18, -10, 0), la=(-44, -6, 0))
    ears(c, 0.6, roll=6, pitch=-4)
    for t in (0.9, 1.3, 1.7):
        c.key(t, la=(-56, -6, 0))
        c.key(t + 0.2, la=(-36, -6, 0), head=(20, -10, 0))
    c.wobble(2.1, 2.9, 5, "la", 6, base=(-40, -6, 0), axis=1)
    c.key(2.9, head=(16, -10, 10))
    flick(c, 3.0, amp=20, base=(0, 0, 8))
    c.key(3.5, waist=(0, 0, 0), head=(-4, 0, 0), la=(-10, 0, 0))
    snort(c, 3.55, base=(-4, 0, 0), amp=7)
    c.key(4.4)


@clip("dweller_lookout", "Leans on the rampart and spits over it", length=5.2, weight=4,
      require=["adult", "job:bastion_dweller"], mirror="never")
def _(c):
    c.key(0.6, waist=(12, 0, 0), la=(-62, -4, 0), ra=(-62, -4, 0), head=(14, 0, 0))
    c.key(1.6, head=(22, -26, 0), waist=(13, -6, 0))
    c.key(2.6, head=(22, 22, 0), waist=(13, 6, 0))
    ears(c, 2.6, roll=10, pitch=-8)
    c.key(3.0, head=(6, 0, 0), waist=(10, 0, 0))
    c.key(3.18, head=(28, 0, 0), waist=(15, 0, 0))
    flick(c, 3.18, amp=24, base=(-8, 0, 10))
    c.key(3.5, head=(20, 0, 0), waist=(12, 0, 0))
    for t in (3.8, 4.1):
        c.key(t, la=(-74, -4, 0))
        c.key(t + 0.14, la=(-60, -4, 0))
    c.key(5.2)


@clip("dweller_stack", "Heaves a gold block onto the hoard", length=4.6, weight=3,
      require=["adult", "job:bastion_dweller"])
def _(c):
    c.key(0.6, waist=(30, 0, 0), la=(-46, -14, 0), ra=(-46, -14, 0), head=(12, 0, 0))
    ears(c, 0.6, roll=-4, pitch=8)
    c.key(1.0, waist=(32, 0, 0), la=(-40, -16, 0), ra=(-40, -16, 0))
    c.key(1.6, waist=(-2, 0, 0), la=(-104, -14, 0), ra=(-104, -14, 0), head=(-10, 0, 0), root_pos=(0, -0.5, 0))
    ears(c, 1.6, roll=20, pitch=-6)
    c.key(2.2, waist=(4, 0, 0), la=(-112, -14, 0), ra=(-112, -14, 0), head=(-6, 0, 0), root_pos=(0, 0, 0))
    c.key(2.5, la=(-92, -14, 0), ra=(-92, -14, 0), waist=(8, 0, 0))
    c.key(2.9, la=(-20, 0, 0), ra=(-20, 0, 0), waist=(-4, 0, 0), head=(-12, 0, 0))
    c.key(3.2, la=(-14, -10, 6), head=(-14, 6, 0), waist=(-6, 0, 0))
    ears(c, 3.2, roll=30)
    c.key(3.4, la=(-40, -48, 0))
    c.key(3.55, la=(-20, -36, 6))
    c.key(4.6)


@clip("wanderer_hitch", "Hitches the pack and gazes down the road", length=5.4, weight=4,
      require=["adult", "job:wanderer"])
def _(c):
    c.key(0.4, la=(-22, -14, 0), ra=(-22, -14, 0), la_pos=(0, -0.8, 0), ra_pos=(0, -0.8, 0))
    c.key(0.65, root_pos=(0, -1.2, 0), la=(-30, -16, 0), ra=(-30, -16, 0))
    c.key(0.9, root_pos=(0, 0, 0), la=(-18, -14, 0), ra=(-18, -14, 0), waist=(6, 0, 0))
    flick(c, 0.9, amp=22)
    c.key(1.3, waist=(0, 0, 0), head=(-8, 0, 0))
    c.key(2.4, head=(-10, 28, 0), waist=(0, 8, 0))
    ears(c, 2.4, roll=8, pitch=6)
    c.hold(2.5, 3.6, head=(-12, 30, 2), waist=(0, 8, 0))
    c.key(4.0, head=(8, 6, 0), waist=(2, 0, 0), la=(-14, -12, 0), ra=(-14, -12, 0))
    c.key(4.4, head=(-4, 0, 0), la_pos=(0, 0, 0), ra_pos=(0, 0, 0))
    ears(c, 4.4, roll=14)
    c.key(5.4)
