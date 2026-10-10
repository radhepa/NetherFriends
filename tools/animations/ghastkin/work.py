from kit import clip
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _ghast import BROW, CLASP, EYE, EYE_L, HEART, VIAL, bow, flutter, hd, merge, osc, ripple, splay, tents  # noqa: E402,F401

# Work: each trade of the Eave, practised standing (well, floating) still. Weights keep the trade visible.


@clip("tearkeeper_fill", "Fills a tear vial", length=5.6, weight=4, require=["adult", "job:tearkeeper"])
def _(c):
    # one tear, properly: the left hand brings it from the eye down into the vial the right hand holds out
    c.key(0.4, ra=(-30, -10, 0), la=(-20, -8, 0), la_pos=(0, 0, 0), **hd(4, 0, 0))
    c.key(1.1, ra=(-83, -21, 0), la=(-120, -16, 0), la_pos=(1, 0, 0), **hd(8, 3, 6))
    c.key(1.4, la=(-118, -16, 0))
    c.wobble(1.4, 2.0, 2.0, "la", 4, base=(-118, -16, 0), axis=1)
    c.key(2.05, la_pos=(1, 0, 0))
    c.key(2.6, la=(-92, -24, 0), la_pos=(0.5, 0, -1), ra=(-82, -21, 0), **hd(14, 0, 3))
    c.key(2.85, la=(-86, -23, 0))
    c.key(3.05, la=(-91, -24, 0), ra=(-80, -21, 0))
    c.key(3.6, **VIAL, la_pos=(0, 0, 0), **hd(26, 0, 0))
    c.wobble(3.7, 4.4, 3.0, "ra", 6, base=(-48, -22, 0), axis=0)
    c.key(4.5, la=(-50, -20, 0), **hd(20, 0, 0))
    c.key(5.1, ra=(-6, -4, 0), la=(-5, -4, 0), **hd(-3, 0, 0))
    c.key(5.6)
    c.key(1.1, root_pos=(0, 0.4, 0))
    c.key(2.6, root_pos=(0, 0.8, 0))
    c.key(4.6, root_pos=(0, 0.2, 0))
    ripple(c, 0.4, 5.4, 2.5, 4, base=[(0.4, tents()), (1.5, splay(-5)), (4.0, splay(-5)), (5.4, tents())])


@clip("tearkeeper_light", "Holds a vial to the light", length=5.0, weight=3, require=["adult", "job:tearkeeper"])
def _(c):
    # up to the glow it goes, swirled gently, judged with a tilt of the head, and brought down with a nod
    c.key(0.4, ra=(-40, -6, 0), ra_pos=(0, 0, 0), **hd(0, 0, 0))
    c.key(1.2, ra=(-160, -10, 0), ra_pos=(1, 0, 0), **hd(-30, -6, 0), root_pos=(0, -0.6, 0))
    c.key(1.4, ra=(-158, -10, 0))
    osc(c, "ra", 1.4, 3.0, 0.8, 6, axis=1, base=[(1.4, (-158, -10, 0)), (3.0, (-157, -10, 0))])
    c.key(2.0, **hd(-33, -8, 8))
    c.key(2.7, **hd(-31, -6, -6))
    c.key(3.2, ra=(-156, -10, 0), ra_pos=(1, 0, 0), **hd(-28, -4, 0))
    c.key(3.55, ra=(-112, -4, 0), ra_pos=(1, 0, -1), **hd(-14, -2, 0))     # down past the face, not through it
    c.key(3.9, ra=(-50, -30, 0), ra_pos=(0, 0, 0), la=(-30, -20, 0), **hd(16, 0, 0), root_pos=(0, 0.3, 0))
    c.key(4.15, **hd(22, 0, 0))
    c.key(4.4, **hd(12, 0, 0))
    c.key(5.0)
    c.key(3.2, la=(0, 0, 0))
    ripple(c, 0.3, 4.8, 2.2, 5, base=[(0.3, tents()), (1.4, tents(pitch=5)), (3.2, tents(pitch=5)), (4.8, tents())])


@clip("songwarden_lament", "Sings a lament", length=6.4, weight=4, require=["adult", "job:songwarden"])
def _(c):
    # the song swells: face lifted, arms opening on the air, the body rocking with it; it ends with folded hands
    osc(c, "root", 0.5, 4.7, 2.1, 3.0, axis=2)
    c.key(0.5, ra=(0, 0, 0), la=(0, 0, 0), root_pos=(0, 0, 0))
    c.key(1.4, ra=(-18, 0, 28), la=(-18, 0, 28), **hd(-14, 0, 0), root_pos=(0, -0.5, 0))
    c.key(2.6, **hd(-22, 0, -6))
    c.key(3.3, ra=(-26, 6, 52), la=(-26, 6, 52), **hd(-26, 0, 6), root_pos=(0, -1.4, 0))
    c.key(3.8, ra=(-24, 6, 55), la=(-24, 6, 55))
    c.key(4.7, ra=(-30, -26, 0), la=(-32, -25, 0), **hd(-6, 0, 0), root_pos=(0, -0.3, 0))
    c.key(5.4, **CLASP, **hd(18, 0, 0), root_pos=(0, 0.6, 0))
    c.key(6.4)
    c.key(0.4, **hd(0, 0, 0))
    ripple(c, 0.5, 6.0, 1.05, 9, lag=0.1, roll=4,
           base=[(0.5, tents()), (3.3, tents(pitch=6)), (5.0, splay(-5)), (6.0, tents())])


@clip("songwarden_ashbells", "Calls the Ashbells", length=5.4, weight=3, require=["adult", "job:songwarden"])
def _(c):
    # a slow beckon up toward the drifting bells, a hand on the heart, head tilted to the faint ringing
    c.key(0.4, ra=(-40, 8, 0), **hd(-4, 0, 0))
    c.key(1.1, ra=(-130, 16, 0), **hd(-18, 10, 4), root_pos=(0, -0.5, 0))
    osc(c, "ra", 1.1, 3.4, 1.15, 14, axis=0, base=[(1.1, (-130, 16, 0)), (3.4, (-128, 18, 0))])
    c.key(0.6, la=(0, 0, 0))
    c.key(1.4, la=(-76, -60, 0), la_pos=(0, 0, -1.5))
    c.key(3.6, la=(-74, -62, 0), la_pos=(0, 0, -1.5))
    c.key(2.2, **hd(-20, 14, 10))
    c.key(3.4, **hd(-22, 16, 11))
    c.key(4.0, ra=(-60, 4, 0), **hd(-6, 6, 2), root_pos=(0, 0, 0))
    c.key(4.3, **hd(8, 4, 0))
    c.key(4.7, la=(-8, -4, 0), la_pos=(0, 0, 0), **hd(-2, 0, 0))
    c.key(5.4)
    ripple(c, 0.4, 3.4, 1.5, 6, roll=3)
    flutter(c, 3.4, 4.4, 5, 9, decay=0.5)


@clip("mender_hands", "Lays on healing hands", length=6.4, weight=4, require=["adult", "job:mender"])
def _(c):
    # bowed over someone hurt, both hands circling slowly just above the wound
    c.key(0.5, **bow(4), **hd(6, 0, 0, w=4), ra=(-25, -6, 0), la=(-25, -6, 0), root_pos=(0, 0, 0))
    c.key(1.4, **bow(16), **hd(14, 0, 0, w=16), ra=(-50, -14, 0), la=(-50, -14, 0), root_pos=(0, 1.0, 0))
    osc(c, "ra", 1.4, 4.8, 1.7, 7, axis=0, base=[(1.4, (-50, -14, 0)), (4.8, (-50, -14, 0))])
    osc(c, "la", 1.4, 4.8, 1.7, 7, axis=0, phase=0.5, base=[(1.4, (-50, -14, 0)), (4.8, (-50, -14, 0))])
    c.key(2.4, **hd(18, -6, -4, w=16))
    c.key(3.6, **hd(17, 6, 4, w=16))
    c.key(4.8, **bow(15), **hd(14, 0, 0, w=15), root_pos=(0, 1.0, 0))
    c.key(5.6, **bow(-2), **hd(-4, 0, 0, w=-2), ra=(-6, -2, 0), la=(-6, -2, 0), root_pos=(0, -0.3, 0))
    c.key(6.4)
    # tentacles keep hanging under the bow, curled a touch forward, rippling slowly
    for i in range(6):
        ch = f"tentacle_{i}"
        base = [(0.5, (-4, 0, 0)), (1.4, (-22, 0, 0)), (4.8, (-21, 0, 0)), (5.6, (2, 0, 0)), (6.2, (0, 0, 0))]
        osc(c, ch, 0.5, 6.2, 2.2, 4, axis=0, phase=-0.1 * i, base=base)


@clip("mender_tear", "Lets a tear fall on a wound", length=5.4, weight=3, require=["adult", "job:mender"])
def _(c):
    # a tear closes a wound: wiped from the eye, held out over the hurt, let fall; a watchful pause; a gentle pat
    c.key(0.4, ra=(-30, -10, 0), ra_pos=(0, 0, 0), **bow(3), **hd(4, 0, 0, w=3))
    c.key(1.1, ra=(-118, -16, 0), ra_pos=(1, 0, 0), **bow(8), **hd(6, -3, -6, w=8))
    c.wobble(1.2, 1.8, 2.0, "ra", 4, base=(-118, -16, 0), axis=1)
    c.key(1.85, ra_pos=(1, 0, 0))
    c.key(2.4, ra=(-54, -15, 0), ra_pos=(0, 0, 0), **bow(14), **hd(18, 0, 0, w=14), root_pos=(0, 0.8, 0))
    c.key(2.6, ra=(-50, -15, 0))
    c.key(2.75, ra=(-55, -15, 0))
    c.key(3.3, ra=(-54, -15, 0), **hd(20, 0, 8, w=14))
    c.key(3.6, ra=(-46, -14, 0))
    c.wobble(3.6, 4.3, 3.0, "ra", 5, base=(-46, -14, 0), axis=0)
    c.key(4.4, **bow(12), **hd(10, 0, 3, w=12))
    c.key(5.0, ra=(-5, -2, 0), **bow(-2), **hd(-4, 0, 0, w=-2), root_pos=(0, -0.3, 0))
    c.key(5.4)
    c.key(0.4, root_pos=(0, 0, 0))
    for i in range(6):
        ch = f"tentacle_{i}"
        base = [(0.4, (-3, 0, 0)), (1.1, (-8, 0, 0)), (2.4, (-18, 0, 0)), (4.4, (-15, 0, 0)), (5.0, (2, 0, 0)),
                (5.3, (0, 0, 0))]
        osc(c, ch, 0.4, 5.3, 2.4, 4, axis=0, phase=-0.1 * i, base=base)


@clip("ashsweeper_sweep", "Sweeps the ash", length=6.6, weight=5, require=["adult", "job:ashsweeper"])
def _(c):
    # a broom held low to the right, long even strokes; the tentacles sweep along underneath
    grip_r, grip_l = (-29, 5, 0), (-50, -51, 0)
    c.key(0.5, ra=(-18, 0, 0), la=(-24, -22, 0), la_pos=(0, 0, 0), **hd(8, 0, 0))
    c.key(1.0, ra=grip_r, la=grip_l, la_pos=(0, 0, -1.2), **hd(22, -10, 0))
    osc(c, "ra", 1.0, 5.6, 2.3, 9, axis=1, base=[(1.0, grip_r), (5.6, grip_r)])
    osc(c, "la", 1.0, 5.6, 2.3, -9, axis=1, base=[(1.0, grip_l), (5.6, grip_l)])
    osc(c, "root", 1.0, 5.6, 2.3, -16, axis=1)
    osc(c, "head", 1.0, 5.6, 2.3, -6, axis=1, phase=-0.08, base=[(1.0, (22, -10, 0)), (5.6, (21, -10, 0))])
    osc(c, "root_pos", 1.0, 5.6, 1.15, 0.4, axis=1)
    c.key(5.6, la_pos=(0, 0, -1.2))
    c.key(6.2, ra=(-4, 0, 0), la=(-4, -2, 0), la_pos=(0, 0, 0))
    c.key(6.6)
    c.key(0.5, veil=(0, 0, 0))
    c.key(1.0, veil=(-5, 0, 0))
    c.key(5.6, veil=(-5, 0, 0))
    ripple(c, 0.6, 6.2, 2.3, 6, lag=0.06, roll=-12)


@clip("ashsweeper_tap", "Taps the ash from the broom", length=4.8, weight=3, require=["adult", "job:ashsweeper"])
def _(c):
    # broom upright, tapped twice on the basalt; the dust cloud waved off with a little cough
    grip_r, grip_l = (-54, -28, 0), (-67, -26, 0)
    c.key(0.4, ra=(-30, -10, 0), la=(-36, -10, 0), **hd(6, 0, 0))
    c.key(0.9, ra=grip_r, la=grip_l, **hd(14, 0, 0), root_pos=(0, -0.4, 0))
    c.key(1.15, ra=(-44, -28, 0), la=(-57, -26, 0), root_pos=(0, 0.6, 0))
    c.key(1.35, ra=grip_r, la=grip_l, root_pos=(0, -0.3, 0))
    c.key(1.6, ra=(-44, -28, 0), la=(-57, -26, 0), root_pos=(0, 0.6, 0))
    c.key(1.85, ra=grip_r, la=grip_l, root_pos=(0, 0, 0), **hd(10, 0, 0))
    c.key(2.2, **hd(2, 26, -6), la=(-62, -24, 0), la_pos=(0, 0, 0))
    c.key(2.5, la=(-110, -8, 0), la_pos=(1, 0, 0), **hd(4, 30, -6))
    c.wobble(2.5, 3.3, 3.5, "la", 12, base=(-110, -8, 0), axis=1)
    c.key(3.35, **hd(12, 26, -4))
    c.key(3.5, **hd(4, 28, -5))
    c.key(3.65, **hd(13, 26, -4))
    c.key(3.8, la=(-70, -24, 0), la_pos=(0, 0, 0), **hd(4, 18, -3))
    c.key(4.3, ra=(-6, -2, 0), la=(-6, -2, 0), **hd(0, 0, 0))
    c.key(4.8)
    ripple(c, 0.4, 4.6, 1.6, 5, base=[(0.4, tents()), (1.2, splay(4)), (2.0, tents()), (4.6, tents())])


@clip("drift_watcher_scan", "Watches for drifting kin", length=6.4, weight=4, require=["adult", "job:drift_watcher"])
def _(c):
    # a hand shading the eyes, rising a little on the air, a slow sweep of the sky from one side to the other
    c.key(0.4, ra=(-40, -10, 0), ra_pos=(0, 0, 0), **hd(-4, 0, 0), root_pos=(0, 0, 0))
    c.key(1.1, **BROW, **hd(-20, -30, 0), root_pos=(0, -1.0, 0))
    c.key(1.5, **hd(-21, -34, 2))
    c.key(3.4, ra=(-137, -8, 0), ra_pos=(0, 0, -2), **hd(-24, 30, -2), root_pos=(0, -1.6, 0))
    c.key(4.0, **hd(-22, 36, -3))
    c.key(4.6, ra=(-136, -8, 0), ra_pos=(0, 0, -2), **hd(-18, 22, -1), root_pos=(0, -1.2, 0))
    c.key(5.4, ra=(-8, -2, 0), ra_pos=(0, 0, 0), **hd(-2, 0, 0), root_pos=(0, 0.2, 0))
    c.key(6.4)
    c.key(1.1, root=(0, 0, 0))
    c.key(1.6, root=(0, -12, 0))
    c.key(3.6, root=(0, 14, 0))
    c.key(4.8, root=(0, 8, 0))
    c.key(5.6, root=(0, 0, 0))
    ripple(c, 0.4, 6.2, 2.6, 6, roll=6, base=[(0.4, tents()), (1.6, tents(pitch=8, roll=-4)),
                                              (3.6, tents(pitch=8, roll=5)), (6.2, tents())])


@clip("drift_watcher_count", "Counts the ghasts", length=6.0, weight=3, require=["adult", "job:drift_watcher"])
def _(c):
    # one, two, three on the high air, a small nod for each; the last one earns a sigh and a hand on the heart.
    # The finger goes where the eyes go (left of centre to their right), the shoulder forward so the arm clears the
    # head
    c.key(0.4, ra=(-40, 0, 0), ra_pos=(0, 0, 0), **hd(-6, 0, 0))
    c.key(1.0, ra=(-148, -16, 12), ra_pos=(0.5, 0, -2.5), **hd(-30, -4, 0))
    c.key(1.25, ra=(-145, -16, 12), **hd(-27, -4, 0))
    c.key(1.4, **hd(-31, -4, 0))
    c.key(1.9, ra=(-150, 8, 14), **hd(-30, 10, 0))
    c.key(2.15, ra=(-147, 8, 14), **hd(-26, 10, 0))
    c.key(2.3, **hd(-30, 10, 0))
    c.key(2.8, ra=(-146, 30, 12), ra_pos=(0.5, 0, -2.5), **hd(-27, 24, 0))
    c.key(3.05, ra=(-143, 30, 12), **hd(-23, 24, 0))
    c.key(3.2, **hd(-27, 24, 0))
    c.key(3.8, ra=(-74, -64, 0), ra_pos=(0, 0, -1.5), **hd(10, 4, 5), root_pos=(0, 1.0, 0))
    c.key(4.8, ra=(-72, -66, 0), ra_pos=(0, 0, -1.5), **hd(14, 0, 6), root_pos=(0, 1.2, 0))
    c.key(5.5, ra=(-6, -4, 0), ra_pos=(0, 0, 0), **hd(-2, 0, 0), root_pos=(0, -0.2, 0))
    c.key(6.0)
    c.key(0.4, root_pos=(0, 0, 0))
    c.key(1.0, root_pos=(0, -0.8, 0))
    c.key(3.2, root_pos=(0, -0.8, 0))
    ripple(c, 0.4, 5.8, 2.4, 5, base=[(0.4, tents()), (1.2, tents(pitch=5)), (3.4, tents(pitch=4)),
                                      (4.2, splay(-7)), (5.0, splay(-6)), (5.8, tents())])
