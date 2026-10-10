"""Magmari at work: Ferriers with the lure-pole, Hearthkeepers, Lava-anglers, Chainwrights, Quartzcutters and
Chartmakers. Every clip needs a grown-up with the job.

The Ferrier's pole hangs off the right hand pointing forward and up (the lure at its tip): swinging the arm back
dips the tip, raising it forward stands the pole upright, and rolling the arm out holds it upright at the side.
"""
from kit import clip

FERRIER = ["adult", "job:ferrier"]


@clip("ferrier_lure_steer", "Steers the striders with the lure", length=6.0, weight=4, require=FERRIER, mirror="never")
def _(c):
    c.key(0.6, ra=(-16, 0, 0), rf=(-10, 0, 0), head=(-12, 0, 0), tail=(10, 0, 0))
    c.key(1.4, ra=(-18, 32, 0), head=(-10, 24, 0), root=(0, 8, 0), la=(-70, -42, 0), lf=(-12, 0, 0), tail=(10, -16, 0))
    c.wobble(1.5, 2.4, 3, "rf", 9, base=(-10, 0, 0))
    c.key(2.6, ra=(-18, 30, 0), head=(-12, 22, 0), root=(0, 8, 0), la=(-64, -40, 0))
    c.key(3.4, ra=(-20, -26, 0), head=(-10, -24, 0), root=(0, -8, 0), la=(-12, 10, 26), lf=(-30, 0, 0), tail=(12, 18, 0))
    c.wobble(3.5, 4.3, 3, "rf", 9, base=(-10, 0, 0))
    c.key(4.5, ra=(-20, -24, 0), head=(-12, -22, 0), root=(0, -8, 0), la=(-10, 10, 24))
    c.key(5.1, ra=(-6, 0, 0), rf=(-4, 0, 0), head=(-4, 0, 0), root=(0, 1, 0), la=(-4, 0, 4), lf=(-6, 0, 0),
          crest=(6, 0, 0), tail=(4, 0, 0))
    c.key(5.75)


@clip("ferrier_pole_push", "Poles off from the Mooring", length=6.4, weight=4, require=FERRIER, mirror="never")
def _(c):
    # the pole is kept upright (arm + forearm pitch about -60) while the hand drives back past the hip
    c.key(0.7, ra=(-46, 0, 6), rf=(-20, 0, 0), head=(-6, 0, 0), la=(-8, 0, 8), tail=(6, 0, 0))
    for start in (0.9, 3.3):
        c.key(start + 0.35, ra=(-50, 0, 6), rf=(-16, 0, 0), waist=(-3, 0, 0), root_pos=(0, -0.4, 0))
        c.key(start + 1.15, ra=(16, 0, 6), rf=(-58, 0, 0), waist=(18, 0, 0), head=(-16, 0, 0), root_pos=(0, 1.2, 0),
              rs=(14, 0, 0), ls=(6, 0, 0), rl=(-12, 0, 0), ll=(14, 0, 0), la=(-34, -8, 10), lf=(-34, 0, 0),
              tail=(26, 0, 0), tail_tip=(14, 0, 0))
        c.key(start + 1.4, ra=(18, 0, 6), rf=(-60, 0, 0), waist=(19, 0, 0))
        c.key(start + 2.2, ra=(-30, 0, 6), rf=(-46, 0, 0), waist=(2, 0, 0), head=(-4, 0, 0), root_pos=(0, -0.3, 0),
              rs=(0, 0, 0), ls=(0, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0), la=(-6, 0, 8), lf=(-8, 0, 0),
              tail=(8, 0, 0), tail_tip=(0, 0, 0))
    c.key(6.0)


@clip("ferrier_sound_depth", "Sounds the lava with the pole", length=5.6, weight=3, require=FERRIER, mirror="never")
def _(c):
    c.key(0.5, ra=(-8, 0, 0), head=(-4, 0, 0))
    c.key(1.3, ra=(38, 0, 4), rf=(-2, 0, 0), waist=(16, 0, 0), head=(6, 0, 0), la=(-28, -12, 0), lf=(-42, 0, 0),
          tail=(20, 0, 0), crest=(4, 0, 0))
    c.key(1.6, ra=(44, 0, 4))
    c.key(1.85, ra=(36, 0, 4))
    c.key(2.1, ra=(44, 0, 4))
    c.key(2.6, ra=(40, 0, 4), head=(8, 0, -4))
    c.key(3.4, ra=(-38, 0, 7), rf=(-34, 0, 0), waist=(0, 0, 0), head=(-24, 0, -8), la=(-6, 0, 4), lf=(-6, 0, 0),
          tail=(6, 0, 0), crest=(8, 0, 0))
    c.key(4.0, ra=(-40, 0, 7), head=(-22, 0, 8))
    c.key(4.4, head=(-6, 0, 0), tail=(14, 10, 0))
    c.key(4.7, head=(4, 0, 0), tail=(10, -8, 0))
    c.key(5.3)


@clip("hearth_stoke", "Stokes the hearth and blows on the coals", length=5.2, weight=4,
      require=["adult", "job:hearthkeeper"])
def _(c):
    c.key(0.5, ra=(-48, -18, 0), rf=(-28, 0, 0), la=(-40, -30, 0), lf=(-42, 0, 0), waist=(8, 0, 0), head=(12, 0, 0),
          crest=(4, 0, 0))
    for t in (0.8, 1.4, 2.0):
        c.key(t, ra=(-62, -16, 0), rf=(-12, 0, 0), la=(-56, -28, 0), lf=(-24, 0, 0), waist=(14, 0, 0), head=(14, 0, 0))
        c.key(t + 0.32, ra=(-46, -18, 0), rf=(-30, 0, 0), la=(-38, -30, 0), lf=(-44, 0, 0), waist=(8, 0, 0),
              head=(12, 0, 0))
    c.key(2.8, waist=(24, 0, 0), head=(8, 0, 0), root_pos=(0, 1.8, 0), rs=(16, 0, 0), ls=(16, 0, 0), rl=(-10, 0, 0),
          ll=(-10, 0, 0), ra=(-34, -8, 8), rf=(-30, 0, 0), la=(-34, -8, 8), lf=(-30, 0, 0), tail=(28, 0, 0),
          tail_tip=(10, 0, 0))
    c.key(3.05, head=(16, 0, 0), crest=(10, 0, 0))
    c.key(3.25, head=(8, 0, 0))
    c.key(3.45, head=(16, 0, 0))
    c.key(3.65, head=(8, 0, 0), crest=(4, 0, 0))
    c.key(4.2, waist=(-4, 0, 0), head=(-10, 0, 5), root_pos=(0, 0, 0), rs=(0, 0, 0), ls=(0, 0, 0), rl=(0, 0, 0),
          ll=(0, 0, 0), ra=(-6, 0, 14), rf=(-14, 0, 0), la=(-6, 0, 14), lf=(-14, 0, 0), tail=(16, 14, 0),
          tail_tip=(0, 10, 0), crest=(10, 0, 0))
    c.key(4.5, tail=(14, -12, 0), tail_tip=(0, -10, 0))
    c.key(5.0)


@clip("hearth_ladle", "Stirs the pot and tastes", length=5.6, weight=3, require=["adult", "job:hearthkeeper"])
def _(c):
    import math
    c.key(0.5, ra=(-40, -15, 0), rf=(-35, 0, 0), la=(10, -70, 30), lf=(-70, 0, 0), head=(20, 0, 0), waist=(6, 0, 0))
    for i in range(9):
        a = i * math.pi / 2
        c.key(0.7 + i * 0.28, ra=(-40 + 7 * math.sin(a), -15 + 10 * math.cos(a), 0), head=(20, 4 * math.cos(a), 0))
    # the stirring forearm and the lean are held while stirring (the forearm used to creep up into the face)
    c.key(2.94, rf=(-35, 0, 0), waist=(6, 0, 0))
    c.key(3.35, ra=(-15, -15, 0), rf=(-118, 0, 0), head=(2, 0, 6), waist=(0, 0, 0))
    c.key(3.75, head=(-8, 0, -6), crest=(10, 0, 0), tail=(22, 0, 0), ra=(-14, -15, 0), rf=(-110, 0, 0))
    c.key(4.15, head=(-10, 0, -8), tail=(20, 14, 0), tail_tip=(0, 10, 0))
    c.key(4.5, head=(6, 0, 0), tail=(20, -12, 0), tail_tip=(0, -10, 0), ra=(-30, -10, 0), rf=(-50, 0, 0),
          la=(10, -70, 30), lf=(-70, 0, 0))
    c.key(4.75, head=(-2, 0, 0))
    c.key(5.4)


@clip("angler_cast", "Casts a line over the lava", length=5.8, weight=4, require=["adult", "job:lava_angler"])
def _(c):
    # a one-handed cast: the rod hand winds up above the right shoulder (both hands overhead crossed the head)
    # while the left steadies the butt at the chin, then whips it forward
    c.key(0.5, ra=(-40, -20, 0), rf=(-40, 0, 0), la=(-50, -30, 0), lf=(-45, 0, 0), head=(4, 0, 0))
    c.key(1.2, ra=(-140, 10, 0), rf=(-40, 0, 0), la=(-75, -35, 0), lf=(-60, 0, 0), waist=(-6, 12, 0),
          head=(-10, -6, 0), root=(0, 10, 0), tail=(10, -20, 0), crest=(-6, 0, 0))
    c.key(1.45, ra=(-75, -15, 0), rf=(-10, 0, 0), la=(-80, -25, 0), lf=(-15, 0, 0), waist=(10, -8, 0), head=(-4, 0, 0),
          root=(0, -4, 0), tail=(14, 20, 0), crest=(6, 0, 0))
    c.key(1.7, ra=(-66, -15, 0), la=(-70, -25, 0), waist=(12, -6, 0))
    c.key(2.2, ra=(-55, -15, 0), rf=(-30, 0, 0), la=(-58, -25, 0), lf=(-35, 0, 0), waist=(4, 0, 0), head=(-6, 0, 0),
          root=(0, 0, 0), tail=(8, 0, 0), crest=(2, 0, 0))
    c.key(3.6, ra=(-56, -15, 0), la=(-59, -25, 0), head=(-6, 0, 4))
    c.key(3.8, ra=(-63, -15, 0), la=(-66, -25, 0), head=(-8, 0, 0), crest=(8, 0, 0), tail=(14, 0, 0))
    c.key(4.2, ra=(-55, -15, 0), la=(-58, -25, 0), head=(-6, 0, -3), crest=(0, 0, 0), tail=(6, 0, 0))
    c.key(5.4)


@clip("angler_reel", "Reels in a bite", length=5.4, weight=3, require=["adult", "job:lava_angler"])
def _(c):
    c.key(0.5, la=(-55, -20, 0), lf=(-35, 0, 0), ra=(-45, -30, 0), rf=(-70, 0, 0), head=(14, 0, 0))
    c.cycle(0.7, 2.6, 0.42, dict(ra=(-40, -30, 0), rf=(-60, 0, 0)), dict(ra=(-50, -26, 0), rf=(-80, 0, 0)))
    # the rod hand holds still while the other reels, then both haul up; elbows bent less so the hands stop
    # in front of the face instead of in it
    c.key(2.6, la=(-55, -20, 0), lf=(-35, 0, 0))
    c.key(2.7, head=(10, 0, 0), crest=(0, 0, 0))
    c.key(3.0, ra=(-75, -25, 0), rf=(-72, 0, 0), la=(-85, -20, 0), lf=(-50, 0, 0), waist=(-10, 0, 0), head=(-8, 0, 0),
          rl=(10, 0, 0), crest=(10, 0, 0), tail=(28, 0, 0), tail_tip=(12, 0, 0))
    c.key(3.25, ra=(-70, -25, 0), la=(-80, -20, 0), waist=(-8, 0, 0))
    c.key(3.5, ra=(-80, -25, 0), rf=(-72, 0, 0), la=(-90, -20, 0), lf=(-50, 0, 0), waist=(-11, 0, 0))
    c.key(4.1, ra=(-40, -20, 0), rf=(-60, 0, 0), la=(-45, -20, 0), lf=(-50, 0, 0), waist=(4, 0, 0), head=(10, 0, -6),
          rl=(0, 0, 0), tail=(16, 14, 0), tail_tip=(0, 12, 0))
    c.key(4.45, head=(8, 0, 6), tail=(16, -12, 0), tail_tip=(0, -10, 0))
    c.key(5.1)


@clip("chain_haul", "Hauls a chain hand over hand", length=6.0, weight=4, require=["adult", "job:chainwright"])
def _(c):
    a = dict(ra=(-82, -8, 0), rf=(-14, 0, 0), la=(-30, -15, 0), lf=(-86, 0, 0), head=(4, -4, 0))
    b = dict(ra=(-30, -15, 0), rf=(-86, 0, 0), la=(-82, -8, 0), lf=(-14, 0, 0), head=(4, 4, 0))
    c.key(0.5, ll=(-16, 0, 0), ls=(6, 0, 0), rl=(8, 0, 0), tail=(18, 0, 0), crest=(4, 0, 0))
    c.cycle(0.6, 5.0, 1.1, a, b)
    c.wave(0.6, 5.0, 0.55, "waist", 4, base=(-5, 0, 0), axis=0)
    c.key(5.2, ll=(-16, 0, 0), ls=(6, 0, 0), rl=(8, 0, 0), tail=(18, 0, 0))
    c.key(5.7)


@clip("chain_hammer", "Hammers a link on the anvil", length=5.2, weight=3, require=["adult", "job:chainwright"])
def _(c):
    c.key(0.45, la=(-45, -20, 0), lf=(-40, 0, 0), head=(16, 0, 0), waist=(6, 0, 0))
    for t in (0.9, 1.6, 2.3):
        # the hammer goes up above the right shoulder (raised behind the head it went through it)
        c.key(t - 0.35, ra=(-130, 10, 0), rf=(-60, 0, 0), waist=(2, 0, 0), crest=(0, 0, 0))
        c.key(t, ra=(-55, -12, 0), rf=(-25, 0, 0), waist=(9, 0, 0), crest=(6, 0, 0), head=(18, 0, 0))
        c.key(t + 0.12, ra=(-62, -12, 0), rf=(-30, 0, 0), crest=(2, 0, 0), head=(15, 0, 0))
    # the tongs hand holds the link on the anvil until the hammering stops, then lifts it before the eyes
    c.key(2.45, la=(-45, -20, 0), lf=(-40, 0, 0))
    c.key(3.0, la=(-90, -10, 10), lf=(-60, 0, 0), ra=(-20, 0, 6), rf=(-20, 0, 0), head=(-12, -10, 0), waist=(0, 0, 0))
    c.key(3.4, la=(-90, 0, 14), head=(-12, -8, 6))
    c.key(3.8, la=(-90, -20, 6), head=(-12, -12, -6))
    c.key(4.2, head=(4, -6, 0), crest=(8, 0, 0), tail=(16, 10, 0))
    c.key(4.9)


@clip("quartz_chisel", "Chisels quartz and holds it to the light", length=5.6, weight=4,
      require=["adult", "job:quartzcutter"])
def _(c):
    c.key(0.5, waist=(16, 0, 0), head=(16, 0, 0), la=(-55, -25, 0), lf=(-40, 0, 0), ra=(-80, -10, 0), rf=(-45, 0, 0))
    for t in (0.8, 1.2, 1.6, 2.0, 2.4):
        c.key(t, ra=(-72, -12, 0), rf=(-38, 0, 0), head=(17, 0, 0))
        c.key(t + 0.2, ra=(-92, -10, 0), rf=(-55, 0, 0), head=(15, 0, 0))
    # the stone hand and the lean are held while chiselling (the stone used to creep up into the face)
    c.key(2.6, waist=(16, 0, 0), la=(-55, -25, 0), lf=(-40, 0, 0))
    # both hands hold the stone up before the eyes, not over the head
    up = dict(ra=(-110, 0, 20), rf=(-50, 0, 0), la=(-110, 0, 20), lf=(-50, 0, 0), waist=(-4, 0, 0))
    c.key(3.2, head=(-24, 0, 0), crest=(6, 0, 0), **up)
    c.key(3.6, root=(0, 12, 0), head=(-24, 0, 8))
    c.key(4.1, root=(0, -8, 0), head=(-22, 0, -6), **up)
    c.key(4.5, root=(0, 0, 0), head=(-14, 0, 0), tail=(18, 10, 0), crest=(10, 0, 0))
    c.key(4.8, tail=(16, -10, 0))
    c.key(5.3)


@clip("chart_trace", "Traces a route on a chart", length=6.0, weight=4, require=["adult", "job:chartmaker"])
def _(c):
    c.key(0.5, la=(-50, -30, 0), lf=(-45, 0, 0), ra=(-45, -25, 0), rf=(-55, 0, 0), head=(20, 0, 0))
    c.key(0.9, ra=(-45, -32, 0), head=(20, -4, 0))
    c.key(1.4, ra=(-49, -20, 0), head=(21, 2, 0))
    c.key(1.9, ra=(-42, -14, 0), head=(19, 5, 0))
    # the tracing forearm stays bent while it traces (it used to straighten out over the chart)
    c.key(2.4, ra=(-47, -26, 0), rf=(-55, 0, 0), head=(20, -2, 0))
    c.key(3.0, head=(-6, 18, 0), ra=(-86, 16, 0), rf=(-6, 0, 0), crest=(6, 0, 0))
    c.key(3.4, head=(-7, 22, 0), ra=(-88, 20, 0))
    c.key(3.9, head=(20, 0, 0), ra=(-44, -26, 0), rf=(-58, 0, 0), crest=(0, 0, 0))
    c.wobble(4.1, 4.8, 6, "rf", 6, base=(-58, 0, 0))
    # the chart hand holds the chart the whole time
    c.key(5.0, head=(10, 0, 0), tail=(14, 0, 0), ra=(-44, -26, 0), la=(-50, -30, 0), lf=(-45, 0, 0))
    c.key(5.2, head=(18, 0, 0))
    c.key(5.7)
