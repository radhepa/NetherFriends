"""Weft greetings, farewells, conversation and chat: measured, courteous, a little pleased with their four hands."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _weft import click, tap, twist, twist_to, behind_back, HIP, HIP_FOREARM, hang_all  # noqa: E402

# the upper right raised beside the head, forearm up: the classic wave (forearm about -85)
WAVE = (-20, 90, 80)
# (the right forearm lies over the left: layered, not passing through each other)
UP_FOLD_R, UP_FOLD_L = twist(-42, -72), twist(-30, -88)
LO_FOLD_R, LO_FOLD_L = twist(-26, -64), twist(-16, -80)
# the upper hands meeting fingertip to fingertip just under the mandibles (forearm -100)
STEEPLE = (-30, -20, 16)


# -- greet ------------------------------------------------------------------------------------------------------
@clip("greet_double_wave", "Wave and a lower wave", trigger="greet", length=3.2, weight=3, require=["adult"])
def _(c):
    # the upper right waves; a beat later the lower right gives its own small wave beneath it; head tilts, pleased
    twist_to(c, 0.0, 0.45, "ra", (0, 0, 0), WAVE)
    c.key(0.45, rf=(-85, 0, 0), head=(-4, 6, -6))
    c.wobble(0.5, 2.0, 2.2, "rf", 18, base=(-85, 0, 0), axis=2)
    c.key(0.8, rla=(-70, 10, 16), rlf=(-50, 0, 0))
    c.wobble(0.9, 2.1, 2.6, "rlf", 16, base=(-50, 0, 0), axis=2)
    c.hold(0.45, 2.1, ra=twist(*WAVE))
    c.key(2.1, rla=(-70, 10, 16), head=(-4, 4, -9))
    click(c, 2.1, 2.5, base=(-4, 4, -9), rate=6, amp=2.5)
    twist_to(c, 2.1, 2.8, "ra", WAVE, (0, 0, 0))
    c.key(2.8, rf=(-20, 0, 0), rla=(-10, 0, 0), rlf=(0, 0, 0))
    c.key(3.2)


@clip("greet_open_bow", "Open bow", trigger="greet", length=3.4, weight=3, require=["adult"])
def _(c):
    # a measured bow: the upper pair opens low and wide, palms out; the lower pair folds neatly at the belly
    c.key(0.3, waist=(-3, 0, 0), head=(-4, 0, 0))
    c.key(1.0, waist=(24, 0, 0), head=(10, 0, 0), ra=(-26, 30, 28), rf=(-30, 0, 0), la=(-26, 30, 28), lf=(-30, 0, 0),
          rla=LO_FOLD_R, rlf=(-34, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0))
    c.key(1.25, waist=(27, 0, 0), ra=(-28, 32, 31), la=(-28, 32, 31))
    c.hold(1.25, 1.7, waist=(27, 0, 0), rla=LO_FOLD_R, rlf=(-34, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0),
           head=(10, 0, 0))
    c.key(2.5, waist=(-2, 0, 0), ra=(-6, 6, 6), rf=(-10, 0, 0), la=(-6, 6, 6), lf=(-10, 0, 0),
          rla=(-8, -4, 0), rlf=(-6, 0, 0), lla=(-8, -4, 0), llf=(-6, 0, 0), head=(-6, 0, 4))
    c.key(3.4)


@clip("greet_shuttle_salute", "Shuttle salute", trigger="greet", length=3.0, weight=2, require=["adult"],
      mirror="never")
def _(c):
    # the lower right lifts the shuttle up before the face like a salute; the head dips to it; the upper pair
    # turns palms out at the sides
    # (the lower arm swings a little out as it rises, so it clears the chest and the shuttle clears the face)
    c.key(0.5, rla=(-104, -22, 10), rlf=(6, 0, 0), ra=(-14, 14, 22), rf=(-26, 0, 0), la=(-14, 14, 22),
          lf=(-26, 0, 0), head=(-2, 0, 0))
    c.key(0.7, rla=(-118, -22, 12), rlf=(18, 0, 0), head=(6, 0, 0))
    c.key(0.9, head=(10, 0, 0))
    c.hold(0.9, 1.7, rla=(-116, -22, 12), rlf=(16, 0, 0), ra=(-14, 14, 22), la=(-14, 14, 22))
    c.key(1.7, head=(6, 0, 4))
    c.key(2.3, rla=(-30, -6, 0), rlf=(4, 0, 0), ra=(-4, 4, 6), la=(-4, 4, 6), head=(-2, 0, 0))
    c.key(3.0)


@clip("greet_click_nod", "Click and nod", trigger="greet", length=2.6, weight=2)
def _(c):
    # a quick nod, a tilt of the head and a run of clicks; the right hand lifts, open (children too)
    c.key(0.3, head=(-6, 0, 0))
    c.key(0.6, head=(14, 0, 0), ra=(-34, 10, 14), rf=(-40, 0, 0))
    c.key(0.85, head=(6, 0, 0))
    c.key(1.05, head=(4, 0, 10))
    click(c, 1.05, 1.8, base=(4, 0, 10), rate=6, amp=3)
    c.hold(0.6, 1.8, ra=(-34, 10, 14), rf=(-40, 0, 0))
    c.key(2.6)


# -- farewell ---------------------------------------------------------------------------------------------------
@clip("farewell_unfurl", "Four hand farewell", trigger="farewell", length=3.4, weight=3, require=["adult"])
def _(c):
    # the lower pair opens outward, then the upper pair; then all four fold in to the chest with a small bow
    c.key(0.5, rla=(-30, 20, 34), rlf=(-6, 0, 0), lla=(-30, 20, 34), llf=(-6, 0, 0), head=(-2, 0, 0))
    c.key(0.9, ra=(-40, 30, 40), rf=(-10, 0, 0), la=(-40, 30, 40), lf=(-10, 0, 0), head=(-6, 0, 0))
    c.key(1.05, ra=(-42, 32, 43), la=(-42, 32, 43))
    c.key(1.4, rla=(-30, 20, 34), lla=(-30, 20, 34))
    c.key(2.0, ra=UP_FOLD_R, rf=(-92, 0, 0), la=UP_FOLD_L, lf=(-80, 0, 0), rla=LO_FOLD_R, rlf=(-34, 0, 0),
          lla=LO_FOLD_L, llf=(-30, 0, 0), waist=(14, 0, 0), head=(10, 0, 0))
    c.key(2.4, waist=(12, 0, 0))
    c.key(3.4)


@clip("farewell_slow_wave", "Slow wave", trigger="farewell", length=3.0, weight=2)
def _(c):
    # one slow, deliberate wave from side to side, the head tilting after it (children too)
    # a side raise, so it stays clear of a child's big head too
    c.key(0.5, ra=(-24, 0, 128), rf=(-24, 0, 0), head=(0, 4, -4))
    c.key(0.9, ra=(-24, 0, 146), head=(0, 6, -6))
    c.key(1.4, ra=(-24, 0, 118), head=(0, 2, 4))
    c.key(1.9, ra=(-24, 0, 146), head=(0, 6, -6))
    c.key(2.3, ra=(-20, 0, 110), rf=(-24, 0, 0), head=(4, 0, 0))
    c.key(3.0)


# -- talk: gestures while their line types out -------------------------------------------------------------------
@clip("talk_explain_weave", "Explains while weaving", trigger="talk", length=3.6, weight=3, require=["adult"],
      mirror="never")
def _(c):
    # the upper hands open palms-up in turn to make each point; the lower pair simply keeps weaving below
    c.key(0.35, ra=(-36, 22, 18), rf=(-52, 0, 0), head=(-2, 6, 0))
    c.key(0.55, ra=(-40, 26, 22), head=(-4, 8, 3))
    c.key(1.0, ra=(-16, 6, 6), rf=(-30, 0, 0))
    c.key(1.1, la=(-36, 22, 18), lf=(-52, 0, 0), head=(-2, -6, 0))
    c.key(1.3, la=(-40, 26, 22), head=(-4, -8, -3))
    c.key(1.8, la=(-16, 6, 6), lf=(-30, 0, 0))
    c.key(1.9, ra=(-38, 20, 16), rf=(-48, 0, 0), la=(-38, 20, 16), lf=(-48, 0, 0), head=(4, 0, 0))
    c.key(2.2, ra=(-42, 26, 22), la=(-42, 26, 22), head=(-2, 0, 6))
    c.key(2.9, ra=(-12, 4, 4), rf=(-20, 0, 0), la=(-12, 4, 4), lf=(-20, 0, 0))
    c.cycle(0.2, 3.2, 0.75, {"rla": (-34, -14, 0), "rlf": (-30, 0, 0), "lla": (-24, -6, 0), "llf": (-22, 0, 0)},
            {"rla": (-24, -6, 0), "rlf": (-22, 0, 0), "lla": (-34, -14, 0), "llf": (-30, 0, 0)})
    c.key(3.6)


@clip("talk_count_points", "Counts off points", trigger="talk", length=3.8, weight=3, require=["adult"])
def _(c):
    # the upper left holds out its palm; the upper right ticks off one, two, three on it, a nod for each;
    # the lower pair stays folded, patient
    c.key(0.4, la=(-50, -10, 0), lf=(-50, 0, 0), ra=(-46, -26, 12), rf=(-50, 0, 0),
          rla=LO_FOLD_R, rlf=(-34, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0), head=(6, -4, 0))
    c.hold(0.4, 3.0, la=(-50, -10, 0), lf=(-50, 0, 0), rla=LO_FOLD_R, rlf=(-34, 0, 0), lla=LO_FOLD_L,
           llf=(-30, 0, 0))
    for i, t in enumerate((0.7, 1.45, 2.2)):
        c.key(t, ra=(-46 - i * 2, -26 + i * 3, 12), rf=(-50, 0, 0), head=(4, -4, 0))
        c.key(t + 0.18, rf=(-62, 0, 0), head=(12, -4, i * 3))
        c.key(t + 0.4, rf=(-50, 0, 0), head=(5, -3, 0))
    c.key(3.0, ra=(-30, -18, 8), rf=(-40, 0, 0), head=(-2, 0, 5))
    c.key(3.8)


@clip("talk_measure", "Two lengths at once", trigger="talk", length=3.6, weight=3, require=["adult"])
def _(c):
    # the upper hands show a length, then a longer one; the lower hands show a much shorter one at the same time,
    # and the head glances from one to the other: about this, or this
    c.key(0.5, ra=(-60, 14, 0), rf=(-30, 0, 0), la=(-60, 14, 0), lf=(-30, 0, 0), head=(-4, 0, 0),
          rla=(-55, -6, 0), rlf=(-5, 0, 0), lla=(-55, -6, 0), llf=(-5, 0, 0))
    c.key(1.1, ra=(-60, 30, 0), la=(-60, 30, 0), head=(-6, 0, 4))
    c.key(1.3, ra=(-60, 34, 0), la=(-60, 34, 0))
    c.key(1.6, head=(18, 0, -4))
    c.key(1.8, rla=(-55, -12, 0), lla=(-55, -12, 0))
    c.key(2.0, rla=(-55, -16, 0), lla=(-55, -16, 0))
    c.hold(1.3, 2.4, ra=(-60, 34, 0), rf=(-30, 0, 0), la=(-60, 34, 0), lf=(-30, 0, 0))
    c.key(2.4, head=(2, 0, 6), rla=(-55, -16, 0), rlf=(-5, 0, 0), lla=(-55, -16, 0), llf=(-5, 0, 0))
    click(c, 2.4, 2.9, base=(2, 0, 6), rate=6, amp=3)
    c.key(3.6)


@clip("talk_four_shrug", "Four hand shrug", trigger="talk", length=2.8, weight=2, require=["adult"])
def _(c):
    # all four hands turn up and out at once, the head tilts: well, what else would one expect
    c.key(0.15, waist=(2, 0, 0))
    c.key(0.55, ra=(-30, 24, 20), rf=(-50, 0, 0), la=(-30, 24, 20), lf=(-50, 0, 0),
          rla=(-30, 20, 30), rlf=(-10, 0, 0), lla=(-30, 20, 30), llf=(-10, 0, 0), head=(-6, 0, 12), waist=(-3, 0, 0))
    c.key(0.75, ra=(-32, 26, 23), la=(-32, 26, 23), rla=(-32, 22, 33), lla=(-32, 22, 33), head=(-7, 0, 14))
    c.hold(0.75, 1.5, ra=(-32, 26, 23), rf=(-50, 0, 0), la=(-32, 26, 23), lf=(-50, 0, 0), rla=(-32, 22, 33),
           rlf=(-10, 0, 0), lla=(-32, 22, 33), llf=(-10, 0, 0), waist=(-3, 0, 0))
    c.key(1.5, head=(-5, 0, 10))
    c.key(2.2, ra=(-6, 4, 4), rf=(-10, 0, 0), la=(-6, 4, 4), lf=(-10, 0, 0), rla=(-4, 2, 4), lla=(-4, 2, 4),
          head=(2, 0, 2), waist=(0, 0, 0))
    c.key(2.8)


@clip("talk_draw_thread", "Draws out a thought", trigger="talk", length=3.4, weight=2, require=["adult"])
def _(c):
    # the upper right pinches the air before the face and draws an invisible thread out to the side, slow and
    # exact; the head follows the line; the upper left opens beneath it
    c.key(0.4, ra=(-80, -20, 0), rf=(-60, 0, 0), head=(-4, 0, 0), la=(-30, 10, 10), lf=(-40, 0, 0))
    c.key(0.55, rf=(-66, 0, 0))
    c.key(1.7, ra=(-80, 40, 10), rf=(-20, 0, 0), head=(-6, 18, 0))
    c.key(1.9, ra=(-80, 44, 12), head=(-6, 20, -3))
    c.hold(1.9, 2.4, la=(-30, 10, 10), lf=(-40, 0, 0))
    click(c, 2.0, 2.5, base=(-6, 20, -3), rate=6, amp=2.5)
    c.key(2.5, ra=(-60, 30, 8), rf=(-30, 0, 0))
    c.key(3.4)


# -- listen: while you choose a reply ------------------------------------------------------------------------------
@clip("listen_steeple", "Listens fingers steepled", trigger="listen", length=4.6, weight=3, require=["adult"])
def _(c):
    # the upper hands meet fingertip to fingertip under the mandibles, the lower pair folded below; slow nods,
    # the fingertips part and meet twice, and one considered click
    c.key(0.6, ra=STEEPLE, rf=(-100, 0, 0), la=STEEPLE, lf=(-100, 0, 0),
          rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0), head=(4, 0, -5))
    c.hold(0.6, 3.8, rf=(-100, 0, 0), lf=(-100, 0, 0), rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L,
           llf=(-30, 0, 0))
    c.key(0.6, ra=STEEPLE, la=STEEPLE)
    for t in (1.5, 2.1):
        c.key(t, ra=STEEPLE, la=STEEPLE)
        c.key(t + 0.15, ra=(-30, -11, 16), la=(-30, -11, 16))
        c.key(t + 0.3, ra=STEEPLE, la=STEEPLE)
    c.key(3.8, ra=STEEPLE, la=STEEPLE)
    c.key(1.4, head=(9, 0, -6))
    c.key(2.0, head=(3, 0, -5))
    c.key(2.6, head=(9, 0, -7))
    c.key(3.1, head=(4, 0, -9))
    click(c, 3.1, 3.7, base=(4, 0, -9), rate=6, amp=2.5)
    c.key(4.6)


@clip("listen_clasped_nod", "Listens hands clasped", trigger="listen", length=4.4, weight=2, require=["adult"])
def _(c):
    # upper hands clasped behind the back, lower hands folded in front: perfectly attentive; slow nods
    behind_back(c, 0.0, 0.7, 3.7, 4.4)
    c.key(0.6, rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0), head=(2, 0, 0))
    c.hold(0.6, 3.7, rla=LO_FOLD_R, rlf=(-30, 0, 0), lla=LO_FOLD_L, llf=(-30, 0, 0))
    c.key(1.2, head=(9, 0, 3))
    c.key(1.7, head=(2, 0, 3))
    c.key(2.4, head=(9, 0, 5))
    c.key(3.0, head=(3, 0, 6))
    c.key(4.4)


@clip("listen_tilt", "Slow listening tilt", trigger="listen", length=4.0, weight=2)
def _(c):
    # the head tilts slowly one way, rests, then the other with a little click (children too)
    c.key(0.8, head=(4, 4, 12), root=(0, 0, 0.8))
    c.hold(0.8, 1.6, head=(4, 4, 12))
    c.key(2.5, head=(5, -3, -10), root=(0, 0, -0.8))
    click(c, 2.5, 3.1, base=(5, -3, -10), rate=6, amp=2.5)
    c.key(4.0)


# -- chat with another Weft ----------------------------------------------------------------------------------------
@clip("chat_show_technique", "Shows a technique", trigger="chat_speak", length=4.4, weight=3, require=["adult"],
      mirror="never")
def _(c):
    # the lower pair demonstrates a pass, slowly and clearly, up near the chest; the upper right points down at it,
    # the upper left opens toward the listener; the head looks from the hands to the partner and back
    c.key(0.5, rla=(-62, -4, 0), rlf=(-20, 0, 0), lla=(-62, -4, 0), llf=(-20, 0, 0),
          ra=(-44, -24, 0), rf=(-62, 0, 0), la=(-30, 16, 14), lf=(-44, 0, 0), head=(16, 0, 0))
    c.hold(0.5, 3.6, ra=(-44, -24, 0), rf=(-62, 0, 0))
    c.cycle(0.5, 3.3, 1.4, {"rla": (-62, -4, 0), "rlf": (-20, 0, 0), "lla": (-62, -4, 0), "llf": (-20, 0, 0)},
            {"rla": (-66, -30, 0), "rlf": (-30, 0, 0), "lla": (-58, 10, 8), "llf": (-14, 0, 0)})
    c.key(1.3, head=(18, 4, 0))
    c.key(1.8, head=(-2, 0, 4), la=(-34, 20, 18))
    c.key(2.4, head=(16, -2, 0), la=(-30, 16, 14))
    c.key(3.2, head=(-2, 0, -5))
    click(c, 3.2, 3.7, base=(-2, 0, -5), rate=6, amp=2.5)
    c.key(3.6, lf=(-44, 0, 0))
    c.key(4.4)


@clip("chat_gossip", "Leans in to gossip", trigger="chat_speak", length=4.2, weight=2, require=["adult"])
def _(c):
    # leans in close, the upper right shields the mandibles, the lower left points discreetly off to one side;
    # a run of quick delighted clicks
    c.key(0.6, waist=(12, -6, 0), ra=(-48, -18, 6), rf=(-112, 0, 0), head=(4, 8, -6),
          lla=(-30, 26, 26), llf=(10, 0, 0))
    c.key(0.8, waist=(13, -7, 0), lla=(-32, 30, 30), llf=(16, 0, 0))
    c.hold(0.8, 3.2, waist=(13, -7, 0), ra=(-48, -18, 6), rf=(-112, 0, 0))
    c.key(1.6, lla=(-32, 30, 30), llf=(16, 0, 0), head=(6, 12, -8))
    tap(c, 1.7, 2.5, "llf", (16, 0, 0), -12, period=0.4)
    click(c, 2.5, 3.1, base=(4, 4, -4), rate=8, amp=3)
    c.key(3.2, lla=(-10, 4, 4), llf=(0, 0, 0))
    c.key(4.2)


@clip("chat_nod_weave", "Nods along weaving", trigger="chat_listen", length=4.6, weight=3, require=["adult"],
      mirror="never")
def _(c):
    # upper arms folded, slow agreeable nods, and below them the lower pair keeps a lazy weave going
    c.key(0.6, ra=UP_FOLD_R, rf=(-90, 0, 0), la=UP_FOLD_L, lf=(-78, 0, 0), head=(4, 0, 0))
    c.hold(0.6, 3.9, ra=UP_FOLD_R, rf=(-90, 0, 0), la=UP_FOLD_L, lf=(-78, 0, 0))
    c.cycle(0.5, 3.7, 1.0, {"rla": (-30, -12, 0), "rlf": (-26, 0, 0), "lla": (-20, -4, 0), "llf": (-18, 0, 0)},
            {"rla": (-20, -4, 0), "rlf": (-18, 0, 0), "lla": (-30, -12, 0), "llf": (-26, 0, 0)})
    for t in (1.0, 2.0, 3.0):
        c.key(t, head=(3, 0, 2))
        c.key(t + 0.35, head=(11, 0, 3))
    c.key(3.9, head=(4, 0, 5))
    c.key(4.6)


@clip("chat_skeptic", "Skeptical hmm", trigger="chat_listen", length=4.2, weight=2, require=["adult"])
def _(c):
    # the head draws back and tilts; upper right on the hip, upper left folded across the chest; the lower right
    # drums on the lower left; a slow click and the smallest shake of the head
    twist_to(c, 0.0, 0.5, "ra", (0, 0, 0), HIP)
    c.key(0.5, head=(-6, 0, 10), rf=(HIP_FOREARM, 0, 0), la=twist(-24, -80), lf=(-72, 0, 0),
          rla=(-26, -30, 0), rlf=(-36, 0, 0), lla=(-16, -10, 0), llf=(-30, 0, 0), waist=(-3, 0, 0))
    c.hold(0.5, 3.4, ra=twist(*HIP), rf=(HIP_FOREARM, 0, 0), la=twist(-24, -80), lf=(-72, 0, 0),
           rla=(-26, -30, 0), lla=(-16, -10, 0), llf=(-30, 0, 0), waist=(-3, 0, 0))
    twist_to(c, 3.4, 4.2, "ra", HIP, (0, 0, 0))
    tap(c, 0.8, 2.0, "rlf", (-36, 0, 0), -14, period=0.3)
    c.key(2.0, head=(-6, 0, 12))
    click(c, 2.1, 2.7, base=(-6, 0, 12), rate=4, amp=3)
    c.key(2.9, head=(-4, -6, 9))
    c.key(3.2, head=(-4, 6, 9))
    c.key(3.4, head=(-4, 0, 8), rlf=(-36, 0, 0))
    c.key(4.2)


hang_all()
