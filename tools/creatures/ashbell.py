"""Ashbell: the Ghastkin's beast. A pale drifter of the Basalt Deltas, like a jellyfish of the air: a soft bell the
colour of delta smoke with a glow at its heart, and threads trailing beneath it, beaded with light. It rings faintly
as it drifts. The Ghastkin tend them round their pods, and say a ghast that hears one remembers being kin, and holds
its fire. About 1.3 blocks from the top of the bell to the tips of the threads.

  python tools/creatures/ashbell.py
"""
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(os.path.dirname(HERE), "people"))

from common import glow_as, hexc, painter, shade  # noqa: E402
from mobkit import Model, Pack, faces  # noqa: E402

NAME = "Ashbell"
BELL = ["#d8d3e0", "#cfcadb", "#dedae6"]
BELL_TOP = "#ece9f2"
BELL_RIM = "#b6b0c6"
CORE = "#bfe9f5"
CORE_GLOW = "#5f8fa0"
THREAD = ["#a9a4b8", "#9f9aaf"]
BEAD = "#d8f0f8"
BEAD_GLOW = "#6a98a8"
FRILL = ["#c4bdd6", "#bab3cc"]
ALPHA = 178          # the bell is half see-through: the glow at its heart shows


def see_through(col, a=ALPHA):
    r, g, b = hexc(col)[:3]
    return (r, g, b, a)


def bell(c, x, y):
    if c.face == "top":
        return see_through(BELL_TOP, 205)
    if c.face == "bottom":
        return see_through(BELL_RIM, 150)
    col = c.pick(BELL)
    if (x + y * 2) % 7 == 0:
        col = BELL_TOP                           # faint ribs
    return see_through(col)


def rim(c, x, y):
    """The scalloped skirt of the bell."""
    if c.face in ("front", "back", "left", "right") and y == c.h - 1 and x % 3 == 1:
        return None
    if c.face == "top":
        return see_through(BELL[1], 190)
    return see_through(BELL_RIM, 200)


def core(c):
    for y in range(c.h):
        for x in range(c.w):
            glow_as(c, x, y, CORE if (x + y) % 3 else "#e8faff", CORE_GLOW)


def thread(c, x, y):
    """A trailing thread, beaded with light every few pixels and lit at the tip."""
    if c.face in ("top", "bottom"):
        return THREAD[0]
    if y % 4 == 3 or y >= c.h - 1:
        glow_as(c, x, y, BEAD, BEAD_GLOW)
        return None
    return c.pick(THREAD)


def frill(c, x, y):
    if c.face in ("top", "bottom"):
        return FRILL[0]
    if y == c.h - 1 and x % 2 == 0:
        return None
    return c.pick(FRILL)


THREADS = 8


def build():
    m = Model("ashbell", 64, 64, seed=229)
    P = Pack(m)
    bl = m.part("bell", (0, 9, 0))
    P.add(bl, (-3.5, -9, -3.5), (7, 2, 7), faces(painter(bell)))            # crown
    P.add(bl, (-5, -7, -5), (10, 5, 10), faces(painter(bell)))              # dome
    P.add(bl, (-6, -2, -6), (12, 2, 12), faces(painter(rim)))               # skirt
    P.add(bl, (-2.5, -6, -2.5), (5, 4, 5), faces(core))                     # the glow at its heart
    for i in range(THREADS):
        a = 2 * math.pi * i / THREADS
        x, z = round(math.cos(a) * 4.5, 1), round(math.sin(a) * 4.5, 1)
        th = m.part(f"thread_{i}", (x, 9, z))
        P.add(th, (-0.5, 0, -0.5), (1, 11 if i % 2 else 9, 1), faces(painter(thread)), share="thread_long" if i % 2 else "thread_short")
    for i, (x, z) in enumerate(((-1.2, 0.6), (1.2, -0.6))):
        fr = m.part(f"frill_{i}", (x, 9, z))
        P.add(fr, (-1, 0, -1), (2, 7, 2), faces(painter(frill)), share="frill")
    P.apply()
    m.save()


if __name__ == "__main__":
    build()
