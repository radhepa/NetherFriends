"""Withren reactions: gifts, friendship, replies, blows. Even their joy is formal; their pain is a rattle."""
from kit import clip

FOLD = (-30, -45, 0)
CHEST = (-60, -50, 0)
# staff or glaive in the right hand: clips that swing that arm would sweep the shaft through the skull
BEARERS = ["job:remembrancer", "job:ashwarden"]


# -- happy ---------------------------------------------------------------------------------------------------------
@clip("happy_flourish", "A rare flourish", trigger="happy", length=3.8, weight=2, require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(-60, 30, 20), head=(-6, 4, 0), waist=(-3, 0, 0))
    c.key(1.0, ra=(-90, 55, 34), head=(-10, 8, 0), waist=(-4, 4, 0), la=(6, 0, 6))
    c.key(1.2, ra=(-92, 58, 36))
    c.key(1.8, ra=CHEST, waist=(28, 0, 0), head=(14, 0, 0), la=(14, 0, 4))
    c.key(2.05, waist=(30, 0, 0))
    c.key(2.6, ra=CHEST, waist=(26, 0, 0), head=(12, 0, 0), la=(12, 0, 4))
    c.key(3.2, ra=(-30, -20, 0), waist=(0, 0, 0), head=(-2, 0, 0), la=(0, 0, 0))
    c.key(3.8)


@clip("happy_rattle", "Rattles with laughter", trigger="happy", length=3.4, weight=2)
def _(c):
    c.key(0.4, head=(-12, 0, 4), waist=(-4, 0, 0), ra=(-6, 0, 12), la=(-6, 0, 12))
    c.wobble(0.5, 2.3, 7, "head", 5, base=(-16, 0, 4), axis=0, decay=0.4)
    c.wobble(0.5, 2.3, 7, "waist", 2, base=(-5, 0, 0), axis=0, decay=0.4)
    c.wobble(0.5, 2.3, 7, "right_arm", 5, base=(-8, 0, 14), axis=2, decay=0.4)
    c.wobble(0.5, 2.3, 7, "left_arm", 5, base=(-8, 0, 14), axis=2, decay=0.4)
    c.wobble(0.5, 2.3, 3.5, "root_pos", 0.4, base=(0, -0.2, 0), axis=1)
    c.key(2.8, head=(8, 0, 0), waist=(2, 0, 0), ra=(0, 0, 2), la=(0, 0, 2))
    c.key(3.4)


@clip("happy_hands_to_three", "Lifts both hands to the Three", trigger="happy", length=4.0, weight=1.5,
      require=["adult"], mirror="never", avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(8, 0, 4), la=(8, 0, 4), head=(6, 0, 0))
    c.key(1.4, ra=(-14, 0, 124), la=(-14, 0, 124), head=(-24, 0, 0), waist=(-5, 0, 0))
    c.key(1.7, ra=(-14, 0, 130), la=(-14, 0, 130), head=(-26, 0, 0), waist=(-6, 0, 0))
    c.key(2.5, ra=(-14, 0, 126), la=(-14, 0, 126), head=(-24, 0, 0), waist=(-5, 0, 0))
    c.key(3.2, ra=CHEST, la=CHEST, head=(10, 0, 0), waist=(4, 0, 0))
    c.key(4.0)


# -- thanks --------------------------------------------------------------------------------------------------------
@clip("thanks_deep_bow", "Bow of thanks", trigger="thanks", length=3.6, weight=2, require=["adult"],
      mirror="never", avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-56, -46, 0), la=(-56, -46, 0), head=(4, 0, 0))
    c.key(0.85, ra=CHEST, la=CHEST)
    c.key(1.5, ra=CHEST, la=CHEST, waist=(34, 0, 0), head=(16, 0, 0))
    c.key(1.75, waist=(36, 0, 0))
    c.key(2.3, ra=CHEST, la=CHEST, waist=(33, 0, 0), head=(15, 0, 0))
    c.key(3.0, ra=(-30, -30, 0), la=(-30, -30, 0), waist=(0, 0, 0), head=(2, 0, 0))
    c.key(3.6)


@clip("thanks_will_remember", "I will remember this", trigger="thanks", length=3.8, weight=2,
      require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-150, 2, -2), head=(0, 0, 4))
    c.key(0.9, ra=(-160, 0, -4), head=(0, 0, 6))
    c.key(1.1, ra=(-154, 0, -4))
    c.key(1.3, ra=(-160, 0, -4))
    c.key(2.0, ra=(-64, -48, 0), head=(6, 0, 0))
    c.key(2.2, ra=CHEST, head=(12, 0, 0), waist=(5, 0, 0))
    c.key(2.9, ra=CHEST, head=(10, 0, 0), waist=(4, 0, 0))
    c.key(3.8)


# -- decline -------------------------------------------------------------------------------------------------------
@clip("decline_slow_shake", "Slow shake of the skull", trigger="decline", length=3.2, weight=2)
def _(c):
    c.key(0.5, ra=(-24, 16, 8), head=(6, 0, 0))
    c.key(0.9, head=(6, -15, 0), ra=(-30, 20, 10))
    c.key(1.5, head=(6, 14, 0))
    c.key(2.1, head=(7, -10, 0))
    c.key(2.5, head=(8, 2, 0), ra=(-26, 16, 8))
    c.key(3.2)


@clip("decline_palm", "Raises a hand: no", trigger="decline", length=3.2, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.45, ra=(-74, -4, 0), head=(4, 6, 0), root=(0, 3, 0))
    c.key(0.6, ra=(-80, -4, 0))
    c.key(1.4, ra=(-78, -4, 0), head=(10, 18, -4), root=(0, 6, 0), waist=(0, 4, 0))
    c.key(2.2, ra=(-30, -4, 0), head=(8, 14, -2), root=(0, 5, 0), waist=(0, 3, 0))
    c.key(3.2)


# -- pleased / annoyed ---------------------------------------------------------------------------------------------
@clip("pleased_nod", "Approving nod", trigger="pleased", length=2.6, weight=2)
def _(c):
    c.key(0.4, head=(-4, 0, 0))
    c.key(0.9, head=(16, 0, 0), waist=(4, 0, 0))
    c.key(1.15, head=(14, 0, 0))
    c.key(1.7, head=(0, 0, 3), waist=(0, 0, 0))
    c.key(2.6)


@clip("annoyed_bone_tap", "Taps a bony foot", trigger="annoyed", length=3.4, weight=2, require=["adult"],
      avoid=BEARERS)
def _(c):
    c.key(0.5, ra=(-46, -56, 0), la=(-44, -54, 0), head=(10, 0, 6), waist=(-2, 0, 0))
    c.cycle(0.7, 2.6, 0.4, dict(rl=(-10, 0, 0)), dict(rl=(0, 0, 0)))
    c.key(1.6, head=(12, 0, 8))
    c.key(2.6, ra=(-46, -56, 0), la=(-44, -54, 0), head=(6, -10, 4))
    c.key(3.4)


@clip("pleased_hand_heart", "Lays a hand on the heart", trigger="pleased", length=3.0, weight=2,
      require=["adult"], avoid=BEARERS)
def _(c):
    c.key(0.6, ra=(-56, -46, 0), head=(2, 0, 3))
    c.key(0.85, ra=CHEST, head=(6, 0, 5), waist=(3, 0, 0))
    c.key(1.6, ra=CHEST, head=(9, 0, 5), waist=(4, 0, 0))
    c.key(2.2, ra=(-30, -26, 0), head=(2, 0, 2), waist=(0, 0, 0))
    c.key(3.0)


@clip("annoyed_rattling_sigh", "A rattling sigh", trigger="annoyed", length=3.0, weight=2)
def _(c):
    c.key(0.6, ra=(-4, 0, 10), la=(-4, 0, 10), head=(-8, 0, 0), root_pos=(0, -0.7, 0))
    c.key(1.1, ra=(4, 0, -2), la=(4, 0, -2), head=(14, 0, 0), waist=(4, 0, 0), root_pos=(0, 0.3, 0))
    c.wobble(1.2, 2.2, 4, "head", 8, base=(14, 0, 0), axis=1, decay=0.5)
    c.key(2.4, ra=(0, 0, 0), la=(0, 0, 0), waist=(2, 0, 0), root_pos=(0, 0, 0))
    c.key(3.0)


# -- hurt ----------------------------------------------------------------------------------------------------------
@clip("hurt_stagger", "Staggers back with a rattle", trigger="hurt", length=1.8, weight=2, blend=(.06, .4),
      mirror="free")
def _(c):
    c.key(0.15, waist=(-12, 0, 4), head=(-16, 0, 6), ra=(-20, 0, 28), la=(-14, 0, 22), root_pos=(0, 0, 1.4),
          rl=(-12, 0, 0), ll=(8, 0, 0))
    c.wobble(0.2, 0.7, 10, "head", 4, base=(-10, 0, 4), axis=2)
    c.key(0.7, waist=(6, 0, 0), ra=(-10, 0, 12), la=(-8, 0, 10), root_pos=(0, 0, 1.0), rl=(-4, 0, 0),
          ll=(2, 0, 0))
    c.key(1.1, waist=(3, 0, 0), head=(6, 0, 0), ra=(-4, 0, 4), la=(-4, 0, 4), root_pos=(0, 0, 0.5))
    c.key(1.8)


@clip("hurt_clutch_ribs", "Clutches the ribs", trigger="hurt", length=2.0, weight=2, require=["adult"],
      blend=(.06, .4), avoid=BEARERS)
def _(c):
    c.key(0.15, waist=(20, 0, -4), head=(14, 0, -6), ra=(-34, -52, 0), la=(-20, 0, 16), root_pos=(0, 0.6, 0.6))
    c.key(0.3, waist=(22, 0, -5), head=(16, 0, -6))
    c.key(0.9, waist=(16, 0, -3), head=(12, 0, -4), ra=(-32, -50, 0), la=(-10, 0, 10), root_pos=(0, 0.4, 0.4))
    c.key(1.4, waist=(6, 0, 0), head=(4, 0, 0), ra=(-16, -20, 0), la=(-2, 0, 2), root_pos=(0, 0, 0))
    c.key(2.0)
