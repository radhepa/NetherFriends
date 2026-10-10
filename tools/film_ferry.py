"""Put the ferry game test's frames together into a film (it isn't committed: it shows Minecraft's own textures).

  gradlew runClientGameTest -PnetherTests=Ferry -PnetherFilm -PtestHeap=3g
  python tools/film_ferry.py [out.mp4]

The test films the game slowed down and notes the game tick of every frame
(build/run/clientGameTest/ferry-frames/frames.txt); each frame is shown for as long as the game time it covers, so
the film runs at the game's real pace. It opens on the conversation window where the crossing was chosen.
Needs ffmpeg on the PATH.
"""
import os
import shutil
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RUN = os.path.join(ROOT, "build", "run", "clientGameTest")
FRAMES = os.path.join(RUN, "ferry-frames")
TICK = 0.05
OPENING = 3.5          # seconds on the conversation window


def main(argv):
    out = argv[0] if argv else os.path.join(ROOT, "build", "ferry", "nether-friends-ferry.mp4")
    os.makedirs(os.path.dirname(os.path.abspath(out)), exist_ok=True)
    rows = [line.split() for line in open(os.path.join(FRAMES, "frames.txt"), encoding="utf-8") if line.strip()]
    work = os.path.join(os.path.dirname(os.path.abspath(out)), "work")
    shutil.rmtree(work, ignore_errors=True)
    os.makedirs(work)
    entries = []
    offer = [f for f in os.listdir(os.path.join(RUN, "screenshots")) if f.endswith("nether-ferry-1-offer.png")]
    if offer:
        from PIL import Image
        Image.open(os.path.join(RUN, "screenshots", offer[0])).convert("RGB").resize((960, 540), Image.LANCZOS).save(
            os.path.join(work, "offer.png"))
        entries.append(("offer.png", OPENING))
    for i, (frame, stage, tick) in enumerate(rows):
        nxt = int(rows[i + 1][2]) if i + 1 < len(rows) else int(tick) + 2
        seconds = max(1, nxt - int(tick)) * TICK
        name = f"frame-{int(frame):04d}.png"
        shutil.copy(os.path.join(FRAMES, name), os.path.join(work, name))
        entries.append((name, seconds))
    with open(os.path.join(work, "list.txt"), "w", encoding="utf-8") as f:
        for name, seconds in entries:
            f.write(f"file '{name}'\nduration {seconds:.3f}\n")
        f.write(f"file '{entries[-1][0]}'\n")
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-f", "concat", "-safe", "0", "-i", "list.txt",
                    "-vf", "minterpolate=fps=30:mi_mode=blend,format=yuv420p", "-c:v", "libx264", "-crf", "20",
                    "-movflags", "+faststart", os.path.abspath(out)], cwd=work, check=True)
    total = sum(s for _, s in entries)
    print(f"{out}: {len(entries)} frames, {total:.1f} s")


if __name__ == "__main__":
    main(sys.argv[1:])
