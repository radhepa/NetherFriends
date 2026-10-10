"""Weft everyday idles: patient, precise, quietly smug about four arms; mandibles click while they think."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _weft import click, tap, twist, twist_to, behind_back, HIP, HIP_FOREARM, hang_all  # noqa: E402

# arms folded: upper pair across the chest, lower pair across the belly
# (the right forearm lies over the left: layered, not passing through each other)
UP_FOLD_R, UP_FOLD_L = twist(-42, -72), twist(-30, -88)
LO_FOLD_R, LO_FOLD_L = twist(-26, -64), twist(-16, -80)


@clip("mandible_click", "Mandible click", length=3.2, weight=2)
def _(c):
    # head dips a little and ticks side to side while the mandibles click; the shoulders barely move
    c.key(0.45, head=(7, -6, 0), root=(0, 0, 0.6))
    click(c, 0.6, 2.2, base=(6, -5, 4), rate=6, amp=3.5)
    c.key(2.55, head=(4, 3, -3))
    c.key(3.2)


@clip("chin_tap_think", "Chin tap think", length=4.6, weight=2, require=["adult"])
def _(c):
    # upper right hand to the mandibles, upper left cradles the elbow, lower pair folds: then a tap tap tap
    # (the hand stops just under the mandible: the elbow lifts out a little instead of the fingers sinking in)
    c.key(0.45, ra=(-40, -22, 12), rf=(-112, 0, 0), la=twist(-26, -84), lf=(-76, 0, 0),
          rla=LO_FOLD_R, rlf=(-34, 0, 0), lla=LO_FOLD_L, llf=(-34, 0, 0), head=(4, 0, 0))
    c.key(0.7, ra=(-38, -22, 12), rf=(-108, 0, 0), la=twist(-24, -80), lf=(-72, 0, 0),
          rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0), head=(6, 0, 7))
    click(c, 0.9, 2.1, base=(6, 0, 7), rate=5, amp=3)
    tap(c, 2.3, 3.3, "rf", (-108, 0, 0), 14, period=0.33)
    c.key(2.3, head=(5, 4, 4))
    c.key(3.3, head=(7, 2, 9))
    c.hold(3.3, 3.6, ra=(-38, -22, 12), la=twist(-24, -80), lf=(-72, 0, 0),
           rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0))
    c.key(4.6)


@clip("four_arm_stretch", "Four arm stretch", length=4.2, weight=2, require=["adult"])
def _(c):
    # a crouch of anticipation, then every arm reaches: upper pair overhead, lower pair out wide
    c.key(0.35, waist=(7, 0, 0), ra=(6, 0, 4), la=(6, 0, 4), head=(6, 0, 0), rla=(4, 0, 0), lla=(4, 0, 0))
    c.key(1.15, waist=(-12, 0, 0), ra=(-12, 0, 163), la=(-12, 0, 163), rf=(12, 0, 0), lf=(12, 0, 0),
          rla=(-6, 0, 68), lla=(-6, 0, 68), rlf=(42, 0, 0), llf=(42, 0, 0), head=(-26, 0, 0))
    c.key(1.4, waist=(-14, 0, 0), ra=(-12, 0, 168), la=(-12, 0, 168), rla=(-6, 0, 74), lla=(-6, 0, 74),
          head=(-28, 0, 0))
    c.key(2.1, waist=(-11, 0, 0), ra=(-12, 0, 162), la=(-12, 0, 162), rla=(-6, 0, 70), lla=(-6, 0, 70),
          head=(-22, 0, 3))
    # lower pair shakes out first, then the upper pair comes down
    c.wobble(2.1, 2.7, 6, "rlf", 14, base=(42, 0, 0))
    c.wobble(2.1, 2.7, 6, "llf", 14, base=(42, 0, 0))
    c.key(2.75, rla=(0, 0, 12), lla=(0, 0, 12), rlf=(10, 0, 0), llf=(10, 0, 0))
    c.key(3.3, waist=(2, 0, 0), ra=(4, 0, 10), la=(4, 0, 10), rf=(-6, 0, 0), lf=(-6, 0, 0), head=(4, 0, 0))
    c.key(4.2)


@clip("smug_fold", "Folds all four", length=4.4, weight=2, require=["adult"])
def _(c):
    # upper pair folds across the chest, lower pair across the belly; chin up, weight on one long leg
    c.key(0.5, ra=UP_FOLD_R, rf=(-92, 0, 0), la=UP_FOLD_L, lf=(-80, 0, 0),
          rla=LO_FOLD_R, rlf=(-38, 0, 0), lla=LO_FOLD_L, llf=(-34, 0, 0), head=(-4, 0, 0))
    c.key(0.75, rf=(-86, 0, 0), lf=(-76, 0, 0), rlf=(-34, 0, 0), llf=(-30, 0, 0), head=(-9, 6, -5),
          root=(0, 0, -1.5), rl=(-3, 0, 0), rs=(9, 0, 0))
    c.hold(0.75, 3.4, ra=UP_FOLD_R, la=UP_FOLD_L, lf=(-76, 0, 0), rla=LO_FOLD_R, lla=LO_FOLD_L,
           llf=(-30, 0, 0), root=(0, 0, -1.5), rl=(-3, 0, 0), rs=(9, 0, 0))
    c.key(2.0, head=(-11, 4, -7))
    # the lower right fingers drum on the left elbow, unhurried
    tap(c, 2.1, 2.9, "rlf", (-34, 0, 0), 10, period=0.27)
    c.key(3.4, head=(-8, 2, -4), rf=(-86, 0, 0), rlf=(-34, 0, 0))
    c.key(4.4)


@clip("ripple_flex", "Spidery finger ripple", length=3.6, weight=1.5, require=["adult"])
def _(c):
    # each forearm flexes in turn, upper right to lower left, and the head follows each hand
    c.key(0.4, ra=(-28, -10, 0), la=(-28, -10, 0), rla=(-18, 0, 8), lla=(-18, 0, 8), head=(14, 0, 0))
    c.key(0.55, rf=(-30, 0, 0))
    c.key(0.8, rf=(-62, 0, 8), head=(16, 10, 2))
    c.key(1.05, rf=(-36, 0, 0), lf=(-30, 0, 0))
    c.key(1.3, lf=(-62, 0, 8), head=(16, -10, -2))
    c.key(1.55, lf=(-36, 0, 0), rlf=(0, 0, 0))
    c.key(1.8, rlf=(-38, 0, 10), head=(24, 8, 0))
    c.key(2.05, rlf=(-6, 0, 0), llf=(0, 0, 0))
    c.key(2.3, llf=(-38, 0, 10), head=(24, -8, 0))
    c.key(2.55, llf=(-6, 0, 0), head=(10, 0, 4))
    c.hold(2.55, 2.9, ra=(-28, -10, 0), la=(-28, -10, 0), rla=(-18, 0, 8), lla=(-18, 0, 8),
           rf=(-36, 0, 0), lf=(-36, 0, 0))
    c.key(3.6)


@clip("shuttle_twirl", "Shuttle twirl", length=3.6, weight=2, require=["adult"], mirror="never")
def _(c):
    # the lower right hand lifts the shuttle, flips it end over end and back, and the head admires the trick
    c.key(0.5, rla=(-34, -12, 0), rlf=(-26, 0, 0), head=(16, 12, 0), ra=(0, 0, 8))
    c.key(0.8, rla=(-40, -14, 0), rlf=(-34, 0, 0), shuttle=(0, 0, 0), head=(18, 14, 2))
    c.key(1.25, shuttle=(180, 0, 0), rlf=(-26, 0, 0))
    c.key(1.4, shuttle=(186, 0, 0), rlf=(-30, 0, 0))
    c.key(1.55, shuttle=(180, 0, 0))
    c.hold(1.55, 2.0, rla=(-40, -14, 0), head=(16, 12, 5))
    c.key(2.45, shuttle=(0, 0, 0), rlf=(-40, 0, 0))
    c.key(2.6, shuttle=(-6, 0, 0), rlf=(-34, 0, 0))
    c.key(2.75, shuttle=(0, 0, 0), head=(4, 6, -4))
    click(c, 2.75, 3.15, base=(4, 6, -4), rate=6, amp=3)
    c.hold(2.75, 3.0, rla=(-36, -12, 0), ra=(0, 0, 8))
    c.key(3.6)


@clip("weight_shift", "Long leg weight shift", length=5.0, weight=1.5)
def _(c):
    # weight onto the right leg, the left knee softens; then over to the other side; the head counters a little
    c.key(0.9, root=(0, 0, -2.2), root_pos=(-0.6, 0, 0), ll=(-5, 0, 0), ls=(11, 0, 0), lft=(-6, 0, 0), head=(0, 0, 2))
    c.key(1.1, root=(0, 0, -2.5), root_pos=(-0.7, 0, 0), head=(0, 0, 2.5))
    c.hold(1.1, 2.0, root=(0, 0, -2.5), root_pos=(-0.7, 0, 0), ll=(-5, 0, 0), ls=(11, 0, 0), lft=(-6, 0, 0))
    c.key(2.9, root=(0, 0, 2.2), root_pos=(0.6, 0, 0), rl=(-5, 0, 0), rs=(11, 0, 0), rft=(-6, 0, 0),
          ll=(0, 0, 0), ls=(0, 0, 0), lft=(0, 0, 0), head=(2, 0, -2))
    c.key(3.1, root=(0, 0, 2.5), root_pos=(0.7, 0, 0), head=(2, 0, -2.5))
    c.hold(3.1, 4.0, root=(0, 0, 2.5), root_pos=(0.7, 0, 0), rl=(-5, 0, 0), rs=(11, 0, 0), rft=(-6, 0, 0))
    c.key(5.0)


@clip("canopy_point", "Points up at the canopy", length=4.4, weight=1.5)
def _(c):
    # something up in the Warped canopy: head tips back and searches, finds it off to the right, and a long finger
    # points where the head looks; a click of approval
    # (overhead, roll tips the arm inward, so the point leans out with a little negative roll: clear of a child's head)
    c.key(0.6, head=(-24, -8, 0), root=(-1.5, 0, 0))
    c.key(1.3, head=(-28, 6, 3), root=(-2, 0, 0))
    c.key(1.7, ra=(-120, 20, -10), rf=(-12, 0, 0), head=(-28, 16, 0))
    c.key(1.9, ra=(-128, 20, -10), rf=(-6, 0, 0))
    c.hold(1.9, 2.9, ra=(-126, 20, -10), rf=(-6, 0, 0), root=(-2, 0, 0))
    click(c, 2.1, 2.8, base=(-28, 16, 0), rate=6, amp=3)
    c.key(3.4, ra=(-30, 0, 4), rf=(-20, 0, 0), head=(-10, 4, 0))
    c.key(4.4)


@clip("patient_tap", "Patient foot tap", length=4.2, weight=1.5)
def _(c):
    # hands behind the back, one foot ticks the toe in a slow even measure, the head tilts and waits
    behind_back(c, 0.0, 0.7, 3.5, 4.2)
    c.key(0.7, head=(4, 0, 6), rl=(-4, 0, 0))
    c.hold(0.7, 3.5, rl=(-4, 0, 0))
    tap(c, 0.9, 3.0, "rft", (0, 0, 0), -22, period=0.7)
    c.key(1.9, head=(6, 2, 9))
    c.key(3.0, head=(3, -3, 4))
    c.key(4.2)


@clip("weave_hands_on_hips", "Weaves with no hands", length=5.0, weight=2, require=["adult"], mirror="never")
def _(c):
    # the upper hands planted on the hips, chin up; below them the lower pair, quite on its own, keeps a little
    # weave going; a glance down to admire it, and a click: who needs hands
    twist_to(c, 0.0, 0.6, "ra", (0, 0, 0), HIP)
    twist_to(c, 0.0, 0.6, "la", (0, 0, 0), HIP)
    c.key(0.6, rf=(HIP_FOREARM, 0, 0), lf=(HIP_FOREARM, 0, 0), head=(-6, 0, 0))
    c.hold(0.6, 4.3, ra=twist(*HIP), la=twist(*HIP), rf=(HIP_FOREARM, 0, 0), lf=(HIP_FOREARM, 0, 0))
    c.cycle(0.6, 3.8, 0.8, {"rla": (-30, -10, 0), "rlf": (-30, 0, 0), "lla": (-18, -4, 0), "llf": (-18, 0, 0)},
            {"rla": (-18, -4, 0), "rlf": (-18, 0, 0), "lla": (-30, -10, 0), "llf": (-30, 0, 0)})
    c.key(1.6, head=(-9, 6, -5))
    c.key(2.6, head=(14, 2, 3))
    c.key(3.8, head=(12, -2, -3))
    click(c, 3.8, 4.3, base=(12, -2, -3), rate=6, amp=2.5)
    twist_to(c, 4.3, 5.0, "ra", HIP, (0, 0, 0))
    twist_to(c, 4.3, 5.0, "la", HIP, (0, 0, 0))
    c.key(5.0)


@clip("scratch_two", "Two scratches at once", length=3.8, weight=1.5, require=["adult"])
def _(c):
    # the upper right hand scratches the back of the head while the lower left scratches the thigh: why choose
    # (the elbow points up and out, so the fingers work at the side of the head instead of sinking into it)
    c.key(0.5, ra=(-132, 10, -8), rf=(-100, 0, 0), lla=(10, 0, 9), llf=(40, 0, 0), head=(10, 0, -6))
    c.key(0.75, ra=(-140, 10, -10), rf=(-110, 0, 0), lla=(12, 0, 10), llf=(45, 0, 0), head=(12, 0, -8))
    c.wobble(0.75, 2.6, 5, "rf", 10, base=(-110, 0, 0))
    c.wobble(0.85, 2.7, 6.5, "llf", 9, base=(45, 0, 0))
    c.hold(0.75, 2.7, ra=(-140, 10, -10), lla=(12, 0, 10))
    c.key(1.6, head=(14, 4, -10))
    c.key(2.7, head=(6, 0, -4))
    c.key(3.2, ra=(-20, 0, 6), rf=(-20, 0, 0), lla=(2, 0, 3), llf=(8, 0, 0), head=(0, 0, 0))
    c.key(3.8)


@clip("silkgrub_pet", "Pets a silkgrub", length=5.2, weight=2, require=["adult"])
def _(c):
    # a silkgrub rides the upper left forearm; the upper right hand strokes it from elbow to wrist, twice, fondly
    # (the left forearm comes in across the chest so the stroking arm can reach it without crossing the body)
    c.key(0.6, la=(-30, -26, 0), lf=(-66, 0, 0), head=(20, -10, 0),
          rla=(-14, -20, 0), rlf=(-26, 0, 0), lla=(-14, -20, 0), llf=(-26, 0, 0))
    c.hold(0.6, 4.4, la=(-30, -26, 0), lf=(-66, 0, 0), rla=(-14, -20, 0), rlf=(-26, 0, 0),
           lla=(-14, -20, 0), llf=(-26, 0, 0))
    for i, t in enumerate((0.9, 2.3)):
        c.key(t, ra=(-22, -40, 16), rf=(-90, 0, 0))
        c.key(t + 0.9, ra=(-42, -20, 16), rf=(-75, 0, 0))
        c.key(t + 1.15, ra=(-34, -28, 16), rf=(-84, 0, 0))
    c.key(1.4, head=(22, -14, 5))
    c.key(2.6, head=(20, -8, -4))
    click(c, 3.6, 4.4, base=(18, -10, 6), rate=5, amp=2.5)
    c.key(5.2)


@clip("thread_check", "Checks a thread", length=4.4, weight=2, require=["adult"])
def _(c):
    # the lower hands lift a strand to the light and pull it taut; the upper right plucks it twice and listens
    c.key(0.6, rla=(-66, -10, 0), rlf=(-18, 0, 0), lla=(-66, -10, 0), llf=(-18, 0, 0), head=(14, 0, 0))
    c.key(1.0, rla=(-72, 6, 8), rlf=(-22, 0, 0), lla=(-72, 6, 8), llf=(-22, 0, 0), head=(18, 0, 0))
    c.key(1.15, rla=(-70, 8, 10), lla=(-70, 8, 10))
    c.hold(1.15, 3.4, rla=(-70, 8, 10), rlf=(-22, 0, 0), lla=(-70, 8, 10), llf=(-22, 0, 0))
    c.key(1.4, ra=(-36, -28, 0), rf=(-58, 0, 0), head=(20, 2, 4))
    tap(c, 1.6, 2.6, "rf", (-58, 0, 0), -12, period=0.5)
    c.key(2.6, ra=(-36, -28, 0), head=(22, 0, 9))
    click(c, 2.7, 3.3, base=(20, 0, 9), rate=6, amp=3)
    c.key(3.4, ra=(-20, -10, 0), rf=(-30, 0, 0))
    c.key(4.4)


@clip("heron_rest", "Rests on one long leg", length=5.4, weight=1.5, require=["adult"])
def _(c):
    # weight over the right leg, the left foot hooks up behind the right knee; four hands clasp loosely; then down
    c.key(0.5, root=(0, 0, -2.4), root_pos=(-0.8, 0, 0))
    c.key(1.0, root=(0, 0, -3), root_pos=(-1, 0, 0), ll=(-8, 0, -8), ls=(80, 0, 0), lft=(24, 0, 0),
          rla=(-14, -20, 0), rlf=(-26, 0, 0), lla=(-14, -20, 0), llf=(-26, 0, 0), head=(2, 0, 3))
    c.key(1.2, ll=(-6, 0, -8), ls=(95, 0, 0), lft=(30, 0, 0))
    c.hold(1.2, 4.2, root=(0, 0, -3), root_pos=(-1, 0, 0), ll=(-6, 0, -8), ls=(95, 0, 0), lft=(30, 0, 0),
           rla=(-14, -20, 0), rlf=(-26, 0, 0), lla=(-14, -20, 0), llf=(-26, 0, 0))
    c.key(2.4, head=(4, 8, 4))
    c.key(3.6, head=(0, -6, 2))
    c.key(4.8, ll=(-4, 0, 0), ls=(10, 0, 0), lft=(0, 0, 0), root=(0, 0, -0.5), root_pos=(-0.2, 0, 0))
    c.key(5.4)


hang_all()
