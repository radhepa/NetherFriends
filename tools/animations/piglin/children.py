"""Piglin children: mischief, play fights and showing off. Babies carry nothing, so both arms are free.

The pack's waist sits at adult hip height, which is up in a baby's head, so these lean with the root instead.
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


@clip("child_raspberry", "Blows a raspberry", length=3.0, weight=2, require=["child"], mirror="free")
def _(c):
    c.key(0.35, root=(6, 0, 0), head=(12, 0, 0), la=(26, 0, 14), ra=(26, 0, 14))
    ears(c, 0.35, roll=20, pitch=-10)
    c.wobble(0.45, 1.5, 9, "head", 10, base=(12, 0, 0), axis=1)
    c.wobble(0.45, 1.5, 9, "left_ear", 18, base=(-10, 0, 20), axis=2)
    c.wobble(0.45, 1.5, 9, "right_ear", 18, base=(-10, 0, 20), axis=2)
    c.key(1.5, root=(6, 0, 0), la=(28, 0, 16), ra=(28, 0, 16))
    c.key(1.9, root=(-4, 0, 0), head=(-16, 0, 6), la=(-10, 0, 20), ra=(-10, 0, 20))
    c.wobble(1.95, 2.6, 7, "head", 5, base=(-16, 0, 6), decay=0.4)
    c.key(3.0)


@clip("child_hop", "Hops about for the fun of it", length=3.0, weight=2, require=["child"], mirror="free")
def _(c):
    for i, t in enumerate((0.3, 1.0, 1.7)):
        c.key(t, root_pos=(0, 0.6, 0), la=(16, 0, 8), ra=(16, 0, 8), rl=(0, 0, 0), ll=(0, 0, 0))
        c.key(t + 0.22, root_pos=(0, -2.6, 0), la=(-70, 0, 30), ra=(-70, 0, 30), rl=(-14, 0, 0), ll=(-8, 0, 0),
              head=(-8, 0, 8 if i % 2 else -8))
        c.key(t + 0.45, root_pos=(0, 0.4, 0), la=(6, 0, 10), ra=(6, 0, 10), rl=(0, 0, 0), ll=(0, 0, 0))
        flick(c, t + 0.45, amp=26)
    c.key(2.4, root_pos=(0, 0, 0), head=(0, 0, 0))
    c.key(3.0)


@clip("child_sneak", "Tiptoes up to snatch something shiny", length=4.4, weight=2, require=["child"],
      mirror="free")
def _(c):
    c.key(0.4, root=(10, 0, 0), root_pos=(0, 0.5, 0), la=(30, 0, 10), ra=(30, 0, 10), head=(-8, 0, 0))
    ears(c, 0.4, roll=-10, pitch=18)
    c.cycle(0.5, 2.3, 0.6, {"rl": (-12, 0, 0), "ll": (8, 0, 0)}, {"rl": (8, 0, 0), "ll": (-12, 0, 0)})
    c.key(1.1, head=(-8, -32, 0))
    c.key(1.8, head=(-8, 30, 0))
    c.key(2.3, head=(-6, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0))
    c.key(2.55, ra=(-92, -10, 0), root=(14, 0, 0))
    c.key(2.8, ra=(-30, -20, 0), root=(2, 0, 0), root_pos=(0, 0, 0))
    ears(c, 2.8, roll=30, pitch=-10)
    c.key(3.0, head=(-14, 0, 8), la=(0, 0, 6))
    c.wobble(3.05, 3.8, 7, "head", 5, base=(-14, 0, 8), decay=0.4)
    c.key(4.4)


@clip("child_spin", "Spins round with arms out until dizzy", length=3.2, weight=1.5, require=["child"],
      mirror="free")
def _(c):
    c.key(0.25, la=(-6, 0, 30), ra=(-6, 0, 30), root=(0, -12, 0))
    c.key(0.6, la=(-6, 0, 84), ra=(-6, 0, 84), root=(0, 70, 0))
    c.key(0.95, root=(0, 190, 0))
    c.key(1.3, root=(0, 300, 0))
    ears(c, 0.6, roll=40)
    ears(c, 1.3, roll=44)
    c.key(1.55, root=(0, 360, 0), la=(-6, 0, 70), ra=(-6, 0, 70))
    c.key(1.5501, root=(0, 0, 0))
    c.wobble(1.6, 2.7, 3, "root", 7, axis=2, decay=0.5)
    c.wobble(1.6, 2.7, 3, "head", 12, axis=2, decay=0.4)
    c.wobble(1.6, 2.7, 3, "left_ear", 20, axis=2, decay=0.4)
    c.wobble(1.65, 2.7, 3, "right_ear", 20, axis=2, decay=0.4)
    c.key(2.2, la=(-4, 0, 30), ra=(-4, 0, 30))
    c.key(3.2)


@clip("child_play_fight", "Play-fights an invisible hoglin", length=3.8, weight=2, require=["child"], mirror="free")
def _(c):
    c.key(0.3, ra=(-160, 10, 0), la=(-10, 0, 40), root=(-4, 0, 0), head=(-4, 0, 0))
    ears(c, 0.3, roll=-12, pitch=24)
    c.key(0.55, ra=(-40, -20, 0), root=(8, 0, 0), root_pos=(0, 0, -1))
    c.key(0.8, ra=(-60, -16, 0), root=(4, 0, 0), root_pos=(0, 0, 0))
    c.key(1.1, ra=(-150, 30, 0), root=(-2, 10, 0))
    c.key(1.35, ra=(-50, -30, 0), root=(8, -10, 0), root_pos=(0, 0, -1), head=(4, -10, 0))
    c.key(1.6, ra=(-62, -20, 0), root=(3, 0, 0), root_pos=(0, 0, 0), head=(0, 0, 0))
    c.key(2.0, ra=(-100, 0, 0), root=(10, 0, 0), root_pos=(0, 0, -1.5), la=(-20, 0, 50))
    c.key(2.4, ra=(-20, 0, 150), la=(-20, 0, 120), root=(-4, 0, 0), root_pos=(0, -1.8, 0), head=(-18, 0, 0))
    ears(c, 2.4, roll=36, pitch=-10)
    c.key(2.65, root_pos=(0, 0.3, 0))
    c.key(2.8, root_pos=(0, 0, 0))
    c.key(3.8)


@clip("child_fidget", "Swings the arms and rocks on the feet", length=3.6, weight=1.5, require=["child"],
      mirror="free")
def _(c):
    c.cycle(0.2, 2.8, 0.8, {"la": (-26, 0, 6), "ra": (22, 0, 6), "root": (3, 0, 0)},
            {"la": (22, 0, 6), "ra": (-26, 0, 6), "root": (-3, 0, 0)})
    c.key(1.0, head=(-4, -24, 0))
    c.key(1.9, head=(-4, 22, 6))
    flick(c, 2.1, side="left", amp=22)
    c.key(2.8, head=(0, 0, 0))
    snort(c, 2.9, amp=6)
    c.key(3.6)
