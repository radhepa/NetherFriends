"""Shared helpers for the Piglin pack. Not a clip module (compile.py skips files that start with an underscore).

Piglin ears hang from the top of the head, already tipped 30 degrees out. In kit terms (left_ear / right_ear):
roll_out + perks them out toward level, roll_out - lets them hang flat against the head, pitch - swings the tips
forward, pitch + pins them back. Most adults carry a sword or crossbow in the right hand, and that arm keeps the
vanilla pose, so everyday gestures are made with the left arm (mirror="never") and read with the head, ears and
body as well.
"""


def ears(c, t, roll=0.0, pitch=0.0, yaw=0.0, left=None, right=None):
    """Key both ears at t (symmetric unless left/right are given as full tuples)."""
    both = (pitch, yaw, roll)
    c.key(t, left_ear=left if left is not None else both, right_ear=right if right is not None else both)


def flick(c, t, side="both", amp=28.0, base=(0.0, 0.0, 0.0), dur=0.32):
    """A quick ear flick starting at t: out, back past rest, settle on base."""
    names = ["left_ear", "right_ear"] if side == "both" else [side + "_ear"]
    for n in names:
        b = base
        c.key(t, **{n: b})
        c.key(t + dur * 0.3, **{n: (b[0] - amp * 0.35, b[1], b[2] + amp)})
        c.key(t + dur * 0.65, **{n: (b[0] + amp * 0.1, b[1], b[2] - amp * 0.25)})
        c.key(t + dur, **{n: b})


def snort(c, t, base=(0.0, 0.0, 0.0), amp=9.0):
    """A pig snort: the snout jerks up then down, ears jump."""
    c.key(t, head=base)
    c.key(t + 0.08, head=(base[0] - amp, base[1], base[2]))
    c.key(t + 0.2, head=(base[0] + amp * 0.6, base[1], base[2]))
    c.key(t + 0.34, head=base)
