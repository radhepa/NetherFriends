"""Piglins stay exactly as they are in Minecraft. The mod changes their code, not their look, so these are
vanilla reference models for the design page: the 26.3 geometry (AdultPiglinModel, BabyPiglinModel) drawn with
the game's own textures, read from the local client jar. Nothing here is painted or shipped.

  piglin        AdultPiglinModel + piglin.png
  piglin_brute  AdultPiglinModel + piglin_brute.png
  piglin_baby   BabyPiglinModel + piglin_baby.png

  python tools/people/piglin.py
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from mobkit import Model  # noqa: E402

NAME = "Piglin"


def adult(mob_id, texture):
    """AdultPiglinModel: PlayerModel limbs (with sleeve/pants overlays), a plain body, the piglin head."""
    m = Model(mob_id, 64, 64, vanilla_texture=texture)
    m.preview = {"right_arm": [-0.1, 0, 0.06], "left_arm": [0.1, 0, -0.06]}
    head = m.part("head")
    head.cube((0, 0), (-5, -8, -4), (10, 8, 8))
    head.cube((31, 1), (-2, -4, -5), (4, 4, 1))                       # snout
    head.cube((2, 4), (2, -2, -5), (1, 2, 1))                         # tusks
    head.cube((2, 0), (-3, -2, -5), (1, 2, 1))
    head.part("left_ear", (4.5, -6, 0), (0, 0, -math.pi / 6)).cube((51, 6), (0, 0, -2), (1, 5, 4))
    head.part("right_ear", (-4.5, -6, 0), (0, 0, math.pi / 6)).cube((39, 6), (-1, 0, -2), (1, 5, 4))
    m.part("body").cube((16, 16), (-4, 0, -2), (8, 12, 4))
    ra = m.part("right_arm", (-5, 2, 0))
    ra.cube((40, 16), (-3, -2, -2), (4, 12, 4)).cube((40, 32), (-3, -2, -2), (4, 12, 4), inflate=0.25)
    la = m.part("left_arm", (5, 2, 0))
    la.cube((32, 48), (-1, -2, -2), (4, 12, 4)).cube((48, 48), (-1, -2, -2), (4, 12, 4), inflate=0.25)
    rl = m.part("right_leg", (-1.9, 12, 0))
    rl.cube((0, 16), (-2, 0, -2), (4, 12, 4)).cube((0, 32), (-2, 0, -2), (4, 12, 4), inflate=0.25)
    ll = m.part("left_leg", (1.9, 12, 0))
    ll.cube((16, 48), (-2, 0, -2), (4, 12, 4)).cube((0, 48), (-2, 0, -2), (4, 12, 4), inflate=0.25)
    m.save()


def baby():
    """BabyPiglinModel (26.3 has its own baby mesh and texture)."""
    m = Model("piglin_baby", 64, 64, vanilla_texture="entity/piglin/piglin_baby")
    m.part("body", (0, 18, -0.5)).cube((0, 13), (-3, -3, -1), (6, 5, 3))
    head = m.part("head", (0, 15, 0))
    head.cube((21, 30), (-1.5, -3, -4.5), (3, 3, 1)).cube((0, 0), (-4.5, -6, -3.5), (9, 6, 7))
    head.part("left_ear", (4.2, -4, 0)).part("left_ear_r1", (1, 1.75, 0), (0, 0, -0.6109)).cube((0, 21), (-0.5, -3, -2), (1, 6, 4))
    head.part("right_ear", (-4.2, -4, 0)).part("right_ear_r1", (-1, 1.75, 0), (0, 0, 0.6109)).cube((18, 13), (-0.5, -3, -2), (1, 6, 4))
    m.part("left_arm", (4, 15, 0)).cube((28, 13), (-1, 0, -1.5), (2, 5, 3))
    m.part("right_arm", (-4, 15, 0)).cube((10, 30), (-1, 0, -1.5), (2, 5, 3))
    m.part("right_leg", (-1.5, 20, 0)).cube((22, 23), (-1.5, 0, -1.5), (3, 4, 3))
    m.part("left_leg", (1.5, 20, 0)).cube((10, 23), (-1.5, 0, -1.5), (3, 4, 3))
    m.save()


def build():
    adult("piglin", "entity/piglin/piglin")
    adult("piglin_brute", "entity/piglin/piglin_brute")
    baby()


if __name__ == "__main__":
    build()
