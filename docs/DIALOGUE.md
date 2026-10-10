# Dialogue

Every people has one dialogue file: `src/main/resources/data/netherfriends/talk/<people>.json` (`piglin`,
`withren`, `magmari`, `weft`, `ghastkin`). The server reads it when the game starts and picks lines from it as
you talk. `python tools/check_talk.py` checks every file (run it after any edit).

## How a conversation goes

- Right-click a resident to open the conversation window. Piglins only talk while you wear gold (golden armour or
  any armour with a gold trim), except a Piglin whose Ingot-friend (level 3) you are: it talks to you, and leaves you
  be, with your gold off. Ghastkin only while you are sneaking: walk up normally and they drift away.
- They open with a **greeting**. Their **topics** sit on the right as numbered replies (keys 1-6). Pick one and
  they answer with one of that topic's lines. Some answers offer **replies** of your own; what you say can win or
  lose friendship.
- The dock along the bottom: **Talk** (small talk: **chatter**), **Gift** (the item in your main hand),
  **Barter** (Piglins only: hand over the gold ingot you're holding) and **Goodbye** (they say a **farewell**
  over your hotbar).
- If they won't talk (a Weft whose forest you cut, anyone you just hit), they say a **refuse** line and offer
  nothing else.

## Friendship

Six levels, 0 to 5. Each people names its own levels (`levels`). Points: the first talk of a day +1, every topic
heard for the first time +1, a reply's `points` (-2 to 2), a gift once a day (loved +4, liked +2, anything else +1,
disliked -1). Levels start at 0, 3, 8, 15, 25 and 40 points. Reaching a level plays its **levelup** line, which may
give a `reward`. Friendship is per player, per resident.

## The file

```json
{
  "levels": ["six names", "for levels", "0 to 5", "...", "...", "..."],
  "greet":   [LINE, ...],
  "chatter": [LINE, ...],
  "topics":  [TOPIC, ...],
  "gift": {
    "loved": ["minecraft:gold_ingot", "#minecraft:piglin_loved"],
    "liked": ["..."],
    "disliked": ["..."],
    "lines": {"loved": [LINE], "liked": [LINE], "neutral": [LINE], "disliked": [LINE], "again": [LINE], "empty": [LINE]}
  },
  "levelup":  [LINE, ...],
  "farewell": [LINE, ...],
  "refuse":   [LINE, ...]
}
```

- **LINE** is a string, or `{"text": "...", "when": {...}, "replies": [REPLY, ...], "reward": {"item": "minecraft:gold_nugget", "count": 9}}`.
  Only `text` is required. `reward` only means something in `levelup`. `replies` only in `topics` and `chatter`.
- **TOPIC** is `{"id": "snake_case", "ask": "What you say", "when": {...}, "lines": [LINE, ...]}`.
- **REPLY** is `{"ask": "What you say", "text": "Their answer", "points": 1}`. One level deep: a reply's answer
  has no replies of its own.
- `gift.again` is said when you already gave them something today; `gift.empty` when your hand is empty.
  Item lists take item ids or `#` item tags.
- `levelup` lines each carry `"when": {"level": [n, n]}` for n = 1 to 5.
- Text is at most 200 characters; an `ask` at most 30. One or two sentences reads best: the box shows four lines.

## Conditions (`when`)

All listed conditions must hold. Lines without `when` can always be said.

| Key | Value | True when |
|---|---|---|
| `level` | `[min, max]` | friendship level is between min and max (0-5, inclusive) |
| `child` | `true` / `false` | the resident is a child |
| `gender` | `"male"` / `"female"` | (never for Withren) |
| `job` | `"Brewer"` or a list | the resident has that job |
| `cold` | `true` / `false` | (Magmari) they've been away from heat too long |
| `first` | `true` | this is the first time this player has ever talked to them |
| `carrying` | item id | the player has that item anywhere in their inventory |
| `holding` | item id | the player holds it in their main hand |
| `place` | `"village"` / `"bastion"` / `"wild"` | where they live (`bastion`: Piglins in a Bastion Remnant) |
| `state` | `"grudge"` / `"angry"` | (refuse lines) a Weft's grudge for cutting the forest / they're fighting you |
| `gold` | `true` / `false` | (Piglins) the player is wearing gold. Only a friend (level 3+) ever talks to you with `false` |

When several greetings match, the most specific one wins often (lines with more conditions are picked more).
Put the plain lines last as fallbacks; every list must have lines with no `when` (or that cover every case).

## Placeholders

| Placeholder | Becomes |
|---|---|
| `{name}` | their name ("Grukka") |
| `{epithet}` | the rest of their name ("Red-Tusk", "who keeps Anwen", "of the Long Chain", "Row Twelve-Eighty", "who was Oriel") |
| `{job}` | their job, lower case ("brewer") |
| `{player}` | the player's name (use rarely) |
| `{village}` | their village's name ("Gildmaw Hold"); "home" when they have none |
| `{neighbour}` | another resident of their village by name, picked at random; "a neighbour" when alone |
| `{clan}` | (Piglins) their clan ("Red-Tusk") |
| `{kept}` | (Withren) the dead person they keep ("Anwen") |
| `{chain}` | (Magmari) their Mooring's chain ("the Long Chain") |
| `{row}` | (Weft) the row of the Long Cloth they were born in ("Twelve-Eighty") |
| `{former}` | (Ghastkin) the name they had before their last grief ("Oriel"); children have none, so never use it in child lines |

## Jobs

| People | Jobs (children are always `Child`) |
|---|---|
| Piglins | Bartermaster, Hunter, Forager, Smelter, Goldsmith, Hoglin-herder, Brute (every Piglin Brute), Bastion-dweller (Piglins of a Bastion), Wanderer |
| Withren | Remembrancer, Ashwarden, Lantern-keeper, Wart-grower, Bonecarver, Brewer |
| Magmari | Ferrier, Hearthkeeper, Lava-angler, Chainwright, Quartzcutter, Chartmaker |
| Weft | Clothkeeper, Weaver, Spinner, Canopy-tender, Dyer, Netcaster |
| Ghastkin | Tearkeeper, Songwarden, Mender, Ashsweeper, Drift-watcher |

## Canon (keep every file consistent with this)

- **The War of the Hoard:** the Piglins wanted the gold under the soul sand valleys. The Magmari ferried Piglin
  war-bands across the lava seas for pay. The Weft stripped bone from both sides' dead for needles and loom
  weights. Piglins took to wearing gold so they could tell friend from foe, because nobody else would wear it.
- **The Last Answer:** the Withren's name for the one Wither they made, at the end of that war. **The Three**,
  **Ashrin, Velmir and Thalys**, gave themselves willingly; their skulls became its heads, the souls in the soul
  sand its body. It could not be aimed or called back. It burned the Piglin bastions (now the Bastion Remnants),
  the Magmari Moorings that had carried the war-bands, and then the Withren's own fortresses (now the Nether
  Fortresses, held by the Hollowed and the blazes). Its grief turned half the Ghastkin into ghasts; the Weft wove
  sixty black rows. Every Vigil has a Hall of the Three with their names.
- **The Hollowed** are wither skeletons: Withren whose souls the Wither burned out. Three Hollowed skulls wake a
  Wither with no willing souls inside, so nothing in it wants to stop. That is how outsiders make Withers. The
  player comes from the world where outsiders make Withers.
- **The Rotted** are zombified piglins. Piglins believe the Overworld rots you.
- **The Long Cloth:** each Loom keeps a copy of the Weft's history, one row woven a year. This year's row is
  Thirteen-Twelve (1312). The sixty black rows of the Wither are rows Eleven-Ninety to Twelve-Forty-Nine. Being
  woven into the Cloth is the Weft's highest honour. Saying: "The forest is the warp. We are the weft."
- **Ghastkin** cry once, properly, for each death, then let it go. Their tears close wounds and wash away memories.
  A Ghastkin takes a new name after every great grief and lets the old one go ("Siel, who was Oriel"). One who
  grieves too long swells, forgets hands and words, and drifts off as a ghast, still crying. Children's eyes are
  still open: they haven't learned to cry. Their Weeping Hall holds the only water in the Nether, the pool of tears.
- **Magmari** walk lava and fear cold: away from lava or fire they cool, go dark and grumpy. Gold melts on their
  skin. Striders are their cattle and boats; ghasts are "the sky-mourners". They are chainwrights. Every Mooring
  has a chain name its people share.
- **Withren** each keep one dead person's name and live to remember them. A child has no one to keep yet; they
  choose at their first Vigil. Their names are on the Name Wall. They don't sleep in beds (beds burst in the Nether).
- **Grudges:** see the table in `PEOPLE.md`. The five peoples never share a village and don't trust each other.

## Voice

| People | Manner |
|---|---|
| Piglins | Loud, boastful, greedy, quick to laugh and quick to swing. Short sentences. Gold is rank, savings and handshake. |
| Withren | Quiet, grave, formal, dry graveyard humour. Never rushed. |
| Magmari | Warm, chatty, hospitable, always haggling, always cold. Short and grumpy when cold. |
| Weft | Patient, precise, quietly smug about four arms; click their mandibles while thinking (*click*). |
| Ghastkin | Soft-spoken, kind, a little melancholy, with a sly streak; never finish a sad story. |

Plain words, no modern slang, no real-world references. Talk to the player as "you". Asterisks mark a gesture:
`*click click*`. The game has no day and night in the Nether, so avoid "tonight" and "this morning".
