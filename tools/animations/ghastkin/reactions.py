from kit import clip
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _ghast import CHIN, CUP, FOLD, HEART, HUG, VIAL, bow, flutter, hd, merge, osc, ripple, splay, tents  # noqa: E402,F401

# Reactions: gifts, friendship, replies and blows. They play over whatever the Ghastkin was doing.


# -- happy ----------------------------------------------------------------------------------------------------------
@clip("happy_flutter", "Happy tentacle flutter", trigger="happy", length=2.6, weight=3)
def _(c):
    c.key(0.25, root_pos=(0, 0.7, 0), ra=(4, 0, 2), la=(4, 0, 2), **hd(5, 0, 0))
    c.key(0.65, root_pos=(0, -2.3, 0), ra=(-18, 6, 26), la=(-18, 6, 26), **hd(-12, 0, 8))
    c.key(1.2, root_pos=(0, -2.0, 0), **hd(-10, 0, -7))
    c.key(1.7, root_pos=(0, -2.4, 0), ra=(-16, 6, 22), la=(-16, 6, 22), **hd(-11, 0, 6))
    c.key(2.25, root_pos=(0, 0.2, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(2.6)
    osc(c, "root", 0.5, 2.0, 0.75, 4, axis=2)
    flutter(c, 0.3, 2.1, 7, 20, base=[(0.3, tents(pitch=4)), (0.8, tents(pitch=-8)), (2.1, tents(pitch=-4))],
            decay=0.4)
    for i in range(6):
        c.key(2.4, **{f"tentacle_{i}": (0, 0, 0)})


@clip("happy_twirl", "Twirls on the air", trigger="happy", length=3.0, weight=2, mirror="free")
def _(c):
    # one slow, delighted turn about, rising as they go (360 is the same pose as 0, so the turn resets unseen)
    c.key(0.3, root=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(0.55, root=(0, -14, 0), root_pos=(0, 0.6, 0))
    c.key(1.2, root=(0, 120, 0), root_pos=(0, -1.6, 0))
    c.key(1.75, root=(0, 250, 0), root_pos=(0, -2.2, 0))
    c.key(2.2, root=(0, 366, 0), root_pos=(0, -1.4, 0))
    c.key(2.45, root=(0, 360, 0), root_pos=(0, -0.3, 0))
    c.key(2.4501, root=(0, 0, 0))
    c.key(3.0)
    c.key(0.4, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(1.0, ra=(-10, 0, 34), la=(-10, 0, 34), **hd(-12, 0, 9))
    c.key(2.0, ra=(-12, 0, 30), la=(-12, 0, 30), **hd(-10, 0, 8))
    c.key(2.6, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    ripple(c, 0.3, 2.8, 1.2, 8, base=[(0.3, tents()), (1.0, splay(12, 10)), (2.1, splay(10, 8)), (2.8, tents())])


@clip("happy_clap", "Soft clapping", trigger="happy", length=2.6, weight=2, require=["adult"])
def _(c):
    open_r, open_l = (-62, 18, 8), (-62, 18, 8)
    shut_r, shut_l = (-66, -16, 0), (-66, -16, 0)     # palms meet (further in and the arms cross)
    c.key(0.25, ra=(-20, 0, 0), la=(-20, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(0.5, ra=open_r, la=open_l, **hd(-6, 0, 6), root_pos=(0, -0.6, 0))
    t = 0.5
    for _ in range(3):
        c.key(t + 0.17, ra=shut_r, la=shut_l)
        c.key(t + 0.4, ra=open_r, la=open_l)
        t += 0.4
    c.key(1.4, **hd(-8, 0, -6), root_pos=(0, -1.2, 0))
    c.key(2.0, ra=(-56, 10, 4), la=(-56, 10, 4), **hd(-4, 0, 4), root_pos=(0, -0.8, 0))
    c.key(2.6)
    flutter(c, 0.4, 2.0, 5, 10, decay=0.5)


# -- thanks ---------------------------------------------------------------------------------------------------------
@clip("thanks_bow_heart", "Bows with a hand on the heart", trigger="thanks", length=3.0, weight=3, require=["adult"])
def _(c):
    c.key(0.3, ra=(-30, -25, 0), **bow(-2), **hd(-2, 0, 0, w=-2))
    c.key(0.9, ra=(-72, -66, 0), ra_pos=(0, 0, -1.5), **bow(14), **hd(12, 0, 5, w=14), root_pos=(0, 0.7, 0))
    c.key(1.6, ra=(-71, -66, 0), ra_pos=(0, 0, -1.5), **bow(15), **hd(13, 0, 6, w=15), root_pos=(0, 0.8, 0))
    c.key(2.3, ra=(-12, -6, 0), ra_pos=(0, 0, 0), **bow(-2), **hd(-4, 0, 3, w=-2), root_pos=(0, -0.4, 0))
    c.key(3.0)
    c.key(0.3, root_pos=(0, 0, 0))
    for i in range(6):
        ch = f"tentacle_{i}"
        c.key(0.3, **{ch: (2, 0, 0)})
        c.key(0.9, **{ch: (-24, 0, 0)})
        c.key(1.6, **{ch: (-25, 0, 0)})
        c.key(2.3, **{ch: (6, 0, 0)})
        c.key(2.7, **{ch: (0, 0, 0)})


@clip("thanks_cupped", "Takes it in cupped hands", trigger="thanks", length=3.2, weight=2, require=["adult"])
def _(c):
    # received like something fragile: cupped hands out, drawn in to the chest, a small grateful nod over them
    c.key(0.3, ra=(-30, -10, 0), la=(-30, -10, 0), **hd(2, 0, 0))
    c.key(0.8, **CUP, **hd(14, 0, 0), root_pos=(0, 0, -0.6))
    c.key(1.1, ra=(-75, -16, 0), la=(-75, -16, 0))
    c.key(1.7, **VIAL, **hd(20, 0, 5), root_pos=(0, 0.5, 0))
    c.key(2.05, **hd(26, 0, 6))
    c.key(2.35, **hd(15, 0, 4))
    c.key(2.8, ra=(-10, -6, 0), la=(-10, -6, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(3.2)
    ripple(c, 0.2, 3.0, 1.5, 5, base=[(0.2, tents()), (0.8, tents(pitch=-6)), (1.8, splay(-5)), (3.0, tents())])


# -- decline --------------------------------------------------------------------------------------------------------
@clip("decline_head_shake", "Gentle shake of the head", trigger="decline", length=2.8, weight=3)
def _(c):
    c.key(0.25, **hd(2, 0, 0), ra=(-10, 0, 0), root_pos=(0, 0, 0))
    c.key(0.6, **hd(6, 0, 4), ra=(-58, 14, 0), root_pos=(0, 0, 0.8))
    c.wobble(0.6, 1.9, 2.2, "head", 15, base=(6, 0, 4), axis=1)
    c.key(1.95, **hd(8, 0, 6), ra=(-56, 14, 0))
    c.key(2.4, **hd(0, 0, 0), ra=(-4, 0, 0), root_pos=(0, 0, 0.2))
    c.key(2.8)
    ripple(c, 0.2, 2.6, 1.3, 5, base=[(0.2, tents()), (0.7, splay(-6, -6)), (1.9, splay(-6, -5)), (2.6, tents())])


@clip("decline_turn_away", "Turns away sadly", trigger="decline", length=3.4, weight=2, require=["adult"])
def _(c):
    c.key(0.3, root=(0, 0, 0), **hd(0, 0, 0))
    c.key(1.0, root=(0, -32, 0), **hd(16, -14, -6), **HUG, root_pos=(0, 0.8, 0.4))
    c.key(2.0, root=(0, -34, 0), **hd(18, -16, -7))
    c.key(2.4, **hd(10, 10, 0))
    c.key(2.9, root=(0, -4, 0), **hd(2, 2, 0), ra=(-6, -3, 0), la=(-6, -3, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0),
          root_pos=(0, 0, 0))
    c.key(3.4)
    ripple(c, 0.2, 3.2, 1.6, 6, roll=5, base=[(0.2, tents()), (1.0, splay(-8, 5)), (2.2, splay(-8, 4)),
                                              (3.2, tents())])


@clip("decline_sly_push", "Pushes it back with a sly smile", trigger="decline", length=2.8, weight=2,
      require=["adult"])
def _(c):
    # "keep it": a soft push of the hand, a tilt, a knowing little nod
    c.key(0.3, ra=(-30, -6, 0), **hd(0, 0, 0))
    c.key(0.75, ra=(-82, -8, 0), ra_pos=(0, 0, 0), **hd(-3, 8, 9))
    c.key(1.1, ra=(-86, -8, 0), ra_pos=(0, 0, -1.8), root_pos=(0, 0, 0.5))
    c.key(1.5, ra=(-84, -8, 0), ra_pos=(0, 0, -1.5), **hd(10, 8, 10))
    c.key(1.85, **hd(-1, 8, 9))
    c.key(2.4, ra=(-6, 0, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(2.8)
    c.key(1.2, tentacle_1=(0, 0, 0))
    c.key(1.5, tentacle_1=(-22, 0, 0))
    c.key(1.9, tentacle_1=(5, 0, 0))
    c.key(2.3, tentacle_1=(0, 0, 0))


# -- pleased / annoyed (a reply they liked, or one that stung) ------------------------------------------------------
@clip("pleased_rise", "Pleased little rise", trigger="pleased", length=2.4, weight=3)
def _(c):
    c.key(0.25, root_pos=(0, 0.3, 0), **hd(2, 0, 0))
    c.key(0.7, root_pos=(0, -1.3, 0), **hd(-4, 0, 9))
    c.key(1.05, **hd(7, 0, 9))
    c.key(1.4, root_pos=(0, -1.1, 0), **hd(-2, 0, 8))
    c.key(2.0, root_pos=(0, 0, 0), **hd(0, 0, 1))
    c.key(2.4)
    flutter(c, 0.4, 1.6, 5, 8, base=[(0.4, tents(pitch=-6)), (1.6, tents(pitch=-4))], decay=0.5)
    for i in range(6):
        c.key(0.25, **{f"tentacle_{i}": (0, 0, 0)})
        c.key(2.1, **{f"tentacle_{i}": (0, 0, 0)})


@clip("pleased_sly", "Hides a pleased smile", trigger="pleased", length=2.8, weight=2, require=["adult"])
def _(c):
    c.key(0.25, ra=(-20, -6, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(0.7, **CHIN, **hd(4, 22, 9), root_pos=(0, 0.3, 0))
    osc(c, "root", 0.7, 2.0, 0.65, 3, axis=2)
    c.key(1.5, **hd(2, 18, 10))
    c.key(2.0, ra=(-104, -16, 0), ra_pos=(1, 0, 0), **hd(1, 6, 6), root_pos=(0, 0, 0))
    c.key(2.45, ra=(-8, -2, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(2.8)
    c.key(0.8, tentacle_0=(0, 0, 0), tentacle_2=(0, 0, 0))
    c.key(1.1, tentacle_0=(-18, 0, 6), tentacle_2=(4, 0, 0))
    c.key(1.4, tentacle_0=(4, 0, 0), tentacle_2=(-18, 0, -6))
    c.key(1.8, tentacle_0=(0, 0, 0), tentacle_2=(0, 0, 0))


@clip("annoyed_huff", "Huffs and drifts back", trigger="annoyed", length=2.6, weight=3)
def _(c):
    c.key(0.15, root_pos=(0, 0, 0), **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(0.45, root_pos=(0, -0.6, 1.6), **hd(-10, -26, -4), ra=(14, 0, 6), la=(14, 0, 6))
    c.key(1.5, root_pos=(0, -0.4, 1.4), **hd(-9, -24, -4), ra=(12, 0, 5), la=(12, 0, 5))
    c.key(2.1, root_pos=(0, 0, 0.3), **hd(-1, -4, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(2.6)
    for i, (k, v) in enumerate(splay(12).items()):
        c.key(0.15, **{k: (0, 0, 0)})
        c.key(0.45, **{k: (v[0] - 8, v[1], v[2])})
        c.key(1.5, **{k: (v[0] - 6, v[1], v[2])})
        c.key(2.2, **{k: (0, 0, 0)})


@clip("annoyed_cold_shoulder", "Turns a cold shoulder", trigger="annoyed", length=3.2, weight=2, require=["adult"])
def _(c):
    # arms folded, a quarter turn away, chin up; one sideways look back to see if you noticed
    c.key(0.3, root=(0, 0, 0), **hd(0, 0, 0), ra=(-20, -15, 0), la=(-20, -15, 0))
    c.key(0.8, root=(0, 34, 0), **hd(-8, 6, -4), **FOLD)
    c.key(1.6, **hd(-9, 4, -4))
    c.key(1.9, **hd(-4, -30, 2))
    c.key(2.3, **hd(-8, 2, -3), **FOLD)
    c.key(2.4, root=(0, 32, 0))
    c.key(2.9, root=(0, 0, 0), **hd(0, 0, 0), ra=(-6, -3, 0), la=(-6, -3, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0))
    c.key(3.2)
    ripple(c, 0.2, 3.0, 1.5, 5, roll=-6)


# -- hurt -----------------------------------------------------------------------------------------------------------
@clip("hurt_recoil", "Recoils on the air", trigger="hurt", length=1.8, weight=3, blend=(0.05, 0.35))
def _(c):
    c.key(0.12, root_pos=(0, -1.6, 2.6), **hd(-18, 0, 6), ra=(-40, 0, 30), la=(-40, 0, 30))
    c.key(0.35, root_pos=(0, -1.8, 2.8), **hd(-14, 0, 4), ra=(-34, 0, 26), la=(-34, 0, 26))
    c.key(0.9, root_pos=(0, -0.6, 1.2), **hd(4, 0, 2), ra=(-12, 0, 8), la=(-12, 0, 8))
    c.key(1.4, root_pos=(0, 0.2, 0.3), **hd(1, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(1.8)
    for i in range(6):
        ch = f"tentacle_{i}"
        c.key(0.12, **{ch: (-30 - 4 * (i % 3), 0, 0)})
        c.key(0.4, **{ch: (-20, 0, 0)})
        c.key(0.8, **{ch: (8, 0, 0)})
        c.key(1.3, **{ch: (-2, 0, 0)})


@clip("hurt_curl", "Curls up in pain", trigger="hurt", length=2.4, weight=2, blend=(0.05, 0.35))
def _(c):
    c.key(0.15, root=(9, 0, 0), root_pos=(0, 0.8, 0), **hd(18, 0, 0), **HUG)
    c.key(0.8, root=(8, 0, 0), root_pos=(0, 1.4, 0), **hd(20, 0, -4))
    c.key(1.5, root=(4, 0, 0), root_pos=(0, 0.9, 0), **hd(12, 0, -2), ra=(-36, -40, 0), la=(-48, -40, 0),
          ra_pos=(0, 0, -0.5), la_pos=(0, 0, -1.2))
    c.key(2.1, root=(0, 0, 0), root_pos=(0, 0, 0), **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), ra_pos=(0, 0, 0),
          la_pos=(0, 0, 0))
    c.key(2.4)
    for i in range(6):
        ch = f"tentacle_{i}"
        c.key(0.15, **{ch: (-38, 0, 0)})
        c.key(0.8, **{ch: (-34, 0, 0)})
        c.key(1.6, **{ch: (-12, 0, 0)})
        c.key(2.2, **{ch: (0, 0, 0)})


@clip("hurt_weep_heal", "Weeps on the wound", trigger="hurt", length=3.4, weight=2, require=["adult"],
      blend=(0.05, 0.35))
def _(c):
    # a hand clamps the hurt arm; a tear is wiped from the eye and pressed to it, and the wound closes
    c.key(0.15, ra=(-88, -58, 0), ra_pos=(0, 0, -2), **hd(10, 18, -6), root_pos=(0, 0.6, 1.0), la=(8, 0, 10))
    c.key(0.8, ra=(-86, -58, 0), ra_pos=(0, 0, -2), **hd(14, 16, -7), root_pos=(0, 0.8, 0.6))
    c.key(1.05, ra=(-96, -34, 0), ra_pos=(0.5, 0, -2), **hd(6, 8, -6))     # out in front of the face, then up
    c.key(1.3, ra=(-120, -16, 0), ra_pos=(1, 0, 0), **hd(8, -4, -6), root_pos=(0, 0.6, 0.3))
    c.key(1.8, ra=(-118, -16, 0), ra_pos=(1, 0, 0), **hd(8, -4, -6))
    c.key(2.0, ra=(-92, -26, 0), ra_pos=(0.5, 0, -1.5))     # down in front of the face before it crosses
    c.key(2.3, ra=(-88, -58, 0), ra_pos=(0, 0, -2), **hd(14, 18, -6))
    c.key(2.6, ra=(-86, -58, 0), ra_pos=(0, 0, -2), la=(6, 0, 8))
    c.key(3.1, ra=(-8, -4, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0), la=(0, 0, 0))
    c.key(3.4)
    ripple(c, 0.1, 3.2, 1.4, 6, base=[(0.1, tents(pitch=-14)), (0.8, splay(-6, -6)), (2.6, splay(-5, -3)),
                                      (3.2, tents())])
