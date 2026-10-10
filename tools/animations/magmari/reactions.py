"""Magmari reactions: gifts, friendship, replies that land or sting, and blows. Big, quick and tail-led.

Clips that raise or fold the right arm avoid the Ferrier (the lure-pole would swing through or over the head);
left-handed ones are kept as written so the Ferrier can play them too, and the Ferrier has pole versions of happy
and pleased that keep the pole upright.
"""
from kit import clip

NOPOLE = ["job:ferrier"]


@clip("happy_hop_clap", "Hops and claps", trigger="happy", length=2.4, weight=2, avoid=NOPOLE)
def _(c):
    c.key(0.2, root_pos=(0, 0.6, 0), ra=(-40, 20, 20), la=(-40, 20, 20), rf=(-30, 0, 0), lf=(-30, 0, 0))
    c.key(0.45, root_pos=(0, -2.2, 0), head=(-12, 0, 0), crest=(10, 0, 0), tail=(32, 0, 0), tail_tip=(16, 0, 0),
          ra=(-62, -30, 0), la=(-62, -30, 0))
    c.key(0.7, root_pos=(0, 0.3, 0), ra=(-55, 15, 15), la=(-55, 15, 15))
    c.key(0.9, root_pos=(0, 0, 0), ra=(-62, -30, 0), la=(-62, -30, 0))
    c.key(1.1, ra=(-55, 15, 15), la=(-55, 15, 15))
    c.key(1.3, ra=(-62, -30, 0), la=(-62, -30, 0))
    c.wave(0.6, 2.0, 0.5, "tail", 22, base=(28, 0, 0), axis=1)
    c.key(1.7, head=(-8, 0, 6), ra=(-30, 0, 10), la=(-30, 0, 10))
    c.key(2.2)


@clip("happy_flare", "Arms up and the crest flares", trigger="happy", length=2.6, weight=2, avoid=NOPOLE)
def _(c):
    # not for the Ferrier: the raised right arm swung the pole down behind the head
    c.key(0.2, root_pos=(0, 0.5, 0), ra=(4, 0, 8), la=(4, 0, 8), head=(6, 0, 0))
    c.key(0.5, ra=(-12, 0, 150), la=(-12, 0, 150), rf=(-14, 0, 0), lf=(-14, 0, 0), head=(-18, 0, 0), crest=(12, 0, 0),
          crest_pos=(0, -0.9, 0), tail=(40, 0, 0), tail_tip=(20, 0, 0), root_pos=(0, -1.2, 0))
    c.key(0.7, ra=(-14, 0, 160), la=(-14, 0, 160), root_pos=(0, 0, 0))
    c.key(1.0, root_pos=(0, -0.8, 0), head=(-20, 0, 6))
    c.key(1.25, root_pos=(0, 0, 0), crest_pos=(0, -0.5, 0))
    c.wobble(1.0, 1.9, 4, "tail", 16, base=(36, 0, 0), axis=1)
    c.key(1.9, ra=(-12, 0, 145), la=(-12, 0, 145), head=(-14, 0, -4))
    c.key(2.4)


@clip("happy_pole_pump", "Pumps the pole and a fist for joy", trigger="happy", length=2.6, weight=2,
      require=["adult", "job:ferrier"], mirror="never")
def _(c):
    # the Ferrier's own joy: the pole stays upright (arm about -42) and is thrust up with each hop
    pole = dict(ra=(-42, 0, 6), rf=(-14, 0, 0))
    c.key(0.2, root_pos=(0, 0.6, 0), la=(-30, 0, 10), lf=(-60, 0, 0), head=(6, 0, 0), **pole)
    # the free fist punches up in front (not a side wave, which is greet_pole_raise)
    c.key(0.45, root_pos=(0, -2.0, 0), ra_pos=(0, -2.5, 0), la=(-140, 10, 0), lf=(-40, 0, 0), head=(-14, 0, 0),
          crest=(12, 0, 0), crest_pos=(0, -0.8, 0), tail=(36, 0, 0), tail_tip=(18, 0, 0))
    c.key(0.7, root_pos=(0, 0.3, 0), ra_pos=(0, 0, 0), la=(-90, 10, 0), lf=(-90, 0, 0))
    c.key(0.95, root_pos=(0, -1.6, 0), ra_pos=(0, -2.5, 0), la=(-140, 10, 0), lf=(-40, 0, 0), head=(-16, 0, 4))
    c.key(1.2, root_pos=(0, 0, 0), ra_pos=(0, 0, 0), la=(-90, 10, 0), lf=(-90, 0, 0), crest_pos=(0, -0.4, 0))
    c.key(1.45, ra_pos=(0, -2.0, 0), la=(-140, 10, 0), lf=(-40, 0, 0))
    c.key(1.7, ra_pos=(0, 0, 0), la=(-90, 10, 0), lf=(-90, 0, 0))
    c.wobble(1.0, 2.0, 4, "tail", 16, base=(34, 0, 0), axis=1)
    c.key(2.0, la=(-30, 0, 10), lf=(-40, 0, 0), head=(-8, 0, 6), **pole)
    c.key(2.5)


@clip("thanks_palms_bow", "Palms together and a bow", trigger="thanks", length=2.6, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    palms = dict(ra=(-38, -28, 0), rf=(-72, 0, 0), la=(-38, -28, 0), lf=(-72, 0, 0))
    c.key(0.45, head=(2, 0, 0), **palms)
    c.key(0.9, waist=(22, 0, 0), head=(12, 0, 0), crest=(14, 0, 0), tail=(26, 0, 0), tail_tip=(10, 0, 0))
    c.key(1.4, waist=(20, 0, 0), **palms)
    c.key(1.8, waist=(-2, 0, 0), head=(-4, 0, 5), crest=(0, 0, 0), tail=(16, 0, 0))
    c.key(2.4)


@clip("thanks_pat_chest", "Pats the chest with a nod", trigger="thanks", length=2.4, weight=2, mirror="never")
def _(c):
    c.key(0.4, la=(-28, -45, 0), lf=(-80, 0, 0), head=(4, 0, 0))
    c.key(0.65, la=(-24, -45, 0), lf=(-70, 0, 0), head=(12, 0, 0), crest=(8, 0, 0))
    c.key(0.85, la=(-30, -45, 0), lf=(-84, 0, 0), head=(4, 0, 0))
    c.key(1.05, la=(-24, -45, 0), lf=(-70, 0, 0), head=(12, 0, 0))
    c.key(1.3, la=(-28, -45, 0), lf=(-80, 0, 0), head=(-4, 0, 6), crest=(0, 0, 0), tail=(18, 14, 0),
          tail_tip=(0, 12, 0))
    c.key(1.6, tail=(18, -12, 0), tail_tip=(0, -10, 0))
    c.key(2.2)


@clip("decline_wave_off", "Waves it off with a head shake", trigger="decline", length=2.4, weight=2, mirror="never")
def _(c):
    c.key(0.35, la=(-58, -12, 10), lf=(-45, 0, 0), head=(4, 0, 0), crest=(-6, 0, 0))
    c.wobble(0.4, 1.5, 3, "la", 16, base=(-58, -12, 10), axis=1)
    c.wobble(0.4, 1.5, 3, "head", 16, base=(4, 0, 0), axis=1)
    c.key(1.7, root=(0, -12, 0), head=(4, -10, 0), tail=(6, 20, 0), crest=(-10, 0, 0), la=(-20, 0, 10), lf=(-20, 0, 0))
    c.key(2.3)


@clip("decline_scoff", "Scoffs and folds the arms", trigger="decline", length=3.0, weight=2, require=["adult"],
      avoid=NOPOLE)
def _(c):
    c.key(0.3, ra=(-40, -20, 0), rf=(-60, 0, 0), head=(4, 0, 0))
    c.key(0.55, ra=(-50, 30, 20), rf=(-20, 0, 0), head=(-14, 16, 0), crest=(0, 0, -10), root=(0, 16, 0),
          tail=(10, -24, 0))
    c.key(1.0, ra=(-5, -55, 0), rf=(-75, 0, 0), la=(-5, -55, 0), lf=(-75, 0, 0), head=(-16, 22, -6), root=(0, 20, 0))
    c.key(1.8, head=(-14, 24, -6), tail=(6, -20, 0), tail_tip=(0, -20, 0))
    c.key(2.1, tail_tip=(0, 10, 0))
    c.key(2.8)


@clip("pleased_point_nod", "Now you're talking point and nod", trigger="pleased", length=2.2, weight=2, avoid=NOPOLE)
def _(c):
    # not for the Ferrier: pointing with the pole hand flipped the pole back over the head
    c.key(0.25, ra=(-30, 0, 6), rf=(-40, 0, 0), root_pos=(0, 0.3, 0))
    c.key(0.5, ra=(-84, 12, 0), rf=(-8, 0, 0), head=(6, 0, -8), crest=(10, 0, 0), tail=(30, 0, 0), tail_tip=(14, 0, 0),
          root_pos=(0, -0.3, 0))
    c.key(0.65, ra=(-80, 12, 0))
    c.key(0.8, ra=(-86, 12, 0), head=(12, 0, -10))
    c.key(1.2, head=(-4, 0, -6), tail=(26, 14, 0))
    c.key(1.5, tail=(26, -12, 0))
    c.key(2.1)


@clip("pleased_pole_thump", "Thumps the pole down and grins", trigger="pleased", length=2.4, weight=2,
      require=["adult", "job:ferrier"], mirror="never")
def _(c):
    # the pole held upright (arm about -46) is lifted and thumped down twice, then the free hand points at you
    pole = dict(ra=(-46, 0, 6), rf=(-16, 0, 0))
    c.key(0.3, ra_pos=(0, -1.5, 0), head=(-4, 0, 0), root_pos=(0, 0.3, 0), **pole)
    c.key(0.45, ra_pos=(0, 0.5, 0), head=(10, 0, 0), crest=(8, 0, 0), tail=(24, 0, 0), tail_tip=(10, 0, 0))
    c.key(0.65, ra_pos=(0, -1.2, 0), head=(0, 0, -4))
    c.key(0.8, ra_pos=(0, 0.5, 0), head=(10, 0, -4), root_pos=(0, 0, 0))
    c.key(1.2, ra_pos=(0, 0, 0), la=(-62, 10, 10), lf=(-36, 0, 0), head=(-4, 0, -8))
    c.key(1.5, la=(-64, 10, 10), tail=(24, 14, 0), tail_tip=(0, 12, 0))
    c.key(1.8, tail=(24, -12, 0), tail_tip=(0, -10, 0), **pole)
    c.key(2.3)


@clip("annoyed_tail_lash", "Stamps and lashes the tail", trigger="annoyed", length=2.6, weight=2)
def _(c):
    c.key(0.3, crest=(-14, 0, 0), head=(6, 0, 0), rl=(-30, 0, 0), rs=(26, 0, 0), rft=(-10, 0, 0), root=(0, 0, 3))
    c.key(0.5, rl=(0, 0, 0), rs=(0, 0, 0), rft=(0, 0, 0), root_pos=(0, 0.6, 0), head=(12, 0, 0), crest=(-18, 0, 0))
    c.key(0.65, root_pos=(0, 0, 0))
    c.wobble(0.4, 1.8, 3, "tail", 32, base=(4, 0, 0), axis=1)
    c.key(1.0, head=(-8, 26, 4), root=(0, 10, 0))
    c.key(1.9, head=(-10, 28, 6), root=(0, 12, 0), crest=(-16, 0, 0))
    c.key(2.4)


@clip("hurt_flinch", "Flinches and guards", trigger="hurt", length=1.4, weight=2, blend=(0.05, 0.3), avoid=NOPOLE)
def _(c):
    c.key(0.12, root=(-6, 0, 0), root_pos=(0, 0, 1), head=(14, -10, 0), ra=(-50, -20, 0), rf=(-60, 0, 0),
          la=(-50, -20, 0), lf=(-60, 0, 0), crest=(-20, 0, 0), tail=(-30, 0, 0), tail_tip=(-20, 0, 0))
    c.key(0.3, root=(-4, 0, 0), head=(10, -8, 0))
    c.key(0.7, root=(-2, 0, 0), root_pos=(0, 0, 0.5), head=(6, -4, 0), ra=(-30, -15, 0), la=(-30, -15, 0),
          crest=(-10, 0, 0), tail=(-14, 0, 0))
    c.key(1.3)


@clip("hurt_stagger", "Staggers back a step", trigger="hurt", length=1.8, weight=1.5, require=["adult"],
      blend=(0.05, 0.3))
def _(c):
    c.key(0.15, root=(-8, 0, -4), root_pos=(0, 0, 1.5), head=(-14, 0, 8), ra=(-10, 0, 40), la=(-10, 0, 40),
          rf=(-20, 0, 0), lf=(-20, 0, 0), crest=(-16, 0, 0), tail=(30, 0, 0), ll=(20, 0, 0), ls=(10, 0, 0))
    c.key(0.4, root=(-5, 0, -2), root_pos=(0, 0, 2), head=(-6, 0, 4), ll=(14, 0, 0), ls=(8, 0, 0))
    c.key(0.9, root=(2, 0, 0), root_pos=(0, 0, 1), head=(10, 0, 0), ra=(-6, 0, 16), la=(-6, 0, 16), crest=(-8, 0, 0),
          tail=(10, 0, 0), ll=(0, 0, 0), ls=(0, 0, 0))
    c.key(1.7)
