"""New dialogue arrives as fragment files, merged into the dialogue files by this script.

    python tools/talk_more.py check tools/talk_more/weft/greet_01.json     check one fragment (merged in memory)
    python tools/talk_more.py status                                       new lines per people, every fragment
    python tools/talk_more.py merge                                        write every fragment into talk/*.json

A fragment lives in tools/talk_more/<people>/<anything>.json and holds any of the dialogue file's keys
(docs/DIALOGUE.md):

    {
      "greet": [LINE, ...], "chatter": [LINE, ...], "farewell": [LINE, ...], "refuse": [LINE, ...],
      "levelup": [LINE, ...],
      "gift": {"lines": {"loved": [LINE, ...], "liked": [...], "neutral": [...], "disliked": [...], "again": [...], "empty": [...]}},
      "topics": [
        {"id": "an_existing_topic", "lines": [LINE, ...]},                         more lines for a topic that exists
        {"id": "a_new_topic", "ask": "What you say", "when": {...}, "lines": [...]} a new topic
      ]
    }

Lists are appended. A topic whose id exists gets the fragment's lines (leave out "ask" and "when"); any other id
is a new topic. Lines whose text is already in the file are skipped, so merging twice changes nothing.
Every check runs the full tools/check_talk.py rules on the merged result.
"""
import copy
import glob
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import check_talk  # noqa: E402

MORE = os.path.join(HERE, "talk_more")
POOLS = ("greet", "chatter", "farewell", "refuse", "levelup")
GIFT_LINES = ("loved", "liked", "neutral", "disliked", "again", "empty")


def text_of(line):
    return line if isinstance(line, str) else (line.get("text") if isinstance(line, dict) else None)


def texts(data):
    out = set()
    for key in POOLS:
        out.update(text_of(l) for l in data.get(key, []))
    for t in data.get("topics", []):
        out.update(text_of(l) for l in t.get("lines", []))
    for v in data.get("gift", {}).get("lines", {}).values():
        out.update(text_of(l) for l in v)
    return out


def merge(data, frag, where, problems):
    """Adds a fragment to a dialogue file in place. Returns (new lines counted like check_talk, skipped duplicates)."""
    before = check_talk.count(data)
    seen = texts(data)
    skipped = 0

    def add(target, lines, key):
        nonlocal skipped
        if not isinstance(lines, list):
            problems.append(f"{where}: {key} must be a list of lines")
            return
        for line in lines:
            t = text_of(line)
            if t is None:
                problems.append(f"{where}: {key}: a line is a string or an object with text")
                continue
            if t in seen:
                skipped += 1
                continue
            seen.add(t)
            target.append(copy.deepcopy(line))

    for key in frag:
        if key not in POOLS + ("gift", "topics"):
            problems.append(f"{where}: unknown key {key}")
    for key in POOLS:
        if key in frag:
            add(data.setdefault(key, []), frag[key], key)
    gift = frag.get("gift", {})
    for k in gift:
        if k != "lines":
            problems.append(f"{where}: gift: only \"lines\" may be added (the item lists stay as they are)")
    for cat, lines in gift.get("lines", {}).items():
        if cat not in GIFT_LINES:
            problems.append(f"{where}: gift.lines: unknown kind {cat}")
            continue
        add(data["gift"]["lines"].setdefault(cat, []), lines, f"gift.lines.{cat}")
    topics = {t["id"]: t for t in data.get("topics", [])}
    for t in frag.get("topics", []):
        if not isinstance(t, dict) or "id" not in t:
            problems.append(f"{where}: a topic needs an id")
            continue
        old = topics.get(t["id"])
        if old is not None:
            for k in ("ask", "when"):
                if k in t and t[k] != old.get(k):
                    problems.append(f"{where}: topic {t['id']} exists: leave out its {k} (or copy it exactly)")
            add(old["lines"], t.get("lines", []), f"topic {t['id']}")
        else:
            new = {k: copy.deepcopy(v) for k, v in t.items() if k != "lines"}
            new["lines"] = []
            add(new["lines"], t.get("lines", []), f"topic {t['id']}")
            data["topics"].append(new)
            topics[t["id"]] = new
    return check_talk.count(data) - before, skipped


def load(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def base(people):
    return load(os.path.join(check_talk.TALK, people + ".json"))


def fragments(people):
    return sorted(glob.glob(os.path.join(MORE, people, "*.json")))


def people_of(path):
    people = os.path.basename(os.path.dirname(os.path.abspath(path)))
    if people not in check_talk.PEOPLES:
        sys.exit(f"{path}: a fragment lives in tools/talk_more/<people>/ (one of {', '.join(check_talk.PEOPLES)})")
    return people


def cmd_check(paths):
    bad = 0
    for path in paths:
        people = people_of(path)
        data, problems = base(people), []
        try:
            frag = load(path)
        except json.JSONDecodeError as e:
            print(f"{path}: not valid JSON: {e}")
            bad += 1
            continue
        added, skipped = merge(data, frag, os.path.basename(path), problems)
        problems += check_talk.Checker(people, data).run()
        # only report rule breaks that this fragment could have caused
        print(f"{os.path.relpath(path)}: +{added} lines" + (f", {skipped} duplicates skipped" if skipped else "")
              + (", ok" if not problems else f", {len(problems)} problems"))
        for p in problems[:60]:
            print("   ", p)
        bad += len(problems)
    return 1 if bad else 0


def cmd_status(write=False):
    bad, total = 0, 0
    for people in check_talk.PEOPLES:
        data, problems = base(people), []
        start = check_talk.count(data)
        rows = []
        for path in fragments(people):
            try:
                added, skipped = merge(data, load(path), os.path.basename(path), problems)
            except json.JSONDecodeError as e:
                problems.append(f"{os.path.basename(path)}: not valid JSON: {e}")
                continue
            rows.append(f"    {os.path.basename(path)}: +{added}" + (f" ({skipped} duplicates skipped)" if skipped else ""))
        problems += check_talk.Checker(people, data).run()
        added = check_talk.count(data) - start
        total += added
        print(f"{people}: {start} -> {check_talk.count(data)} lines (+{added}), {len(data['topics'])} topics"
              + (f", {len(problems)} problems" if problems else ""))
        print("\n".join(rows))
        for p in problems[:40]:
            print("   ", p)
        bad += len(problems)
        if write and not problems:
            for t in data["topics"]:
                t.pop("_lines", None)   # check_talk's scratch key
            text = dump(data)
            assert json.loads(text) == data
            with open(os.path.join(check_talk.TALK, people + ".json"), "w", encoding="utf-8") as f:
                f.write(text)
    print(f"all: +{total} new lines")
    if write and bad:
        print("nothing written for peoples with problems")
    return 1 if bad else 0


def dump(data):
    """The dialogue files' own layout: one line of dialogue per line of the file."""
    def one(v):
        return json.dumps(v, ensure_ascii=False)

    def block(lines, pad):
        return [pad + one(l) + ("," if i < len(lines) - 1 else "") for i, l in enumerate(lines)]

    out = ["{"]
    keys = list(data)
    for i, key in enumerate(keys):
        comma = "," if i < len(keys) - 1 else ""
        v = data[key]
        if key in POOLS:
            out += [f'  "{key}": ['] + block(v, "    ") + ["  ]" + comma]
        elif key == "topics":
            out.append('  "topics": [')
            for j, t in enumerate(v):
                head = {k: t[k] for k in t if k != "lines"}
                out.append("    " + one(head)[:-1] + ', "lines": [')
                out += block(t["lines"], "      ")
                out.append("    ]}" + ("," if j < len(v) - 1 else ""))
            out.append("  ]" + comma)
        elif key == "gift":
            out.append('  "gift": {')
            gk = list(v)
            for j, g in enumerate(gk):
                c = "," if j < len(gk) - 1 else ""
                if g == "lines":
                    out.append('    "lines": {')
                    cats = list(v["lines"])
                    for m, cat in enumerate(cats):
                        out += [f'      "{cat}": ['] + block(v["lines"][cat], "        ") + \
                               ["      ]" + ("," if m < len(cats) - 1 else "")]
                    out.append("    }" + c)
                else:
                    out.append(f'    "{g}": ' + one(v[g]) + c)
            out.append("  }" + comma)
        else:
            out.append(f'  "{key}": ' + one(v) + comma)
    out.append("}")
    return "\n".join(out) + "\n"


def main(argv):
    if not argv or argv[0] not in ("check", "status", "merge"):
        print(__doc__)
        return 2
    if argv[0] == "check":
        return cmd_check(argv[1:])
    return cmd_status(write=argv[0] == "merge")


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
