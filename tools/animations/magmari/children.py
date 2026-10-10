"""Magmari children at play: tail chasing, hopping stone to stone, riding a pretend strider, sneaking up to pounce,
and fidgeting. The child model has no forearms or tail tip, and its hips sit far below the pack's waist, so these
clips move the whole body with root instead of bending the waist.
"""
from kit import clip

CHILD = ["child"]


@clip("child_tail_chase", "Chases their own tail", length=3.6, weight=3, require=CHILD, mirror="free")
def _(c):
    c.key(0.3, head=(10, 50, 0), root=(0, 20, 0), tail=(10, -30, 0))
    c.key(0.9, root=(0, 160, 0), head=(14, 60, 0), root_pos=(0, -0.8, 0), ra=(-60, 0, 10), la=(-30, 0, 20))
    c.key(1.2, root_pos=(0, 0, 0))
    c.key(1.5, root=(0, 290, 0), root_pos=(0, -0.8, 0), tail=(14, -40, 0))
    c.key(1.75, root=(0, 300, 0), root_pos=(0, 0, 0), head=(8, -20, 0))
    c.key(2.4, root=(0, 140, 0), head=(12, -55, 0), root_pos=(0, -0.7, 0), tail=(14, 36, 0), ra=(-30, 0, 20),
          la=(-60, 0, 10))
    c.key(2.7, root_pos=(0, 0, 0))
    c.key(3.1, root=(0, -8, 0), head=(-6, 0, 0), tail=(10, 0, 0))
    c.key(3.5)


@clip("child_stone_hop", "Hops stone to stone", length=3.2, weight=3, require=CHILD, mirror="free")
def _(c):
    for i, t in enumerate((0.3, 0.75, 1.2, 1.65, 2.1)):
        s = 1 if i % 2 == 0 else -1
        c.key(t, root_pos=(0, -1.8, 0), rl=(-35 if s > 0 else 0, 0, 0), rs=(30 if s > 0 else 0, 0, 0),
              ll=(0 if s > 0 else -35, 0, 0), ls=(0 if s > 0 else 30, 0, 0), ra=(-10, 0, 60), la=(-10, 0, 60),
              root=(0, 0, 6 * s), head=(-6, 0, -6 * s), tail=(20, -20 * s, 0))
        c.key(t + 0.22, root_pos=(0, 0.4, 0), ra=(-10, 0, 45), la=(-10, 0, 45))
    c.key(2.6, root_pos=(0, 0, 0), rl=(0, 0, 0), rs=(0, 0, 0), ll=(0, 0, 0), ls=(0, 0, 0), ra=(-20, 0, 70),
          la=(-20, 0, 70), root=(0, 0, 0), head=(-12, 0, 0), tail=(26, 0, 0), crest=(10, 0, 0))
    c.key(3.1)


@clip("child_strider_ride", "Rides a pretend strider", length=4.0, weight=2, require=CHILD)
def _(c):
    c.key(0.4, rl=(-10, 0, 14), ll=(-10, 0, 14), rs=(14, 0, 0), ls=(14, 0, 0), root_pos=(0, 0.8, 0),
          la=(-55, -15, 0), ra=(-10, 0, 140))
    c.cycle(0.5, 3.0, 0.5, dict(root_pos=(0, 1.4, 0), head=(4, 0, 0), ra=(-10, 0, 150), tail=(14, 0, 0)),
            dict(root_pos=(0, 0.2, 0), head=(-4, 0, 0), ra=(-10, 0, 125), tail=(24, 0, 0)))
    c.key(3.3, la=(-50, -15, 0), rl=(-10, 0, 14), ll=(-10, 0, 14), rs=(14, 0, 0), ls=(14, 0, 0))
    c.key(3.8)


@clip("child_sneak_pounce", "Sneaks up and pounces", length=4.0, weight=2, require=CHILD)
def _(c):
    # the crouch keeps the feet planted (legs bend as the body lowers) and is held until the pounce
    crouch = dict(rl=(-30, 0, 0), ll=(-30, 0, 0), rs=(40, 0, 0), ls=(40, 0, 0), rft=(-10, 0, 0), lft=(-10, 0, 0),
                  ra=(-70, -10, 0), la=(-70, -10, 0), tail=(-10, 0, 0), crest=(-10, 0, 0))
    c.key(0.5, root_pos=(0, 1.2, 0), head=(6, -30, 0), **crouch)
    c.key(1.2, head=(6, 30, 0))
    c.key(1.8, head=(6, -25, 0), root=(0, -6, 0), root_pos=(0, 1.2, 0), **crouch)
    c.key(2.3, head=(8, 0, 0), root=(0, 0, 0), root_pos=(0, 1.6, 0), rl=(-38, 0, 0), ll=(-38, 0, 0), rs=(52, 0, 0),
          ls=(52, 0, 0), rft=(-14, 0, 0), lft=(-14, 0, 0), ra=(-70, -10, 0), la=(-70, -10, 0), tail=(-10, 0, 0),
          crest=(-10, 0, 0))
    # claws out in front: arms raised past the shoulder would sink into the big head
    c.key(2.6, root_pos=(0, -2.4, -1.2), ra=(-115, 0, 25), la=(-115, 0, 25), head=(-14, 0, 0), rl=(0, 0, 0),
          ll=(0, 0, 0), rs=(0, 0, 0), ls=(0, 0, 0), rft=(0, 0, 0), lft=(0, 0, 0), tail=(36, 0, 0), crest=(12, 0, 0))
    c.key(2.9, root_pos=(0, 0.4, -1.2))
    c.key(3.2, root_pos=(0, 0, -1.0), ra=(-105, 0, 22), la=(-105, 0, 22))
    c.key(3.8)


@clip("child_fidget", "Tugs the crest and kicks a foot", length=3.6, weight=2, require=CHILD)
def _(c):
    # the hand comes up beside the head (a side raise clears the big head) and tugs at the crest
    c.key(0.4, ra=(-10, 0, 160), head=(8, 0, -10), crest=(0, 0, -12))
    c.wobble(0.5, 1.4, 4, "crest", 8, base=(0, 0, -12), axis=2)
    c.key(1.2, ra=(-10, 0, 160))
    c.key(1.6, ra=(0, 0, 6), head=(0, 0, 0), crest=(0, 0, 0), rl=(0, 0, 0), rs=(0, 0, 0))
    c.key(1.9, rl=(-24, 0, 0), rs=(-10, 0, 0))
    c.key(2.15, rl=(10, 0, 0), rs=(10, 0, 0))
    c.key(2.4, rl=(-20, 0, 0), rs=(-8, 0, 0))
    c.key(2.7, rl=(0, 0, 0), rs=(0, 0, 0))
    c.wave(1.6, 3.4, 0.9, "root", 6, axis=1)
    c.wave(0.4, 3.4, 1.0, "tail", 22, base=(10, 0, 0), axis=1)
    c.key(3.5)
