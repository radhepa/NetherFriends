"""Piglins with company: loud greetings, haggling hands, boasts and gossip."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _pig import ears, flick, snort  # noqa: E402


# -- greet ------------------------------------------------------------------------------------------------------------
@clip("greet_fist_up", "Throws a fist up and bellows", trigger="greet", length=2.8, weight=3, require=["adult"],
      mirror="never")
def _(c):
    c.key(0.25, la=(10, 0, 4), head=(6, 0, 0))
    c.key(0.6, la=(-36, 0, 158), head=(-18, 0, 0), waist=(-5, 0, 0))
    ears(c, 0.6, roll=34, pitch=-8)
    c.key(0.8, la=(-36, 0, 140))
    c.key(1.0, la=(-36, 0, 164), head=(-20, 0, 4))
    c.hold(1.1, 1.6, la=(-36, 0, 158), head=(-14, 0, 6), waist=(-5, 0, 0))
    c.key(2.1, la=(-20, 0, 30), head=(-2, 0, 0), waist=(0, 0, 0))
    ears(c, 2.1, roll=10)
    c.key(2.8)


@clip("greet_belly_slap", "Slaps the belly laughing and points at you", trigger="greet", length=3.0, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.3, head=(-14, 0, 0), waist=(-5, 0, 0), la=(-40, -20, 0))
    c.key(0.45, la=(-16, -12, 4))
    c.key(0.65, la=(-40, -20, 0), head=(-18, 0, 4))
    c.key(0.8, la=(-16, -12, 4))
    c.wobble(0.3, 1.1, 7, "head", 4, base=(-16, 0, 2))
    ears(c, 0.9, roll=26, pitch=-6)
    c.key(1.35, la=(-88, 6, 0), head=(6, 0, 0), waist=(2, 0, 0))
    c.key(1.5, la=(-92, 6, 0), head=(12, 0, 0))
    c.hold(1.6, 2.2, la=(-88, 6, 0), head=(4, 0, 0), waist=(2, 0, 0))
    flick(c, 1.6, amp=20, base=(-6, 0, 26))
    c.key(3.0)


@clip("greet_snort_nod", "Chin up with a snort and a deep nod", trigger="greet", length=2.4, weight=2)
def _(c):
    c.key(0.3, head=(-14, 0, 0))
    snort(c, 0.4, base=(-14, 0, 0), amp=8)
    flick(c, 0.45, amp=24)
    c.key(1.0, head=(18, 0, 0), waist=(4, 0, 0))
    c.key(1.2, head=(14, 0, 0))
    ears(c, 1.2, roll=12, pitch=-6)
    c.key(1.6, head=(-2, 0, 0), waist=(0, 0, 0))
    c.key(2.4)


@clip("greet_child_wave", "Waves both arms and hops", trigger="greet", length=2.6, weight=3, require=["child"],
      mirror="free")
def _(c):
    c.key(0.25, root_pos=(0, 0.4, 0), la=(-10, 0, 40), ra=(-10, 0, 40))
    c.key(0.45, root_pos=(0, -1.6, 0), la=(-10, 0, 165), ra=(-10, 0, 140))
    c.key(0.65, root_pos=(0, 0, 0))
    ears(c, 0.45, roll=34, pitch=-10)
    c.cycle(0.7, 1.9, 0.4, {"la": (-10, 0, 140), "ra": (-10, 0, 172)}, {"la": (-10, 0, 172), "ra": (-10, 0, 140)})
    c.key(1.1, root_pos=(0, 0.3, 0))
    c.key(1.3, root_pos=(0, -1.4, 0), head=(-10, 0, 8))
    c.key(1.5, root_pos=(0, 0, 0), head=(-6, 0, -6))
    flick(c, 1.5, amp=22, base=(-10, 0, 30))
    c.key(2.6)


# -- farewell ---------------------------------------------------------------------------------------------------------
@clip("farewell_wave_off", "Turns away and waves you off over the shoulder", trigger="farewell", length=2.6, weight=2,
      mirror="never")
def _(c):
    # already turning to go: the body swings away, the head stays on you, the free hand flaps up by the ear
    c.key(0.4, root=(0, 22, 0), head=(-4, -18, 0), la=(-120, 8, 14))
    ears(c, 0.4, roll=6, pitch=8)
    c.key(0.62, la=(-158, 0, -18))
    c.key(0.82, la=(-128, 8, 12))
    c.key(1.02, la=(-160, 0, -20))
    flick(c, 1.02, side="left", amp=20, base=(8, 0, 6))
    c.key(1.3, la=(-128, 8, 12))
    c.key(1.8, root=(0, 28, 0), head=(-6, -12, 2), la=(-16, 0, 6))
    c.key(2.6)


@clip("farewell_flourish_bow", "A showy bow with a sweep of the arm", trigger="farewell", length=3.0, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.35, la=(-20, 0, 96), head=(-12, 0, 0), waist=(-4, 0, 0))
    ears(c, 0.35, roll=30, pitch=-8)
    c.key(0.7, la=(-70, 30, 50), head=(-4, 0, 0), waist=(6, 0, 0))
    c.key(1.05, la=(-34, 36, 26), head=(14, 0, 0), waist=(24, 0, 0))
    ears(c, 1.05, roll=-8, pitch=16)
    c.hold(1.15, 1.65, la=(-30, 36, 24), head=(16, 0, 0), waist=(25, 0, 0))
    c.key(2.1, la=(-10, 0, 8), head=(-12, 0, 4), waist=(-4, 0, 0))
    snort(c, 2.15, base=(-12, 0, 4), amp=7)
    flick(c, 2.2, amp=22, base=(-6, 0, 24))
    c.key(3.0)


@clip("farewell_child_scamper", "Quick wave then makes to scamper off", trigger="farewell", length=2.4, weight=2,
      require=["child"], mirror="free")
def _(c):
    c.key(0.2, la=(-10, 0, 120))
    c.wobble(0.3, 0.9, 4, "la", 16, base=(-10, 0, 150), axis=2)
    ears(c, 0.5, roll=26)
    c.key(1.1, la=(-20, 0, 10), root=(0, 26, 0), head=(0, -20, 0))
    c.cycle(1.2, 2.0, 0.3, {"rl": (-22, 0, 0), "ll": (22, 0, 0), "ra": (22, 0, 0), "la": (-22, 0, 0)},
            {"rl": (22, 0, 0), "ll": (-22, 0, 0), "ra": (-22, 0, 0), "la": (22, 0, 0)})
    c.key(2.0, root=(0, 22, 0), head=(-4, -16, 0))
    c.key(2.4)


# -- talk (gestures while their line types out) -----------------------------------------------------------------------
@clip("talk_brag", "Thumbs the chest then sweeps an arm at it all", trigger="talk", length=3.6, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.3, waist=(-6, 0, 0), head=(-12, 0, 0))
    c.key(0.5, la=(-40, -26, 0))
    c.key(0.68, la=(-30, -22, 0))
    c.key(0.85, la=(-42, -26, 0))
    ears(c, 0.6, roll=24, pitch=-6)
    c.key(1.4, la=(-72, 40, 50), head=(-10, -20, 0), waist=(-4, -6, 0))
    c.key(1.6, la=(-76, 44, 56))
    c.hold(1.7, 2.3, la=(-74, 44, 54), head=(-12, -22, -4), waist=(-4, -6, 0))
    flick(c, 2.0, amp=18, base=(-6, 0, 24))
    c.key(2.8, la=(-20, 0, 10), head=(-4, 0, 0), waist=(0, 0, 0))
    c.key(3.6)


@clip("talk_swagger", "Rocks on the heels with a thumb in the belt", trigger="talk", length=3.4, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.35, la=(-14, -12, -2), waist=(-5, 0, 0), head=(-12, 0, 0))
    ears(c, 0.35, roll=20, pitch=-6)
    c.cycle(0.5, 2.5, 1.0, {"root": (-4, 6, -2), "head": (-14, -4, -4)}, {"root": (3, -6, 2), "head": (-8, 4, 4)})
    flick(c, 1.0, amp=18, base=(-6, 0, 20))
    flick(c, 2.0, amp=18, base=(-6, 0, 20))
    c.key(2.5, waist=(-5, 0, 0), la=(-14, -12, -2))
    snort(c, 2.6, base=(-12, 0, 0), amp=7)
    c.key(3.4)


@clip("talk_shrug", "Big shrug: what can you do", trigger="talk", length=2.4, weight=2)
def _(c):
    c.key(0.2, head=(4, 0, 0))
    c.key(0.5, ra=(-18, 0, 24), la=(-18, 0, 24), ra_pos=(0, -1, 0), la_pos=(0, -1, 0), head=(4, 0, 14))
    ears(c, 0.5, roll=20, pitch=-6)
    c.key(0.65, ra=(-20, 0, 27), la=(-20, 0, 27), head=(4, 0, 16))
    c.hold(0.75, 1.3, ra=(-18, 0, 24), la=(-18, 0, 24), ra_pos=(0, -1, 0), la_pos=(0, -1, 0), head=(4, 0, 14))
    c.key(1.8, ra=(0, 0, 0), la=(0, 0, 0), ra_pos=(0, 0, 0), la_pos=(0, 0, 0), head=(0, 0, 2))
    c.key(2.4)


@clip("talk_story", "Paints the story with a sweep and a slam", trigger="talk", length=4.2, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.4, la=(-70, -26, 0), head=(0, 12, 0), waist=(0, 6, 0))
    c.key(1.5, la=(-80, 30, 30), head=(-4, -18, 0), waist=(0, -6, 0))
    ears(c, 1.5, roll=14)
    c.key(2.0, la=(-150, 0, -14), head=(-16, -6, 0), waist=(-5, 0, 0))
    ears(c, 2.0, roll=30, pitch=-10)
    c.key(2.2, la=(-156, 0, -16))
    c.key(2.45, la=(-34, 0, 0), head=(10, 0, 0), waist=(6, 0, 0), root_pos=(0, 0.6, 0))
    c.key(2.6, root_pos=(0, 0, 0), head=(6, 0, 0))
    flick(c, 2.45, amp=26, base=(-10, 0, 30))
    c.key(3.4, la=(-14, 0, 0), head=(0, 0, 0), waist=(0, 0, 0))
    c.key(4.2)


@clip("talk_grumble", "Grumbles with the head low and ears back", trigger="talk", length=3.0, weight=1.5)
def _(c):
    c.key(0.4, head=(12, 0, 0), waist=(4, 0, 0))
    ears(c, 0.4, roll=-12, pitch=22)
    c.wobble(0.5, 2.1, 3, "head", 8, base=(12, 0, 0), axis=1)
    snort(c, 2.2, base=(10, 0, 0), amp=7)
    ears(c, 2.3, roll=-12, pitch=22)
    c.key(3.0)


@clip("talk_point_jab", "Jabs a finger at you to make the point", trigger="talk", length=2.8, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.35, la=(-90, 4, 0), head=(4, 0, 0))
    ears(c, 0.35, roll=-6, pitch=14)
    for t in (0.6, 1.0, 1.4):
        c.key(t, la=(-94, 4, 0), la_pos=(0, 0, -1.2), head=(9, 0, 0))
        c.key(t + 0.2, la=(-88, 4, 0), la_pos=(0, 0, 0), head=(3, 0, 0))
    c.key(2.0, la=(-86, 4, 0), head=(2, 0, -6))
    c.key(2.8)


# -- listen (while you choose a reply) --------------------------------------------------------------------------------
@clip("listen_skeptic", "Tilts the head and squints skeptically", trigger="listen", length=3.4, weight=2)
def _(c):
    c.key(0.6, head=(6, 8, 14))
    ears(c, 0.6, left=(-8, 0, 26), right=(6, 0, -10))
    c.hold(0.7, 2.2, head=(7, 10, 15))
    ears(c, 2.2, left=(-8, 0, 26), right=(6, 0, -10))
    snort(c, 2.35, base=(6, 10, 14), amp=6)
    c.key(3.4)


@clip("listen_greedy_lean", "Leans in rubbing the belly at the sound of gold", trigger="listen", length=3.6,
      weight=2, require=["adult"], mirror="never")
def _(c):
    c.key(0.6, waist=(10, 0, 0), head=(-6, 0, 0), la=(-32, -20, 0))
    ears(c, 0.6, roll=30, pitch=-20)
    c.wobble(0.7, 2.6, 3, "la", 7, base=(-32, -20, 0), axis=1)
    c.key(1.6, head=(-8, 0, 6))
    flick(c, 1.8, amp=16, base=(-20, 0, 30))
    c.key(2.6, waist=(9, 0, 0), head=(-6, 0, 4))
    c.key(3.6)


@clip("listen_nod_along", "Nods along with twitching ears", trigger="listen", length=3.0, weight=2)
def _(c):
    for t in (0.4, 0.85, 1.75, 2.15):
        c.key(t, head=(13, 0, 0))
        c.key(t + 0.22, head=(1, 0, 0))
    flick(c, 0.6, side="left", amp=18)
    flick(c, 1.95, side="right", amp=18)
    c.key(1.3, head=(2, -6, 3))
    c.key(3.0)


# -- chatting with another Piglin -------------------------------------------------------------------------------------
@clip("chat_boast", "Tells a big tale and slaps the belly laughing", trigger="chat_speak", length=4.2, weight=2,
      require=["adult"], mirror="never")
def _(c):
    c.key(0.4, la=(-60, 10, 10), head=(-4, 0, 0))
    c.key(1.0, la=(-148, -8, 0), head=(-18, 0, 0), waist=(-5, 0, 0))
    ears(c, 1.0, roll=30, pitch=-8)
    c.key(1.2, la=(-154, -8, 0))
    c.key(1.5, la=(-30, -20, 0), head=(-8, 0, 0), waist=(2, 0, 0))
    c.key(1.62, la=(-14, -14, 4))
    c.key(1.78, la=(-32, -20, 0))
    c.key(1.92, la=(-14, -14, 4))
    c.key(2.1, head=(-22, 0, 4), waist=(-7, 0, 0))
    c.wobble(2.15, 3.2, 7, "head", 5, base=(-20, 0, 4), decay=0.4)
    c.wobble(2.2, 3.2, 7, "left_ear", 14, base=(-6, 0, 24), axis=2, decay=0.4)
    c.wobble(2.15, 3.2, 7, "right_ear", 14, base=(-6, 0, 24), axis=2, decay=0.4)
    c.key(3.5, la=(-10, 0, 4), waist=(-2, 0, 0))
    c.key(4.2)


@clip("chat_nudge", "Nudges with an elbow and snickers", trigger="chat_speak", length=3.4, weight=2,
      mirror="never")
def _(c):
    # turns the free side to the neighbour, eyes still on them, and jabs the elbow out: nudge nudge
    c.key(0.35, waist=(4, 12, 0), head=(4, -12, 10), la=(-8, 0, 6))
    ears(c, 0.35, roll=16, pitch=-8)
    for t in (0.55, 0.9, 1.25):
        c.key(t, la=(-14, 0, 38), head=(8, -12, 12), waist=(4, 14, 0))
        c.key(t + 0.17, la=(-8, 0, 6), head=(3, -12, 10), waist=(4, 11, 0))
        flick(c, t, side="left", amp=18, base=(-8, 0, 16))
    # then the snicker: snout ducked, shoulders shaking, ears jiggling
    c.key(1.7, head=(14, -6, 6), waist=(6, 4, 0), la=(-6, 0, 4))
    c.wobble(1.75, 2.7, 8, "head", 4, base=(14, -6, 6))
    c.wobble(1.75, 2.7, 8, "waist", 2, base=(6, 4, 0))
    c.wobble(1.8, 2.7, 8, "left_ear", 12, base=(-8, 0, 16), axis=2)
    c.wobble(1.75, 2.7, 8, "right_ear", 12, base=(-8, 0, 16), axis=2)
    snort(c, 2.8, base=(6, -2, 2), amp=7)
    c.key(3.4)


@clip("chat_laugh_along", "Laughs and slaps a thigh", trigger="chat_listen", length=3.0, weight=2, mirror="never")
def _(c):
    c.key(0.3, head=(-16, 0, 0), waist=(-4, 0, 0))
    c.wobble(0.35, 1.9, 7, "head", 5, base=(-16, 0, 0), decay=0.4)
    c.wobble(0.4, 1.9, 7, "left_ear", 16, base=(-4, 0, 18), axis=2, decay=0.4)
    c.wobble(0.35, 1.9, 7, "right_ear", 16, base=(-4, 0, 18), axis=2, decay=0.4)
    c.key(0.6, la=(-34, 0, 6), waist=(6, 0, 0))
    c.key(0.75, la=(-2, 0, 4), waist=(10, 0, 0))
    c.key(1.0, la=(-34, 0, 6), waist=(5, 0, 0))
    c.key(1.15, la=(-2, 0, 4), waist=(9, 0, 0))
    c.key(1.6, la=(-6, 0, 2), waist=(0, 0, 0))
    c.key(3.0)


@clip("chat_ears_perk", "Ears prick up at the juicy bit", trigger="chat_listen", length=3.4, weight=2)
def _(c):
    c.key(0.6, head=(4, 0, 8))
    ears(c, 0.6, roll=-4, pitch=4)
    c.key(1.05, head=(-6, 0, 0), waist=(6, 0, 0))
    ears(c, 1.1, roll=36, pitch=-18)
    c.key(1.25, head=(-8, 0, 0))
    c.hold(1.35, 2.2, head=(-6, 0, -4), waist=(6, 0, 0))
    ears(c, 2.2, roll=32, pitch=-16)
    c.key(2.5, head=(12, 0, 0), waist=(3, 0, 0))
    c.key(2.75, head=(0, 0, 0))
    c.key(3.4)
