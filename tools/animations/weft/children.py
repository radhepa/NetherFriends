"""Weft children: two arms, no forearms, big heads, and no patience at all yet. They play at being grown-ups.

The pack's waist bends at the grown-ups' hip, which on a child is up in the head, so child clips never use it:
leans go through the root (about the feet) instead."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from kit import clip  # noqa: E402
from _weft import click, tap  # noqa: E402

CHILD = ["child"]


# -- play, fidgets, mischief ------------------------------------------------------------------------------------
@clip("child_four_arm_pretend", "Pretends to have four arms", length=4.4, weight=2, require=CHILD)
def _(c):
    # one arm high and one low, like the grown-ups' two pairs; swap them, swap again; then a little sigh: only two
    c.key(0.4, ra=(-20, 0, 100), la=(-10, 0, 40), head=(0, 10, 0))
    c.key(0.55, ra=(-20, 0, 106), la=(-10, 0, 44))
    c.key(1.1, ra=(-10, 0, 40), la=(-20, 0, 100), head=(0, -10, 0))
    c.key(1.25, ra=(-10, 0, 36), la=(-20, 0, 106))
    c.key(1.8, ra=(-20, 0, 100), la=(-10, 0, 40), head=(4, 10, 6))
    c.key(1.95, ra=(-20, 0, 106), la=(-10, 0, 44))
    c.wobble(1.95, 2.6, 6, "ra", 8, base=(-20, 0, 106), axis=2)
    c.key(2.6, la=(-10, 0, 44), head=(6, 6, 8))
    c.key(3.2, ra=(4, 0, 4), la=(4, 0, 4), head=(18, 0, 0), root=(3, 0, 0))
    c.key(3.6, head=(16, 0, 4))
    c.key(4.4)


@clip("child_hopscotch", "Hopscotch hops", length=3.2, weight=2, require=CHILD, mirror="free")
def _(c):
    # three little hops on one foot, the other tucked up behind, arms out for balance; a two-footed landing
    c.key(0.25, root_pos=(0, 0.4, 0), ll=(30, 0, 0), ra=(-10, 0, 40), la=(-10, 0, 40))
    for i, t in enumerate((0.45, 1.05, 1.65)):
        c.key(t, root_pos=(0, -1.6, -0.6 * (i + 1)), ll=(44, 0, 0), rl=(-8, 0, 0), head=(-6, 0, 0))
        c.key(t + 0.3, root_pos=(0, 0.2, -0.6 * (i + 1) - 0.3), ll=(36, 0, 0), rl=(0, 0, 0), head=(4, 0, 0))
        c.key(t + 0.45, root_pos=(0, 0, -0.6 * (i + 1) - 0.3))
    c.key(2.3, root_pos=(0, -1.2, -2.2), ll=(0, 0, 0), ra=(-30, 0, 60), la=(-30, 0, 60), head=(-10, 0, 0))
    c.key(2.55, root_pos=(0, 0.5, -2.2), ra=(-10, 0, 30), la=(-10, 0, 30), head=(6, 0, 0))
    c.key(2.8, root_pos=(0, 0, -1.4), head=(-4, 0, 6))
    c.key(3.2)


@clip("child_string_game", "String game", length=4.6, weight=2, require=CHILD)
def _(c):
    # a loop of thread between the hands, pulled taut and let in, over and over, head tilted close; a proud click
    c.key(0.5, ra=(-70, -8, 0), la=(-70, -8, 0), head=(16, 0, 6))
    c.cycle(0.5, 3.3, 0.7, {"ra": (-70, -8, 0), "la": (-70, -8, 0)}, {"ra": (-72, 14, 10), "la": (-72, 14, 10)})
    c.key(1.4, head=(18, 0, -6))
    c.key(2.4, head=(18, 0, 8))
    c.key(3.3, head=(-6, 0, 0))
    click(c, 3.4, 3.9, base=(-6, 0, 4), rate=7, amp=4)
    c.hold(3.3, 3.9, ra=(-70, -8, 0), la=(-70, -8, 0))
    c.key(4.6)


@clip("child_spider_pounce", "Spider pounce", length=3.6, weight=2, require=CHILD)
def _(c):
    # creeps a step with arms up like clutching legs, wiggles them... and pounces at nothing
    c.key(0.4, ra=(-130, 20, 10), la=(-130, 20, 10), root=(6, 0, 0), head=(4, 0, 0), rl=(-10, 0, 0))
    c.key(0.8, rl=(0, 0, 0), ll=(-10, 0, 0), root_pos=(0, 0, -0.6))
    c.key(1.2, ll=(0, 0, 0), rl=(-10, 0, 0), root_pos=(0, 0, -1.2))
    c.wobble(1.2, 2.0, 5, "ra", 12, base=(-130, 20, 10))
    c.wobble(1.25, 2.05, 5, "la", 12, base=(-130, 20, 10))
    c.key(2.0, rl=(0, 0, 0), root=(10, 0, 0), root_pos=(0, 0.6, -1.2))
    c.key(2.3, root=(-2, 0, 0), root_pos=(0, -1.8, -3.0), ra=(-80, 0, 20), la=(-80, 0, 20), head=(-10, 0, 0))
    c.key(2.55, root=(4, 0, 0), root_pos=(0, 0.3, -3.4), ra=(-60, 0, 10), la=(-60, 0, 10), head=(8, 0, 0))
    c.key(2.9, root=(0, 0, 0), root_pos=(0, 0, -3.0), head=(0, 0, 8))
    c.key(3.6)


@clip("child_poke_hide", "Pokes and hides it", length=3.4, weight=2, require=CHILD)
def _(c):
    # a quick poke at something (someone?), the arm whipped behind the back, the head turned away all innocence
    c.key(0.3, ra=(-40, 0, 0), head=(6, 0, 0), root=(-2, 0, 0))
    c.key(0.5, ra=(-95, -6, 0), root=(4, 0, 0), root_pos=(0, 0, -0.5))
    c.key(0.65, ra=(-80, -6, 0))
    c.key(0.8, ra=(-95, -6, 0))
    c.key(1.15, ra=(40, -20, 0), root=(0, 0, 0), root_pos=(0, 0, 0), head=(-8, -30, 10))
    c.hold(1.15, 2.6, ra=(40, -20, 0))
    c.key(1.6, la=(30, -20, 0), head=(-10, -34, 12))
    c.key(2.2, head=(-8, -24, -6))
    click(c, 2.2, 2.7, base=(-8, -24, -6), rate=8, amp=3)
    c.hold(1.6, 2.6, la=(30, -20, 0))
    c.key(3.4)


@clip("child_greet_bounce", "Bouncy wave", trigger="greet", length=2.6, weight=3, require=CHILD)
def _(c):
    # both arms up and waving, a bounce on the toes
    # (a child's arms are short: straight up they vanish behind the head, so the wave is a V out to the sides)
    c.key(0.3, ra=(-14, 0, 140), la=(-14, 0, 140), head=(-10, 0, 0))
    c.cycle(0.3, 1.9, 0.5, {"ra": (-14, 0, 158), "la": (-14, 0, 124)}, {"ra": (-14, 0, 124), "la": (-14, 0, 158)})
    for t in (0.4, 1.0, 1.6):
        c.key(t, root_pos=(0, 0, 0))
        c.key(t + 0.18, root_pos=(0, -1.0, 0))
        c.key(t + 0.36, root_pos=(0, 0, 0))
    c.key(1.96, head=(-6, 0, 10))
    c.key(2.6)


@clip("child_talk_chatter", "Chatters away", trigger="talk", length=3.0, weight=3, require=CHILD)
def _(c):
    # arms flap out in turn with every word, head bobbing along; far too much to say
    c.cycle(0.2, 2.6, 0.6, {"ra": (-50, 20, 40), "la": (-10, 0, 10), "head": (-4, 8, 4)},
            {"ra": (-10, 0, 10), "la": (-50, 20, 40), "head": (2, -8, -4)})
    c.key(2.6, ra=(-30, 0, 20), la=(-30, 0, 20), head=(-8, 0, 0))
    c.key(3.0)


@clip("child_chat_boast", "Boasts with big arms", trigger="chat_speak", length=3.4, weight=3, require=CHILD)
def _(c):
    # "this big!": arms thrown wide; then a thumb to its own chest; chin up
    c.key(0.4, ra=(-30, 0, 80), la=(-30, 0, 80), head=(-8, 0, 0), root=(-2, 0, 0))
    c.key(0.6, ra=(-30, 0, 90), la=(-30, 0, 90))
    c.hold(0.6, 1.4, ra=(-30, 0, 90), la=(-30, 0, 90))
    c.key(1.9, ra=(-56, -44, 10), la=(-10, 0, 10), head=(-14, 6, 6), root=(-3, 0, 0))
    tap(c, 1.9, 2.7, "ra", (-56, -44, 10), -10, period=0.4)
    c.key(2.7, head=(-12, 0, -6))
    c.key(3.4)


@clip("child_chat_listen", "Listens swaying", trigger="chat_listen", length=3.8, weight=3, require=CHILD)
def _(c):
    # hands behind the back, swaying side to side on the heels, head tilting after each word
    c.key(0.4, ra=(30, -10, 0), la=(30, -10, 0))
    c.hold(0.4, 3.2, ra=(30, -10, 0), la=(30, -10, 0))
    c.key(0.9, root=(0, 0, 3), head=(2, 0, -8))
    c.key(1.7, root=(0, 0, -3), head=(2, 0, 8))
    c.key(2.5, root=(0, 0, 3), head=(4, 0, -6))
    c.key(3.2, root=(0, 0, 0), head=(4, 0, 0))
    c.key(3.8)


# -- reactions --------------------------------------------------------------------------------------------------
@clip("child_happy_jump", "Jumps for joy", trigger="happy", length=2.4, weight=3, require=CHILD)
def _(c):
    # two big jumps, arms flung up and out, legs kicking back
    for t in (0.15, 1.0):
        c.key(t, root_pos=(0, 0.6, 0), ra=(-10, 0, 40), la=(-10, 0, 40), head=(6, 0, 0))
        c.key(t + 0.3, root_pos=(0, -3.0, 0), ra=(-14, 0, 150), la=(-14, 0, 150), rl=(20, 0, 0), ll=(16, 0, 0),
              head=(-14, 0, 0))
        c.key(t + 0.55, root_pos=(0, -1.6, 0), rl=(6, 0, 0), ll=(4, 0, 0))
        c.key(t + 0.75, root_pos=(0, 0.5, 0), ra=(-10, 0, 60), la=(-10, 0, 60), rl=(0, 0, 0), ll=(0, 0, 0),
              head=(4, 0, 0))
    c.key(2.0, root_pos=(0, 0, 0), head=(-6, 0, 8))
    c.key(2.4)


@clip("child_thanks_hug", "Hugs the gift", trigger="thanks", length=3.0, weight=3, require=CHILD)
def _(c):
    # the gift squeezed to the chest with both arms, a happy sway side to side
    # (one arm a little over the other, so they wrap the gift instead of passing through each other)
    c.key(0.4, ra=(-66, -36, 6), la=(-60, -38, 4), head=(14, 0, 0))
    c.hold(0.4, 2.3, ra=(-66, -36, 6), la=(-60, -38, 4))
    c.key(0.9, root=(0, 0, 4), head=(12, 0, -10))
    c.key(1.5, root=(0, 0, -4), head=(12, 0, 10))
    c.key(2.1, root=(0, 0, 2), head=(8, 0, -4))
    c.key(2.4, root=(0, 0, 0))
    c.key(3.0)


@clip("child_decline_pout", "Pouts and turns away", trigger="decline", length=3.0, weight=3, require=CHILD)
def _(c):
    # arms crossed hard, the whole body turned away, chin down and head turned further
    c.key(0.35, ra=(-64, -42, 6), la=(-52, -44, 6), root=(0, -20, 0), head=(10, -16, 6))
    c.key(0.5, root=(0, -24, 0), head=(12, -20, 8))
    c.hold(0.5, 2.3, ra=(-64, -42, 6), la=(-52, -44, 6), root=(0, -24, 0))
    c.key(1.4, head=(14, -18, 10))
    c.key(1.8, head=(6, -6, 4))
    c.key(2.0, head=(12, -22, 8))
    c.key(3.0)


@clip("child_pleased_wiggle", "Pleased wiggle", trigger="pleased", length=2.2, weight=3, require=CHILD)
def _(c):
    # a quick wiggle side to side, arms flapping low, a pleased head tilt and clicks
    c.cycle(0.1, 1.5, 0.35, {"root": (0, 0, 4), "ra": (-10, 0, 30), "la": (-10, 0, 10)},
            {"root": (0, 0, -4), "ra": (-10, 0, 10), "la": (-10, 0, 30)})
    c.key(1.5, head=(-6, 0, 12))
    click(c, 1.5, 1.9, base=(-6, 0, 12), rate=8, amp=4)
    c.key(2.2)


@clip("child_annoyed_stomp", "Stamps a foot", trigger="annoyed", length=2.6, weight=3, require=CHILD)
def _(c):
    # arms stiff and straight down, a foot lifted and stamped, twice, and a cross little head shake
    c.key(0.3, ra=(14, 0, 14), la=(14, 0, 14), head=(8, 0, 0))
    c.hold(0.3, 1.9, ra=(14, 0, 14), la=(14, 0, 14))
    for t in (0.35, 0.95):
        c.key(t, rl=(-34, 0, 0), root_pos=(0, -0.3, 0))
        c.key(t + 0.18, rl=(4, 0, 0), root_pos=(0, 0.3, 0))
        c.key(t + 0.32, rl=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(1.4, head=(10, -14, 0))
    c.key(1.6, head=(10, 14, 0))
    c.key(1.8, head=(10, -8, 0))
    c.key(2.0, head=(8, 0, 0))
    c.key(2.6)


@clip("child_hurt_flinch", "Flinches", trigger="hurt", length=1.6, weight=3, require=CHILD, blend=(0.06, 0.35))
def _(c):
    # arms fly up in front of the big head (not into it), the body jerks back; then a sniffly recovery
    c.key(0.1, ra=(-98, -24, 28), la=(-98, -24, 28), root=(-8, 0, 0), root_pos=(0, 0, 1.0), head=(16, 0, 0))
    c.key(0.4, ra=(-92, -24, 26), la=(-92, -24, 26), root=(-5, 0, 0), head=(14, 0, 4))
    c.key(0.9, ra=(-30, -10, 4), la=(-30, -10, 4), root=(0, 0, 0), root_pos=(0, 0, 0.4), head=(10, 0, 0))
    c.wobble(0.9, 1.3, 6, "head", 3, base=(10, 0, 0), axis=2)
    c.key(1.6)
