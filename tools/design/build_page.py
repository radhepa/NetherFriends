"""Inline the people models (geometry + textures) into the design page.

  python tools/design/build_page.py <out.html>

Rebuild the models first (tools/people/*.py); the page reads the generated files. Vanilla textures (the Piglins,
the Withren's Wither head) are read from the local Minecraft jar and only ever end up in the built page.
"""
import base64
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.dirname(HERE))
from mobkit import GEOMETRY_DIR, REFERENCE_DIR, TEXTURE_DIR, vanilla_png  # noqa: E402

# page id -> (geometry id, texture id); texture variants share their mob's geometry
PEOPLE = {
    "piglin": ("piglin", None),
    "piglin_brute": ("piglin_brute", None),
    "piglin_baby": ("piglin_baby", None),
    "withren": ("withren", "withren"),
    "withren_remembrancer": ("withren_remembrancer", "withren_remembrancer"),
    "withren_ashwarden": ("withren_ashwarden", "withren_ashwarden"),
    "withren_child": ("withren_child", "withren"),          # classic baby: shares the adult texture
    "magmari": ("magmari", "magmari"),
    "magmari_cold": ("magmari", "magmari_cold"),
    "magmari_ferrier": ("magmari_ferrier", "magmari_ferrier"),
    "magmari_child": ("magmari_child", "magmari_child"),
    "magmari_child_cold": ("magmari_child", "magmari_child_cold"),
    "magmari_female": ("magmari_female", "magmari_female"),
    "magmari_female_cold": ("magmari_female", "magmari_female_cold"),
    "weft": ("weft", "weft"),
    "weft_female": ("weft_female", "weft_female"),
    "weft_child": ("weft_child", "weft_child"),
    "ghastkin": ("ghastkin", "ghastkin"),
    "ghastkin_female": ("ghastkin_female", "ghastkin_female"),
    "ghastkin_child": ("ghastkin_child", "ghastkin_child"),
}


# in-game screenshots (tools/design/shots/<name>.jpg, made by pick_shots.py; not committed) with captions
SHOTS_DIR = os.path.join(HERE, "shots")
CAPTIONS_FILE = os.path.join(SHOTS_DIR, "captions.json")


def shots_html(talk=False):
    """The in-game photos: village ones, or (talk=True) the conversation window's, whose names start with talk-."""
    if not os.path.exists(CAPTIONS_FILE):
        return ""
    with open(CAPTIONS_FILE, encoding="utf-8") as f:
        captions = json.load(f)
    out = []
    for name, (title, text) in captions.items():
        if name.startswith("talk-") != talk:
            continue
        data = base64.b64encode(read(os.path.join(SHOTS_DIR, name + ".jpg"))).decode()
        out.append(f'<figure><img src="data:image/jpeg;base64,{data}" alt="{title}: {text}" loading="lazy">'
                   f'<figcaption><b>{title}</b> · {text}</figcaption></figure>')
    return "".join(out)


def uri(data):
    return "data:image/png;base64," + base64.b64encode(data).decode()


def read(path):
    with open(path, "rb") as f:
        return f.read()


def main():
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(HERE, "out", "people.html")
    data, vanilla = {}, {}
    for pid, (geo_id, tex_id) in PEOPLE.items():
        path = os.path.join(GEOMETRY_DIR, geo_id + ".json")
        if not os.path.exists(path):
            path = os.path.join(REFERENCE_DIR, geo_id + ".json")
        with open(path, encoding="utf-8") as f:
            geo = json.load(f)
        borrowed = {c["texture"] for p in geo["parts"] for c in p["cubes"] if c.get("texture")}
        for b in borrowed | ({geo["vanilla_texture"]} if geo.get("vanilla_texture") else set()):
            vanilla.setdefault(b, uri(vanilla_png(b)))
        if geo.get("vanilla_texture"):
            data[pid] = {"geo": geo, "tex": vanilla[geo["vanilla_texture"]], "glow": None}
            continue
        glow = os.path.join(TEXTURE_DIR, tex_id + "_glow.png")
        data[pid] = {"geo": geo, "tex": uri(read(os.path.join(TEXTURE_DIR, tex_id + ".png"))),
                     "glow": uri(read(glow)) if os.path.exists(glow) else None}
    data["vanilla"] = vanilla
    with open(os.path.join(HERE, "people_page.html"), encoding="utf-8") as f:
        page = f.read()
    page = page.replace("/*__DATA__*/null", json.dumps(data, separators=(",", ":")))
    page = page.replace("<!--__SHOTS__-->", shots_html())
    page = page.replace("<!--__TALKSHOTS__-->", shots_html(talk=True))
    os.makedirs(os.path.dirname(os.path.abspath(out)), exist_ok=True)
    with open(out, "w", encoding="utf-8") as f:
        f.write(page)
    print(out, os.path.getsize(out) // 1024, "KB")


if __name__ == "__main__":
    main()
