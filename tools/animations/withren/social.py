"""Withren meeting people: formal greetings, measured talk, patient listening."""
from kit import clip

FOLD = (-30, -45, 0)
CHEST = (-60, -50, 0)
CHIN = (-100, -55, 0)
BEHIND = (25, 35, 0)
PALM = (-132, 14, 4)          # an open hand raised at head height
LIFT_LA = (-55, 10, 0)
LIFT_LANTERN = (2.8, -3, -6.5)
# staff or glaive in the right hand and no hip lantern: they greet, talk and part with work.py's bearer clips
BEARERS = ["job:remembrancer", "job:ashwarden"]


# -- greet ---------------------------------------------------------------------------------------------------------
@clip("solemn_bow", "Solemn bow", trigger="greet", length=3.6, weight=3, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-56, -46, 0), la=(6, 0, 2), head=(4, 0, 0))
    c.key(0.9, ra=CHEST, head=(6, 0, 0))
    c.key(1.5, ra=CHEST, waist=(26, 0, 0), head=(14, 0, 0), la=(10, 0, 2))
    c.key(1.75, waist=(28, 0, 0), head=(15, 0, 0))
    c.key(2.2, ra=CHEST, waist=(26, 0, 0), head=(14, 0, 0))
    c.key(2.8, ra=(-40, -36, 0), waist=(0, 0, 0), head=(2, 0, 0), la=(0, 0, 0))
    c.key(3.6)


@clip("raised_palm", "Raises an open palm", trigger="greet", length=3.0, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.7, ra=(-128, 12, 4), head=(-4, 0, 0))
    c.key(0.9, ra=(-136, 15, 4))
    c.key(1.1, ra=PALM, head=(8, 0, 0))
    c.key(1.6, ra=PALM, head=(10, 0, 0))
    c.key(2.0, ra=(-128, 14, 4), head=(0, 0, 0))
    c.key(3.0)


@clip("offer_name", "Greets with the kept name", trigger="greet", length=3.8, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-56, -46, 0), head=(8, 0, 0))
    c.key(0.9, ra=CHEST, head=(10, 0, 0))
    c.key(1.4, ra=CHEST, head=(9, 0, 0))
    c.key(2.0, ra=(-74, 14, 6), head=(-2, 0, 0), waist=(4, 0, 0))
    c.key(2.2, ra=(-78, 16, 6))
    c.key(2.9, ra=(-74, 14, 6), head=(6, 0, 0), waist=(4, 0, 0))
    c.key(3.8)


@clip("lantern_greet", "Lifts the lantern to see you", trigger="greet", length=3.2, weight=2, mirror="never",
      avoid=BEARERS)
def _(c):
    c.key(0.7, la=(-62, 12, 0), lantern_pos=(3, -3.4, -7.2), lantern=(0, 0, 8), head=(-2, -4, 0))
    c.key(1.0, la=(-66, 12, 0), lantern_pos=(3, -3.7, -7.8), lantern=(0, 0, -5))
    c.key(1.5, la=(-64, 12, 0), lantern=(0, 0, 3), head=(8, 0, 0), waist=(3, 0, 0))
    c.key(2.1, lantern_pos=(3, -3.5, -7.4), lantern=(0, 0, -1), head=(2, 0, 0))
    c.key(3.2)


# -- farewell ------------------------------------------------------------------------------------------------------
@clip("parting_bow", "Parting bow with folded hands", trigger="farewell", length=3.6, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.7, ra=FOLD, la=FOLD, head=(4, 0, 0))
    c.key(1.4, ra=FOLD, la=FOLD, waist=(30, 0, 0), head=(16, 0, 0))
    c.key(1.7, waist=(32, 0, 0))
    c.key(2.2, ra=FOLD, la=FOLD, waist=(30, 0, 0), head=(16, 0, 0))
    c.key(2.9, ra=FOLD, la=FOLD, waist=(0, 0, 0), head=(4, 0, 0))
    c.key(3.6)


@clip("go_remembered", "Go remembered", trigger="farewell", length=3.6, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.7, ra=PALM, head=(-2, 0, 0))
    c.key(1.3, ra=PALM, head=(8, 0, 0))
    c.key(1.9, ra=(-64, -48, 0), head=(10, 0, 0))
    c.key(2.1, ra=CHEST, head=(12, 0, 0), waist=(6, 0, 0))
    c.key(2.8, ra=CHEST, head=(10, 0, 0), waist=(5, 0, 0))
    c.key(3.6)


# -- talk (gestures while their line types out) --------------------------------------------------------------------
@clip("talk_open_palm", "Opens a palm", trigger="talk", length=3.0, weight=2, avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(-34, 4, 4), head=(4, 0, 0))
    c.key(0.9, ra=(-52, 18, 8), head=(-2, 4, 0))
    c.key(1.1, ra=(-55, 20, 8))
    c.key(1.9, ra=(-50, 16, 8), head=(6, 2, 0))
    c.key(2.4, ra=(-20, 4, 2), head=(2, 0, 0))
    c.key(3.0)


@clip("talk_trace_point", "Traces the point in the air", trigger="talk", length=3.4, weight=2,
      require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(-80, -16, 0), head=(-2, -4, 0))
    c.key(0.8, ra=(-90, -18, 0))
    c.key(1.2, ra=(-84, -2, 0), head=(-4, 0, 0))
    c.key(1.5, ra=(-94, 6, 0))
    c.key(1.8, ra=(-86, 10, 0), head=(2, 4, 0))
    c.key(2.1, ra=(-88, 8, 0), head=(6, 3, 0))
    c.key(2.7, ra=(-40, -4, 0), head=(2, 0, 0))
    c.key(3.4)


@clip("talk_from_chest", "Speaks from the chest", trigger="talk", length=3.2, weight=2, avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-56, -46, 0), head=(6, 0, 0))
    c.key(0.9, ra=CHEST, head=(8, 0, 0), waist=(3, 0, 0))
    c.key(1.4, head=(3, 0, -3))
    c.key(1.9, head=(9, 0, 0))
    c.key(2.4, ra=CHEST, head=(5, 0, 2), waist=(3, 0, 0))
    c.key(3.2)


@clip("talk_weigh", "Weighs the words", trigger="talk", length=3.6, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-44, 12, 6), la=(-44, 12, 6), head=(4, 0, 0))
    c.key(1.0, ra=(-52, 12, 6), la=(-38, 12, 6), head=(4, -6, -4))
    c.key(1.6, ra=(-38, 12, 6), la=(-52, 12, 6), head=(4, 6, 4))
    c.key(2.2, ra=(-48, 12, 6), la=(-42, 12, 6), head=(6, 0, 0))
    c.key(2.9, ra=(-24, 4, 2), la=(-24, 4, 2), head=(2, 0, 0))
    c.key(3.6)


@clip("talk_upward", "Points up to the Three", trigger="talk", length=3.6, weight=1.5, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(-120, -4, 0), head=(-6, 0, 0))
    c.key(1.0, ra=(-162, -6, 0), head=(-22, 0, 0), waist=(-3, 0, 0))
    c.key(1.2, ra=(-166, -6, 0), head=(-24, 0, 0))
    c.key(1.9, ra=(-162, -6, 0), head=(-20, 0, 0), waist=(-3, 0, 0))
    c.key(2.5, ra=(-60, -46, 0), head=(6, 0, 0), waist=(2, 0, 0))
    c.key(3.6)


@clip("talk_dry_aside", "A dry aside", trigger="talk", length=3.0, weight=1.5)
def _(c):
    c.key(0.5, head=(2, -16, 6), ra=(0, 0, 4))
    c.key(0.8, head=(4, -20, 8), ra=(-6, 0, 12), root_pos=(0, -0.3, 0))
    c.key(1.1, ra=(-2, 0, 4), root_pos=(0, 0, 0))
    c.wobble(1.2, 1.8, 6, "head", 3, base=(-4, -18, 6), axis=0)
    c.key(2.2, head=(4, 0, 0))
    c.key(3.0)


# -- listen (while you choose a reply) ----------------------------------------------------------------------------
@clip("listen_folded", "Listens with folded hands", trigger="listen", length=4.4, weight=2, avoid=BEARERS)
def _(c):
    c.key(0.8, ra=FOLD, la=FOLD, head=(6, 0, 3))
    c.key(1.8, head=(8, 0, 7))
    c.key(2.4, head=(12, 0, 6))
    c.key(2.8, head=(7, 0, 6))
    c.key(3.6, ra=FOLD, la=FOLD, head=(6, 0, 4))
    c.key(4.4)


@clip("listen_tilt", "Tilts the skull to listen", trigger="listen", length=3.8, weight=2)
def _(c):
    c.key(0.9, head=(4, 6, -12), root=(0, 0, -1))
    c.key(1.4, head=(5, 7, -14))
    c.key(2.6, head=(6, 6, -13), root=(0, 0, -1))
    c.key(3.1, head=(10, 0, -4), root=(0, 0, 0))
    c.key(3.8)


@clip("listen_considers", "Considers your words", trigger="listen", length=4.6, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.8, ra=(-96, -52, 0), la=(-28, -44, 0), head=(4, 0, 4))
    c.key(1.1, ra=CHIN, la=(-30, -46, 0), head=(6, 0, 6))
    c.key(2.2, head=(4, -4, 8))
    c.key(2.7, head=(12, -2, 6))
    c.key(3.0, head=(4, -2, 6))
    c.key(3.8, ra=CHIN, la=(-30, -46, 0), head=(6, 0, 4))
    c.key(4.6)


# -- chat with a neighbour -----------------------------------------------------------------------------------------
@clip("chat_recount", "Recounts a memory", trigger="chat_speak", length=4.8, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.9, head=(-16, 10, 0), ra=(-20, 0, 4), waist=(-2, 0, 0))
    c.key(1.6, head=(-18, 12, -3), ra=(-24, 0, 6))
    c.key(2.3, ra=(-58, 14, 8), head=(2, 0, 0), waist=(3, 0, 0))
    c.key(2.5, ra=(-62, 16, 8))
    c.key(3.2, ra=(-56, 10, 8), head=(6, 0, 3))
    c.key(3.9, ra=(-24, 2, 2), head=(2, 0, 0), waist=(0, 0, 0))
    c.key(4.8)


@clip("chat_trace_for", "Traces a name for a neighbour", trigger="chat_speak", length=4.6, weight=2,
      require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-60, -46, 0), head=(6, 0, 0))
    c.key(0.9, ra=CHEST, head=(8, 0, 0))
    c.key(1.5, ra=(-90, -14, 0), head=(-2, -4, 0))
    strokes = [(-98, -16), (-86, -10), (-96, -4), (-84, 2), (-94, 8)]
    t = 1.75
    for pitch, yaw in strokes:
        c.key(t, ra=(pitch, yaw, 0), head=(-2, yaw * 0.4, 0))
        t += 0.3
    c.key(t + 0.3, ra=(-70, 8, 4), head=(6, 0, 4))
    c.key(4.6)


@clip("chat_slow_nods", "Slow nods", trigger="chat_listen", length=4.2, weight=2)
def _(c):
    c.key(0.6, head=(2, 0, 4))
    for i, t in enumerate((1.0, 2.0, 3.0)):
        c.key(t, head=(13 - i * 2, 0, 4))
        c.key(t + 0.45, head=(1, 0, 4))
    c.key(4.2)


@clip("chat_listen_behind", "Listens with hands behind", trigger="chat_listen", length=4.8, weight=2,
      require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.9, ra=BEHIND, la=BEHIND, head=(4, 0, -6), waist=(3, 0, 0))
    c.key(2.0, head=(6, 0, -9), waist=(4, 0, 0))
    c.key(2.6, head=(12, 0, -6))
    c.key(3.0, head=(6, 0, -6))
    c.key(4.0, ra=BEHIND, la=BEHIND, head=(4, 0, -4), waist=(3, 0, 0))
    c.key(4.8)
