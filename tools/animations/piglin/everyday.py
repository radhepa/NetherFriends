"""Everyday Piglin moments: gold in the hand, a nose for more of it, and a lot of swagger."""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


# grown-ups only: a baby's short arm raised to the snout disappears into its big head
@clip("coin_bite", "Bites a coin to test it", length=4.0, weight=2, require=["adult"], mirror="never")
def _(c):
    c.key(0.25, la=(6, 0, 0), head=(4, 0, 0))
    c.key(0.65, la=(-100, -24, 6), head=(10, -6, 0))
    c.key(0.9, la=(-112, -28, 6), head=(14, -10, -7))
    ears(c, 0.9, roll=14, pitch=-6)
    c.key(1.4, la=(-112, -30, 6), head=(12, -12, -9))
    c.key(1.55, head=(23, -6, 0))
    flick(c, 1.55, amp=22, base=(-6, 0, 14))
    c.key(1.72, head=(11, -6, 0))
    c.key(1.86, head=(21, -6, 0))
    c.key(2.1, la=(-98, -26, 6), head=(4, -4, 6))
    c.key(2.55, head=(18, 0, 0), la=(-92, -26, 6))
    c.key(2.85, head=(-8, 0, 0))
    ears(c, 2.85, roll=32, pitch=-8)
    c.key(3.25, la=(-26, -16, 0), head=(-4, 0, 0))
    ears(c, 3.4, roll=8)
    c.key(4.0)


@clip("pouch_count", "Counts the gold in the belt pouch", length=4.6, weight=2, require=["adult"], mirror="never")
def _(c):
    c.key(0.45, la=(-30, -18, 0), head=(26, -8, 0), waist=(6, -4, 0))
    ears(c, 0.45, roll=-6, pitch=-8)
    for i, t in enumerate((0.8, 1.25, 1.7, 2.15)):
        c.key(t, la=(-42, -20, 0), head=(29, -8, 2 if i % 2 else -2))
        c.key(t + 0.22, la=(-28, -18, 0), head=(25, -8, 0))
        flick(c, t, side="left" if i % 2 else "right", amp=16, base=(-8, 0, -6))
    c.key(2.7, la=(-30, -18, 0), head=(20, -6, 0), waist=(6, -4, 0))
    c.key(3.05, head=(-6, 4, 0), waist=(-3, 0, 0))
    ears(c, 3.05, roll=30, pitch=-4)
    c.key(3.3, la=(-20, -16, 0))
    c.key(3.45, la=(-34, -18, 0))
    c.key(3.6, la=(-20, -16, 0))
    c.key(3.75, la=(-34, -18, 0))
    c.key(4.6)


@clip("chest_thump", "Thumps the belly proudly", length=3.4, weight=1.5, require=["adult"], mirror="never")
def _(c):
    c.key(0.35, la=(-8, 0, 8), waist=(3, 0, 0), head=(4, 0, 0))
    c.key(0.6, la=(-44, -36, 0), waist=(-6, 0, 0), head=(-12, 0, 0))
    c.key(0.78, la=(-22, -28, 6))
    c.key(0.98, la=(-46, -38, 0), waist=(-8, 0, 0), head=(-16, 6, 0))
    ears(c, 0.6, roll=26, pitch=-6)
    flick(c, 0.98, amp=20, base=(-6, 0, 26))
    c.key(1.5, la=(-38, -32, 0), waist=(-9, 0, 2), head=(-20, 10, 4))
    c.hold(1.6, 2.3, waist=(-9, 0, 2), head=(-20, 10, 4))
    ears(c, 2.3, roll=26)
    c.key(2.8, la=(-10, -10, 2), waist=(-2, 0, 0), head=(-4, 0, 0))
    ears(c, 2.9, roll=4)
    c.key(3.4)


@clip("snout_sniff", "Sniffs the air for something worth having", length=3.4, weight=2)
def _(c):
    c.key(0.4, head=(-16, 0, 0))
    ears(c, 0.4, roll=18, pitch=-14)
    c.wobble(0.45, 0.95, 7, "head", 4, base=(-16, 0, 0))
    c.key(1.3, head=(-12, -26, -4))
    c.wobble(1.35, 1.75, 7, "head", 4, base=(-12, -26, -4))
    c.key(2.1, head=(-14, 22, 4))
    ears(c, 2.1, roll=26, pitch=-18)
    c.wobble(2.15, 2.5, 7, "head", 4, base=(-14, 22, 4))
    snort(c, 2.6, base=(-6, 18, 2), amp=10)
    flick(c, 2.66, amp=24, base=(-10, 0, 14))
    c.key(3.4)


@clip("ear_scratch", "Scratches behind an ear", length=3.6, weight=1.5, require=["adult"], mirror="never")
def _(c):
    # the hand comes up in front (a side raise this high swings the arm into the head) and the head leans into it;
    # grown-ups only, since a baby's short arm vanishes into its head either way
    c.key(0.2, la=(4, 0, 4))
    c.key(0.5, la=(-120, 0, -8), head=(4, 0, 8))
    c.key(0.8, la=(-170, 0, -14), head=(8, 0, 16), waist=(3, 0, 3))
    ears(c, 0.8, left=(4, 0, 10), right=(0, 0, -6))
    c.wobble(0.85, 2.2, 5, "la", 7, base=(-170, 0, -14))
    c.wobble(0.8, 2.2, 5, "left_ear", 12, base=(4, 0, 10), axis=2)
    c.key(1.3, head=(10, 4, 20))
    # found the spot: a hind leg kicks like a hog's being scratched
    c.wobble(1.3, 2.1, 6, "ll", 12, base=(-8, 0, 3))
    c.rest(2.4, "ll")
    c.key(2.2, head=(10, 4, 20), waist=(3, 0, 3))
    c.key(2.6, la=(-50, 0, 6), head=(2, 0, 4), waist=(0, 0, 0))
    flick(c, 2.65, side="left", amp=26)
    flick(c, 2.8, side="right", amp=18)
    c.key(3.6)


@clip("shoulder_roll", "Rolls the shoulders and cracks the neck", length=3.8, weight=1, require=["adult"])
def _(c):
    c.key(0.4, ra=(14, 0, 6), la=(14, 0, 6), ra_pos=(0, -1, 0), la_pos=(0, -1, 0))
    c.key(0.8, ra=(-10, 0, 4), la=(-10, 0, 4), ra_pos=(0, 0, 0), la_pos=(0, 0, 0))
    c.key(1.2, ra=(12, 0, 6), la=(12, 0, 6), ra_pos=(0, -1, 0), la_pos=(0, -1, 0))
    c.key(1.55, ra=(0, 0, 0), la=(0, 0, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0))
    c.key(1.6, head=(0, 0, 0))
    c.key(2.0, head=(4, 0, 22))
    c.key(2.12, head=(4, 0, 27))
    flick(c, 2.12, side="right", amp=24)
    c.key(2.5, head=(4, 0, -20))
    c.key(2.62, head=(4, 0, -26))
    flick(c, 2.62, side="left", amp=24)
    c.key(3.0, head=(-6, 0, 0), waist=(-4, 0, 0))
    ears(c, 3.0, roll=18)
    c.key(3.8)


@clip("gold_scan", "Scans the ground for a glint of gold", length=4.6, weight=2)
def _(c):
    c.key(0.8, head=(8, -34, 0))
    c.key(1.5, head=(14, -38, -4), waist=(6, -8, 0))
    ears(c, 1.2, roll=0)
    ears(c, 1.5, roll=30, pitch=-16)
    c.key(1.7, head=(18, -40, -6), waist=(9, -10, 0))
    c.hold(1.75, 2.4, head=(18, -40, -6), waist=(9, -10, 0))
    ears(c, 2.4, roll=30, pitch=-16)
    c.key(2.9, head=(22, -20, 0), waist=(4, -4, 0))
    ears(c, 3.0, roll=-14, pitch=10)
    c.key(3.5, head=(24, -6, 0), waist=(5, 0, 0))
    c.key(4.0, head=(6, 0, 0), waist=(1, 0, 0))
    ears(c, 4.0, roll=-6, pitch=4)
    c.key(4.6)


@clip("big_yawn", "Big yawn and a stretch", length=4.2, weight=1)
def _(c):
    c.key(0.5, head=(6, 0, 0), ra=(6, 0, 0), la=(6, 0, 0))
    c.key(1.2, head=(-34, 0, 0), waist=(-8, 0, 0), ra=(-14, 0, 150), la=(-14, 0, 150))
    ears(c, 1.2, roll=-16, pitch=28)
    c.key(2.0, head=(-38, 0, 4), waist=(-10, 0, 0), ra=(-16, 0, 162), la=(-16, 0, 162))
    ears(c, 2.0, roll=-18, pitch=32)
    c.key(2.5, head=(4, 0, 0), waist=(2, 0, 0), ra=(-4, 0, 40), la=(-4, 0, 40))
    c.wobble(2.6, 3.3, 5, "head", 9, base=(4, 0, 0), axis=2, decay=0.6)
    c.wobble(2.6, 3.3, 5, "left_ear", 18, axis=2, decay=0.5)
    c.wobble(2.6, 3.3, 5, "right_ear", 18, axis=2, decay=0.5)
    c.key(3.3, ra=(0, 0, 0), la=(0, 0, 0))
    c.key(4.2)


@clip("impatient_tap", "Taps a foot waiting for someone late", length=4.2, weight=1, require=["adult"])
def _(c):
    c.key(0.4, head=(-4, 18, 0), waist=(0, 4, 0))
    for i, t in enumerate((0.6, 1.0, 1.4, 1.8)):
        c.key(t, rl=(0, 0, 0))
        c.key(t + 0.14, rl=(-12, 0, 2))
        c.key(t + 0.3, rl=(0, 0, 0))
    flick(c, 1.0, side="right", amp=16)
    c.key(1.9, head=(-6, -22, -4), waist=(0, -4, 0))
    flick(c, 2.0, side="left", amp=16)
    c.key(2.5, head=(-6, -24, -4))
    c.key(2.9, head=(16, 0, 0), waist=(4, 0, 0), la=(6, 0, 0), ra=(6, 0, 0))
    ears(c, 2.9, roll=-14, pitch=6)
    c.key(3.4, head=(-4, 0, 0), waist=(0, 0, 0), la=(0, 0, 0), ra=(0, 0, 0))
    ears(c, 3.5, roll=0)
    c.key(4.2)


@clip("polish_band", "Polishes a gold armband on the belly", length=4.8, weight=2, require=["adult"], mirror="never")
def _(c):
    c.key(0.4, la=(-30, -24, 0), head=(22, -6, 0))
    ears(c, 0.4, roll=-4, pitch=-6)
    n = 10
    for i in range(n + 1):
        t = 0.5 + 2.0 * i / n
        a = i / n * 3 * math.tau
        c.key(t, la=(-32 + 6 * math.sin(a), -26 + 8 * math.cos(a), 0), head=(22 + 2 * math.sin(a), -6, 0))
    c.key(2.9, la=(-60, -30, 0), head=(6, -6, 0))
    c.key(3.3, la=(-100, -28, 6), head=(-8, -12, -6))
    ears(c, 3.3, roll=34, pitch=-10)
    c.key(3.8, la=(-104, -30, 8), head=(-10, -14, -8))
    snort(c, 3.85, base=(-10, -14, -8), amp=6)
    c.key(4.3, la=(-20, -10, 0), head=(-2, 0, 0))
    ears(c, 4.3, roll=8)
    c.key(4.8)


@clip("hoard_glance", "Glances over a shoulder and guards the pouch", length=4.4, weight=1.5, require=["adult"],
      mirror="never")
def _(c):
    c.key(0.3, la=(-24, -18, 0), head=(0, 0, 0))
    c.key(0.55, head=(-4, 58, 0), waist=(0, 14, 0))
    c.key(0.65, head=(-4, 64, 0), waist=(0, 15, 0))
    ears(c, 0.55, roll=-12, pitch=26)
    c.hold(0.8, 1.5, head=(2, 60, 4), waist=(0, 14, 0), la=(-26, -20, 0))
    c.key(1.9, head=(2, -54, -4), waist=(0, -12, 0))
    c.key(2.0, head=(2, -58, -4), waist=(0, -13, 0))
    ears(c, 1.9, roll=-12, pitch=26)
    c.hold(2.1, 2.8, head=(4, -55, -2), waist=(0, -12, 0), la=(-26, -20, 0))
    c.key(3.2, head=(14, -4, 0), waist=(2, 0, 0))
    ears(c, 3.3, roll=10)
    c.key(3.45, la=(-16, -14, 0))
    c.key(3.6, la=(-30, -18, 0))
    c.key(3.75, la=(-16, -14, 0))
    c.key(4.4)


@clip("ash_shake", "Shakes the ash off like a hog", length=2.6, weight=1)
def _(c):
    c.key(0.25, head=(10, 0, 0))
    ears(c, 0.25, roll=-8, pitch=10)
    c.wobble(0.35, 1.45, 6, "head", 20, base=(8, 0, 0), axis=2, decay=0.5)
    c.wobble(0.38, 1.5, 6, "left_ear", 30, axis=2, decay=0.4)
    c.wobble(0.33, 1.5, 6, "right_ear", 30, axis=2, decay=0.4)
    c.wobble(0.35, 1.45, 6, "waist", 4, axis=2, decay=0.6)
    snort(c, 1.7, base=(0, 0, 0), amp=10)
    c.key(2.6)


@clip("coin_flip", "Flips a coin and catches it", length=3.6, weight=2, mirror="never")
def _(c):
    c.key(0.4, la=(-44, -16, 0), head=(14, -6, 0))
    c.key(0.62, la=(-36, -16, 0))
    c.key(0.78, la=(-76, -16, 0), head=(4, -6, 0))
    c.key(1.25, la=(-58, -16, 0), head=(-34, -4, 0))
    ears(c, 1.25, roll=24, pitch=-10)
    c.key(1.75, head=(4, -6, 0), la=(-54, -16, 0))
    c.key(1.88, la=(-40, -16, 0), head=(16, -6, 0))
    c.key(2.05, la=(-50, -16, 0), head=(20, -8, -6))
    ears(c, 2.0, roll=6)
    c.key(2.5, la=(-52, -16, 0), head=(22, -8, -8))
    c.key(2.8, head=(-6, 0, 4))
    flick(c, 2.8, amp=24, base=(0, 0, 10))
    c.key(3.1, la=(-16, -8, 0))
    c.key(3.6)


@clip("private_chuckle", "Chuckles at a private joke", length=3.2, weight=1)
def _(c):
    c.key(0.35, head=(12, 10, 0))
    c.wobble(0.45, 1.8, 7, "head", 5, base=(12, 10, 0), decay=0.3)
    c.wobble(0.45, 1.8, 7, "waist", 2.5, decay=0.3)
    c.wobble(0.5, 1.8, 7, "left_ear", 12, axis=2, decay=0.3)
    c.wobble(0.45, 1.8, 7, "right_ear", 12, axis=2, decay=0.3)
    c.key(2.15, head=(-18, 4, 6), waist=(-4, 0, 0))
    snort(c, 2.2, base=(-18, 4, 6), amp=7)
    c.key(3.2)
