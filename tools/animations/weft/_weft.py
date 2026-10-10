"""Shared helpers for the Weft pack (not a clip module: compile.py skips files starting with _)."""
import math


def click(c, start, end, base=(0, 0, 0), rate=6.0, amp=3.0, axis=2):
    """Mandible clicking: quick small head ticks (a tilt by default) around a base head pose."""
    c.wobble(start, end, rate, "head", amp, base=base, axis=axis)
    return c


def tap(c, start, end, channel, base, delta, period=0.3, axis=0):
    """A precise repeated tap on one channel: base -> base+delta -> base ... ending on base."""
    b = tuple(base)
    a = list(base)
    a[axis] += delta
    c.cycle(start, end, period, {channel: b}, {channel: tuple(a)}, end_on="a")
    return c


def _rx(a):
    c, s = math.cos(a), math.sin(a)
    return [[1, 0, 0], [0, c, -s], [0, s, c]]


def _ry(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, 0, s], [0, 1, 0], [-s, 0, c]]


def _rz(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, -s, 0], [s, c, 0], [0, 0, 1]]


def _mul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(3)) for j in range(3)] for i in range(3)]


def twist(pitch, turn, roll=0.0):
    """An arm pose that turns the limb about its own hanging axis FIRST (turn - points the elbow bend across the
    chest, + outward), then raises it by pitch and lifts it out by roll, in the kit's (pitch, yaw_out, roll_out)
    convention. Folding arms needs this: a plain yaw swings the whole raised arm instead of turning the forearm."""
    m = _mul(_rz(math.radians(roll)), _mul(_rx(math.radians(pitch)), _ry(math.radians(turn))))
    y = math.asin(max(-1.0, min(1.0, -m[2][0])))
    x = math.atan2(m[2][1], m[2][2])
    z = math.atan2(m[1][0], m[0][0])
    return tuple(round(math.degrees(v), 2) for v in (x, y, z))


def twist_to(c, t0, t1, channel, start, end, steps=3):
    """Key a twisted arm pose along the way from start to end (each a (pitch, turn[, roll]) triple), so the
    Euler angles in between follow the real path instead of swinging the arm wide."""
    s = list(start) + [0] * (3 - len(start))
    e = list(end) + [0] * (3 - len(end))
    for i in range(steps + 1):
        f = i / steps
        f = f * f * (3 - 2 * f)
        c.key(t0 + (t1 - t0) * i / steps, **{channel: twist(*[a + (b - a) * f for a, b in zip(s, e)])})
    return c


# an upper hand on the hip: twist this, forearm pitch HIP_FOREARM
HIP = (0, -90, 48)
HIP_FOREARM = -60

# upper hands clasped behind the back: twist BACK, forearm BACK_FOREARM (the left a touch lower, the hands meet
# just behind the small of the back)
BACK = (22, -90, 4)
BACK_L = (20, -90, 4)
BACK_FOREARM = -26
_BACK_WAY = (24, -45, 8)


def behind_back(c, t0, t1, t2, t3):
    """The upper hands go behind the back (t0..t1), stay clasped (t1..t2) and come back (t2..t3). They travel by
    way of a turned-out, swung-back arm so the bent forearms never sweep through the body or the legs."""
    for t_a, t_b, out in ((t0, t1, True), (t2, t3, False)):
        mid = round(t_a + (t_b - t_a) * 0.5, 4)
        for ch, pose in (("ra", BACK), ("la", BACK_L)):
            if out:
                twist_to(c, t_a, mid, ch, (0, 0, 0), _BACK_WAY, steps=2)
                twist_to(c, mid, t_b, ch, _BACK_WAY, pose, steps=2)
            else:
                twist_to(c, t_a, mid, ch, pose, _BACK_WAY, steps=2)
                twist_to(c, mid, t_b, ch, _BACK_WAY, (0, 0, 0), steps=2)
        c.key(mid, rf=(-6, 0, 0), lf=(-6, 0, 0))
    c.hold(t1, t2, ra=twist(*BACK), la=twist(*BACK_L), rf=(BACK_FOREARM, 0, 0), lf=(BACK_FOREARM + 2, 0, 0))
    c.rest(t3, "rf", "lf")
    return c


def _pitch_at(keys, t, length):
    """The pitch component of a channel at t, interpolated linearly between keys (rest at the clip's ends)."""
    pts = dict(keys)
    pts.setdefault(0.0, (0.0, 0.0, 0.0))
    pts.setdefault(round(length, 4), (0.0, 0.0, 0.0))
    ts = sorted(pts)
    if t <= ts[0]:
        return pts[ts[0]][0]
    for a, b in zip(ts, ts[1:]):
        if a <= t <= b:
            f = (t - a) / (b - a) if b > a else 0.0
            return pts[a][0] + (pts[b][0] - pts[a][0]) * f
    return pts[ts[-1]][0]


def hang_tails(c, amount=0.7, least=6.0):
    """Secondary motion for the women's head wraps: counter the head's and the waist's pitch so the two long
    tails keep hanging (and lag a little) instead of swinging into the back on a look up or sticking out on a bow."""
    head = c.channels.get(("head", "rot"), {})
    waist = c.channels.get(("waist", "rot"), {})
    times = sorted(set(head) | set(waist))
    if not times:
        return
    vals = [-(_pitch_at(head, t, c.length) + _pitch_at(waist, t, c.length)) * amount for t in times]
    if max(abs(v) for v in vals) < least:
        return
    track = c.channels.setdefault(("wrap_tails", "rot"), {})
    for t, v in zip(times, vals):
        # the cloth lags: each key lands a touch later than the motion that causes it
        lag = round(t + 0.08, 4)
        if lag < c.length - 0.05:
            track[lag] = (round(v, 2), 0.0, 0.0)
    track[round(c.length, 4)] = (0.0, 0.0, 0.0)


def hang_all():
    """Give every clip registered so far by this module the hanging head tails."""
    import kit
    for c in kit.CLIPS:
        hang_tails(c)
