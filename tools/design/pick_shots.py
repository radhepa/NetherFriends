"""Copy chosen game-test screenshots into tools/design/shots/ as small JPEGs for the design page.

  python tools/design/pick_shots.py <screenshot.png>=<name> ...

Screenshots come from gradlew runClientGameTest (build/run/clientGameTest/screenshots). The shots folder is not
committed: game screenshots show Minecraft's own textures.
"""
import os
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "shots")
os.makedirs(OUT, exist_ok=True)
for arg in sys.argv[1:]:
    src, name = arg.rsplit("=", 1)
    img = Image.open(src).convert("RGB")
    img.thumbnail((1120, 630), Image.LANCZOS)
    img.save(os.path.join(OUT, name + ".jpg"), quality=82, optimize=True)
    print(name, os.path.getsize(os.path.join(OUT, name + ".jpg")) // 1024, "KB")
