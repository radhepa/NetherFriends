# Stories and the Journal

The conversation window has three tabs on the friendship card: **Talk** (topics and small talk, `DIALOGUE.md`),
**Story** and **Journal**.

## Story

Every grown resident has one story of their own: something on their mind that has to do with their job, a thing
they need, and the personal reason behind it. Each player walks through it with them in four steps:

1. **Hear it.** From friendship level 2, the Story tab tells you what's on their mind (`intro`, one page at a time:
   *Go on*). On the last page you can offer to help (*I'll help*, the story begins) or not (*Not now*).
2. **Bring it.** They ask for something (`request`: an item and a count). Hold that many in your main hand and press
   *Here* on the Story tab. They thank you (`thanks`): +5 friendship.
3. **Hear the rest.** Once you're at level 3 and it's a later day than the one you brought it, they're ready (a
   `ready` line, *I'm listening*) and tell you why it mattered (`confide`, page by page). Until then they say a
   `waiting` line.
4. **Answer them.** The last `confide` page ends on a question or a choice. You answer with one of the two
   `endings`; they reply with its `text`, give you the story's `reward`, and +5 friendship. The story is done; the
   Story tab says an `after` line from then on.

Children have no story yet: the Story tab says a `child` line. Below level 2 a grown resident says an `early` line.

Which story a resident tells is picked from the stories for their job, by who they are (the same resident always
has the same one), and kept once you begin it.

## Journal

Kept per player, per resident, by the game (no file to write). A Journal page appears at once (it's your notes,
not their speech) in a taller box; scroll it with the mouse wheel.

- **Memories:** newest first, each with its day: meeting them, each friendship level, gifts and how they took
  them, each step of their story (an ending's `memory` is its line).
- **About:** their full name, people, job, home, their kin line (clan, the name a Withren keeps, the Mooring's chain,
  the row a Weft was born in, a Ghastkin's former name), friendship, the days you've talked, topics heard, and the
  gifts you've found out they love, like or dislike.
- **Story notes:** the story's title and its four steps: done, next (with what to do), or later.

## The file

One per people: `src/main/resources/data/netherfriends/stories/<people>.json`. Check it with
`python tools/check_stories.py` (or `python tools/check_stories.py weft`).

```json
{
  "early":   [LINE, ...],
  "child":   [LINE, ...],
  "waiting": [LINE, ...],
  "ready":   [LINE, ...],
  "stories": [STORY, ...]
}
```

- **LINE** is a string, or `{"text": "...", "when": {...}}` with the conditions of `DIALOGUE.md` (`job`, `gender`,
  `place`, `level`, `cold`; not `first`, `state`, `carrying`, `holding`). At most 200 characters. Every list needs
  plain lines (no `when`) as fallbacks.
  - `early` (at least 4): not yet: they don't tell their story to someone they barely know. Point gently at
    friendship ("talk to me a while longer").
  - `child` (at least 4): a child's answer to "tell me your story": they haven't got one yet, or a small secret
    or wish in its place.
  - `waiting` (at least 5): mid-story, not ready to tell the rest yet. Come back another day / when we know each
    other better. Never mention game terms like "level".
  - `ready` (at least 4): mid-story, ready now: "There's more to it. Will you listen?"
- **STORY:**

```json
{
  "id": "snake_case, unique in the file",
  "jobs": ["Smelter"],
  "title": "The Cold Furnace",
  "intro": ["page", "page"],
  "request": {"item": "minecraft:magma_block", "count": 8, "ask": "...", "thanks": "..."},
  "confide": ["page", "page", "page"],
  "endings": [
    {"ask": "Light it again.", "text": "...", "memory": "..."},
    {"ask": "Let it rest.", "text": "...", "memory": "..."}
  ],
  "reward": {"item": "minecraft:gold_ingot", "count": 4},
  "after": ["...", "..."]
}
```

| Key | Rules |
|---|---|
| `jobs` | One or more jobs of this people (`DIALOGUE.md`), never `Child`. Every job needs at least two stories. |
| `title` | 3 to 40 characters, unique in the file. Shown in the Journal. |
| `intro` | 2 or 3 pages, each at most 200 characters. What's on their mind; the last page says what they need. |
| `request.item`, `count` | A vanilla item id (`minecraft:...`) the player can get in survival, and how many. You hold them in one hand, so never more than one stack (16 for snowballs, pearls, honey bottles; 1 for buckets, tools, armour). Ask for amounts that take a little effort, not a grind. |
| `request.ask` | At most 200. Asks for exactly that: say the number and the thing ("Bring me eight magma blocks"). |
| `request.thanks` | At most 200. When you bring it. |
| `confide` | 2 or 3 pages, at most 200 each. The personal heart of it. The last page ends on the question the two endings answer. |
| `endings` | Exactly 2. `ask` at most 30 (what you say), `text` at most 200 (their reply, how it ends), `memory` at most 120: one sentence for the Journal, second person, past tense ("You told {name} to light the furnace again."). Both endings are kind; they differ in what they choose. |
| `reward` | An item and a count (at most one stack), worth the trouble, fitting them. |
| `after` | 2 or 3 lines, at most 200 each: what they say on the Story tab once it's over. |

Placeholders are the ones in `DIALOGUE.md` (`{name}`, `{village}`, `{neighbour}`, `{clan}`, `{kept}`, ...).
`{village}` becomes "home" for a resident without one (wanderers, Bastion-dwellers), so write it so that reads.
No em dashes. Canon and voice: `DIALOGUE.md`.
