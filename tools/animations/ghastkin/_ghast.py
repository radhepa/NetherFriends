"""Shared poses and motions for the Ghastkin pack. Not a clip module (compile.py skips files starting with _).

The Ghastkin float: no legs, six tentacles (four on children) hanging from the hips, single-piece arms (no
elbows), a cape (male, child of the body) or a long veil (female, child of the head).

Tentacles are centre parts: pitch + swings the tip BACK (it trails), pitch - curls it forward; roll + swings the
tip to the person's RIGHT. Adults: 0 front-right, 1 front-centre, 2 front-left, 3 back-right, 4 back-centre,
5 back-left. Children: 0 front-right, 1 front-left, 2 back-right, 3 back-left (4 and 5 are skipped).

The waist turns every upper part about the hips, tentacles included, so a bow tips the whole body; counter-rotate
the tentacles (bow() does) so they keep hanging. The pack's hip sits inside a child's head: keep waist moves out
of clips children play and sway them with the root instead.
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

N = 6
SIDE = [1, 0, -1, 1, 0, -1]    # +1 on the person's right
ROW = [-1, -1, -1, 1, 1, 1]    # -1 front row, +1 back row
FRONT_TO_BACK = [0, 1, 2, 3, 4, 5]
RIGHT_TO_LEFT = [0, 3, 1, 4, 2, 5]


def T(i):
    return f"tentacle_{i}"


def merge(*dicts):
    out = {}
    for d in dicts:
        out.update(d)
    return out


def tents(pitch=0.0, yaw=0.0, roll=0.0):
    """Every tentacle the same."""
    return {T(i): (pitch, yaw, roll) for i in range(N)}


def splay(a, pitch=0.0):
    """Tips spread outward by about a degrees (negative gathers them together); pitch adds to all (+ trails)."""
    return {T(i): (ROW[i] * a + pitch, 0.0, SIDE[i] * a * 0.8) for i in range(N)}


def bow(w, tent_keep=1.0):
    """Waist bow of w degrees with the tentacles kept hanging. The cape (fixed at the top of the back) stays along
    the back when they bow forward, flaring a little; when they lean back it swings free of the back."""
    cape = w * 0.1 if w > 0 else -w * 0.6
    return merge({"waist": (w, 0, 0), "cape": (cape, 0, 0)}, tents(pitch=-w * tent_keep))


def hd(p=0.0, y=0.0, r=0.0, w=0.0):
    """Head pose with the veil kept clear (w: any waist pitch the head also rides on). The veil is pinned at the
    top back of the head: looking down it rides with the head (countering only a little, or it would hang through
    the head); looking up it is countered so it doesn't swing into the back."""
    t = p + w
    return {"head": (p, y, r), "veil": (-t * (0.25 if t > 0 else 0.85), -y * 0.15, -r * 0.6)}


# -- arm poses (single-piece arms; checked on the sheet and for overlap) -----------------------------------------
# The arms are long and straight and each shoulder sits under a corner of the head, so a hand raised to the face
# runs the arm through the chin. Hands for the face stop a little in front of it with the shoulder nudged out
# (ra_pos x 1) or forward (z -); hands that meet stop where they touch instead of crossing through each other.
# Keep a HEART's ra_pos keyed for as long as the hand stays on the heart, or the arm sinks into the chest.
HEART = {"ra": (-72, -66, 0), "ra_pos": (0, 0, -1.5)}          # right hand across the chest, over the heart
EYE = {"ra": (-118, -16, 0), "ra_pos": (1, 0, 0)}              # right hand before the right eye
EYE_L = {"la": (-118, -16, 0), "la_pos": (1, 0, 0)}
CHIN = {"ra": (-106, -16, 0), "ra_pos": (1, 0, 0)}              # right hand at the chin / hiding a smile
BROW = {"ra": (-138, -8, 0), "ra_pos": (0, 0, -2)}              # shading the eyes
CLASP = {"ra": (-35, -27, 0), "la": (-36, -26, 0)}              # hands together before the belly
FOLD = {"ra": (-76, -60, 0), "la": (-58, -52, 0), "ra_pos": (0, 0, -2.2), "la_pos": (0, 0, -0.8)}
HUG = {"ra": (-48, -56, 0), "la": (-66, -60, 0), "ra_pos": (0, 0, -1.2), "la_pos": (0, 0, -2.2)}
VIAL = {"ra": (-48, -22, 0), "la": (-50, -20, 0)}               # both hands at the tear vial on the chest
CUP = {"ra": (-77, -16, 0), "la": (-77, -16, 0)}                # cupped hands held out


def aim(side, x, y, z, child=False, roll=0.0):
    """Arm angles (pitch, yaw_out, roll_out) pointing the arm from its shoulder toward the model-space point
    (x- the person's right, y- up, z- forward). The arm is 10.5 px to the hand (child 4.5), so the hand ends on
    that line, not necessarily at the point."""
    sx, sy = (2.7, 12.5) if child else (4.0, -3.0)
    if side == "r":
        sx = -sx
    dx, dy, dz = x - sx, y - sy, z
    n = math.sqrt(dx * dx + dy * dy + dz * dz)
    dx, dy, dz = dx / n, dy / n, dz / n
    p = -math.acos(max(-1.0, min(1.0, dy)))
    s = math.sin(p)
    yaw = math.degrees(math.atan2(dx / s, dz / s)) if abs(s) > 1e-6 else 0.0
    p = math.degrees(p)
    if abs(yaw) > 90:   # behind: swing up past the vertical instead of twisting the arm round
        p, yaw = -360 - p, yaw - 180 if yaw > 0 else yaw + 180
    if side == "l":
        yaw = -yaw
    return (round(p, 1), round(yaw, 1), roll)


def env(keys, t):
    """Smooth (smoothstep) interpolation through [(time, (x, y, z))]."""
    if not keys:
        return (0.0, 0.0, 0.0)
    if t <= keys[0][0]:
        return tuple(keys[0][1])
    for (t0, a), (t1, b) in zip(keys, keys[1:]):
        if t <= t1:
            s = (t - t0) / (t1 - t0) if t1 > t0 else 1.0
            s = s * s * (3 - 2 * s)
            return tuple(a[k] + (b[k] - a[k]) * s for k in range(3))
    return tuple(keys[-1][1])


def clear(c, channel, start, end):
    """Drop a channel's keys from start to end, so a dense motion written there isn't spiked by stray pose keys
    (bow() keys the tentacles too); the motion's base envelope carries the pose instead."""
    from kit import part_name
    keys = c.channels.get(part_name(channel), {})
    for t in [t for t in keys if start - 1e-6 <= t <= end + 1e-6]:
        del keys[t]


def osc(c, channel, start, end, period, amp, axis=0, base=None, phase=0.0, steps=8, fade=True, decay=0.0):
    """A sine of amp degrees (or pixels) on one component of a channel, on top of a keyed envelope base
    [(t, (x, y, z))]. Fades in and out over half a period. Replaces the channel's other keys in that span."""
    clear(c, channel, start, end)
    n = max(2, int(round((end - start) / period * steps)))
    for i in range(n + 1):
        t = start + (end - start) * i / n
        v = list(env(base or [], t))
        f = min(1.0, (t - start) / (period / 2), (end - t) / (period / 2)) if fade else 1.0
        f *= 1 - decay * (t - start) / max(1e-6, end - start)
        v[axis] += amp * math.sin(((t - start) / period + phase) * math.tau) * max(0.0, f)
        c.key(t, **{channel: tuple(v)})
    return c


def ripple(c, start, end, period, amp, base=None, lag=0.12, order=FRONT_TO_BACK, roll=0.0, steps=8, decay=0.0):
    """The tentacles ripple one after another (pitch sine of amp degrees, each lagging the last by lag of a
    period, roll adds a sideways sway a quarter period behind). base: [(t, pose dict of tentacles)] envelope."""
    for i in range(N):
        clear(c, T(i), start, end)
        keys = [(t, p.get(T(i), (0, 0, 0))) for t, p in (base or [])]
        lag_i = order.index(i) * lag
        n = max(2, int(round((end - start) / period * steps)))
        for s in range(n + 1):
            t = start + (end - start) * s / n
            v = list(env(keys, t))
            f = min(1.0, (t - start) / (period / 2), (end - t) / (period / 2))
            f = max(0.0, f) * (1 - decay * (t - start) / max(1e-6, end - start))
            ph = ((t - start) / period - lag_i) * math.tau
            v[0] += amp * math.sin(ph) * f
            if roll:
                v[2] += roll * math.cos(ph) * f
            c.key(t, **{T(i): tuple(v)})
    return c


def flutter(c, start, end, rate, amp, base=None, decay=0.0):
    """Quick happy flutter: neighbours beat against each other."""
    for i in range(N):
        clear(c, T(i), start, end)
        keys = [(t, p.get(T(i), (0, 0, 0))) for t, p in (base or [])]
        steps = max(2, int((end - start) * rate * 2))
        sign0 = 1 if i % 2 else -1
        for s in range(steps + 1):
            t = start + (end - start) * s / steps
            v = list(env(keys, t))
            k = 0 if s in (0, steps) else (1 if s % 2 else -1) * sign0
            v[0] += k * amp * (1 - decay * s / steps)
            c.key(t, **{T(i): tuple(v)})
    return c
