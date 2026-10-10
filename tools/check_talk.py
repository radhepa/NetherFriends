"""Checks the dialogue files in src/main/resources/data/netherfriends/talk against docs/DIALOGUE.md.

    python tools/check_talk.py              every people
    python tools/check_talk.py weft         one file

Exits non-zero and lists every problem if anything is wrong; otherwise prints a count of lines per file.
"""
import glob
import itertools
import json
import os
import re
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TALK = os.path.join(ROOT, "src", "main", "resources", "data", "netherfriends", "talk")
PEOPLES = ["piglin", "withren", "magmari", "weft", "ghastkin"]
JOBS = {
    "piglin": ["Bartermaster", "Hunter", "Forager", "Smelter", "Goldsmith", "Hoglin-herder", "Brute", "Bastion-dweller", "Wanderer"],
    "withren": ["Remembrancer", "Ashwarden", "Lantern-keeper", "Wart-grower", "Bonecarver", "Brewer"],
    "magmari": ["Ferrier", "Hearthkeeper", "Lava-angler", "Chainwright", "Quartzcutter", "Chartmaker"],
    "weft": ["Clothkeeper", "Weaver", "Spinner", "Canopy-tender", "Dyer", "Netcaster"],
    "ghastkin": ["Tearkeeper", "Songwarden", "Mender", "Ashsweeper", "Drift-watcher"],
}
COMMON_PLACEHOLDERS = {"name", "epithet", "job", "player", "village", "neighbour"}
OWN_PLACEHOLDERS = {"piglin": {"clan"}, "withren": {"kept"}, "magmari": {"chain"}, "weft": {"row"}, "ghastkin": {"former"}}
WHEN_KEYS = {"level", "child", "gender", "job", "cold", "first", "carrying", "holding", "place", "state"}
MAX_TEXT, MAX_ASK = 200, 30
MINIMUMS = {"greet": 14, "chatter": 16, "farewell": 5, "refuse": 3}


def vanilla_ids():
    pattern = os.path.expanduser("~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.3/*.jar")
    jars = glob.glob(pattern)
    if not jars:
        return None, None
    items, tags = set(), set()
    with zipfile.ZipFile(jars[0]) as z:
        for n in z.namelist():
            m = re.fullmatch(r"assets/minecraft/items/(.+)\.json", n)
            if m:
                items.add("minecraft:" + m.group(1))
            m = re.fullmatch(r"data/minecraft/tags/item/(.+)\.json", n)
            if m:
                tags.add("minecraft:" + m.group(1))
    return items, tags


ITEMS, TAGS = vanilla_ids()


class Checker:
    def __init__(self, people, data):
        self.people, self.data, self.problems = people, data, []
        self.allowed = COMMON_PLACEHOLDERS | OWN_PLACEHOLDERS[people]

    def bad(self, where, msg):
        self.problems.append(f"{self.people}: {where}: {msg}")

    def item(self, where, ident):
        if not isinstance(ident, str) or ":" not in ident:
            self.bad(where, f"not an item id: {ident!r}")
            return
        if ITEMS is None or not ident.startswith(("minecraft:", "#minecraft:")):
            return
        if ident.startswith("#"):
            if ident[1:] not in TAGS:
                self.bad(where, f"no such item tag {ident}")
        elif ident not in ITEMS:
            self.bad(where, f"no such item {ident}")

    def text(self, where, text, limit=MAX_TEXT):
        if not isinstance(text, str) or not text.strip():
            self.bad(where, "empty text")
            return
        if len(text) > limit:
            self.bad(where, f"{len(text)} characters (max {limit}): {text[:60]}...")
        for ph in re.findall(r"\{([^}]*)\}", text):
            if ph not in self.allowed:
                self.bad(where, f"unknown placeholder {{{ph}}}")
        if "—" in text:
            self.bad(where, "em dash (use a comma, colon or full stop)")

    def when(self, where, when):
        if when is None:
            return
        if not isinstance(when, dict):
            self.bad(where, "when must be an object")
            return
        for k, v in when.items():
            if k not in WHEN_KEYS:
                self.bad(where, f"unknown condition {k}")
            elif k == "level":
                if not (isinstance(v, list) and len(v) == 2 and all(isinstance(n, int) and 0 <= n <= 5 for n in v) and v[0] <= v[1]):
                    self.bad(where, f"level must be [min, max] within 0-5: {v}")
            elif k in ("child", "cold", "first"):
                if not isinstance(v, bool):
                    self.bad(where, f"{k} must be true or false")
                if k == "cold" and self.people != "magmari":
                    self.bad(where, "cold is for Magmari only")
            elif k == "gender":
                if v not in ("male", "female"):
                    self.bad(where, f"gender must be male or female: {v}")
                if self.people == "withren":
                    self.bad(where, "Withren have no gender")
            elif k == "job":
                for j in (v if isinstance(v, list) else [v]):
                    if j not in JOBS[self.people] + ["Child"]:
                        self.bad(where, f"unknown job {j}")
            elif k in ("carrying", "holding"):
                self.item(where, v)
            elif k == "place":
                if v not in ("village", "bastion", "wild"):
                    self.bad(where, f"place must be village, bastion or wild: {v}")
            elif k == "state":
                if v not in ("grudge", "angry"):
                    self.bad(where, f"state must be grudge or angry: {v}")

    def line(self, where, line, replies=False, reward=False):
        if isinstance(line, str):
            self.text(where, line)
            return {"text": line}
        if not isinstance(line, dict) or "text" not in line:
            self.bad(where, "a line is a string or an object with text")
            return None
        for k in line:
            if k not in ("text", "when", "replies", "reward"):
                self.bad(where, f"unknown key {k}")
        self.text(where, line["text"])
        self.when(where, line.get("when"))
        if "replies" in line:
            if not replies:
                self.bad(where, "replies only belong in topics and chatter")
            rs = line["replies"]
            if not isinstance(rs, list) or not 1 <= len(rs) <= 4:
                self.bad(where, "replies: 1 to 4")
            else:
                for i, r in enumerate(rs):
                    w = f"{where} reply {i + 1}"
                    if not isinstance(r, dict) or set(r) - {"ask", "text", "points"} or "ask" not in r or "text" not in r:
                        self.bad(w, "a reply is {ask, text, points}")
                        continue
                    self.text(w, r["ask"], MAX_ASK)
                    self.text(w, r["text"])
                    p = r.get("points", 0)
                    if not isinstance(p, int) or not -2 <= p <= 2:
                        self.bad(w, f"points must be -2 to 2: {p}")
        if "reward" in line:
            if not reward:
                self.bad(where, "reward only belongs in levelup")
            r = line["reward"]
            if not isinstance(r, dict) or "item" not in r:
                self.bad(where, "reward is {item, count}")
            else:
                self.item(where, r["item"])
                c = r.get("count", 1)
                if not isinstance(c, int) or not 1 <= c <= 64:
                    self.bad(where, f"reward count 1-64: {c}")
        return line

    def lines(self, key, items, **kw):
        if not isinstance(items, list) or not items:
            self.bad(key, "needs a list of lines")
            return []
        checked = (self.line(f"{key}[{i + 1}]", item, **kw) for i, item in enumerate(items))
        return [line for line in checked if line is not None]

    def run(self):
        d = self.data
        for k in d:
            if k not in ("levels", "greet", "chatter", "topics", "gift", "levelup", "farewell", "refuse"):
                self.bad("file", f"unknown key {k}")
        levels = d.get("levels")
        if not (isinstance(levels, list) and len(levels) == 6 and all(isinstance(s, str) and 0 < len(s) <= 24 for s in levels)):
            self.bad("levels", "six names, each at most 24 characters")
        pools = {}
        for key in ("greet", "chatter", "farewell", "refuse"):
            pools[key] = self.lines(key, d.get(key), replies=(key == "chatter"))
            if len(pools[key]) < MINIMUMS[key]:
                self.bad(key, f"{len(pools[key])} lines, want at least {MINIMUMS[key]}")
        # topics
        topics = d.get("topics")
        ids = set()
        if not isinstance(topics, list) or len(topics) < 12:
            self.bad("topics", "want at least 12 topics")
            topics = topics if isinstance(topics, list) else []
        for i, t in enumerate(topics):
            w = f"topic {t.get('id', i + 1) if isinstance(t, dict) else i + 1}"
            if not isinstance(t, dict):
                self.bad(w, "a topic is an object")
                continue
            for k in t:
                if k not in ("id", "ask", "when", "lines"):
                    self.bad(w, f"unknown key {k}")
            if not re.fullmatch(r"[a-z][a-z0-9_]*", str(t.get("id", ""))):
                self.bad(w, "id must be snake_case")
            if t.get("id") in ids:
                self.bad(w, "duplicate id")
            ids.add(t.get("id"))
            self.text(w + " ask", t.get("ask", ""), MAX_ASK)
            self.when(w, t.get("when"))
            ls = self.lines(w, t.get("lines"), replies=True)
            if len(ls) < 3:
                self.bad(w, f"{len(ls)} lines, want at least 3")
            t["_lines"] = ls
        # gifts
        gift = d.get("gift", {})
        for cat in ("loved", "liked", "disliked"):
            lst = gift.get(cat)
            if not isinstance(lst, list) or not lst:
                self.bad(f"gift.{cat}", "needs a list of items")
            else:
                for ident in lst:
                    self.item(f"gift.{cat}", ident)
        glines = gift.get("lines", {})
        for cat in ("loved", "liked", "neutral", "disliked", "again", "empty"):
            pools["gift." + cat] = self.lines(f"gift.lines.{cat}", glines.get(cat))
            if len(pools["gift." + cat]) < 3:
                self.bad(f"gift.lines.{cat}", "want at least 3 lines")
        # level ups
        ups = self.lines("levelup", d.get("levelup"), reward=True)
        for n in range(1, 6):
            if not any((l.get("when") or {}).get("level") == [n, n] for l in ups):
                self.bad("levelup", f"no line for reaching level {n}")
        if self.problems:
            return self.problems
        self.coverage(pools, topics)
        return self.problems

    # -- every kind of resident must have something to say ----------------------------------------------

    def matches(self, line, ctx):
        w = line.get("when") or {}
        for k, v in w.items():
            if k == "level" and not v[0] <= ctx["level"] <= v[1]:
                return False
            if k == "child" and v != ctx["child"]:
                return False
            if k == "gender" and v != ctx["gender"]:
                return False
            if k == "job" and ctx["job"] not in (v if isinstance(v, list) else [v]):
                return False
            if k == "cold" and v != ctx["cold"]:
                return False
            if k == "first" and v != ctx["first"]:
                return False
            if k in ("carrying", "holding"):
                return False
            if k == "place" and v != ctx["place"]:
                return False
            if k == "state" and v != ctx["state"]:
                return False
        return True

    def coverage(self, pools, topics):
        genders = [None] if self.people == "withren" else ["male", "female"]
        places = ["village", "bastion", "wild"] if self.people == "piglin" else ["village", "wild"]
        jobs = JOBS[self.people]
        missing = {}
        for child, level, gender, place, cold in itertools.product([False, True], range(6), genders, places, [False, True]):
            if cold and self.people != "magmari":
                continue
            for job in (["Child"] if child else jobs):
                if self.people == "piglin" and job == "Bastion-dweller" and place != "bastion":
                    continue
                ctx = dict(child=child, level=level, gender=gender, job=job, cold=cold, first=False, place=place, state=None)
                for key in ("greet", "chatter", "farewell") + tuple(k for k in pools if k.startswith("gift.")):
                    if not any(self.matches(l, ctx) for l in pools[key]):
                        missing.setdefault(key, set()).add(self.describe(ctx))
                firstctx = dict(ctx, first=True)
                if not any(self.matches(l, firstctx) for l in pools["greet"]):
                    missing.setdefault("greet (first meeting)", set()).add(self.describe(ctx))
                for state in (["grudge", "angry"] if self.people == "weft" else ["angry"]):
                    if not any(self.matches(l, dict(ctx, state=state)) for l in pools["refuse"]):
                        missing.setdefault(f"refuse ({state})", set()).add(self.describe(ctx))
                open_topics = [t for t in topics if self.matches(t, ctx) and any(self.matches(l, ctx) for l in t["_lines"])]
                if len(open_topics) < 3:
                    missing.setdefault("topics (fewer than 3 to ask)", set()).add(self.describe(ctx))
        for key, who in missing.items():
            sample = ", ".join(sorted(who)[:4])
            self.bad(key, f"nothing to say for {len(who)} kinds of resident, e.g. {sample}")

    @staticmethod
    def describe(ctx):
        bits = [ctx["job"], f"level {ctx['level']}"]
        if ctx["gender"]:
            bits.append(ctx["gender"])
        if ctx["cold"]:
            bits.append("cold")
        if ctx["place"] != "village":
            bits.append(ctx["place"])
        return " ".join(bits)


def count(data):
    n = 0
    for key in ("greet", "chatter", "farewell", "refuse", "levelup"):
        n += len(data.get(key, []))
    for t in data.get("topics", []):
        for l in t.get("lines", []):
            n += 1 + (len(l.get("replies", [])) if isinstance(l, dict) else 0)
    for c in data.get("chatter", []):
        if isinstance(c, dict):
            n += len(c.get("replies", []))
    n += sum(len(v) for v in data.get("gift", {}).get("lines", {}).values())
    return n


def main(argv):
    peoples = argv or PEOPLES
    problems = []
    if ITEMS is None:
        print("note: Minecraft jar not found in the Gradle cache, item ids not checked")
    for people in peoples:
        path = os.path.join(TALK, people + ".json")
        if not os.path.exists(path):
            problems.append(f"{people}: missing {path}")
            continue
        try:
            with open(path, encoding="utf-8") as f:
                data = json.load(f)
        except json.JSONDecodeError as e:
            problems.append(f"{people}: not valid JSON: {e}")
            continue
        found = Checker(people, data).run()
        problems += found
        if not found:
            print(f"{people}: ok, {count(data)} lines, {len(data['topics'])} topics")
    if problems:
        print("\n".join(problems))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
