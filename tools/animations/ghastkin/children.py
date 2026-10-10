from kit import clip
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _ghast import flutter, hd, osc, ripple, splay, tents  # noqa: E402,F401

# Children: big heads, open eyes (they haven't learned to cry yet), four short tentacles, short arms that can't
# reach over their heads, so their gestures go out to the side. Quicker and bouncier than the grown-ups. No waist
# moves here: the pack's hip sits inside a child's head, so they sway with the root.

KID = ["child"]
FACE_R, FACE_L = (-108, -18, 0), (-108, -18, 0)     # both hands up before the face (arms pushed forward a bit)
FACE_POS = (0, 0, -2)
WAVE = (-25, 0, 128)                                # arm raised out to the side, clear of the big head


@clip("child_greet_wave", "Big bouncy wave", trigger="greet", length=2.6, weight=3, require=KID)
def _(c):
    c.key(0.2, root_pos=(0, 0.6, 0), ra=(0, 0, 10), **hd(3, 0, 0))
    c.key(0.5, root_pos=(0, -1.8, 0), ra=WAVE, **hd(-8, 0, 8))
    osc(c, "ra", 0.5, 2.0, 0.45, 14, axis=2, base=[(0.5, WAVE), (2.0, WAVE)])
    c.key(0.85, root_pos=(0, -0.4, 0))
    c.key(1.2, root_pos=(0, -1.7, 0), **hd(-6, 0, -6))
    c.key(1.55, root_pos=(0, -0.3, 0))
    c.key(1.9, root_pos=(0, -1.2, 0), **hd(-6, 0, 7))
    c.key(2.3, root_pos=(0, 0, 0), ra=(0, 0, 0), **hd(0, 0, 0))
    c.key(2.6)
    flutter(c, 0.3, 2.2, 6, 16, decay=0.3)


@clip("child_farewell_bounce", "Bouncy goodbye", trigger="farewell", length=2.8, weight=3, require=KID)
def _(c):
    # waving with the other hand, bobbing backwards away
    w = WAVE
    c.key(0.25, la=(0, 0, 12), **hd(2, 0, 0), root_pos=(0, 0.4, 0))
    c.key(0.6, la=w, **hd(-6, 0, -8), root_pos=(0, -1.5, 0.6))
    osc(c, "la", 0.6, 2.2, 0.5, 14, axis=2, base=[(0.6, w), (2.2, w)])
    c.key(0.95, root_pos=(0, -0.2, 1.2))
    c.key(1.3, root_pos=(0, -1.5, 1.8))
    c.key(1.65, root_pos=(0, -0.2, 2.3))
    c.key(2.0, root_pos=(0, -1.0, 2.6), **hd(-4, 0, -6))
    c.key(2.5, la=(0, 0, 0), root_pos=(0, 0, 0.8), **hd(0, 0, 0))
    c.key(2.8)
    ripple(c, 0.2, 2.6, 0.7, 14, lag=0.2, base=[(0.2, tents()), (0.8, tents(pitch=-10)), (2.2, tents(pitch=-10)),
                                                (2.6, tents())])


@clip("child_thanks_bob", "Bobs a quick thank-you", trigger="thanks", length=2.2, weight=3, require=KID)
def _(c):
    c.key(0.2, root=(0, 0, 0), root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(0.5, root=(10, 0, 0), root_pos=(0, 0.6, 0), ra=(20, 0, 10), la=(20, 0, 10), **hd(18, 0, 0))
    c.key(0.8, root=(-3, 0, 0), root_pos=(0, -1.6, 0), ra=(-30, 0, 30), la=(-30, 0, 30), **hd(-8, 0, 6))
    c.key(1.1, root=(6, 0, 0), root_pos=(0, 0.2, 0), ra=(10, 0, 8), la=(10, 0, 8), **hd(12, 0, -4))
    c.key(1.4, root=(-2, 0, 0), root_pos=(0, -1.0, 0), ra=(-20, 0, 22), la=(-20, 0, 22), **hd(-6, 0, 5))
    c.key(1.9, root=(0, 0, 0), root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(2.2)
    flutter(c, 0.3, 1.8, 6, 14, decay=0.4)


@clip("child_talk_chatter", "Chatters with wiggles", trigger="talk", length=2.6, weight=3, require=KID)
def _(c):
    # everything at once: head bobbing, little arms flapping, a wriggle side to side
    c.key(0.2, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.cycle(0.35, 2.0, 0.5, {"ra": (-40, 0, 28), "la": (-10, 0, 12), "head": (-6, 6, 7)},
            {"ra": (-12, 0, 12), "la": (-40, 0, 28), "head": (4, -6, -7)})
    c.key(2.35, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(2.6)
    osc(c, "root", 0.3, 2.3, 0.5, 4, axis=2)
    osc(c, "root_pos", 0.3, 2.3, 0.5, -0.8, axis=1, phase=0.25)
    ripple(c, 0.2, 2.4, 0.6, 12, lag=0.2)


@clip("child_listen_sway", "Sways and stares", trigger="listen", length=3.2, weight=3, require=KID)
def _(c):
    # wide open eyes, head cocked, rocking side to side, a tentacle kicking idly
    c.key(0.3, **hd(0, 0, 0))
    c.key(0.8, **hd(-6, 0, 12))
    c.key(1.6, **hd(-5, 0, -12))
    c.key(2.4, **hd(-6, 0, 10))
    c.key(2.9, **hd(0, 0, 0))
    c.key(3.2)
    osc(c, "root", 0.3, 2.9, 1.6, 5, axis=2, phase=0.25)
    ripple(c, 0.3, 2.9, 1.6, 0, roll=-6)
    osc(c, "tentacle_1", 0.4, 2.8, 0.6, 22, axis=0)


@clip("child_fake_cry", "Tries to cry like the grown-ups", trigger="idle", length=4.6, weight=2, require=KID)
def _(c):
    # hands to the face, a big scrunched sob... a peek between the fingers: nothing came. A shrug and a bounce.
    c.key(0.3, ra=(-20, -6, 0), la=(-20, -6, 0), **hd(0, 0, 0), root_pos=(0, 0, 0), ra_pos=(0, 0, 0),
          la_pos=(0, 0, 0))
    c.key(0.8, ra=FACE_R, la=FACE_L, ra_pos=FACE_POS, la_pos=FACE_POS, **hd(6, 0, 0), root_pos=(0, 0.6, 0))
    c.wobble(0.9, 1.9, 4.0, "head", 4, base=(6, 0, 0), axis=0)
    c.key(1.9, root_pos=(0, 0.8, 0))
    c.key(2.3, ra=(-108, 6, 0), la=(-112, -18, 0), **hd(0, -10, 8))
    c.key(2.7, ra=(-104, 12, 0), la=(-110, -10, 0), **hd(-4, -4, 10))
    c.key(3.1, ra=(-10, 0, 34), la=(-10, 0, 34), ra_pos=(0, 0, 0), la_pos=(0, 0, 0), **hd(-6, 0, -9),
          root_pos=(0, -0.8, 0))
    c.key(3.35, root_pos=(0, 0.2, 0))
    c.key(3.6, ra=(-6, 0, 10), la=(-6, 0, 10), root_pos=(0, -1.4, 0), **hd(-4, 0, 4))
    c.key(4.1, ra=(0, 0, 0), la=(0, 0, 0), root_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(4.6)
    ripple(c, 0.3, 4.4, 1.4, 6, base=[(0.3, tents()), (1.0, splay(-8)), (2.0, splay(-8)), (3.0, splay(8)),
                                      (4.4, tents())])


@clip("child_tangle", "Gets their tentacles in a tangle", trigger="idle", length=4.4, weight=2, require=KID)
def _(c):
    # two tentacles cross and catch; a puzzled look down, a wriggle, a flutter, and they spring free
    c.key(0.3, **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(1.0, **hd(26, 8, 6), ra=(-16, 0, 14), la=(-16, 0, 14))
    c.key(1.8, **hd(28, -8, -6))
    c.key(2.6, **hd(24, 4, 8))
    c.key(3.2, **hd(-6, 0, 0), ra=(-20, 0, 30), la=(-20, 0, 30))
    c.key(3.9, **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(4.4)
    for ch, cross in (("tentacle_0", -22), ("tentacle_1", 22), ("tentacle_2", -16), ("tentacle_3", 16)):
        c.key(0.3, **{ch: (0, 0, 0)})
        c.key(1.0, **{ch: (-6, 0, cross)})
        c.wobble(1.4, 2.8, 4.0, ch, 9, base=(-6, 0, cross), axis=2)
        c.key(3.1, **{ch: (-10, 0, -cross * 0.6)})
        c.key(3.5, **{ch: (4, 0, cross * 0.2)})
        c.key(3.9, **{ch: (0, 0, 0)})
    c.key(0.3, root_pos=(0, 0, 0))
    c.key(2.9, root_pos=(0, 0.4, 0))
    c.key(3.15, root_pos=(0, -1.6, 0))
    c.key(3.7, root_pos=(0, 0.2, 0))


@clip("child_bounce_play", "Bounces on the air", trigger="idle", length=3.6, weight=2, require=KID)
def _(c):
    # boing, boing, boing: arms up on each rise, the tentacles trailing behind each bounce
    c.key(0.2, root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    t = 0.2
    for i in range(3):
        c.key(t + 0.18, root_pos=(0, 0.7, 0), ra=(10, 0, 6), la=(10, 0, 6), **hd(6, 0, 0))
        c.key(t + 0.5, root_pos=(0, -2.6 + 0.4 * i, 0), ra=(-20, 0, 48), la=(-20, 0, 48), **hd(-10, 0, 0))
        c.key(t + 0.8, root_pos=(0, -0.4, 0))
        t += 0.85
    c.key(3.2, root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(3.6)
    for k in range(4):
        ch = f"tentacle_{k}"
        c.key(0.2, **{ch: (0, 0, 0)})
        t = 0.2
        for i in range(3):
            c.key(t + 0.2, **{ch: (-14, 0, 0)})
            c.key(t + 0.55, **{ch: (18, 0, 0)})
            t += 0.85
        c.key(3.1, **{ch: (0, 0, 0)})


@clip("child_catch_ash", "Catches a falling ash flake", trigger="idle", length=4.2, weight=1.5, require=KID)
def _(c):
    # (child_fake_cry has the hands over the face) a flake of Delta ash drifts down: eyes up, a sway under it,
    # hands out, a clap and a hop to catch it, a peek inside... nothing there. Hands thrown open, a bounce.
    c.key(0.3, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(0.9, ra=(-40, 0, 22), la=(-40, 0, 22), **hd(-22, 10, 6), root_pos=(0.8, 0, 0))
    c.key(1.5, ra=(-50, -4, 12), la=(-50, -4, 12), **hd(-14, -4, -4), root_pos=(-0.4, -0.3, 0))
    c.key(1.85, ra=(-58, -18, 0), la=(-58, -18, 0), **hd(-6, 0, 0), root_pos=(0, -0.9, 0))
    c.key(2.1, root_pos=(0, 0.2, 0))
    c.key(2.5, ra=(-52, -18, 0), la=(-52, -18, 0), **hd(20, 0, 0))
    c.key(2.9, **hd(22, 0, 7))
    c.key(3.2, ra=(-40, 10, 34), la=(-40, 10, 34), **hd(6, 0, -6), root_pos=(0, -1.2, 0))
    c.key(3.5, root_pos=(0, 0.2, 0))
    c.key(3.9, ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(4.2)
    ripple(c, 0.3, 1.8, 1.2, 8, roll=6)
    flutter(c, 1.8, 2.4, 7, 14, decay=0.6)
    flutter(c, 3.1, 3.8, 6, 12, decay=0.6)


@clip("child_hide", "Ducks down to hide", trigger="idle", length=4.4, weight=1.5, require=KID)
def _(c):
    # mischief: sink low, peek this way and that, then pop up as if nothing happened
    c.key(0.3, root_pos=(0, 0, 0), **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(0.8, root_pos=(0, 2.4, 0), **hd(10, 0, 0), ra=(-30, -20, 0), la=(-30, -20, 0))
    c.key(1.4, **hd(6, 30, 6))
    c.key(1.7, **hd(6, 30, 6))
    c.key(2.2, **hd(6, -30, -6))
    c.key(2.5, **hd(6, -30, -6))
    c.key(2.9, root_pos=(0, 2.4, 0), **hd(8, 0, 0))
    c.key(3.2, root_pos=(0, -1.6, 0), **hd(-8, 0, 7), ra=(-20, 0, 40), la=(-20, 0, 40))
    c.key(3.6, root_pos=(0, -0.6, 0), **hd(-4, 0, -4))
    c.key(4.0, root_pos=(0, 0, 0), **hd(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0))
    c.key(4.4)
    ripple(c, 0.3, 2.9, 1.3, 6, base=[(0.3, tents()), (0.8, splay(10, -10)), (2.9, splay(10, -10))])
    flutter(c, 2.9, 4.0, 6, 14, base=[(2.9, splay(10, -10)), (3.3, tents(pitch=8)), (4.0, tents())], decay=0.5)


@clip("child_chase_tentacle", "Chases their own tentacle", trigger="idle", length=4.6, weight=1.5,
      require=KID, mirror="free")
def _(c):
    # a back tentacle flicks; they look round for it, turn after it, and it flicks away again
    c.key(0.3, root=(0, 0, 0), **hd(0, 0, 0))
    c.key(0.6, tentacle_2=(0, 0, 0))
    c.key(0.85, tentacle_2=(30, 0, -10))
    c.key(1.1, tentacle_2=(0, 0, 0))
    c.key(1.0, **hd(14, 40, 8))
    c.key(1.9, root=(0, 70, 0), **hd(16, 46, 8))
    c.key(2.2, tentacle_2=(0, 0, 0))
    c.key(2.45, tentacle_2=(34, 0, -12))
    c.key(2.7, tentacle_2=(0, 0, 0))
    c.key(3.0, root=(0, 140, 0), **hd(16, 44, 8))
    c.key(3.3, root=(0, 150, 0), **hd(-4, 0, -6))
    c.key(4.2, root=(0, 0, 0), **hd(0, 0, 0))
    c.key(4.6)
    for i, base in ((0, 0), (1, 0), (3, 0)):
        osc(c, f"tentacle_{i}", 0.3, 4.3, 0.9, 10, axis=0, phase=-0.2 * i)
    c.key(0.3, root_pos=(0, 0, 0))
    c.key(1.9, root_pos=(0, -0.8, 0))
    c.key(3.0, root_pos=(0, -0.4, 0))
    c.key(4.2, root_pos=(0, 0, 0))
