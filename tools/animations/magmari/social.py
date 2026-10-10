"""Magmari meeting people: big hospitable greetings, goodbyes that wish you warmth, chatty haggling hands while
they talk, and how they listen. Ferriers (lure-pole in the right hand) get their own greeting and talk.
"""
from kit import clip

NOPOLE = ["job:ferrier"]


# -- greet ---------------------------------------------------------------------------------------------------------
@clip("greet_open_arms", "Throws the arms wide in welcome", trigger="greet", length=2.6, weight=3, require=["adult"],
      avoid=NOPOLE + ["cold"])
def _(c):
    c.key(0.25, ra=(-10, -10, 4), la=(-10, -10, 4), head=(6, 0, 0), root_pos=(0, 0.4, 0))
    c.key(0.7, ra=(-35, 30, 62), rf=(-30, 0, 0), la=(-35, 30, 62), lf=(-30, 0, 0), head=(-10, 0, 0), crest=(10, 0, 0),
          tail=(28, 0, 0), tail_tip=(15, 0, 0), root=(-3, 0, 0), root_pos=(0, -0.6, 0))
    c.key(0.95, ra=(-38, 34, 68), la=(-38, 34, 68), head=(-12, 0, 3))
    c.key(1.6, ra=(-34, 30, 62), la=(-34, 30, 62), head=(-8, 0, 5), root_pos=(0, 0, 0))
    c.key(1.85, tail=(26, 16, 0), tail_tip=(10, 12, 0))
    c.key(2.1, tail=(24, -14, 0), tail_tip=(8, -10, 0))
    c.key(2.5)


@clip("greet_big_wave", "Waves big overhead", trigger="greet", length=2.4, weight=2, avoid=NOPOLE)
def _(c):
    c.key(0.35, ra=(-10, 0, 140), rf=(-30, 0, 0), head=(-6, 0, -6), tail=(16, 0, 0), crest=(6, 0, 0))
    c.wobble(0.4, 1.8, 2.5, "ra", 16, base=(-10, 0, 148), axis=2)
    c.wave(0.4, 1.9, 0.7, "tail", 18, base=(18, 0, 0), axis=1)
    c.key(0.6, root_pos=(0, -0.8, 0))
    c.key(0.9, root_pos=(0, 0, 0))
    c.key(2.2)


@clip("greet_hand_heart", "Hand to the heart and a little bow", trigger="greet", length=2.6, weight=2,
      require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.45, ra=(-25, -35, 0), rf=(-85, 0, 0), head=(2, 0, 0))
    c.key(0.9, waist=(18, 0, 0), head=(10, 0, 0), crest=(12, 0, 0), tail=(22, 0, 0), la=(6, 0, 6))
    c.key(1.4, waist=(16, 0, 0), head=(8, 0, 0))
    c.key(1.8, waist=(-2, 0, 0), head=(-6, 0, 4), crest=(-2, 0, 0), tail=(14, 10, 0), ra=(-24, -35, 0), rf=(-80, 0, 0))
    c.key(2.4)


@clip("greet_beckon_fire", "Beckons them in to the warmth", trigger="greet", length=3.0, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.4, ra=(-62, 8, 8), rf=(-20, 0, 0), head=(-4, 0, 6))
    for t in (0.7, 1.15, 1.6):
        c.key(t, rf=(-85, 0, 0), ra=(-58, 8, 8))
        c.key(t + 0.22, rf=(-25, 0, 0), ra=(-62, 8, 8))
    c.key(1.4, la=(-10, 40, 50), lf=(-20, 0, 0), root=(0, -10, 0), head=(-4, -20, 0), tail=(18, 16, 0))
    c.key(2.2, la=(-14, 44, 54), head=(-6, -10, 4), tail=(18, -10, 0))
    c.key(2.8)


@clip("greet_child_peekaboo", "Peekaboo then arms out wide", trigger="greet", length=2.4, weight=3,
      require=["child"])
def _(c):
    # hands over the face in a little crouch, then pop up with the arms flung wide (the old hop with both arms up
    # was happy_flare again)
    hide = dict(ra=(-110, 0, 25), la=(-110, 0, 25), root_pos=(0, 0.6, 0), rl=(-14, 0, 0), ll=(-14, 0, 0),
                rs=(18, 0, 0), ls=(18, 0, 0), tail=(20, 40, 0), crest=(-8, 0, 0))
    c.key(0.25, head=(2, 0, 0), **hide)
    c.key(0.6, head=(4, 0, 6))
    c.key(0.9, head=(2, 0, -6), **hide)
    c.key(1.1, root_pos=(0, -1.8, 0), rl=(0, 0, 0), ll=(0, 0, 0), rs=(0, 0, 0), ls=(0, 0, 0), ra=(-20, 0, 95),
          la=(-20, 0, 95), head=(-12, 0, 0), crest=(10, 0, 0), tail=(30, 0, 0))
    c.key(1.35, root_pos=(0, 0.3, 0))
    c.key(1.55, root_pos=(0, 0, 0), ra=(-20, 0, 90), la=(-20, 0, 90), head=(-8, 0, 6), tail=(28, 18, 0))
    c.key(1.8, tail=(28, -18, 0))
    c.key(2.3)


@clip("greet_pole_raise", "Raises the lure-pole in greeting", trigger="greet", length=2.8, weight=3,
      require=["adult", "job:ferrier"], mirror="never")
def _(c):
    # the pole is lifted upright (arm about -50, the hand raised with ra_pos); pitching the arm further leaned it
    # back over the head
    c.key(0.25, ra=(6, 0, 4), root_pos=(0, 0.4, 0))
    c.key(0.65, ra=(-50, 0, 7), rf=(-12, 0, 0), ra_pos=(0, -1.5, 0), la=(-10, 0, 135), lf=(-25, 0, 0),
          head=(-10, 0, 0), crest=(8, 0, 0), tail=(22, 0, 0), root_pos=(0, -0.5, 0))
    c.key(0.9, ra=(-52, 0, 7), ra_pos=(0, -2.0, 0), la=(-10, 0, 150))
    c.key(1.15, la=(-10, 0, 125))
    c.key(1.4, ra=(-38, 0, 7), rf=(-28, 0, 0), ra_pos=(0, 0, 0), root_pos=(0, 0.8, 0), la=(-10, 0, 145),
          head=(4, 0, 0))
    c.key(1.6, root_pos=(0, 0, 0), head=(-4, 0, 5))
    c.key(2.0, ra=(-36, 0, 7), la=(-6, 0, 30), lf=(-10, 0, 0), tail=(18, 12, 0))
    c.key(2.6)


# -- farewell ------------------------------------------------------------------------------------------------------
@clip("farewell_wave_tail", "Waves with hand and tail", trigger="farewell", length=2.8, weight=2, avoid=NOPOLE)
def _(c):
    c.key(0.4, ra=(-20, 10, 100), rf=(-35, 0, 0), head=(4, 0, -8), tail=(14, 0, 0))
    c.wave(0.4, 2.2, 0.9, "ra", 18, base=(-20, 10, 105), axis=2)
    c.wave(0.5, 2.3, 0.9, "tail", 24, base=(16, 0, 0), axis=1)
    c.key(2.6)


@clip("farewell_keep_warm", "Touches the brow and wishes them warmth", trigger="farewell", length=2.8, weight=2,
      require=["adult"], avoid=NOPOLE)
def _(c):
    # two fingers to the brow, then the hand goes out to them (reaching up to the crest sank the forearm into the
    # head, and was crest_scratch's pose)
    c.key(0.5, ra=(-50, -10, 10), rf=(-100, 0, 0), head=(8, 0, 0), crest=(6, 0, 0))
    c.key(0.75, head=(12, 0, 0), ra=(-50, -10, 10), rf=(-100, 0, 0))
    c.key(1.3, ra=(-72, 12, 10), rf=(-14, 0, 0), head=(-4, 0, 0), waist=(8, 0, 0), crest=(0, 0, 0), tail=(16, 0, 0))
    c.key(1.55, ra=(-68, 14, 12), waist=(10, 0, 0))
    c.key(2.0, ra=(-60, 12, 10), rf=(-20, 0, 0), waist=(4, 0, 0), head=(-2, 0, 4))
    c.key(2.6)


@clip("farewell_pole_dip", "Dips the lure in farewell", trigger="farewell", length=2.8, weight=3,
      require=["adult", "job:ferrier"], mirror="never")
def _(c):
    c.key(0.35, ra=(-14, 0, 0), head=(-4, 0, 0))
    c.key(0.8, ra=(32, 0, 4), rf=(-4, 0, 0), waist=(12, 0, 0), head=(10, 0, 0), crest=(10, 0, 0), la=(-14, 0, 60),
          lf=(-30, 0, 0))
    c.key(1.2, ra=(30, 0, 4), waist=(10, 0, 0), la=(-14, 0, 75))
    c.key(1.5, la=(-14, 0, 55))
    c.key(1.9, ra=(-20, 0, 2), waist=(0, 0, 0), head=(-4, 0, 5), la=(-10, 0, 20), crest=(0, 0, 0), tail=(16, 10, 0))
    c.key(2.6)


# -- talk (while their line types out) -----------------------------------------------------------------------------
@clip("talk_haggle_chop", "Chops a price into the palm", trigger="talk", length=3.2, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.35, la=(-40, -25, 0), lf=(-55, 0, 0), ra=(-62, -28, 0), rf=(-70, 0, 0), head=(6, 0, 0))
    c.key(0.6, ra=(-38, -30, 0), rf=(-45, 0, 0), head=(12, 0, 0), crest=(5, 0, 0))
    c.key(0.85, ra=(-62, -28, 0), rf=(-72, 0, 0), head=(4, 0, 0))
    c.key(1.1, ra=(-38, -30, 0), rf=(-45, 0, 0), head=(12, 0, 0))
    c.key(1.7, ra=(-40, 10, 20), rf=(-115, 0, 0), la=(-10, 0, 8), lf=(-15, 0, 0), head=(-4, 0, 8), tail=(16, 0, 0),
          crest=(8, 0, 0))
    c.key(1.95, ra=(-44, 10, 20), head=(-4, 0, 10))
    c.wobble(2.0, 2.6, 4, "ra", 4, base=(-42, 10, 20))
    c.key(2.6, rf=(-110, 0, 0), head=(2, 0, 6))
    c.key(3.05)


@clip("talk_two_hands", "Talks with both hands", trigger="talk", length=3.6, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.4, ra=(-35, 15, 10), rf=(-45, 0, 0), la=(-20, 5, 6), lf=(-30, 0, 0), head=(0, 6, 6))
    c.key(1.0, ra=(-22, 5, 6), rf=(-30, 0, 0), la=(-38, 18, 12), lf=(-50, 0, 0), head=(2, -6, -6), tail=(10, 14, 0))
    c.key(1.6, ra=(-40, 18, 14), rf=(-50, 0, 0), la=(-24, 5, 6), lf=(-34, 0, 0), head=(-2, 6, 6), tail=(10, -12, 0))
    # ends on an angler's "this big!": hands out in front, held apart (it used to end on talk_shrug's pose)
    c.key(2.1, ra=(-55, 12, 0), rf=(-35, 0, 0), la=(-55, 12, 0), lf=(-35, 0, 0), head=(-4, 0, 0), crest=(6, 0, 0))
    c.key(2.4, ra=(-58, 20, 0), rf=(-30, 0, 0), la=(-58, 20, 0), lf=(-30, 0, 0), head=(6, 0, 0), root_pos=(0, -0.4, 0))
    c.key(2.8, ra=(-58, 22, 0), rf=(-30, 0, 0), la=(-58, 22, 0), lf=(-30, 0, 0), head=(2, 0, 6), root_pos=(0, 0, 0))
    c.key(3.4)


@clip("talk_point_sea", "Points out across the lava sea", trigger="talk", length=3.4, weight=1.5, require=["adult"],
      mirror="never")
def _(c):
    c.key(0.3, la=(-20, 0, 6), head=(2, 0, 0))
    c.key(0.7, la=(-88, 34, 10), lf=(-6, 0, 0), head=(-6, -30, 0), root=(0, -10, 0), crest=(6, 0, 0), tail=(12, 14, 0))
    c.key(0.9, la=(-92, 36, 10))
    c.key(1.6, la=(-88, 34, 10), head=(-6, -32, 4))
    c.key(2.1, head=(4, 0, 0), root=(0, -4, 0), la=(-50, 20, 20), lf=(-30, 0, 0))
    c.key(2.35, head=(-2, 0, 0))
    c.key(2.6, head=(4, 0, 0))
    c.key(3.2)


@clip("talk_shrug", "What can you do? shrug", trigger="talk", length=2.6, weight=2, avoid=NOPOLE)
def _(c):
    # not for the Ferrier: the shrug swung the pole up past the face
    c.key(0.3, root_pos=(0, 0.3, 0))
    c.key(0.75, ra=(-32, 18, 18), rf=(-48, 0, 0), la=(-32, 18, 18), lf=(-48, 0, 0), head=(-4, 0, 12), crest=(0, 0, 10),
          root_pos=(0, -0.7, 0), tail=(6, 12, 0))
    c.key(1.4, ra=(-34, 20, 20), la=(-34, 20, 20), head=(-6, 0, 14), crest=(0, 0, 12))
    c.key(1.7, root_pos=(0, 0, 0), tail=(6, -14, 0))
    c.key(2.4)


@clip("talk_lean_secret", "Leans in with a secret", trigger="talk", length=3.6, weight=1.5, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.5, waist=(12, 0, 0), root_pos=(0, 0, -0.8), head=(-4, 18, 0), ra=(-30, -15, 25), rf=(-120, 0, 0),
          crest=(-8, 0, 0))
    c.key(1.0, head=(-6, -16, 0))
    c.key(1.5, head=(-6, 0, 6), waist=(14, 0, 0), tail=(10, 0, 0))
    c.key(2.4, head=(-4, 0, 8), waist=(13, 0, 0), ra=(-30, -15, 25), rf=(-120, 0, 0), crest=(-8, 0, 0))
    c.key(2.8, waist=(-2, 0, 0), root_pos=(0, 0, 0), head=(6, 0, -4), ra=(-10, 0, 8), rf=(-20, 0, 0), crest=(6, 0, 0),
          tail=(16, 12, 0))
    c.key(3.1, head=(-2, 0, -4))
    c.key(3.5)


@clip("talk_child_bounce", "Bounces while chattering", trigger="talk", length=2.8, weight=3, require=["child"])
def _(c):
    c.cycle(0.2, 2.4, 0.7, dict(ra=(-35, 0, 8), la=(15, 0, 8), head=(-6, 0, 6), root_pos=(0, -0.9, 0)),
            dict(ra=(15, 0, 8), la=(-35, 0, 8), head=(-2, 0, -6), root_pos=(0, 0, 0)))
    c.wave(0.2, 2.5, 0.7, "tail", 22, base=(20, 0, 0), axis=1)
    c.key(2.7)


@clip("talk_pole_lean", "Leans on the pole and talks with the free hand", trigger="talk", length=3.4, weight=3,
      require=["adult", "job:ferrier"], mirror="never")
def _(c):
    c.key(0.5, ra=(-30, 0, 5), rf=(-22, 0, 0), root=(0, 0, -4), head=(0, 0, -6), la=(-36, 14, 10), lf=(-48, 0, 0))
    c.key(1.0, la=(-30, 8, 8), lf=(-60, 0, 0), head=(4, 0, -4))
    c.key(1.4, la=(-40, 18, 14), lf=(-42, 0, 0), head=(-2, 4, -8), tail=(10, 12, 0))
    c.key(1.9, la=(-28, 6, 8), lf=(-62, 0, 0), head=(4, 0, -4))
    c.key(2.4, la=(-38, 22, 20), lf=(-40, 0, 0), head=(-4, 0, -6), crest=(6, 0, 0), tail=(10, -10, 0),
          ra=(-30, 0, 5), rf=(-22, 0, 0), root=(0, 0, -4))
    c.key(3.2)


# -- listen (while you choose a reply) -----------------------------------------------------------------------------
@clip("listen_chin", "Hand to the chin weighing it up", trigger="listen", length=4.0, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.6, ra=(-15, -15, 0), rf=(-115, 0, 0), la=(-5, -50, 0), lf=(-70, 0, 0), head=(4, 0, 10), crest=(0, 0, 6))
    c.key(1.5, head=(8, 0, 12))
    c.key(1.8, head=(2, 0, 12))
    c.key(2.1, head=(8, 0, 12), tail=(4, 10, 0), tail_tip=(0, 16, 0))
    c.key(2.4, tail=(4, 0, 0), tail_tip=(0, -10, 0))
    c.key(2.7, tail_tip=(0, 12, 0))
    c.key(3.1, head=(4, 0, 8), ra=(-15, -15, 0), rf=(-115, 0, 0), la=(-5, -50, 0), lf=(-70, 0, 0), crest=(0, 0, 6))
    c.key(3.7)


@clip("listen_nod_tail", "Nods along with the tail swaying", trigger="listen", length=3.6, weight=2)
def _(c):
    c.key(0.4, head=(4, 0, 4))
    c.key(0.75, head=(14, 0, 4), crest=(4, 0, 0))
    c.key(1.05, head=(0, 0, 4), crest=(0, 0, 0))
    c.key(1.4, head=(12, 0, 4))
    c.key(1.75, head=(2, 0, 6))
    c.wave(0.3, 3.2, 1.4, "tail", 16, base=(8, 0, 0), axis=1)
    c.key(2.6, head=(0, 0, -6), crest=(6, 0, 0))
    c.key(3.3)


@clip("listen_skeptic", "Folded arms at a doubtful offer", trigger="listen", length=4.2, weight=1.5,
      require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.6, ra=(-5, -55, 0), rf=(-75, 0, 0), la=(-5, -55, 0), lf=(-75, 0, 0), head=(-10, -14, -6), crest=(0, 0, -10),
          root=(0, 0, 2), tail=(-8, 0, 0))
    c.key(1.3, head=(-12, -16, -8))
    for t in (1.6, 2.2):
        c.key(t, tail_tip=(-20, 0, 0))
        c.key(t + 0.2, tail_tip=(4, 0, 0))
    c.key(2.8, head=(-4, 8, 6), crest=(0, 0, 8))
    c.key(3.2, head=(6, 6, 4), ra=(-5, -55, 0), rf=(-75, 0, 0), la=(-5, -55, 0), lf=(-75, 0, 0), root=(0, 0, 2),
          tail=(-8, 0, 0))
    c.key(4.0)


# -- chatting with one of their own --------------------------------------------------------------------------------
@clip("chat_gossip", "Gossips with a hand on the hip", trigger="chat_speak", length=3.8, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.5, ra=(10, -70, 30), rf=(-70, 0, 0), la=(-40, 6, 12), lf=(-62, 0, 0), head=(-2, 0, 8), root=(0, 0, -3))
    c.wobble(0.7, 1.6, 3, "lf", 14, base=(-62, 0, 0))
    c.key(1.9, la=(-58, 16, 18), lf=(-40, 0, 0), head=(-8, 0, -6), crest=(8, 0, 0), tail=(14, 14, 0))
    c.key(2.2, la=(-54, 16, 18), head=(4, 0, -6))
    c.key(2.6, la=(-30, 0, 10), lf=(-70, 0, 0), head=(-4, 0, 6), tail=(14, -12, 0))
    c.key(2.9, head=(6, 0, 6), ra=(10, -70, 30), rf=(-70, 0, 0), root=(0, 0, -3))
    c.key(3.6)


@clip("chat_laugh", "Tells a joke and slaps a thigh laughing", trigger="chat_speak", length=3.0, weight=2,
      mirror="never")
def _(c):
    c.key(0.4, la=(-50, 10, 10), lf=(-40, 0, 0), head=(-2, 0, 0))
    c.key(0.75, la=(8, 0, 10), lf=(-10, 0, 0), head=(-22, 0, 6), root=(-4, 0, 0), crest=(-8, 0, 0), tail=(22, 0, 0))
    c.wobble(0.8, 2.0, 4, "root_pos", 0.6, base=(0, -0.2, 0), axis=1)
    c.wobble(0.85, 2.0, 4, "head", 6, base=(-20, 0, 6), axis=0)
    c.key(1.25, la=(-35, 10, 10), lf=(-40, 0, 0))
    c.key(1.5, la=(8, 0, 10), lf=(-10, 0, 0))
    c.wave(0.9, 2.4, 0.6, "tail", 20, base=(22, 0, 0), axis=1)
    c.key(2.2, root=(-2, 0, 0), crest=(4, 0, 0))
    c.key(2.8)


@clip("chat_listen_lean", "Leans on one leg and listens", trigger="chat_listen", length=4.0, weight=2)
def _(c):
    c.key(0.8, root=(0, 4, 3), head=(2, -4, 8), tail=(4, -16, 0), rs=(4, 0, 0))
    c.key(1.6, head=(8, -4, 8))
    c.key(1.9, head=(0, -4, 8))
    c.key(2.4, head=(6, -2, 6), tail=(4, -10, 0))
    c.key(3.0, head=(2, 0, 4), crest=(4, 0, 0))
    c.key(3.8)


@clip("chat_listen_gasp", "Gasps at the news hands to cheeks", trigger="chat_listen", length=3.2, weight=1.5,
      require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.25, root_pos=(0, 0.3, 0))
    c.key(0.5, ra=(-22, -22, 0), rf=(-118, 0, 0), la=(-22, -22, 0), lf=(-118, 0, 0), head=(-12, 0, 0), crest=(10, 0, 0),
          crest_pos=(0, -0.8, 0), tail=(34, 0, 0), tail_tip=(20, 0, 0), root=(-4, 0, 0), root_pos=(0, -0.4, 0))
    c.key(0.75, crest_pos=(0, -0.4, 0), head=(-14, 0, 2))
    c.key(1.6, head=(-10, 0, -4), root_pos=(0, 0, 0), ra=(-22, -22, 0), rf=(-118, 0, 0), la=(-22, -22, 0),
          lf=(-118, 0, 0))
    c.key(2.0, ra=(-10, 0, 8), rf=(-30, 0, 0), la=(-10, 0, 8), lf=(-30, 0, 0), head=(8, 0, 0), crest=(0, 0, 0),
          crest_pos=(0, 0, 0), tail=(12, 0, 0), tail_tip=(0, 0, 0), root=(0, 0, 0))
    c.key(2.3, head=(-2, 0, 0))
    c.key(3.0)
