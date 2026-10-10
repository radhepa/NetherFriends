"""Magmari everyday idles: warming hands, basking, tail and crest business, a haggler's habits.

The Ferrier carries a long lure-pole on the right forearm, so clips that fold the right arm up avoid them.
"""
from kit import clip

NOPOLE = ["job:ferrier"]


@clip("warm_hands", "Rubs the hands warm", length=4.0, weight=2, require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.5, ra=(-30, -20, 0), rf=(-45, 0, 0), la=(-30, -20, 0), lf=(-45, 0, 0), head=(14, 0, 0), crest=(-4, 0, 0))
    c.cycle(0.7, 2.1, 0.32, dict(ra=(-34, -25, 0), la=(-27, -16, 0)), dict(ra=(-27, -16, 0), la=(-34, -25, 0)))
    c.key(2.6, ra=(-58, 8, 6), rf=(-28, 0, 0), la=(-58, 8, 6), lf=(-28, 0, 0), head=(4, 0, 0), waist=(7, 0, 0),
          crest=(6, 0, 0), tail=(14, 0, 0))
    c.key(3.2, ra=(-56, 8, 6), la=(-56, 8, 6), head=(2, 0, 3), waist=(6, 0, 0), tail=(18, 6, 0))
    c.key(3.9)


@clip("tail_glance", "Glances back at the tail", length=3.6, weight=2)
def _(c):
    c.key(0.35, head=(4, -10, 0))
    # the body turns more and the head less, so the jaw clears the shoulder
    c.key(0.9, head=(12, 54, -6), root=(0, 24, 0), tail=(6, -40, 0), tail_tip=(0, -25, 0))
    c.key(1.25, tail=(14, -52, 0), tail_tip=(0, -5, 0))
    c.key(1.55, tail=(8, -38, 0), tail_tip=(0, -38, 0))
    c.key(1.85, tail=(12, -48, 0), tail_tip=(0, -12, 0))
    c.key(2.2, head=(12, 56, -6), root=(0, 24, 0), tail=(6, -40, 0), tail_tip=(0, -20, 0))
    c.key(2.7, head=(0, -6, 0), root=(0, -2, 0), tail=(0, 8, 0), tail_tip=(0, 6, 0))
    c.key(3.3)


@clip("ash_shake", "Shakes the ash off the crest", length=2.6, weight=1.5)
def _(c):
    c.key(0.3, head=(10, 0, 0), crest=(8, 0, 0))
    c.wobble(0.45, 1.35, 5, "head", 20, base=(6, 0, 0), axis=2, decay=0.5)
    c.wobble(0.55, 1.5, 5, "crest", 16, axis=2, decay=0.4)
    c.wobble(0.6, 1.6, 4, "tail", 14, axis=1, decay=0.5)
    c.key(1.75, head=(-8, 0, 0), crest=(-6, 0, 0))
    c.key(2.3)


@clip("bask", "Basks in the heat", length=5.0, weight=2, avoid=["cold"])
def _(c):
    c.key(0.9, head=(-26, 0, 0), ra=(-8, 8, 24), la=(-8, 8, 24), rf=(-18, 0, 0), lf=(-18, 0, 0), crest=(-10, 0, 0),
          tail=(16, 0, 0), root=(-3, 0, 0))
    c.key(2.6, head=(-30, 0, 5), ra=(-8, 8, 27), la=(-8, 8, 27))
    c.wave(1.0, 4.0, 1.5, "tail", 12, base=(18, 0, 0), axis=1)
    c.key(3.9, head=(-27, 0, -3), ra=(-6, 8, 24), la=(-6, 8, 24), crest=(-12, 0, 0), root=(-3, 0, 0))
    c.key(4.6)


@clip("tiptoe_stretch", "Stretches up on the toes", length=4.0, weight=1.5, require=["adult"],
      avoid=NOPOLE + ["cold"])
def _(c):
    c.key(0.35, ra=(-4, 0, 12), la=(-4, 0, 12), root_pos=(0, 0.6, 0), head=(6, 0, 0))
    c.key(1.1, ra=(-12, 0, 158), la=(-12, 0, 158), rf=(-12, 0, 0), lf=(-12, 0, 0), waist=(-9, 0, 0), head=(-18, 0, 0),
          root_pos=(0, -1.4, 0), rs=(-10, 0, 0), ls=(-10, 0, 0), rft=(18, 0, 0), lft=(18, 0, 0), tail=(30, 0, 0),
          tail_tip=(14, 0, 0), crest=(6, 0, 0))
    c.key(1.5, ra=(-14, 0, 164), la=(-14, 0, 164), waist=(-11, 0, 0), head=(-22, 0, 0), tail=(36, 0, 0))
    c.key(2.2, ra=(-12, 0, 160), la=(-12, 0, 160), waist=(-10, 0, 0), head=(-20, 0, 0), tail=(33, 0, 0))
    c.key(2.8, ra=(2, 0, 22), la=(2, 0, 22), rf=(-6, 0, 0), lf=(-6, 0, 0), waist=(3, 0, 0), head=(5, 0, 0),
          root_pos=(0, 0.5, 0), rs=(4, 0, 0), ls=(4, 0, 0), rft=(-3, 0, 0), lft=(-3, 0, 0), tail=(-6, 0, 0),
          tail_tip=(-6, 0, 0), crest=(-3, 0, 0))
    c.key(3.6)


@clip("crest_scratch", "Scratches under the crest", length=3.2, weight=1.5, require=["adult"], avoid=NOPOLE)
def _(c):
    # elbow out in front and the hand up beside the head: reaching over the top sank the forearm into the head
    c.key(0.6, ra=(-90, 20, 0), rf=(-100, 0, 0), head=(8, 0, -12))
    c.wobble(0.7, 2.2, 5, "rf", 8, base=(-100, 0, 0))
    c.wobble(0.8, 2.2, 5, "crest", 6, axis=2)
    c.wave(0.8, 2.4, 0.8, "tail", 10, axis=1)
    c.key(2.4, ra=(-86, 20, 0), rf=(-100, 0, 0), head=(4, 0, -8))
    c.key(3.0)


@clip("shade_gaze", "Shades the eyes and scans the sea", length=5.0, weight=1.5, require=["adult"], mirror="never")
def _(c):
    # the hand stays at the brow, in front of the face, the whole time
    shade = dict(la=(-62, -15, 10), lf=(-96, 0, 0), waist=(4, 0, 0))
    # the whole body turns to scan, so the head barely turns away from the shading hand
    c.key(0.6, head=(-6, -2, 0), **shade)
    c.key(1.5, head=(-8, -5, 0), root=(0, -20, 0))
    c.key(2.4, head=(-8, -4, 0), root=(0, -20, 0))
    c.key(3.3, head=(-6, 4, 0), root=(0, 18, 0), tail=(0, -10, 0))
    c.key(3.9, head=(-6, 5, 0), root=(0, 18, 0), **shade)
    c.key(4.6)


@clip("hum_sway", "Hums and sways", length=5.2, weight=2, avoid=["cold"])
def _(c):
    c.wave(0.2, 5.0, 1.6, "root", 3.5, axis=2)
    c.wave(0.2, 5.0, 1.6, "head", 9, axis=2, phase=0.12)
    c.wave(0.2, 5.0, 1.6, "tail", 22, axis=1, phase=0.3)
    c.wave(0.2, 5.0, 0.8, "crest", 5, axis=2, phase=0.2)
    c.wave(0.2, 5.0, 1.6, "ra", 7, axis=0)
    c.wave(0.2, 5.0, 1.6, "la", 7, axis=0, phase=0.5)


@clip("count_wares", "Counts wares on the fingers", length=4.4, weight=1.5, require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.5, la=(-38, -24, 0), lf=(-62, 0, 0), ra=(-42, -30, 0), rf=(-66, 0, 0), head=(18, 0, 4))
    for i, t in enumerate((0.8, 1.35, 1.9, 2.45)):
        c.key(t, ra=(-47, -34 + i * 3, 0), head=(22, 0, 4))
        c.key(t + 0.25, ra=(-40, -30 + i * 3, 0), head=(16, 0, 4))
    c.key(2.7, la=(-38, -24, 0), lf=(-62, 0, 0), rf=(-66, 0, 0))
    c.key(3.1, head=(4, 0, -6), crest=(6, 0, 0), tail=(20, 0, 0), ra=(-36, -24, 0))
    c.key(3.4, head=(10, 0, -4), tail=(10, 12, 0))
    c.key(4.1)


@clip("yawn", "A long yawn behind a hand", length=3.6, weight=1, mirror="never")
def _(c):
    # the left hand covers the mouth (left, so a Ferrier's pole hand stays put); the right arm stretches back
    c.key(0.4, head=(6, 0, 0), crest=(3, 0, 0))
    c.key(1.1, head=(-26, 0, 4), la=(-62, -25, 12), lf=(-100, 0, 0), ra=(14, 0, 22), root=(-3, 0, 0),
          crest=(-18, 0, 0), tail=(-12, 0, 0), tail_tip=(-10, 0, 0))
    c.key(1.9, head=(-29, 0, 6), la=(-62, -25, 12), lf=(-100, 0, 0), ra=(16, 0, 24), crest=(-20, 0, 0))
    c.key(2.4, head=(8, 0, 0), la=(-4, 0, 4), lf=(-10, 0, 0), ra=(-2, 0, 4), root=(1, 0, 0), crest=(4, 0, 0),
          tail=(10, 0, 0), tail_tip=(6, 0, 0))
    c.wobble(2.4, 3.0, 5, "head", 7, base=(8, 0, 0), axis=2, decay=0.6)
    c.key(3.3)


@clip("look_around", "Looks about with the crest up", length=4.0, weight=2)
def _(c):
    c.key(0.25, head=(4, 6, 0))
    c.key(0.7, head=(-6, -46, 0), crest=(8, 0, 0), root=(0, -6, 0))
    c.key(0.95, tail=(0, -18, 0))
    c.key(1.5, head=(-4, -42, 4))
    c.key(2.0, head=(-6, 42, -3), root=(0, 6, 0), crest=(9, 0, 0))
    c.key(2.3, tail=(0, 20, 0))
    c.key(2.8, head=(-5, 40, 0))
    c.key(3.25, head=(2, -5, 0), root=(0, -1, 0), crest=(-3, 0, 0), tail=(0, -4, 0))
    c.key(3.75)


@clip("bite_token", "Bites a trade token to test it", length=4.6, weight=1.5, require=["adult"], avoid=NOPOLE)
def _(c):
    # a haggler's habit: eye the token, bite it at the corner of the mouth, hold it to the light, pocket it
    # (replaces arms-folded tail tapping, which was the listen_skeptic pose again)
    c.key(0.5, ra=(-30, -20, 0), rf=(-60, 0, 0), head=(16, 0, 0), la=(-6, 0, 6))
    c.key(1.0, ra=(-15, -15, 0), rf=(-118, 0, 0), head=(4, 0, -8), crest=(2, 0, 0))
    c.key(1.2, head=(8, 0, -8), crest=(6, 0, 0))
    c.key(1.4, head=(3, 0, -8), crest=(2, 0, 0))
    c.key(1.6, head=(8, 0, -8), ra=(-15, -15, 0), rf=(-118, 0, 0))
    c.key(2.1, ra=(-105, -5, 15), rf=(-50, 0, 0), head=(-18, 0, 0), crest=(4, 0, 0), tail=(6, 0, 0))
    c.key(2.6, ra=(-103, -5, 15), rf=(-52, 0, 0), head=(-18, 0, 4))
    c.key(3.1, ra=(-8, -20, 10), rf=(-34, 0, 0), head=(10, 0, 0), crest=(8, 0, 0), tail=(18, 14, 0),
          tail_tip=(0, 12, 0), la=(-4, 0, 4))
    c.key(3.5, head=(2, 0, 4), tail=(18, -12, 0), tail_tip=(0, -10, 0))
    c.key(4.3)


@clip("pat_belly", "Pats a well-fed belly", length=4.0, weight=1.2, require=["adult"], avoid=NOPOLE + ["cold"])
def _(c):
    # elbows out wide and leaning back, so it no longer reads as listen_skeptic's folded arms
    belly = dict(ra=(0, -60, 40), la=(0, -60, 40))
    c.key(0.6, rf=(-85, 0, 0), lf=(-85, 0, 0), root=(-6, 0, 0), head=(-10, 0, 4), **belly)
    for t in (0.9, 1.35, 1.8, 2.25):
        c.key(t, rf=(-75, 0, 0), lf=(-95, 0, 0))
        c.key(t + 0.2, rf=(-95, 0, 0), lf=(-75, 0, 0))
    c.wave(0.8, 3.4, 1.3, "tail", 16, axis=1)
    c.key(2.8, head=(-16, 0, -4), root=(-7, 0, 0), rf=(-85, 0, 0), lf=(-85, 0, 0), crest=(6, 0, 0), **belly)
    c.key(3.6)


@clip("strider_whistle", "Whistles for a strider", length=4.6, weight=1.2, require=["adult"], avoid=NOPOLE)
def _(c):
    c.key(0.5, ra=(-15, -15, 0), rf=(-118, 0, 0), head=(2, 20, 0), root=(0, 10, 0))
    c.key(0.8, head=(-8, 22, 0), crest=(10, 0, 0), waist=(-4, 0, 0), tail=(14, 0, 0))
    c.key(1.4, head=(-8, 24, 0), crest=(12, 0, 0), waist=(-5, 0, 0), ra=(-16, -15, 0), rf=(-120, 0, 0))
    c.key(1.9, ra=(-20, 20, 140), rf=(-40, 0, 0), head=(-4, 28, 0), crest=(4, 0, 0), waist=(0, 0, 0))
    c.key(2.25, ra=(-20, 20, 120), rf=(-70, 0, 0))
    c.key(2.6, ra=(-20, 20, 140), rf=(-40, 0, 0))
    c.key(2.95, ra=(-20, 20, 120), rf=(-70, 0, 0))
    c.key(3.4, ra=(0, 0, 10), rf=(-10, 0, 0), head=(-2, 26, 0), tail=(6, -10, 0))
    c.key(4.2)
