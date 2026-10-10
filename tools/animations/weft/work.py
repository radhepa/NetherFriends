"""Weft work: every trade, the lower pair usually at the fine work while the upper pair holds, beats or reaches.

The shuttle rides in the right lower hand, so shuttle work never mirrors for the left-handed."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _weft import click, tap, twist, twist_to, HIP, HIP_FOREARM, hang_all  # noqa: E402

# the loom: upper pair on the beater (out / pulled in), lower pair passing the shuttle across the shed
BEAT_OUT = dict(ra=(-46, -6, 0), rf=(-44, 0, 0), la=(-46, -6, 0), lf=(-44, 0, 0))
BEAT_IN = dict(ra=(-34, -6, 0), rf=(-78, 0, 0), la=(-34, -6, 0), lf=(-78, 0, 0))
SHED_R = dict(rla=(-50, 8, 8), rlf=(-30, 0, 0), lla=(-50, -30, 0), llf=(-36, 0, 0))   # shuttle on the right
SHED_L = dict(rla=(-55, -40, 0), rlf=(-34, 0, 0), lla=(-50, 6, 6), llf=(-30, 0, 0))   # passed to the left


@clip("weaver_loom", "Works the loom", length=6.4, weight=5, require=["adult", "job:weaver"], mirror="never")
def _(c):
    # lower pair throws the shuttle through the shed and back; after each pass the upper pair beats the weft home
    c.key(0.6, waist=(6, 0, 0), head=(14, 6, 0), **BEAT_OUT, **SHED_R)
    t = 0.6
    for i in range(4):
        shed = SHED_L if i % 2 == 0 else SHED_R
        c.key(t + 0.7, head=(15, -6 if i % 2 == 0 else 6, 0), **shed)
        c.key(t + 0.7, **BEAT_OUT)
        c.key(t + 1.0, **BEAT_IN)
        c.key(t + 1.15, ra=(-36, -6, 0), rf=(-74, 0, 0), la=(-36, -6, 0), lf=(-74, 0, 0))
        c.key(t + 1.35, **BEAT_OUT)
        # a treadle under the long feet changes the shed
        foot = "rft" if i % 2 == 0 else "lft"
        c.key(t + 0.15, **{foot: (0, 0, 0)})
        c.key(t + 0.35, **{foot: (10, 0, 0)})
        c.key(t + 0.7, **{foot: (0, 0, 0)})
        t += 1.35
    c.key(t, waist=(6, 0, 0))
    click(c, t - 0.4, t + 0.2, base=(12, 0, 0), rate=6, amp=3)
    c.key(6.4)


@clip("weaver_pattern", "Counts the pattern", length=5.8, weight=4, require=["adult", "job:weaver"], mirror="never")
def _(c):
    # the upper left holds the pattern up to read; the upper right counts threads in the air, tick by tick;
    # the lower pair never stops its small steady weave
    c.key(0.6, la=(-78, -14, 0), lf=(-62, 0, 0), ra=(-70, 10, 0), rf=(-40, 0, 0), head=(-4, -12, 0))
    c.hold(0.6, 5.0, la=(-78, -14, 0), lf=(-62, 0, 0))
    c.cycle(0.4, 5.2, 0.9, {"rla": (-34, -10, 0), "rlf": (-28, 0, 0), "lla": (-24, -16, 0), "llf": (-22, 0, 0)},
            {"rla": (-26, -22, 0), "rlf": (-22, 0, 0), "lla": (-32, -6, 0), "llf": (-30, 0, 0)})
    # counting across: four ticks moving left to right, the head following each
    for i in range(5):
        t = 1.1 + i * 0.5
        yaw = 12 - i * 9
        c.key(t, ra=(-74, yaw, 0), rf=(-40, 0, 0), head=(0, yaw * 0.6, 0))
        c.key(t + 0.18, rf=(-52, 0, 0))
        c.key(t + 0.3, rf=(-40, 0, 0))
    c.key(3.8, head=(-4, -14, 4))                  # back to the pattern: yes
    click(c, 3.9, 4.6, base=(-4, -14, 4), rate=6, amp=3)
    c.key(4.6, ra=(-60, -14, 0), rf=(-48, 0, 0))
    c.key(5.8)


@clip("spinner_draw", "Draws a thread", length=6.4, weight=5, require=["adult", "job:spinner"], mirror="never")
def _(c):
    # the spindle (the shuttle) hangs from the lower right hand and is flicked to spin; the upper left draws the
    # thread up and away, slow and even, the head rising with it; twice
    for k, t in enumerate((0.2, 3.2)):
        c.key(t, la=(-60, -10, 0), lf=(-50, 0, 0), ra=(-62, -16, 0), rf=(-58, 0, 0))
        c.key(t + 0.35, llf=(-60, 0, 14), rla=(-30, -8, 0), rlf=(-24, 0, 0))
        c.key(t + 0.55, llf=(-20, 0, -6), shuttle=(0, 0 if k == 0 else 180, 0))
        c.key(t + 1.2, shuttle=(0, 180 if k == 0 else 0, 0), llf=(-6, 0, 0))
        c.key(t + 0.6, head=(-2, -6, 0))
        c.key(t + 2.2, la=(-150, 8, 4), lf=(-8, 0, 0), head=(-30, -18, 0))
        c.key(t + 2.45, la=(-154, 8, 4), head=(-32, -18, 3))
        c.key(t + 2.5, shuttle=(0, 180 if k == 0 else 0, 0))
    c.hold(0.55, 5.6, rla=(-30, -8, 0), rlf=(-24, 0, 0))
    c.key(3.1, shuttle=(0, 180, 0))
    click(c, 5.65, 6.1, base=(-20, -10, 3), rate=6, amp=2.5)
    c.key(6.4)


@clip("spinner_wind", "Winds a skein", length=5.6, weight=4, require=["adult", "job:spinner"], mirror="never")
def _(c):
    # the upper pair holds a skein out on spread fingers; the lower right loops the thread round and round the
    # ball held in the lower left; the head nods along with the loops
    c.key(0.6, ra=(-72, 16, 8), rf=(-40, 0, 0), la=(-72, 16, 8), lf=(-40, 0, 0),
          lla=(-44, -16, 0), llf=(-30, 0, 0), head=(12, 0, 0))
    c.hold(0.6, 4.8, ra=(-72, 16, 8), la=(-72, 16, 8), lla=(-44, -16, 0), llf=(-30, 0, 0))
    loop = [(-30, -6), (-46, -20), (-58, -8), (-44, 6)]
    t = 0.6
    i = 0
    while t < 4.6:
        p, y = loop[i % 4]
        c.key(t, rla=(p, y, 0), rlf=(-34, 0, 0), head=(12 + (2 if i % 2 else -1), 0, 0))
        t += 0.25
        i += 1
    # the skein hands give a little with the pull
    c.wobble(0.8, 4.6, 2, "rf", 4, base=(-40, 0, 0))
    c.wobble(0.8, 4.6, 2, "lf", 4, base=(-40, 0, 0))
    c.key(4.8, rla=(-40, -8, 0), rlf=(-30, 0, 0))
    c.key(5.6)


@clip("clothkeeper_fold", "Folds the Long Cloth", length=6.4, weight=5, require=["adult", "job:clothkeeper"])
def _(c):
    # the upper pair holds the cloth's edges out wide, brings them together; the lower pair takes the fold;
    # a bow to lay it down, and a last smoothing stroke
    c.key(0.7, ra=(-80, 34, 12), rf=(-14, 0, 0), la=(-80, 34, 12), lf=(-14, 0, 0), head=(4, 0, 0))
    c.key(0.9, ra=(-84, 38, 14), la=(-84, 38, 14))
    c.key(1.9, ra=(-80, -14, 0), rf=(-30, 0, 0), la=(-80, -14, 0), lf=(-30, 0, 0),
          rla=(-60, -18, 0), rlf=(-20, 0, 0), lla=(-60, -18, 0), llf=(-20, 0, 0), head=(14, 0, 0))
    c.key(2.4, rla=(-70, -10, 0), rlf=(-10, 0, 0), lla=(-70, -10, 0), llf=(-10, 0, 0))
    # bow to lay it on the table
    c.key(3.3, waist=(26, 0, 0), ra=(-30, -10, 0), rf=(-30, 0, 0), la=(-30, -10, 0), lf=(-30, 0, 0),
          rla=(-46, -10, 0), lla=(-46, -10, 0), head=(10, 0, 0))
    c.key(3.5, waist=(28, 0, 0))
    c.key(4.0, waist=(24, 0, 0), rla=(-40, 10, 6), lla=(-40, -24, 0))
    c.key(4.6, rla=(-40, -26, 0), head=(12, -8, 0))
    c.key(4.9, waist=(22, 0, 0))
    c.key(5.6, waist=(0, 0, 0), ra=(0, 0, 0), rf=(0, 0, 0), la=(0, 0, 0), lf=(0, 0, 0), rla=(-10, -6, 0),
          lla=(-10, -6, 0), head=(-6, 0, 3))
    c.key(6.4)


@clip("clothkeeper_inspect", "Holds the cloth to the light", length=5.6, weight=3,
      require=["adult", "job:clothkeeper"])
def _(c):
    # the upper pair lifts a length of cloth high against the light; the head reads it edge to edge, clicking;
    # the lower right rubs the hem between finger and thumb
    # (overhead, a small roll keeps the arms clear of the head and the hands a cloth's width apart)
    c.key(0.8, ra=(-140, 14, 6), rf=(-16, 0, 0), la=(-140, 14, 6), lf=(-16, 0, 0), waist=(-6, 0, 0),
          head=(-20, 14, 0), rla=(-40, -14, 0), rlf=(-30, 0, 0))
    c.key(1.0, ra=(-144, 14, 6), la=(-144, 14, 6))
    c.hold(1.0, 4.4, ra=(-144, 14, 6), la=(-144, 14, 6), rf=(-16, 0, 0), lf=(-16, 0, 0), waist=(-6, 0, 0),
           rla=(-40, -14, 0))
    c.key(2.4, head=(-22, -2, 3))
    c.key(3.6, head=(-20, -16, -2))
    click(c, 3.6, 4.3, base=(-20, -16, -2), rate=5, amp=3)
    c.wobble(1.0, 3.4, 4, "rlf", 6, base=(-30, 0, 0))
    c.key(4.4, head=(-10, 0, 0))
    c.key(5.6)


@clip("canopy_reach", "Tends the canopy overhead", length=6.0, weight=5, require=["adult", "job:canopy_tender"])
def _(c):
    # up on the long toes, the upper pair reaches into the canopy and pinches off something; the lower pair holds
    # the basket at the hip; down, and the find goes in the basket
    c.key(0.4, rla=(-40, -22, 0), rlf=(-46, 0, 0), lla=(-40, -22, 0), llf=(-46, 0, 0), waist=(4, 0, 0), head=(4, 0, 0))
    c.key(1.2, ra=(-168, -4, 6), rf=(-14, 0, 0), la=(-160, -4, 0), lf=(-20, 0, 0), waist=(-8, 0, 0),
          head=(-30, 0, 0), root_pos=(0, -1.2, 0), rft=(-18, 0, 0), lft=(-18, 0, 0))
    c.key(1.4, ra=(-172, -4, 6), head=(-31, 4, 0))
    c.hold(1.4, 3.4, la=(-160, -4, 0), lf=(-20, 0, 0), waist=(-8, 0, 0), root_pos=(0, -1.2, 0),
           rft=(-18, 0, 0), lft=(-18, 0, 0))
    c.hold(1.4, 5.0, rla=(-40, -22, 0), rlf=(-46, 0, 0), lla=(-40, -22, 0), llf=(-46, 0, 0))
    # fingers work at a stem: the forearm pinches and twists
    c.wobble(1.4, 2.8, 4, "rf", 12, base=(-14, 0, 0))
    c.key(2.0, head=(-31, -4, 4))
    c.key(2.8, ra=(-172, -4, 6), head=(-30, 2, 0))
    click(c, 2.8, 3.3, base=(-30, 2, 0), rate=6, amp=3)
    # bring it down to the basket the lower pair still holds, and drop it in from above
    c.key(4.2, ra=(-46, -24, 8), rf=(-56, 0, 0), la=(-10, 0, 4), lf=(-10, 0, 0), waist=(10, 0, 0),
          head=(18, 8, 0), root_pos=(0, 0, 0), rft=(0, 0, 0), lft=(0, 0, 0))
    c.key(4.6, ra=(-40, -24, 8), rf=(-44, 0, 0), waist=(12, 0, 0))
    c.key(4.9, ra=(-46, -24, 8), rf=(-56, 0, 0))
    c.key(6.0)


@clip("canopy_tug", "Tests a canopy strand", length=5.0, weight=3, require=["adult", "job:canopy_tender"])
def _(c):
    # the upper right hand takes a hanging strand high up and gives it two firm tugs, leaning back on each;
    # the upper left steadies higher still; the lower pair hold the coil of strand they carry
    c.key(0.6, ra=(-150, -6, 4), rf=(-24, 0, 0), la=(-164, -4, -4), lf=(-12, 0, 0), head=(-26, 0, 0),
          rla=(-34, -24, 0), rlf=(-50, 0, 0), lla=(-34, -24, 0), llf=(-50, 0, 0))
    c.hold(0.6, 4.0, la=(-164, -4, -4), lf=(-12, 0, 0), rla=(-34, -24, 0), rlf=(-50, 0, 0),
           lla=(-34, -24, 0), llf=(-50, 0, 0))
    for t in (1.1, 2.1):
        c.key(t, ra=(-150, -6, 4), rf=(-24, 0, 0), root=(0, 0, 0), head=(-26, 0, 0))
        c.key(t + 0.2, ra=(-128, -6, 4), rf=(-44, 0, 0), root=(-3, 0, 0), head=(-22, 0, 0))
        c.key(t + 0.45, ra=(-132, -6, 4), rf=(-40, 0, 0), root=(-2.4, 0, 0))
        c.key(t + 0.8, ra=(-150, -6, 4), rf=(-24, 0, 0), root=(0, 0, 0), head=(-26, 0, 0))
    c.key(3.2, head=(-24, 6, 4))
    click(c, 3.2, 3.8, base=(-24, 6, 4), rate=6, amp=3)
    c.key(4.0, ra=(-60, 0, 4), rf=(-20, 0, 0), head=(-6, 0, 0))
    c.key(5.0)


@clip("dyer_stir", "Stirs the dye vat", length=6.0, weight=5, require=["adult", "job:dyer"], mirror="never")
def _(c):
    # bent over the vat, both lower hands on the paddle, stirring round; the upper right fans the fumes away,
    # the upper left braced on the hip; a sniff, a click
    twist_to(c, 0.0, 0.6, "la", (0, 0, 0), HIP)
    c.key(0.6, waist=(18, 0, 0), head=(14, 0, 0), lf=(HIP_FOREARM, 0, 0), ra=(-40, 20, 20), rf=(-50, 0, 0))
    c.hold(0.6, 4.6, waist=(18, 0, 0), la=twist(*HIP), lf=(HIP_FOREARM, 0, 0))
    twist_to(c, 4.6, 5.4, "la", HIP, (0, 0, 0))
    ring = [(-50, -10), (-62, -24), (-50, -38), (-38, -24)]
    t, i = 0.6, 0
    while t < 4.4:
        p, y = ring[i % 4]
        c.key(t, rla=(p, y + 14, 0), rlf=(-34, 0, 0), lla=(p, y, 0), llf=(-34, 0, 0))
        t += 0.3
        i += 1
    c.wobble(0.8, 3.4, 2.5, "ra", 14, base=(-40, 20, 20), axis=1)
    c.key(3.4, ra=(-40, 20, 20), rf=(-50, 0, 0), head=(16, 0, 0))
    c.key(3.9, head=(-4, 0, 0), waist=(12, 0, 0))
    c.key(4.1, head=(0, 0, 0))
    click(c, 4.1, 4.6, base=(0, 0, 0), rate=6, amp=3)
    c.key(6.0)


@clip("dyer_wring", "Wrings out dyed cloth", length=5.2, weight=4, require=["adult", "job:dyer"])
def _(c):
    # the upper pair twists the cloth in opposite directions, the lower pair holds the hanging tail; a shake, and
    # the head pulls back from the drips
    c.key(0.6, ra=(-60, -10, 0), rf=(-50, 0, 0), la=(-56, -10, 0), lf=(-40, 0, 0),
          rla=(-50, -14, 0), rlf=(-20, 0, 0), lla=(-50, -14, 0), llf=(-20, 0, 0), head=(14, 0, 0))
    c.hold(0.6, 3.4, rla=(-50, -14, 0), rlf=(-20, 0, 0), lla=(-50, -14, 0), llf=(-20, 0, 0))
    for t in (1.0, 1.8, 2.6):
        c.key(t, ra=(-60, -10, 0), rf=(-50, 0, 0), la=(-56, -10, 0), lf=(-40, 0, 0))
        c.key(t + 0.45, ra=(-64, -16, 0), rf=(-54, 0, 0), la=(-54, 4, 10), lf=(-36, 0, 0))
    c.key(1.2, head=(16, 0, 5))
    c.key(2.4, head=(18, 0, -4), waist=(4, 0, 0))
    c.key(3.2, head=(-10, -10, 0), waist=(-6, 0, 0))     # a drip: pulls back
    c.wobble(3.4, 4.2, 6, "rla", 10, base=(-50, -14, 0))
    c.wobble(3.4, 4.2, 6, "lla", 10, base=(-50, -14, 0))
    c.key(3.6, ra=(-50, -8, 0), rf=(-40, 0, 0), la=(-50, -8, 0), lf=(-40, 0, 0), head=(6, 0, 0), waist=(0, 0, 0))
    c.key(5.2)


@clip("netcaster_cast", "Casts a net", length=5.6, weight=5, require=["adult", "job:netcaster"])
def _(c):
    # wind up with the body turned away, the upper pair swings the net back; fling it high and wide; the lower pair
    # pays out the line, then hauls it in hand over hand
    c.key(0.8, waist=(4, 24, 0), ra=(20, 10, 18), rf=(-30, 0, 0), la=(-36, -30, 16), lf=(-60, 0, 0),
          head=(4, -14, 0), rla=(-30, -10, 0), rlf=(-40, 0, 0), lla=(-30, -10, 0), llf=(-40, 0, 0),
          rl=(-4, 0, 0), rs=(10, 0, 0))
    c.key(1.0, waist=(5, 28, 0), ra=(26, 12, 20))
    c.key(1.35, waist=(-4, -12, 0), ra=(-130, 6, 24), rf=(-4, 0, 0), la=(-120, 20, 30), lf=(-4, 0, 0),
          head=(-14, 4, 0), rla=(-60, 0, 6), rlf=(-10, 0, 0), lla=(-56, 0, 6), llf=(-10, 0, 0),
          rl=(0, 0, 0), rs=(0, 0, 0))
    c.key(1.6, waist=(-2, -16, 0), ra=(-118, 8, 26), la=(-112, 20, 30), head=(-16, 0, 0))
    c.key(2.4, waist=(2, -6, 0), ra=(-40, 0, 6), rf=(-20, 0, 0), la=(-40, 0, 6), lf=(-20, 0, 0), head=(6, 0, 0))
    # haul in: lower hands alternate, pulling back to the hip
    c.cycle(2.4, 4.8, 0.6, {"rla": (-60, -6, 0), "rlf": (-20, 0, 0), "lla": (-10, 0, 0), "llf": (-60, 0, 0)},
            {"rla": (-10, 0, 0), "rlf": (-60, 0, 0), "lla": (-60, -6, 0), "llf": (-20, 0, 0)})
    c.key(4.8, waist=(2, 0, 0), head=(8, 0, 4))
    c.key(5.6)


@clip("netcaster_mend", "Mends a net", length=6.0, weight=4, require=["adult", "job:netcaster"])
def _(c):
    # the upper pair holds a torn stretch of net open; the lower pair ties the knot in the gap, quick and small;
    # the head bends close and clicks at the finished knot
    c.key(0.6, ra=(-62, 14, 14), rf=(-44, 0, 0), la=(-62, 14, 14), lf=(-44, 0, 0), head=(20, 0, 0), waist=(6, 0, 0))
    c.hold(0.6, 5.0, ra=(-62, 14, 14), rf=(-44, 0, 0), la=(-62, 14, 14), lf=(-44, 0, 0), waist=(6, 0, 0))
    c.key(0.8, rla=(-64, -16, 0), rlf=(-30, 0, 0), lla=(-64, -16, 0), llf=(-30, 0, 0))
    t = 1.0
    while t < 3.8:
        c.key(t, rla=(-70, -26, 0), rlf=(-40, 0, 10), lla=(-60, -8, 0), llf=(-22, 0, 0))
        c.key(t + 0.2, rla=(-60, -8, 0), rlf=(-22, 0, 0), lla=(-70, -26, 0), llf=(-40, 0, 10))
        t += 0.4
    c.key(4.0, rla=(-64, 4, 8), rlf=(-26, 0, 0), lla=(-64, 4, 8), llf=(-26, 0, 0))     # pull the knot tight
    c.key(4.15, rla=(-64, 8, 12), lla=(-64, 8, 12))
    c.key(1.6, head=(22, 4, 4))
    c.key(3.2, head=(22, -4, -3))
    c.key(4.2, head=(18, 0, 6))
    click(c, 4.2, 4.9, base=(18, 0, 6), rate=6, amp=3)
    c.key(5.0, rla=(-40, -10, 0), lla=(-40, -10, 0))
    c.key(6.0)


hang_all()
