"""Brutes: the vault guards. Slow, wide, heavy; ears pinned back. Their axe arm keeps the vanilla pose."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


@clip("brute_guard", "Stands guard and sweeps the hall with a glare", length=6.0, weight=4, require=["brute"],
      mirror="never")
def _(c):
    c.key(0.8, rl=(0, 0, 7), ll=(0, 0, 7), waist=(-5, 0, 0), head=(8, 0, 0), la=(0, 0, 14))
    ears(c, 0.8, roll=-10, pitch=22)
    c.key(2.2, head=(8, -34, 0), waist=(-5, -6, 0))
    c.hold(2.3, 2.8, head=(10, -36, 0), waist=(-5, -6, 0))
    c.key(4.2, head=(8, 32, 0), waist=(-5, 6, 0))
    c.hold(4.3, 4.8, head=(10, 34, 0), waist=(-5, 6, 0))
    snort(c, 4.85, base=(10, 34, 0), amp=7)
    c.key(5.3, head=(6, 0, 0), waist=(-3, 0, 0))
    ears(c, 5.3, roll=-10, pitch=22)
    c.key(6.0)


# the axe arm keeps the vanilla pose in game, so a two-handed grip on the haft never reads: the free hand counts
@clip("brute_count_hoard", "Counts the vault's gold with a jabbing finger", length=5.4, weight=3, require=["brute"],
      mirror="never")
def _(c):
    c.key(0.6, waist=(10, 0, 0), head=(18, 0, 0), la=(-40, 12, 8))
    ears(c, 0.6, roll=-8, pitch=16)
    # one pile, two, three, four: out to the left and across to the right, the snout following the finger
    for t, yaw in ((1.0, 16), (1.7, 2), (2.4, -14), (3.1, -28)):
        c.key(t, la=(-42, yaw, 6), head=(18, -yaw * 0.7, 0))
        c.key(t + 0.18, la=(-54, yaw, 6), head=(24, -yaw * 0.7, 0))
        c.key(t + 0.4, la=(-44, yaw, 6), head=(19, -yaw * 0.7, 0))
        flick(c, t + 0.18, amp=12, base=(16, 0, -8))
    # a suspicious squint back over the count, then a satisfied snort: it's all there
    c.key(3.8, head=(12, 10, 8), la=(-30, -10, 4))
    c.key(4.2, head=(14, -6, -6))
    snort(c, 4.4, base=(12, 0, 0), amp=8)
    ears(c, 4.6, roll=-8, pitch=16)
    c.key(4.8, waist=(3, 0, 0), head=(4, 0, 0), la=(-10, 0, 2))
    c.key(5.4)


@clip("brute_menace", "Looms forward and stamps", length=4.2, weight=3, require=["brute"], mirror="never")
def _(c):
    c.key(0.6, waist=(14, 0, 0), head=(-4, 0, 0), la=(-8, 0, 18), ra=(-8, 0, 18))
    ears(c, 0.6, roll=-14, pitch=28)
    snort(c, 0.9, base=(-4, 0, 0), amp=8)
    c.key(1.5, rl=(-24, 0, 0), waist=(10, 0, 0))
    c.key(1.7, rl=(4, 0, 0), waist=(16, 0, 0), root_pos=(0, 0.6, 0))
    c.key(1.85, rl=(0, 0, 0), root_pos=(0, 0, 0))
    snort(c, 1.8, base=(-6, 0, 0), amp=10)
    c.hold(2.2, 3.3, waist=(15, 0, 0), head=(-8, 0, 0), la=(-10, 0, 20), ra=(-10, 0, 20))
    c.key(3.3, waist=(15, 0, 0))
    ears(c, 3.3, roll=-14, pitch=28)
    c.key(4.2)


@clip("brute_chin_lift", "A curt chin lift", trigger="greet", length=2.4, weight=4, require=["brute"])
def _(c):
    c.key(0.5, head=(6, 0, 0))
    c.key(0.85, head=(-16, 0, 0), waist=(-3, 0, 0))
    ears(c, 0.85, roll=-8, pitch=16)
    c.hold(0.9, 1.5, head=(-16, 0, 0), waist=(-3, 0, 0))
    snort(c, 1.5, base=(-16, 0, 0), amp=6)
    c.key(2.4)
