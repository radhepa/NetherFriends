"""A Magmari away from heat cools, goes dark and grumpy: shivering, rubbing and hugging themselves, blowing on
their hands, stamping, the crest laid flat and the tail curled round the legs; children huddle small. Weighted
high so they show when cold.
"""
from kit import clip

NOPOLE = ["job:ferrier"]


@clip("cold_shiver", "Shivers with the crest drooping", length=3.4, weight=5, require=["cold"])
def _(c):
    c.key(0.5, head=(14, 0, 0), crest=(-22, 0, 0), tail=(-30, 0, 0), tail_tip=(-30, 0, 0), root=(3, 0, 0),
          root_pos=(0, 0.6, 0), rs=(6, 0, 0), ls=(6, 0, 0), ra=(-4, -6, 0), la=(-4, -6, 0))
    c.wobble(0.6, 2.8, 9, "root", 1.6, base=(3, 0, 0), axis=2)
    c.wobble(0.6, 2.8, 11, "head", 3, base=(14, 0, 0), axis=1)
    c.wobble(0.7, 2.8, 8, "tail", 5, base=(-30, 0, 0), axis=1)
    c.key(2.8, crest=(-22, 0, 0), tail_tip=(-30, 0, 0), root_pos=(0, 0.6, 0), rs=(6, 0, 0), ls=(6, 0, 0),
          ra=(-4, -6, 0), la=(-4, -6, 0))
    c.key(3.2)


@clip("cold_rub_arms", "Rubs the arms for warmth", length=3.6, weight=5, require=["adult", "cold"], avoid=NOPOLE)
def _(c):
    # elbows bent a little less than before, so the rubbing hands stay on the arms instead of in the chest and chin
    c.key(0.5, ra=(-12, -48, 0), rf=(-92, 0, 0), la=(-12, -48, 0), lf=(-92, 0, 0), waist=(8, 0, 0), head=(12, 0, 0),
          crest=(-18, 0, 0), tail=(-30, 24, 0), tail_tip=(-20, 20, 0))
    c.cycle(0.7, 2.9, 0.36, dict(ra=(-18, -50, 0), la=(-6, -46, 0)), dict(ra=(-6, -46, 0), la=(-18, -50, 0)))
    c.wobble(0.7, 2.9, 6, "head", 3, base=(12, 0, 0), axis=2)
    # hold the hands on the arms until the rubbing stops
    c.key(2.9, rf=(-92, 0, 0), lf=(-92, 0, 0), waist=(8, 0, 0), crest=(-18, 0, 0), tail=(-30, 24, 0),
          tail_tip=(-20, 20, 0))
    c.key(3.4)


@clip("cold_hug_self", "Hugs themselves and stamps for warmth", length=4.2, weight=5, require=["adult", "cold"],
      avoid=NOPOLE)
def _(c):
    # grown-ups only: without forearms a child's arms would sink into the belly (children huddle instead).
    # Stamping the feet keeps it apart from cold_rub_arms, which has nearly the same arms.
    hug = dict(ra=(-25, -60, 0), rf=(-80, 0, 0), la=(-25, -60, 0), lf=(-80, 0, 0), crest=(-26, 0, 0),
               tail=(-36, 44, 0), tail_tip=(-20, 40, 0))
    c.key(0.6, head=(16, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0), rs=(0, 0, 0), ls=(0, 0, 0), **hug)
    for i, t in enumerate((1.0, 1.5, 2.0, 2.5, 3.0)):
        lift, plant = ("rl", "ll") if i % 2 == 0 else ("ll", "rl")
        shin = "rs" if lift == "rl" else "ls"
        side = 1 if lift == "rl" else -1
        c.key(t, **{lift: (-24, 0, 0), shin: (22, 0, 0)}, root=(0, 0, 3 * side), head=(18, 0, -3 * side))
        c.key(t + 0.2, **{lift: (0, 0, 0), shin: (0, 0, 0)}, root_pos=(0, 0.3, 0))
        c.key(t + 0.3, root_pos=(0, 0, 0))
    c.key(3.5, head=(16, 0, 0), root=(0, 0, 0), **hug)
    c.key(4.0)


@clip("cold_child_huddle", "Huddles small and shivers", length=4.2, weight=5, require=["child", "cold"])
def _(c):
    huddle = dict(root_pos=(0, 1.0, 0), rl=(-30, 0, 0), ll=(-30, 0, 0), rs=(40, 0, 0), ls=(40, 0, 0),
                  ra=(-50, -30, 0), la=(-50, -30, 0), crest=(-20, 0, 0), tail=(-30, 50, 0))
    c.key(0.6, head=(18, 0, 0), **huddle)
    c.wobble(0.8, 3.2, 9, "root", 1.5, axis=2)
    c.key(1.8, head=(20, 0, 0))
    c.key(2.3, head=(12, -24, 6))
    c.key(2.8, head=(20, 0, 0))
    c.key(3.4, **huddle)
    c.key(4.0)


@clip("cold_blow_hands", "Blows into cupped hands", length=3.6, weight=4, require=["adult", "cold"], avoid=NOPOLE)
def _(c):
    c.key(0.5, ra=(-22, -22, 0), rf=(-112, 0, 0), la=(-22, -22, 0), lf=(-112, 0, 0), head=(12, 0, 0), crest=(-20, 0, 0),
          tail=(-24, 0, 0), waist=(6, 0, 0))
    for t in (0.9, 1.6):
        c.key(t, head=(16, 0, 0), root_pos=(0, -0.3, 0))
        c.key(t + 0.35, head=(10, 0, 0), root_pos=(0, 0, 0))
    c.cycle(2.1, 2.9, 0.3, dict(ra=(-26, -24, 0), la=(-18, -20, 0)), dict(ra=(-18, -20, 0), la=(-26, -24, 0)))
    # the cupped hands stay at the mouth until the rubbing ends
    c.key(2.9, rf=(-112, 0, 0), lf=(-112, 0, 0), crest=(-20, 0, 0), tail=(-24, 0, 0), waist=(6, 0, 0))
    c.key(3.1, head=(4, -20, 0))
    c.key(3.5)


@clip("cold_tail_wrap", "Curls the tail round the legs", length=4.2, weight=4, require=["cold"])
def _(c):
    wrap = dict(tail_tip=(-24, 50, 0), crest=(-24, 0, 0), root_pos=(0, 0.8, 0), rs=(8, 0, 0), ls=(8, 0, 0),
                rl=(-4, 0, 0), ll=(-4, 0, 0))
    c.key(0.8, tail=(-40, 55, 0), head=(18, 0, 0), **wrap)
    c.key(2.0, head=(20, 0, 4))
    c.wobble(2.3, 3.0, 3, "head", 12, base=(18, 0, 0), axis=1)
    c.key(3.4, head=(16, 0, 0), tail=(-38, 52, 0), **wrap)
    c.key(4.0)


@clip("cold_greet_curt", "A curt cold nod", trigger="greet", length=2.0, weight=6, require=["cold"])
def _(c):
    c.key(0.3, head=(4, 0, 0), crest=(-20, 0, 0), tail=(-24, 0, 0), root_pos=(0, 0.4, 0))
    c.key(0.55, head=(16, 0, 0))
    c.key(0.85, head=(-2, 8, -6))
    c.key(1.3, head=(2, 10, -4), tail=(-24, 16, 0))
    c.key(1.8)


@clip("cold_talk_grumble", "Grumbles hugging themselves", trigger="talk", length=3.2, weight=6,
      require=["adult", "cold"], avoid=NOPOLE)
def _(c):
    c.key(0.4, ra=(-25, -60, 0), rf=(-80, 0, 0), la=(-25, -60, 0), lf=(-80, 0, 0), head=(10, 0, 0), crest=(-20, 0, 0),
          tail=(-28, 30, 0))
    c.wobble(0.5, 1.3, 3, "head", 10, base=(10, 0, 0), axis=1)
    c.key(1.6, ra=(-70, 25, 10), rf=(-15, 0, 0), head=(4, 20, 0))
    c.key(1.8, ra=(-74, 25, 10))
    c.key(2.3, ra=(-25, -60, 0), rf=(-80, 0, 0), head=(12, 0, -6))
    c.wobble(2.4, 3.0, 8, "root", 1.4, axis=2)
    # the left arm keeps hugging throughout
    c.key(2.7, ra=(-25, -60, 0), rf=(-80, 0, 0), la=(-25, -60, 0), lf=(-80, 0, 0), crest=(-20, 0, 0),
          tail=(-28, 30, 0))
    c.key(3.1)
