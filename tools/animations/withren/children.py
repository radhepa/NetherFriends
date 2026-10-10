"""Withren children: they have no one to keep yet, so they wonder, copy the grown-ups badly and get up to mischief.
The child's head is full size on a half-size body, so raised arms go out to the side (roll) to clear the skull."""
from kit import clip

KID = ["child"]
CHEST = (-60, -50, 0)


# -- idle ----------------------------------------------------------------------------------------------------------
@clip("child_name_peek", "Peeks up at the Name Wall", length=4.8, weight=2, require=KID)
def _(c):
    c.key(0.7, head=(-20, 0, 0), root_pos=(0, -0.6, 0))
    c.key(1.1, head=(-28, -6, 0), root_pos=(0, -1.0, 0), rl=(4, 0, 0), ll=(4, 0, 0))
    c.key(1.7, ra=(-118, 10, 56), head=(-30, 4, 4), root_pos=(0, -1.1, 0))
    c.key(2.4, ra=(-116, 14, 56), head=(-26, 10, 8))
    c.key(2.9, ra=(-30, 0, 6), head=(-10, 6, 12), root_pos=(0, 0, 0), rl=(0, 0, 0), ll=(0, 0, 0))
    c.key(3.4, ra=CHEST, head=(10, 0, 8))
    c.key(4.0, ra=CHEST, head=(12, 0, 4))
    c.key(4.8)


@clip("child_lantern_swing", "Swings the lantern", length=4.6, weight=2, require=KID, mirror="never")
def _(c):
    c.wave(0.2, 4.2, 1.1, "lantern", 32, axis=0)
    c.wave(0.2, 4.2, 1.1, "root", 4, axis=2, phase=0.5)
    c.wave(0.2, 4.2, 1.1, "head", 8, base=(14, -10, 0), axis=2, phase=0.6)
    c.wave(0.2, 4.2, 1.1, "left_arm", 12, base=(-10, 0, 6), axis=0, phase=0.1)
    c.key(4.6)


@clip("child_head_rattle", "Rattles its own head", length=3.4, weight=1.5, require=KID)
def _(c):
    c.key(0.3, head=(0, -10, 0), ra=(0, 0, 14), la=(0, 0, 14))
    c.wobble(0.35, 1.2, 6, "head", 20, axis=1)
    c.key(1.2, ra=(-10, 0, 40), la=(-10, 0, 40))
    c.wave(1.3, 3.0, 0.8, "head", 12, axis=2)
    c.wave(1.3, 3.0, 0.8, "root", 5, axis=2, phase=0.5)
    c.key(2.4, ra=(-6, 0, 34), la=(-6, 0, 30))
    c.key(3.0, ra=(0, 0, 6), la=(0, 0, 6))
    c.key(3.4)


@clip("child_bow_practice", "Practises a grown-up bow", length=4.6, weight=2, require=KID)
def _(c):
    c.key(0.5, ra=(-56, -46, 0), head=(-4, 0, 0))
    c.key(0.8, ra=CHEST, head=(-6, 0, 0))
    c.key(1.6, ra=CHEST, root=(20, 0, 0), head=(24, 0, 0), la=(10, 0, 4))
    c.key(1.8, root=(22, 0, 4), head=(26, 0, 4))
    c.wobble(1.9, 2.4, 6, "root", 2, base=(21, 0, 2), axis=2)
    c.key(2.7, root=(-4, 0, 0), head=(-10, 0, 0), ra=(-10, 0, 30), la=(-10, 0, 30), root_pos=(0, 0, 0.6))
    c.key(3.0, root=(-1, 0, 0), ra=(-6, 0, 18), la=(-6, 0, 18), root_pos=(0, 0, 0))
    c.key(3.6, root=(0, 0, 0), head=(-12, 0, 0), ra=(0, 0, 0), la=(0, 0, 0), root_pos=(0, -0.4, 0))
    c.key(4.0, head=(-10, 0, 0), root_pos=(0, 0, 0))
    c.key(4.6)


@clip("child_boo", "Jumps out with a boo", length=3.0, weight=1.5, require=KID, mirror="never")
def _(c):
    c.key(0.6, root=(7, 0, 0), head=(8, 0, 0), ra=(10, 0, 6), la=(10, 0, 6), root_pos=(0, 1.0, 0))
    c.key(0.85, root=(8, 0, 0), root_pos=(0, 1.2, 0))
    c.key(1.05, root=(-2, 0, 0), head=(-6, 0, 0), ra=(-100, 46, 44), la=(-100, 46, 44), root_pos=(0, -1.6, 0))
    c.key(1.25, root_pos=(0, 0, 0), root=(3, 0, 0), head=(4, 0, 0))
    c.key(1.6, ra=(-96, 44, 40), la=(-96, 44, 40), root=(4, 0, 0))
    c.wobble(1.7, 2.4, 6, "head", 5, base=(-4, 0, 6), axis=0)
    c.key(2.4, ra=(-20, 0, 10), la=(-20, 0, 10), root=(0, 0, 0))
    c.key(3.0)


@clip("child_hop_stones", "Hops from stone to stone", length=4.4, weight=1.5, require=KID, mirror="free")
def _(c):
    t = 0.3
    for i in range(4):
        side = 1 if i % 2 else -1
        c.key(t, root_pos=(0, 0.4, 0), ra=(4, 0, 20), la=(4, 0, 20), rl=(0, 0, 0), ll=(0, 0, 0))
        c.key(t + 0.25, root_pos=(0, -2.2, 0), root=(0, 12 * side, 0), ra=(-8, 0, 36),
              la=(-8, 0, 36), rl=(-34 if side > 0 else 0, 0, 0), ll=(0 if side > 0 else -34, 0, 0),
              head=(-6, -8 * side, 0))
        c.key(t + 0.5, root_pos=(0, 0.5, 0), rl=(0, 0, 0), ll=(0, 0, 0))
        t += 0.9
    c.key(t + 0.2, root_pos=(0, 0, 0), root=(0, 0, 0), head=(0, 0, 0), ra=(0, 0, 6), la=(0, 0, 6))
    c.key(4.4)


# -- greet ---------------------------------------------------------------------------------------------------------
@clip("child_little_bow", "Little bow", trigger="greet", length=2.6, weight=3, require=KID)
def _(c):
    c.key(0.4, ra=(-56, -46, 0), head=(-4, 0, 0), root_pos=(0, -0.5, 0))
    c.key(0.6, ra=CHEST, root_pos=(0, 0, 0))
    c.key(1.0, ra=CHEST, root=(14, 0, 0), head=(22, 0, 0))
    c.key(1.2, root=(15, 0, 0), head=(23, 0, 0))
    c.key(1.7, ra=(-20, 0, 6), root=(-2, 0, 0), head=(-10, 0, 0), root_pos=(0, -0.6, 0))
    c.key(2.0, root=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(2.6)


@clip("child_shy_wave", "Shy wave", trigger="greet", length=3.0, weight=2, require=KID)
def _(c):
    c.key(0.5, root=(0, -10, 0), head=(6, 6, -10), la=(-30, -40, 0))
    c.key(0.8, ra=(-30, 10, 74))
    c.cycle(0.9, 2.1, 0.4, dict(ra=(-30, 10, 88)), dict(ra=(-30, 10, 66)))
    c.key(2.1, root=(0, -10, 0), head=(8, 4, -12), la=(-30, -40, 0))
    c.key(3.0)


@clip("child_skull_drum", "Drums on its own skull", length=3.6, weight=1.5, require=KID, mirror="never")
def _(c):
    c.key(0.5, ra=(-10, 0, 120), la=(-10, 0, 120), head=(-4, 0, 0))
    t = 0.7
    for i in range(6):
        c.key(t, ra=(-10, 0, 146 if i % 2 == 0 else 116), la=(-10, 0, 116 if i % 2 == 0 else 146),
              head=(-6, 0, 4 if i % 2 == 0 else -4))
        t += 0.22
    c.key(t + 0.3, ra=(-10, 0, 124), la=(-10, 0, 124), head=(-12, 0, 8))
    c.key(t + 0.8, ra=(0, 0, 10), la=(0, 0, 10), head=(-6, 0, 4))
    c.key(3.6)


# -- conversation and chat -----------------------------------------------------------------------------------------
@clip("child_farewell_wave", "Waves goodbye with the whole arm", trigger="farewell", length=2.8, weight=3,
      require=KID)
def _(c):
    c.key(0.4, ra=(-10, 0, 84), head=(-4, 0, 0))
    c.cycle(0.6, 2.0, 0.45, dict(ra=(-10, 0, 122), head=(-4, 0, 5)), dict(ra=(-10, 0, 84), head=(-4, 0, -3)))
    c.key(2.3, ra=(0, 0, 20), head=(0, 0, 0))
    c.key(2.8)


@clip("child_eager_tell", "Tells it eagerly", trigger="talk", length=2.8, weight=3, require=KID)
def _(c):
    c.key(0.3, ra=(-50, 30, 40), la=(-50, 30, 40), root_pos=(0, -0.6, 0), head=(-6, 0, 0))
    c.key(0.5, root_pos=(0, 0, 0))
    c.key(0.9, ra=(-40, 20, 24), la=(-60, 36, 50), head=(2, -6, 6))
    c.key(1.3, ra=(-62, 36, 50), la=(-40, 20, 24), head=(2, 6, -6), root_pos=(0, -0.5, 0))
    c.key(1.5, root_pos=(0, 0, 0))
    c.key(2.0, ra=(-40, 20, 30), la=(-40, 20, 30), head=(4, 0, 0))
    c.key(2.8)


@clip("child_listen_sway", "Sways while listening", trigger="listen", length=4.0, weight=3, require=KID)
def _(c):
    c.key(0.5, ra=(25, 35, 0), la=(25, 35, 0), head=(4, 0, 0))
    c.wave(0.5, 3.5, 1.5, "root", 6, base=(0, 0, 0), axis=1)
    c.wave(0.5, 3.5, 1.5, "head", 8, base=(4, 0, 0), axis=2, phase=0.2)
    c.key(3.5, ra=(25, 35, 0), la=(25, 35, 0))
    c.key(4.0)


@clip("child_ghost_story", "Plays the ghost in its story", trigger="chat_speak", length=4.4, weight=3,
      require=KID, mirror="never")
def _(c):
    # not child_boo's jump-scare: up on its toes, limp hands held out, drifting from side to side going wooo
    c.key(0.5, root=(4, 0, 0), head=(8, 0, 0), ra=(-20, 4, 4), la=(-20, 4, 4))
    c.key(1.0, root=(0, 0, 0), head=(-6, 0, 0), ra=(-78, 8, 4), la=(-78, 8, 4), root_pos=(0, -0.8, 0))
    c.wave(1.0, 3.4, 1.2, "root", 5, axis=2)
    c.wave(1.0, 3.4, 1.2, "head", 9, base=(-6, 0, 0), axis=2, phase=0.25)
    c.wave(1.0, 3.4, 0.8, "right_arm", 7, base=(-78, 8, 4), axis=0)
    c.wave(1.0, 3.4, 0.8, "left_arm", 7, base=(-78, 8, 4), axis=0, phase=0.5)
    c.wave(1.0, 3.4, 1.2, "root_pos", 0.4, base=(0, -0.8, 0), axis=1, phase=0.25)
    c.key(3.8, ra=(-12, 0, 8), la=(-12, 0, 8), head=(-2, 0, 8), root_pos=(0, 0, 0))
    c.key(4.4)


@clip("child_chat_bob", "Bobs along to a friend", trigger="chat_listen", length=3.6, weight=3, require=KID)
def _(c):
    c.key(0.4, head=(4, 0, 10))
    for i, t in enumerate((0.8, 1.6, 2.4)):
        c.key(t, head=(14, 0, 10 if i % 2 == 0 else -10), root_pos=(0, 0.4, 0))
        c.key(t + 0.35, head=(2, 0, 8 if i % 2 == 0 else -8), root_pos=(0, 0, 0))
    c.key(3.6)


@clip("child_happy_hop", "Happy hops", trigger="happy", length=2.6, weight=3, require=KID, mirror="never")
def _(c):
    for t in (0.2, 1.0):
        c.key(t, root_pos=(0, 0.8, 0), root=(4, 0, 0), ra=(0, 0, 20), la=(0, 0, 20), head=(4, 0, 0))
        c.key(t + 0.3, root_pos=(0, -2.6, 0), root=(-2, 0, 0), ra=(-10, 0, 112), la=(-10, 0, 112),
              head=(-12, 0, 0), rl=(-14, 0, 0), ll=(-14, 0, 0))
        c.key(t + 0.6, root_pos=(0, 0.6, 0), root=(3, 0, 0), ra=(-6, 0, 60), la=(-6, 0, 60), rl=(0, 0, 0),
              ll=(0, 0, 0))
    c.key(2.0, root_pos=(0, 0, 0), root=(0, 0, 0), ra=(0, 0, 10), la=(0, 0, 10), head=(-6, 0, 0))
    c.key(2.6)


@clip("child_thanks_bob", "Quick thank-you bob", trigger="thanks", length=2.4, weight=3, require=KID,
      mirror="never")
def _(c):
    c.key(0.35, ra=(-56, -46, 0), la=(-56, -46, 0), head=(-4, 0, 0))
    c.key(0.5, ra=CHEST, la=CHEST)
    c.key(0.85, ra=CHEST, la=CHEST, root=(13, 0, 0), head=(20, 0, 0))
    c.key(1.25, root=(-1, 0, 0), head=(-8, 0, 0), root_pos=(0, -0.8, 0))
    c.key(1.5, root=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(2.4)


# -- reactions -----------------------------------------------------------------------------------------------------
@clip("child_decline_pout", "Turns away with a pout", trigger="decline", length=2.8, weight=3, require=KID,
      mirror="free")
def _(c):
    c.key(0.35, root=(0, 14, 0), head=(-8, 16, 6), ra=(-36, -54, 0), la=(-36, -54, 0))
    c.key(0.55, root=(0, 18, 0), head=(-10, 22, 8))
    c.key(1.6, root=(0, 17, 0), head=(-12, 24, 8), ra=(-38, -56, 0), la=(-38, -56, 0))
    c.key(1.9, head=(-4, 8, 4))
    c.key(2.2, head=(-10, 22, 8))
    c.key(2.8)


@clip("child_hurt_flinch", "Flinches and covers its skull", trigger="hurt", length=1.6, weight=3, require=KID,
      blend=(.06, .4), mirror="never")
def _(c):
    c.key(0.15, root=(8, 0, 0), head=(10, 0, 0), ra=(-10, 0, 120), la=(-10, 0, 120), root_pos=(0, 0.8, 0.6))
    c.wobble(0.2, 0.7, 8, "head", 4, base=(10, 0, 0), axis=1)
    c.key(0.8, root=(5, 0, 0), ra=(-8, 0, 100), la=(-8, 0, 100), root_pos=(0, 0.5, 0.4))
    c.key(1.2, root=(1, 0, 0), head=(2, 0, 0), ra=(0, 0, 20), la=(0, 0, 20), root_pos=(0, 0, 0))
    c.key(1.6)
