"""Checks the story files in src/main/resources/data/netherfriends/stories against docs/STORIES.md.

    python tools/check_stories.py              every people
    python tools/check_stories.py weft         one file

Exits non-zero and lists every problem if anything is wrong; otherwise prints a count of stories per file.
"""
import json
import os
import re
import sys

from check_talk import ITEMS, JOBS, PEOPLES, ROOT, Checker

STORIES = os.path.join(ROOT, "src", "main", "resources", "data", "netherfriends", "stories")
POOLS = {"early": 4, "child": 4, "waiting": 5, "ready": 4}
LINE_WHEN = {"job", "gender", "place", "level", "cold", "gold"}
STORY_KEYS = {"id", "jobs", "title", "intro", "request", "confide", "endings", "reward", "after"}
MAX_TEXT, MAX_ASK, MAX_MEMORY = 200, 30, 120


class StoryChecker(Checker):
    def pages(self, where, pages, low, high):
        if not isinstance(pages, list) or not low <= len(pages) <= high:
            self.bad(where, f"{low} to {high} pages")
            return
        for i, page in enumerate(pages):
            if not isinstance(page, str):
                self.bad(f"{where}[{i + 1}]", "a page is a string")
            else:
                self.text(f"{where}[{i + 1}]", page)

    def stack(self, where, stack):
        if not isinstance(stack, dict) or "item" not in stack:
            self.bad(where, "needs {item, count}")
            return
        self.item(where, stack["item"])
        if str(stack["item"]).startswith("#"):
            self.bad(where, "an item, not a tag")
        count = stack.get("count", 1)
        if not isinstance(count, int) or not 1 <= count <= 64:
            self.bad(where, f"count 1-64: {count}")

    def run(self):
        d = self.data
        for k in d:
            if k not in set(POOLS) | {"stories"}:
                self.bad("file", f"unknown key {k}")
        for key, least in POOLS.items():
            lines = self.lines(key, d.get(key))
            for line in lines:
                for k in (line.get("when") or {}):
                    if k not in LINE_WHEN:
                        self.bad(key, f"condition {k} doesn't apply here")
            if len(lines) < least:
                self.bad(key, f"{len(lines)} lines, want at least {least}")
            if sum(1 for line in lines if not line.get("when")) < 2:
                self.bad(key, "want at least 2 plain lines (no when) as fallbacks")
        stories = d.get("stories")
        if not isinstance(stories, list) or not stories:
            self.bad("stories", "needs a list of stories")
            return self.problems
        ids, titles, per_job = set(), set(), {job: 0 for job in JOBS[self.people]}
        for i, s in enumerate(stories):
            where = f"story {s.get('id', i + 1) if isinstance(s, dict) else i + 1}"
            if not isinstance(s, dict):
                self.bad(where, "a story is an object")
                continue
            for k in s:
                if k not in STORY_KEYS:
                    self.bad(where, f"unknown key {k}")
            for k in STORY_KEYS - set(s):
                self.bad(where, f"missing {k}")
            sid = s.get("id", "")
            if not re.fullmatch(r"[a-z][a-z0-9_]*", str(sid)):
                self.bad(where, "id must be snake_case")
            if sid in ids:
                self.bad(where, "duplicate id")
            ids.add(sid)
            jobs = s.get("jobs")
            if not isinstance(jobs, list) or not jobs:
                self.bad(where, "jobs: a list of at least one job")
            else:
                for job in jobs:
                    if job not in JOBS[self.people]:
                        self.bad(where, f"unknown job {job}")
                    else:
                        per_job[job] += 1
            title = s.get("title", "")
            if not isinstance(title, str) or not 3 <= len(title) <= 40:
                self.bad(where, "title: 3 to 40 characters")
            elif title in titles:
                self.bad(where, f"duplicate title {title}")
            else:
                self.text(where + " title", title)
            titles.add(title)
            self.pages(where + " intro", s.get("intro"), 2, 3)
            self.pages(where + " confide", s.get("confide"), 2, 3)
            self.pages(where + " after", s.get("after"), 2, 3)
            request = s.get("request")
            self.stack(where + " request", request)
            if isinstance(request, dict):
                for k in request:
                    if k not in ("item", "count", "ask", "thanks"):
                        self.bad(where + " request", f"unknown key {k}")
                self.text(where + " request.ask", request.get("ask", ""))
                self.text(where + " request.thanks", request.get("thanks", ""))
            endings = s.get("endings")
            if not isinstance(endings, list) or len(endings) != 2:
                self.bad(where, "exactly 2 endings")
            else:
                for n, e in enumerate(endings):
                    w = f"{where} ending {n + 1}"
                    if not isinstance(e, dict) or set(e) != {"ask", "text", "memory"}:
                        self.bad(w, "an ending is {ask, text, memory}")
                        continue
                    self.text(w + " ask", e["ask"], MAX_ASK)
                    self.text(w + " text", e["text"])
                    self.text(w + " memory", e["memory"], MAX_MEMORY)
            self.stack(where + " reward", s.get("reward"))
        for job, n in per_job.items():
            if n < 2:
                self.bad("stories", f"{job} has {n} stories, want at least 2")
        return self.problems


def main(argv):
    peoples = argv or PEOPLES
    problems = []
    if ITEMS is None:
        print("note: Minecraft jar not found in the Gradle cache, item ids not checked")
    for people in peoples:
        path = os.path.join(STORIES, people + ".json")
        if not os.path.exists(path):
            problems.append(f"{people}: missing {path}")
            continue
        try:
            with open(path, encoding="utf-8") as f:
                data = json.load(f)
        except json.JSONDecodeError as e:
            problems.append(f"{people}: not valid JSON: {e}")
            continue
        found = StoryChecker(people, data).run()
        problems += found
        if not found:
            print(f"{people}: ok, {len(data['stories'])} stories")
    if problems:
        print("\n".join(problems))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
