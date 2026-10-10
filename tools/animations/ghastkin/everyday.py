from kit import clip
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _ghast import (BROW, CLASP, EYE, FOLD, HEART, HUG, RIGHT_TO_LEFT, VIAL, aim, bow, clear, flutter, hd,  # noqa: E402,F401
                    merge, osc, ripple, splay, tents)

# Everyday idles: what any Ghastkin does while hanging about on the air. Soft, slow, a little sad, now and then sly.


@clip("drift_bob", "Drifts on the air", length=5.0, weight=2)
def _(c):
    # the body wanders a little to their left and back, bobbing; tentacles trail the drift and ripple
    osc(c, "root_pos", 0.2, 4.8, 2.3, -1.1, axis=1,
        base=[(0.2, (0, 0, 0)), (1.7, (1.3, 0, -0.3)), (3.4, (-0.7, 0, 0.2)), (4.8, (0, 0, 0))])
    c.key(0.3, root=(0, 0, 0))
    c.key(1.7, root=(0, 4, 2.2))
    c.key(3.4, root=(0, -3, -1.6))
    c.key(4.8, root=(0, 0, 0))
    ripple(c, 0.2, 4.8, 2.3, 7, roll=4, lag=0.14,
           base=[(0.2, tents()), (1.9, tents(pitch=3, roll=7)), (3.6, tents(pitch=2, roll=-6)), (4.8, tents())])
    c.key(0.4, **hd(0, 0, 0))
    c.key(1.9, **hd(-3, 6, -3))
    c.key(3.6, **hd(2, -5, 3))
    c.key(4.8, **hd(0, 0, 0))


@clip("tentacle_ripple", "Ripples their tentacles", length=4.0, weight=2)
def _(c):
    # a deliberate wave runs from front to back while they look down at it, lifting a little as it passes
    ripple(c, 0.3, 3.7, 1.5, 17, lag=0.16, roll=5,
           base=[(0.3, tents()), (1.2, tents(pitch=-4)), (3.0, tents(pitch=-4)), (3.7, tents())])
    c.key(0.5, **hd(6, 0, 0))
    c.key(1.1, **hd(17, 4, -3))
    c.key(2.6, **hd(15, -4, 3))
    c.key(3.4, **hd(-2, 0, 0))
    c.key(3.9, **hd(0, 0, 0))
    c.key(0.5, root_pos=(0, 0.3, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(1.4, root_pos=(0, -0.9, 0), ra=(-6, 0, 9), la=(-4, 0, 7))
    c.key(2.6, root_pos=(0, -0.6, 0), ra=(-5, 0, 7), la=(-6, 0, 9))
    c.key(3.6, root_pos=(0, 0.15, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(4.0)


@clip("wipe_tear", "Wipes away a tear", length=4.6, weight=1, require=["adult"])
def _(c):
    # rare: they cry once, properly, and are careful who they cry for (the hand stops before the eye, see _ghast)
    c.key(0.45, ra=(-30, -8, 0), ra_pos=(0, 0, 0), **hd(4, 0, 0), root_pos=(0, 0, 0))
    c.key(1.05, ra=(-120, -16, 0), ra_pos=(1, 0, 0), **hd(9, -4, -7), root_pos=(0, 0.7, 0))
    c.key(1.2, ra=(-118, -14, 0))
    c.wobble(1.2, 2.3, 2.0, "ra", 5, base=(-118, -14, 0), axis=1)
    c.key(2.35, ra_pos=(1, 0, 0), **hd(8, -3, -6))
    # a breath out: the hand drifts down, the head lifts, the body sinks then rises
    c.key(2.9, ra=(-62, -12, 0), ra_pos=(0.5, 0, 0), **hd(-4, 0, -2), root_pos=(0, 1.0, 0))
    c.key(3.6, ra=(-8, -3, 0), ra_pos=(0, 0, 0), **hd(2, 0, 0), root_pos=(0, -0.4, 0))
    c.key(4.6)
    ripple(c, 0.4, 4.4, 2.0, 5,
           base=[(0.4, tents()), (1.2, splay(-6, 4)), (2.9, splay(-8, 5)), (4.4, tents())])


@clip("stroke_ashbell", "Strokes a passing Ashbell", length=5.0, weight=1.5, require=["adult"])
def _(c):
    # an Ashbell drifts by at shoulder height: a hand reaches out to stroke it, and the eyes follow it away
    c.key(0.4, ra=(-30, 6, 0), ra_pos=(0, 0, 0), **hd(0, 4, 0), root_pos=(0, 0, 0))
    c.key(1.1, ra=(-108, 30, 0), ra_pos=(0.5, 0, 0), **hd(-4, 14, -4), root_pos=(0, -0.6, 0))
    osc(c, "ra", 1.2, 3.0, 0.9, 8, axis=0, base=[(1.2, (-108, 30, 0)), (3.0, (-106, 28, 0))])
    c.key(2.0, **hd(-2, 16, -7))
    c.key(3.0, ra_pos=(0.5, 0, 0), **hd(-4, 10, -3))
    c.key(3.7, ra=(-70, 8, 0), ra_pos=(0, 0, 0), **hd(-6, -8, 0), root_pos=(0, -0.3, 0))
    c.key(4.3, ra=(-8, 0, 0), **hd(-3, -14, 0), root_pos=(0, 0, 0))
    c.key(4.8, **hd(0, 0, 0))
    c.key(5.0)
    ripple(c, 0.3, 4.8, 2.0, 5, roll=4, base=[(0.3, tents()), (1.2, tents(pitch=4, roll=5)),
                                              (3.2, tents(pitch=3, roll=-4)), (4.8, tents())])


@clip("heart_sigh", "Hand over the heart", length=4.4, weight=2, require=["adult"])
def _(c):
    # a deep breath in (rising, face lifted), then the sigh: the body sinks and the head drops over the hand
    c.key(0.4, ra=(-25, -20, 0), ra_pos=(0, 0, -0.5), **hd(-3, 0, 0), root_pos=(0, -0.3, 0))
    c.key(1.1, ra=(-76, -64, 0), ra_pos=(0, 0, -1.5), **hd(-8, 0, 2), root_pos=(0, -0.8, 0))
    c.key(1.3, ra=(-72, -66, 0))
    c.key(2.0, **hd(17, 0, 7), root_pos=(0, 1.4, 0))
    c.key(2.9, ra=(-71, -66, 0), ra_pos=(0, 0, -1.5), **hd(15, 0, 6), root_pos=(0, 1.1, 0))
    c.key(3.5, ra=(-20, -16, 0), ra_pos=(0, 0, -0.4), **hd(-4, 0, 0), root_pos=(0, -0.5, 0))
    c.key(4.4)
    # the tentacles gather and droop with the sigh
    base = [(0.4, tents()), (1.1, tents(pitch=4)), (2.0, splay(-8, -7)), (2.9, splay(-7, -6)), (3.6, tents()),
            (4.2, tents())]
    ripple(c, 0.4, 4.2, 2.2, 4, base=base)


@clip("sky_watch", "Gazes at the high air", length=5.2, weight=1.5)
def _(c):
    # up toward where the ghasts drift: they rise a little, scan slowly, and sink back
    c.key(0.6, **hd(-10, -6, 0), root_pos=(0, -0.3, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(1.4, **hd(-27, -18, 3), root_pos=(0, -1.4, 0), ra=(6, 0, 6), la=(5, 0, 5))
    c.key(2.8, **hd(-29, 16, -4), root_pos=(0, -1.7, 0), ra=(8, 0, 7), la=(7, 0, 6))
    c.key(3.6, **hd(-26, 20, -5))
    c.key(4.4, **hd(-3, 4, 0), root_pos=(0, 0.3, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(5.2)
    ripple(c, 0.4, 5.0, 2.4, 6, base=[(0.4, tents()), (1.6, tents(pitch=7)), (3.8, tents(pitch=6)), (5.0, tents())])


@clip("sly_glance", "Sly sideways glance", length=3.4, weight=1.5, require=["adult"])
def _(c):
    # the eyes slide to the side while the body turns a little the other way, as if not looking at all; a tentacle
    # flicks like a cat's tail; then a knowing little nod (no hand: pleased_sly hides the smile)
    c.key(0.15, **hd(2, -3, 0))
    c.key(0.45, **hd(-3, 34, 5), root_pos=(0, 0.5, 0))
    c.key(0.6, **hd(-2, 31, 4))
    c.key(0.3, root=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(0.9, root=(0, -9, 0), ra=(6, 0, 4), la=(4, 0, 3))
    c.key(1.7, root=(0, -8, 0), **hd(-2, 30, 6))
    c.key(1.95, **hd(7, 28, 5))
    c.key(2.2, root=(0, -6, 0), **hd(0, 26, 4), root_pos=(0, 0.2, 0))
    c.key(2.8, root=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(1, -3, 0), root_pos=(0, 0, 0))
    c.key(3.4)
    c.key(0.4, tentacle_2=(0, 0, 0))
    c.key(0.7, tentacle_2=(-26, 0, -10))
    c.key(1.0, tentacle_2=(6, 0, 2))
    c.key(1.3, tentacle_2=(0, 0, 0))
    c.key(1.6, tentacle_0=(0, 0, 0))
    c.key(1.85, tentacle_0=(-20, 0, 8))
    c.key(2.2, tentacle_0=(4, 0, 0))
    c.key(2.5, tentacle_0=(0, 0, 0))


@clip("sink_in_thought", "Sinks into a sad thought", length=5.6, weight=1.5, require=["adult"])
def _(c):
    # the thought weighs them down; a small shake of the head lets it go, and they float back up
    c.key(0.5, **hd(5, 0, 0), root_pos=(0, 0.3, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(2.0, **hd(24, -5, -6), root_pos=(0, 2.2, 0), ra=(-12, -8, 0), la=(-10, -9, 0))
    c.key(3.0, **hd(26, -6, -7), root_pos=(0, 2.4, 0))
    c.key(3.3, **hd(18, -4, -4))
    c.wobble(3.3, 3.95, 3.0, "head", 9, base=(18, -4, -4), axis=1)
    c.key(4.6, **hd(-4, 0, 0), root_pos=(0, -0.6, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(5.6)
    ripple(c, 0.4, 5.4, 2.6, 4, decay=0.3,
           base=[(0.4, tents()), (2.0, splay(-10, 3)), (3.2, splay(-11, 3)), (4.4, splay(4, -3)), (5.4, tents())])


@clip("self_hug", "Hugs themselves", length=4.6, weight=1)
def _(c):
    # arms wrap round the middle, shoulders draw in, a slow rock side to side
    c.key(0.4, ra=(-20, -15, 0), la=(-18, -14, 0), **hd(4, 0, 0))
    c.key(1.1, **HUG, **hd(14, 0, 0), root_pos=(0, 0.6, 0))
    osc(c, "root", 1.1, 3.6, 2.4, 3.0, axis=2)
    c.key(1.6, **hd(15, 0, -5))
    c.key(2.4, **hd(14, 0, 6))
    c.key(3.2, **hd(13, 0, -3))
    c.key(3.6, ra=(-47, -55, 0), la=(-65, -59, 0), ra_pos=(0, 0, -1.2), la_pos=(0, 0, -2.2), root_pos=(0, 0.4, 0))
    c.key(4.2, ra=(-6, -4, 0), la=(-5, -4, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(4.6)
    ripple(c, 0.4, 4.4, 2.4, 5, roll=3, base=[(0.4, tents()), (1.2, splay(-6)), (3.6, splay(-6)), (4.4, tents())])


@clip("hum_sway", "Hums a slow tune", length=6.0, weight=2)
def _(c):
    # the whole body rocks on the air to a tune only they hear; the head leans the other way, the tentacles follow
    osc(c, "root", 0.3, 5.7, 2.7, 3.2, axis=2)
    osc(c, "head", 0.3, 5.7, 2.7, -6, axis=2, phase=-0.12, base=[(0.3, (0, 0, 0)), (1.2, (-6, 0, 0)),
                                                                   (4.8, (-5, 0, 0)), (5.7, (0, 0, 0))])
    osc(c, "veil", 0.3, 5.7, 2.7, 4, axis=2, phase=-0.2, base=[(0.3, (0, 0, 0)), (1.2, (5, 0, 0)),
                                                                 (4.8, (4, 0, 0)), (5.7, (0, 0, 0))])
    osc(c, "ra", 0.3, 5.7, 2.7, 5, axis=2, phase=0.1, base=[(0.3, (0, 0, 0)), (1.2, (-6, 0, 3)), (4.8, (-5, 0, 3)),
                                                            (5.7, (0, 0, 0))])
    osc(c, "la", 0.3, 5.7, 2.7, -5, axis=2, phase=0.1, base=[(0.3, (0, 0, 0)), (1.2, (-5, 0, 3)), (4.8, (-6, 0, 3)),
                                                             (5.7, (0, 0, 0))])
    osc(c, "root_pos", 0.3, 5.7, 1.35, -0.5, axis=1)
    ripple(c, 0.3, 5.7, 2.7, 5, roll=-8, lag=0.08, order=[0, 3, 1, 4, 2, 5])


@clip("turn_about", "Turns slowly on the air", length=5.2, weight=1)
def _(c):
    # the head leads, the body follows round to their right, a look about, and back with a small overshoot
    c.key(0.3, **hd(0, 0, 0), root=(0, 0, 0))
    c.key(0.8, **hd(-3, 22, 0))
    c.key(1.7, root=(0, 50, 0), **hd(-4, 8, -3))
    c.key(2.1, root=(0, 54, 0))
    c.key(2.6, root=(0, 51, 0), **hd(-6, -16, 3))
    c.key(3.1, **hd(-2, -24, 2))
    c.key(4.0, root=(0, -5, 0), **hd(0, -6, 0))
    c.key(4.6, root=(0, 1.5, 0), **hd(0, 1, 0))
    c.key(5.2)
    c.key(0.3, root_pos=(0, 0, 0))
    c.key(1.7, root_pos=(0, -0.8, 0))
    c.key(3.2, root_pos=(0, -0.5, 0))
    c.key(4.6, root_pos=(0, 0.2, 0))
    ripple(c, 0.3, 5.0, 2.0, 7, roll=7, base=[(0.3, tents()), (1.5, tents(pitch=6, roll=8)), (2.4, tents(pitch=3)),
                                              (3.7, tents(pitch=5, roll=-7)), (5.0, tents())])


@clip("curl_tentacle", "Curls a tentacle", length=4.4, weight=1.5)
def _(c):
    # a front tentacle curls up to be looked at, wriggles, and is let go with a little flick
    c.key(0.4, tentacle_0=(0, 0, 0), **hd(4, 0, 0), ra=(0, 0, 0))
    c.key(1.3, tentacle_0=(-52, 0, 14), **hd(27, 14, -4), ra=(-26, -6, 4))
    osc(c, "tentacle_0", 1.3, 2.8, 0.75, 8, axis=0, base=[(1.3, (-52, 0, 14)), (2.8, (-50, 0, 12))])
    c.key(1.9, **hd(29, 16, -8))
    c.key(2.6, **hd(25, 12, 4), ra=(-30, -8, 4))
    c.key(3.05, tentacle_0=(12, 0, -3), ra=(-4, 0, 0))
    c.key(3.4, tentacle_0=(-5, 0, 2), **hd(3, 2, 0))
    c.key(3.8, tentacle_0=(0, 0, 0), **hd(0, 0, 0), ra=(0, 0, 0))
    c.key(4.4)
    for i in (1, 2, 3, 4, 5):
        osc(c, f"tentacle_{i}", 0.4, 4.0, 2.0, 5, axis=0, phase=-0.12 * i)


@clip("doze_float", "Dozes on the air", length=6.4, weight=1)
def _(c):
    # the head nods down, the body sinks, the tentacles go slack... then a start, a flutter, a sheepish look round
    c.key(0.5, **hd(4, 0, 0), root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(2.4, **hd(24, 0, 5), root_pos=(0, 1.8, 0), ra=(-9, -4, 0), la=(-8, -4, 0))
    c.key(3.6, **hd(31, 0, 7), root_pos=(0, 2.6, 0), ra=(-12, -5, 0), la=(-11, -5, 0))
    c.key(3.85, **hd(-10, 0, -2), root_pos=(0, -1.3, 0), ra=(-14, 0, 22), la=(-12, 0, 20))
    c.key(4.2, **hd(-4, 0, 0), root_pos=(0, -0.6, 0), ra=(-4, 0, 6), la=(-4, 0, 6))
    c.key(4.7, **hd(2, 18, 0))
    c.key(5.3, **hd(2, -16, 2), root_pos=(0, 0.2, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(5.9, **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(6.4)
    for i in range(6):
        ch = f"tentacle_{i}"
        c.key(0.5, **{ch: (0, 0, 0)})
        c.key(3.6, **{ch: (5, 0, 0)})
    flutter(c, 3.75, 4.7, 7, 16, decay=0.6)
    for i in range(6):
        c.key(5.4, **{f"tentacle_{i}": (0, 0, 0)})


@clip("cape_settle", "Shrugs the cape straight", length=3.8, weight=1, require=["adult", "male"])
def _(c):
    # a shrug that flicks the short cape out behind, a look over the shoulder, a pat to settle it
    # (cape pitch + swings its hem out behind; below zero it goes into the back)
    c.key(0.35, root_pos=(0, 0.4, 0), ra=(4, 0, 2), la=(4, 0, 2), cape=(4, 0, 0), **hd(4, 0, 0))
    c.key(0.75, root_pos=(0, -0.8, 0), ra=(32, 0, 15), la=(30, 0, 13), cape=(38, 0, 0), **hd(-4, 0, 0))
    c.key(1.25, root_pos=(0, 0.2, 0), ra=(14, 0, 6), la=(10, 0, 4), cape=(12, 0, 0), **hd(9, 34, -4))
    c.key(1.7, ra=(38, 12, 18), la=(4, 0, 2), cape=(7, 0, 0), **hd(11, 40, -5))
    c.wobble(1.75, 2.55, 3.0, "ra", 6, base=(38, 12, 18), axis=0)
    c.wobble(1.75, 2.55, 3.0, "cape", 4, base=(6, 0, 0), axis=0)
    c.key(2.6, la=(2, 0, 1), **hd(8, 30, -3), root_pos=(0, 0, 0))
    c.key(3.2, ra=(-3, 0, 0), la=(0, 0, 0), cape=(2, 0, 0), **hd(-2, -2, 0))
    c.key(3.8)
    ripple(c, 0.3, 3.6, 1.6, 6, base=[(0.3, tents()), (0.8, tents(pitch=-6)), (1.6, tents(pitch=3)), (3.6, tents())])


@clip("veil_settle", "Draws the veil close", length=4.6, weight=1, require=["adult", "female"])
def _(c):
    # the right hand finds the veil's edge behind her and draws it round in front; she bows into it a moment
    c.key(0.4, ra=(14, 6, 8), **hd(2, 14, -2))
    c.key(1.0, ra=(36, 14, 22), **hd(4, 26, -4))
    c.key(1.15, ra=(37, 15, 22))
    c.key(1.15, ra_pos=(0, 0, 0), la_pos=(0, 0, 0))
    c.key(1.8, ra=(-60, -44, 0), ra_pos=(0, 0, -2), la=(-12, -8, 0), **hd(10, -4, -4), root_pos=(0, 0.3, 0))
    c.key(2.3, ra=(-62, -46, 0), ra_pos=(0, 0, -2), la=(-42, -28, 0), la_pos=(0, 0, -0.5), **hd(15, -8, -6),
          root_pos=(0, 0.7, 0))
    c.key(3.0, ra=(-61, -45, 0), ra_pos=(0, 0, -2), la=(-41, -27, 0), la_pos=(0, 0, -0.5), **hd(14, -6, -5),
          root_pos=(0, 0.6, 0))
    c.key(3.6, ra=(-8, -4, 0), la=(-5, -3, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0), **hd(-2, 0, 0),
          root_pos=(0, -0.2, 0))
    c.key(4.6)
    # the veil: pulled round her right side, held, let go with a swing past rest. hd() keys the veil too, so its keys
    # are cleared first (they flapped it back and forth); a big yaw would swing half of it through the head
    clear(c, "veil", 0.0, 4.6)
    c.key(0.4, veil=(-1, -2, 1))
    c.key(1.2, veil=(-3, 6, -3))
    c.key(1.9, veil=(2, -10, 13))
    c.key(3.0, veil=(3, -10, 14))
    c.key(3.6, veil=(3, 6, -6))
    c.key(4.1, veil=(-1, -2, 1))
    ripple(c, 0.3, 4.4, 2.0, 4, base=[(0.3, tents()), (2.0, tents(pitch=-4, roll=4)), (3.2, tents(pitch=-4, roll=4)),
                                      (4.4, tents())])


@clip("ash_brush", "Brushes ash from the shoulder", length=3.8, weight=1, require=["adult"])
def _(c):
    # Delta ash settles on everything: flick, flick, a turn of the head from the dust, a shake of the hand
    c.key(0.35, ra=(-30, -20, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(0.8, ra=(-99, -50, 0), ra_pos=(0, 0, -1.5), **hd(-4, 24, -6))
    c.wobble(0.85, 1.75, 3.5, "ra", 9, base=(-99, -50, 0), axis=1)
    c.key(1.9, ra=(-96, -48, 0), ra_pos=(0, 0, -1.5), **hd(-6, 30, -8))
    c.key(2.15, **hd(4, 26, -4))
    c.key(2.4, ra=(-40, 8, 20), ra_pos=(0, 0, 0), **hd(2, 6, 0))
    c.wobble(2.45, 3.1, 4.0, "ra", 10, base=(-40, 8, 20), axis=2)
    c.key(3.3, ra=(-6, 0, 2), **hd(0, 0, 0))
    c.key(3.8)
    c.key(0.8, root=(0, 0, 0))
    c.key(1.3, root=(0, -6, 0))
    c.key(2.4, root=(0, -3, 0))
    c.key(3.3, root=(0, 0, 0))
    ripple(c, 0.3, 3.6, 1.5, 5, roll=4)


@clip("let_go_name", "Lets a name go", length=5.6, weight=1, require=["adult"])
def _(c):
    # an old name, held in cupped hands, lifted to the face, then let go up into the air
    c.key(0.5, ra=(-30, -12, 0), la=(-30, -12, 0), **hd(6, 0, 0), root_pos=(0, 0, 0), ra_pos=(0, 0, 0),
          la_pos=(0, 0, 0))
    c.key(1.3, ra=(-77, -16, 0), la=(-77, -16, 0), **hd(22, 0, 0), root_pos=(0, 0.6, 0))
    c.key(2.0, ra=(-79, -16, 0), la=(-78, -15, 0), **hd(24, 0, 4), ra_pos=(0, 0, 0), la_pos=(0, 0, 0))
    c.key(2.7, ra=(-116, -12, 0), la=(-116, -12, 0), **hd(4, 0, 0), root_pos=(0, 0, 0), ra_pos=(1, 0, -1.5),
          la_pos=(1, 0, -1.5))
    c.key(3.0, ra=(-117, -13, 0), la=(-117, -12, 0))
    c.key(3.6, ra=(-150, 30, 0), la=(-150, 30, 0), **hd(-28, 0, 0), root_pos=(0, -1.6, 0), ra_pos=(0, 0, 0),
          la_pos=(0, 0, 0))
    c.key(4.1, ra=(-140, 38, 0), la=(-140, 38, 0), **hd(-30, 3, 2), root_pos=(0, -1.8, 0))
    c.key(4.9, ra=(-12, 6, 6), la=(-12, 6, 6), **hd(-4, 0, 0), root_pos=(0, 0.3, 0))
    c.key(5.6)
    ripple(c, 0.4, 3.3, 1.8, 4, base=[(0.4, tents()), (1.3, tents(pitch=-5)), (3.3, tents(pitch=-3))])
    flutter(c, 3.3, 4.4, 6, 10, base=[(3.3, tents(pitch=-3)), (4.4, tents(pitch=6))], decay=0.5)
    for i in range(6):
        c.key(5.2, **{f"tentacle_{i}": (0, 0, 0)})


@clip("vial_check", "Counts the tears in the vial", length=4.6, weight=1.5, require=["adult"])
def _(c):
    # head bowed over the little vial on its cord, turned to the light, counted with small nods
    c.key(0.4, ra=(-20, -12, 0), la=(-20, -12, 0), **hd(8, 0, 0))
    c.key(1.0, **VIAL, **hd(29, 0, 0))
    osc(c, "ra", 1.0, 2.6, 0.8, 4, axis=1, base=[(1.0, (-48, -22, 0)), (2.6, (-49, -22, 0))])
    osc(c, "la", 1.0, 2.6, 0.8, -4, axis=1, base=[(1.0, (-50, -20, 0)), (2.6, (-51, -20, 0))])
    c.key(1.5, **hd(28, 0, 7))
    c.key(2.0, **hd(31, 0, -6))
    c.key(2.6, **hd(26, 0, 0))
    c.wobble(2.6, 3.3, 3.0, "head", 5, base=(26, 0, 0), axis=0)
    c.key(2.6, veil=(-7, 0, 0))
    c.key(3.3, veil=(-7, 0, 0))
    c.key(3.9, ra=(-6, -3, 0), la=(-6, -3, 0), **hd(-3, 0, 0))
    c.key(4.6)
    c.key(2.6, root_pos=(0, 0, 0))
    c.key(3.6, root_pos=(0, 0.8, 0))
    c.key(4.4, root_pos=(0, 0, 0))
    ripple(c, 0.3, 4.4, 2.2, 4)
