from kit import clip
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _ghast import CHIN, CLASP, EYE, FOLD, HEART, bow, flutter, hd, merge, osc, ripple, splay, tents  # noqa: E402,F401

# Meeting, talking and parting. Soft-spoken: small gestures, the body drifting toward or away from the listener.


# -- greet ----------------------------------------------------------------------------------------------------------
@clip("greet_dip", "Dips on the air in greeting", trigger="greet", length=3.2, weight=3, require=["adult"])
def _(c):
    # the Ghastkin curtsy (thanks_bow_heart is the bow): a small lift, then a dip on the air, head bowed, arms swept
    # back like folded wings and the tentacles fanned forward beneath; up again with the tentacles trailing
    c.key(0.3, root_pos=(0, -0.4, 0), ra=(2, 0, 3), la=(2, 0, 3), **hd(-3, 0, 0))
    c.key(1.0, root_pos=(0, 1.8, 0), ra=(18, 0, 12), la=(18, 0, 12), **hd(14, 0, 4))
    c.key(1.7, root_pos=(0, 1.9, 0), ra=(20, 0, 13), la=(20, 0, 13), **hd(15, 0, 5))
    c.key(2.3, root_pos=(0, -0.6, 0), ra=(4, 0, 4), la=(4, 0, 4), **hd(-4, 0, 2))
    c.key(2.8, root_pos=(0, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), **hd(0, 0, 0))
    c.key(3.2)
    ripple(c, 0.2, 3.0, 1.4, 4, base=[(0.2, tents()), (0.3, tents(pitch=3)), (1.0, splay(10, -16)),
                                      (1.7, splay(11, -17)), (2.3, tents(pitch=9)), (3.0, tents())])


@clip("greet_shy_wave", "Small shy wave", trigger="greet", length=2.8, weight=3, require=["adult"])
def _(c):
    c.key(0.3, ra=(-40, 10, 0), la=(-10, -5, 0), **hd(4, 0, 0), root_pos=(0, 0.4, 0))
    c.key(0.8, ra=(-124, 26, 0), la=(-42, -32, 0), **hd(6, 0, 9), root_pos=(0, -0.3, 0))
    osc(c, "ra", 0.8, 2.0, 0.55, 13, axis=2, base=[(0.8, (-124, 26, 0)), (2.0, (-120, 26, 0))])
    c.key(2.0, la=(-40, -31, 0), **hd(8, 0, 10))
    c.key(2.5, ra=(-10, 2, 0), la=(-5, -3, 0), **hd(0, 0, 2), root_pos=(0, 0, 0))
    c.key(2.8)
    ripple(c, 0.2, 2.6, 1.2, 6, base=[(0.2, tents()), (0.8, splay(-6, -6)), (2.0, splay(-6, -5)), (2.6, tents())])


@clip("greet_drift_close", "Drifts close with open hands", trigger="greet", length=2.8, weight=2)
def _(c):
    # shy but glad: a little closer on the air, both hands held open low, a nod; the tentacles trail the drift
    c.key(0.3, root_pos=(0, 0, 0.3), ra=(-4, 0, 2), la=(-4, 0, 2), **hd(2, 0, 0))
    c.key(1.0, root_pos=(0, -0.5, -1.6), ra=(-38, 14, 6), la=(-38, 14, 6), **hd(4, 0, 8))
    c.key(1.45, **hd(11, 0, 8))
    c.key(1.8, root_pos=(0, -0.4, -1.7), ra=(-36, 14, 6), la=(-36, 14, 6), **hd(2, 0, 7))
    c.key(2.4, root_pos=(0, 0, -0.3), ra=(-6, 0, 0), la=(-6, 0, 0), **hd(0, 0, 1))
    c.key(2.8)
    ripple(c, 0.2, 2.6, 1.2, 5, base=[(0.2, tents()), (0.4, tents(pitch=-4)), (1.0, tents(pitch=12)),
                                      (1.8, tents(pitch=8)), (2.3, tents(pitch=-5)), (2.6, tents())])


@clip("greet_sly_nod", "A sly little nod", trigger="greet", length=2.4, weight=2, require=["adult"])
def _(c):
    # head tipped, one slow nod, a lazy flick of two fingers from the brow
    c.key(0.3, **hd(-3, 6, 8), root_pos=(0, 0, 0))
    c.key(0.75, **hd(14, 8, 10), root_pos=(0, 0.5, 0))
    c.key(1.1, **hd(-2, 8, 9), root_pos=(0, 0, 0))
    c.key(0.5, ra=(-10, 0, 0))
    c.key(0.95, ra=(-138, 20, 0))
    c.key(1.35, ra=(-120, 38, 10))
    c.key(1.9, ra=(-6, 2, 0), **hd(0, 2, 2))
    c.key(2.4)
    c.key(0.9, tentacle_2=(0, 0, 0))
    c.key(1.2, tentacle_2=(-24, 0, -8))
    c.key(1.6, tentacle_2=(4, 0, 0))
    c.key(1.9, tentacle_2=(0, 0, 0))


# -- farewell -------------------------------------------------------------------------------------------------------
@clip("farewell_drift_wave", "Waves and drifts back", trigger="farewell", length=3.4, weight=3, require=["adult"])
def _(c):
    c.key(0.3, ra=(-40, 10, 0), **hd(2, 0, 0), root_pos=(0, 0, 0))
    c.key(0.8, ra=(-122, 26, 0), **hd(4, 0, 7), root_pos=(0, -0.4, 0.6))
    osc(c, "ra", 0.8, 2.4, 0.7, 14, axis=2, base=[(0.8, (-122, 26, 0)), (2.4, (-118, 26, 0))])
    c.key(2.4, **hd(6, 0, 9), root_pos=(0, -0.9, 2.2))
    c.key(3.0, ra=(-8, 2, 0), **hd(0, 0, 2), root_pos=(0, -0.2, 0.8))
    c.key(3.4)
    ripple(c, 0.3, 3.2, 1.4, 6, base=[(0.3, tents()), (1.2, tents(pitch=-10)), (2.4, tents(pitch=-12)), (3.2, tents())])


@clip("farewell_heart", "Hand to heart and turns away", trigger="farewell", length=3.8, weight=2, require=["adult"])
def _(c):
    c.key(0.3, ra=(-30, -25, 0), **hd(0, 0, 0))
    c.key(0.9, ra=(-72, -66, 0), ra_pos=(0, 0, -1.5), **bow(9), **hd(8, 0, 4, w=9))
    c.key(1.5, ra=(-71, -66, 0), ra_pos=(0, 0, -1.5), **bow(10), **hd(10, 0, 5, w=10))
    c.key(2.2, ra=(-14, -6, 0), ra_pos=(0, 0, 0), **bow(0), **hd(-2, -28, 0), root=(0, 26, 0))
    c.key(2.8, **hd(2, -34, 3), root=(0, 30, 0))
    c.key(3.4, ra=(0, 0, 0), **hd(0, -4, 0), root=(0, 4, 0))
    c.key(3.8)
    c.key(1.5, root=(0, 0, 0))
    ripple(c, 0.2, 3.6, 1.6, 6, roll=6, base=[(0.2, tents()), (1.0, tents(pitch=-9)), (1.6, tents(pitch=-10)),
                                              (2.6, tents(pitch=5, roll=-6)), (3.6, tents())])


# -- talk (gestures while their line types out) ---------------------------------------------------------------------
@clip("talk_open_palm", "An open hand", trigger="talk", length=2.8, weight=3)
def _(c):
    c.key(0.3, ra=(-20, 6, 0), **hd(0, 0, 0))
    c.key(0.75, ra=(-60, 24, 8), **hd(-4, 6, 4))
    osc(c, "ra", 0.75, 2.2, 0.7, 7, axis=0, base=[(0.75, (-60, 24, 8)), (2.2, (-56, 22, 8))])
    c.key(1.1, **hd(4, 6, 4))
    c.key(1.5, **hd(-3, 4, 5))
    c.key(1.9, **hd(3, 4, 3))
    c.key(2.5, ra=(-6, 0, 0), **hd(0, 0, 0))
    c.key(2.8)
    c.key(0.3, root=(0, 0, 0))
    c.key(1.2, root=(1.5, 0, -1.5))
    c.key(2.4, root=(0, 0, 0))
    ripple(c, 0.2, 2.6, 1.3, 5)


@clip("talk_drift_hands", "Hands drifting apart", trigger="talk", length=3.4, weight=2, require=["adult"])
def _(c):
    # telling of something that drifted away: both hands start together and float slowly apart
    c.key(0.3, ra=(-25, -10, 0), la=(-25, -10, 0), **hd(2, 0, 0))
    c.key(0.9, ra=(-62, -16, 0), la=(-62, -16, 0), **hd(8, 0, 0))
    c.key(2.3, ra=(-70, 38, 12), la=(-66, 34, 10), **hd(-6, 16, 3))
    c.key(2.7, ra=(-64, 40, 12), la=(-60, 36, 10))
    c.key(3.1, ra=(-8, 4, 0), la=(-8, 4, 0), **hd(0, 2, 0))
    c.key(3.4)
    ripple(c, 0.3, 3.2, 1.6, 6, base=[(0.3, tents()), (0.9, tents(pitch=-4)), (2.4, splay(7)), (3.2, tents())])


@clip("talk_trail_off", "Never finishes a sad story", trigger="talk", length=4.4, weight=3, require=["adult"])
def _(c):
    # the hand rises to make a point... stops... sinks; the eyes go aside; then a sly look back and a tiny shrug
    c.key(0.3, ra=(-20, 4, 0), **hd(-2, 0, 0))
    c.key(0.9, ra=(-82, 18, 6), **hd(-8, 4, 3), root_pos=(0, -0.4, 0))
    c.key(1.15, ra=(-84, 18, 6))
    c.key(2.0, ra=(-34, 6, 2), **hd(16, 28, -4), root_pos=(0, 0.9, 0))
    c.key(2.6, ra=(-26, 4, 2), **hd(18, 30, -5), root_pos=(0, 1.0, 0))
    c.key(3.1, **hd(2, 4, 9), root_pos=(0, 0.2, 0))
    c.key(3.3, ra=(-18, 2, 14), la=(-6, 0, 12), root_pos=(0, -0.5, 0))
    c.key(3.6, ra=(-14, 2, 4), la=(-2, 0, 3), **hd(4, 6, 7), root_pos=(0, 0, 0))
    c.key(4.4)
    c.key(2.9, la=(0, 0, 0))
    ripple(c, 0.2, 4.2, 2.0, 5, base=[(0.2, tents()), (2.0, splay(-7, 3)), (2.8, splay(-7, 3)), (3.4, splay(5)),
                                      (4.2, tents())])


@clip("talk_heart", "Speaks from the heart", trigger="talk", length=3.2, weight=2, require=["adult"])
def _(c):
    c.key(0.3, ra=(-30, -25, 0), la=(-10, 4, 0), **hd(0, 0, 0))
    c.key(0.8, ra=(-72, -66, 0), ra_pos=(0, 0, -1.5), la=(-36, 22, 14), **hd(6, -6, 4))
    c.key(1.2, ra=(-76, -65, 0), **hd(12, -6, 4))
    c.key(1.5, ra=(-71, -66, 0), **hd(4, -6, 4))
    c.key(1.9, ra=(-76, -65, 0), la=(-40, 24, 14), **hd(11, -4, 3))
    c.key(2.2, ra=(-71, -66, 0), ra_pos=(0, 0, -1.5), **hd(3, -2, 2))
    c.key(2.8, ra=(-8, -4, 0), ra_pos=(0, 0, 0), la=(-4, 0, 0), **hd(0, 0, 0))
    c.key(3.2)
    ripple(c, 0.2, 3.0, 1.5, 5)


@clip("talk_point_up", "Points to the high air", trigger="talk", length=3.0, weight=2, require=["adult"])
def _(c):
    c.key(0.3, ra=(-40, 14, 0), **hd(0, 0, 0))
    c.key(0.8, ra=(-150, 40, 0), **hd(-22, 12, -3), root_pos=(0, -0.6, 0))
    c.key(1.1, ra=(-146, 40, 0))
    c.key(1.6, ra=(-148, 40, 0), **hd(-20, 14, -4))
    c.key(2.0, ra=(-70, 24, 0), **hd(4, 0, 2), root_pos=(0, 0.2, 0))
    c.key(2.6, ra=(-6, 2, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(3.0)
    ripple(c, 0.2, 2.8, 1.4, 6, base=[(0.2, tents()), (0.8, tents(pitch=6)), (1.8, tents(pitch=5)), (2.8, tents())])


@clip("talk_secret", "Leans in with a secret", trigger="talk", length=3.6, weight=2, require=["adult"])
def _(c):
    # a sly lean, a hand at the side of the mouth, a look either way, then back with a knowing nod
    c.key(0.3, root=(0, 0, 0), **hd(0, 0, 0), ra=(-20, -5, 0), ra_pos=(0, 0, 0))
    c.key(0.9, root=(5, 0, -3), **hd(4, -6, 9), ra=(-114, -6, 0), ra_pos=(1, 0, 0))
    c.key(1.4, **hd(3, 14, 8))
    c.key(1.8, **hd(3, -12, 8))
    c.key(2.2, **hd(5, -4, 9), ra=(-112, -6, 0), ra_pos=(1, 0, 0))
    c.key(2.8, root=(-1, 0, 1), **hd(12, 0, 4), ra=(-20, -4, 0), ra_pos=(0, 0, 0))
    c.key(3.1, **hd(0, 0, 2))
    c.key(3.6)
    c.key(0.3, root_pos=(0, 0, 0))
    c.key(1.0, root_pos=(0, 0, -1.2))
    c.key(2.5, root_pos=(0, 0, -1.0))
    c.key(3.3, root_pos=(0, 0, 0))
    ripple(c, 0.2, 3.4, 1.7, 5, base=[(0.2, tents()), (1.0, tents(pitch=8)), (2.5, tents(pitch=7)), (3.4, tents())])


# -- listen (while you choose a reply) ------------------------------------------------------------------------------
@clip("listen_tilt", "Tilts the head to listen", trigger="listen", length=3.4, weight=3)
def _(c):
    c.key(0.4, **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(1.1, **hd(4, -6, 13), root_pos=(0, 0.3, 0))
    c.key(1.7, **hd(9, -6, 12))
    c.key(2.1, **hd(3, -5, 13))
    c.key(2.8, **hd(0, 0, 2), root_pos=(0, -0.2, 0))
    c.key(3.4)
    ripple(c, 0.3, 3.2, 1.6, 5, lag=0.1, roll=3)


@clip("listen_clasped", "Listens with hands clasped", trigger="listen", length=3.8, weight=2, require=["adult"])
def _(c):
    c.key(0.4, ra=(-16, -12, 0), la=(-16, -12, 0), **hd(2, 0, 0))
    c.key(1.0, **CLASP, **hd(6, 4, -6))
    c.key(1.5, **hd(13, 4, -6))
    c.key(1.9, **hd(5, 4, -6))
    c.key(2.4, **hd(12, 3, -5))
    c.key(2.8, ra=(-34, -27, 0), la=(-35, -26, 0), **hd(4, 2, -4))
    c.key(3.4, ra=(-5, -3, 0), la=(-5, -3, 0), **hd(0, 0, 0))
    c.key(3.8)
    ripple(c, 0.3, 3.6, 1.8, 4)


@clip("listen_chin", "A hand to the chin", trigger="listen", length=4.0, weight=2, require=["adult"])
def _(c):
    # weighing your words: a hand at the chin, the other arm across the waist, a slow tilt this way and that
    c.key(0.4, ra=(-30, -12, 0), la=(-20, -20, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(1.0, **CHIN, la=(-46, -56, 0), la_pos=(0, 0, -1.0), **hd(6, -5, -6))
    osc(c, "ra", 1.2, 2.8, 0.55, 3, axis=0, base=[(1.2, (-106, -16, 0)), (2.8, (-105, -16, 0))])
    c.key(1.9, **hd(4, 6, 7))
    c.key(2.8, ra_pos=(1, 0, 0), la=(-45, -56, 0), la_pos=(0, 0, -1.0), **hd(8, 4, 5))
    c.key(3.5, ra=(-8, -3, 0), ra_pos=(0, 0, 0), la=(-6, -3, 0), la_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(4.0)
    ripple(c, 0.3, 3.8, 1.9, 4, roll=3)


# -- chatting with one of their own ---------------------------------------------------------------------------------
@clip("chat_weigh", "Weighs it on both hands", trigger="chat_speak", length=3.8, weight=3)
def _(c):
    # on the one hand... on the other: the hands rise and fall in turn, the head tipping toward the one held up
    # (talk_open_palm is the one-handed telling)
    c.key(0.3, ra=(-20, 4, 0), la=(-20, 4, 0), **hd(0, 0, 0))
    c.key(0.8, ra=(-44, 10, 6), la=(-44, 10, 6), **hd(2, 0, 0))
    up, down = (-62, 18, 10), (-32, 6, 4)
    c.cycle(1.0, 2.9, 1.0, {"ra": up, "la": down, **hd(3, 6, -7)}, {"ra": down, "la": up, **hd(3, -6, 7)})
    c.key(3.4, ra=(-5, 0, 0), la=(-5, 0, 0), **hd(0, 0, 0))
    c.key(3.8)
    osc(c, "root", 0.8, 3.2, 2.0, 2, axis=2)
    ripple(c, 0.2, 3.6, 1.8, 5, roll=3)


@clip("chat_shape_ghast", "Shapes a ghast in the air", trigger="chat_speak", length=4.4, weight=2, require=["adult"])
def _(c):
    # both hands draw something big and round on the air... then fall; the head drops after them
    c.key(0.3, ra=(-30, -6, 0), la=(-30, -6, 0), **hd(0, 0, 0))
    c.key(0.9, ra=(-130, -8, 0), la=(-130, -8, 0), **hd(-14, 0, 0), root_pos=(0, -0.5, 0))
    c.key(1.6, ra=(-112, 40, 22), la=(-112, 40, 22), **hd(-10, 0, 0))
    c.key(2.3, ra=(-62, 46, 28), la=(-62, 46, 28), **hd(0, 0, 0))
    c.key(2.8, ra=(-34, 10, 10), la=(-34, 10, 10), **hd(4, 0, 0), root_pos=(0, 0, 0))
    c.key(3.3, ra=(-6, 0, 0), la=(-6, 0, 0), **hd(18, 0, -5), root_pos=(0, 0.8, 0))
    c.key(3.9, **hd(12, 0, -3), root_pos=(0, 0.5, 0))
    c.key(4.4)
    ripple(c, 0.2, 4.2, 1.6, 6, base=[(0.2, tents()), (0.9, tents(pitch=5)), (2.4, splay(6)), (3.3, splay(-6)),
                                      (4.2, tents())])


@clip("chat_nod", "Nods along", trigger="chat_listen", length=3.2, weight=3)
def _(c):
    c.key(0.3, **hd(0, 0, 0))
    c.key(0.7, **hd(11, -3, 4))
    c.key(1.05, **hd(1, -3, 4))
    c.key(1.45, **hd(10, -3, 4))
    c.key(1.85, **hd(0, -2, 3))
    c.key(2.7, **hd(2, 0, 1))
    c.key(3.2)
    osc(c, "root_pos", 0.3, 2.9, 1.3, 0.5, axis=1)
    ripple(c, 0.2, 3.0, 1.4, 5)


@clip("chat_vial_listen", "Listens and toys with the vial cord", trigger="chat_listen", length=4.0, weight=2,
      require=["adult"])
def _(c):
    # the sly streak (heart_sigh already has the hand on the heart): one arm across the waist, the other hand idly
    # turning the tear vial on its cord, head cocked, a front tentacle tapping, slow nods as if they knew it already
    c.key(0.4, ra=(-20, -10, 0), la=(-16, -14, 0), la_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(1.0, ra=(-58, -28, 0), la=(-30, -46, 0), la_pos=(0, 0, -0.8), **hd(-3, -4, 9), root_pos=(0, 0, 0.3))
    c.wobble(1.1, 2.9, 2.0, "ra", 6, base=(-58, -28, 0), axis=1)
    c.key(1.8, **hd(7, -4, 9))
    c.key(2.2, **hd(-2, -4, 9))
    c.key(2.7, **hd(5, -3, 7))
    c.key(3.0, ra=(-57, -28, 0), la=(-30, -46, 0), la_pos=(0, 0, -0.8), root_pos=(0, 0, 0.3))
    c.key(3.6, ra=(-6, -3, 0), la=(-6, -3, 0), la_pos=(0, 0, 0), **hd(0, 0, 0), root_pos=(0, 0, 0))
    c.key(4.0)
    c.key(0.8, tentacle_1=(0, 0, 0))
    c.wobble(1.1, 2.9, 2.5, "tentacle_1", 12, base=(-10, 0, 0), axis=0)
    c.key(3.3, tentacle_1=(0, 0, 0))
    for i in (0, 2, 3, 4, 5):
        osc(c, f"tentacle_{i}", 0.3, 3.8, 1.9, 4, axis=0, phase=-0.1 * i)
