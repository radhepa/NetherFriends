# The Peoples of the Nether (design draft 4)

The mod: **Nether Friends** (mod id `netherfriends`, set in one place: `tools/mobkit.py`).
This is a separate mod and repo from Village Friends and End Expansion. From Village Friends we take only the
look and layout of the conversation screen (see `CONVERSATION_UI.md`), recoloured netherrack. All dialogue is
new. No animations yet.

Models (same box-model kit as Not-So-Vanilla Mobs and End Expansion):

    python tools/people/piglin.py      vanilla reference: piglin, piglin_brute, piglin_baby
    python tools/people/withren.py     withren (+ withren_child), withren_remembrancer, withren_ashwarden
    python tools/people/magmari.py     magmari, magmari_female (+ _cold each), magmari_ferrier, magmari_child (+ _cold)
    python tools/people/weft.py        weft, weft_female, weft_child
    python tools/people/ghastkin.py     ghastkin, ghastkin_female, ghastkin_child
    python tools/preview/preview.py withren withren_child --views 4
    python tools/design/build_page.py <out.html>     the design page

## Decisions (user, 2026-10-09)

- Piglins keep the vanilla model and skin; only their code changes. Every Piglin, in Bastions too, can be talked
  to while you wear gold, and they also get their own villages (Holds).
- Gold = golden armour or any armour with a gold trim.
- Brutes respect gold, except in a Bastion's treasure vault.
- The Withren head is the Wither's head (the user's reference image is pixel-identical to the vanilla Wither
  center head).
- Summoning a Wither turns the Withren cold, not hostile.
- Magmari are peaceful toward the player.
- Babies: Piglins use the vanilla baby piglin as is. Withren children are the classic Minecraft baby: the adult
  body at half size with the same full-size Wither head ("a cuter Withren"). The user likes the baby Magmari.
- Every people has its own separate villages. The peoples do not coexist peacefully. No mixed villages.
- Names Withren and Magmari kept.
- Gender: male and female like Village Friends for Piglins (names/voices only, vanilla look), Magmari, Weft and
  Ghastkin. Withren are genderless ("they").
- Two more interactable peoples (my design, user asked 2026-10-09): the Weft (Warped Forest) and the Ghastkin
  (Basalt Deltas). The user approved both tempers and asked for a more ghast-like name for the Basalt Deltas
  people, so the Lachryn became the Ghastkin.

## Vanilla textures are borrowed, never copied

The Piglins and the Withren's head use the game's own textures. The kit marks these as borrowed:
`Model(..., vanilla_texture="entity/piglin/piglin")` for a whole vanilla mob (geometry goes to
`tools/design/reference/`, nothing is painted), and `part.cube(uv, ..., texture="entity/wither/wither")` for one
cube. `mobkit.vanilla_png()` reads them from the local Minecraft client jar for previews and the design page only.
In game, the Withren renderer draws its head with `minecraft:textures/entity/wither/wither.png` (center head UV
0,0, 8x8x8, adults and children alike), so it follows resource packs. Never commit vanilla
PNGs to the repo.

## Piglins (the existing mob, recoded)

- **Look:** unchanged: AdultPiglinModel / BabyPiglinModel, piglin.png, piglin_brute.png, piglin_baby.png.
- **Where:** Bastions (as now) and Holds (new villages). Both talkable with gold.
- **Gold** is rank, savings and handshake. No gold = enemy.
- **Manner:** loud, boastful, greedy, quick to laugh and quick to swing.
- **Names:** short, with a clan shared by the household of a longhouse: Grukka Red-Tusk, Snorr Gild-Snout,
  Brakka Ash-Bristle.
- **Belief:** the Overworld rots you. Zombified piglins are "the Rotted".
- **Jobs:** Bartermaster (the barter ring), Hunter, Forager, Smelter, Goldsmith, Hoglin-herder, Brute; Piglins
  of a Bastion are Bastion-dwellers, any others Wanderers.

### The gold rule

- **No gold:** every piglin that sees you attacks (Bastion or Hold); in a Hold a Brute blows the war-horn and the
  whole village joins. No talking, no bartering.
- **Wearing gold:** right-click opens the conversation screen. Talk, barter (right-click holding a gold ingot as
  in vanilla, or Barter in the dock), friendship counted in gold nuggets. Thieves are still attacked (chests, gold blocks, the Hoard).
  Taking your gold off in front of them makes them attack.
- **Friends don't need gold:** once you're a Piglin's Ingot-friend (friendship level 3), that one Piglin knows your
  face. It won't attack you with your gold off, still talks to you, and stays out of it when the others pick a fight
  with you. Every other Piglin still wants gold. Hitting your friend, or robbing a chest or gold block in front of
  it, angers it as usual; a Brute friend still guards the Bastion vault. (`GoldRule.trusts`, the sensor mixin.)
- **Other changes:** homes, jobs, routines in Holds; memory of the player; babies follow gold-wearers; Piglins
  attack Withren and Magmari on sight (gold melts on Magmari skin, so they can never show it); zombified piglins
  unchanged.
- **Hooks in 26.3:** `PiglinAi.isWearingSafeArmor(LivingEntity)` (tag `minecraft:piglin_safe_armor`),
  `PiglinAi.mobInteract(...)`, `PiglinAi.findNearestValidAttackTarget(...)`. Piglins stay vanilla entities.

## Withren (WITH-ren): the Wither's own people

- **Body:** the player's body and height (1.8 blocks). Ash-dark skin, bony knuckles.
- **Head:** the Wither's center head at normal head size (borrowed vanilla texture, see above).
- **Temper:** neutral toward the player. Hit one and every Withren nearby fights back; the Vigil remembers.
- **Dress:** soul-cloth (slate, soul-soil brown), bone toggles, soul lanterns. Remembrancers: hood, stole with a
  tally mark per name kept. Ashwardens: basalt carved with the Wither's ribcage, circlet with three empty sockets.
- **Manner:** quiet, grave, formal, dry graveyard humour.
- **Names:** each carries one dead person's name they swore to remember: "Sereth, who keeps Anwen". The name
  they keep is one of the names on their own Vigil's Name Wall.
- **Children:** the classic Minecraft baby (`mobkit.babify`): same parts and texture as the adult villager, body
  parts at half scale (ModelPart x/y/zScale), the head left at full size, about 1.25 blocks. A child has no one to
  keep yet; they choose a name at their first Vigil.
- **Jobs:** Wart-grower, Brewer, Bonecarver, Lantern-keeper, Remembrancer (elder), Ashwarden (guard).

### The Wither, as they tell it

- Three Withren give themselves willingly (the Three: Ashrin, Velmir and Thalys, named in every Hall of the Three); their skulls become its heads, the souls in the soul sand its body.
- It can't be aimed or recalled; it burns until the three souls inside are spent.
- Made once, at the end of the War of the Hoard: it burned the Piglin bastions (the Bastion Remnants), the
  Magmari Moorings that carried the Piglin war-bands, and the Withren's own fortresses.
- Wither skeletons are **the Hollowed**: Withren whose souls it burned out. Three Hollowed skulls also wake a
  Wither, but one with no willing souls inside, so nothing in it wants to stop. That is how outsiders make Withers.
- **Player deeds:** carrying wither skulls → they ask what you mean to do; summoning a Wither → friendship with every
  Withren drops hard, still neutral; killing one → honour, and the Nether Star (the three souls fused) can be laid
  to rest in a Hall of the Three for a big reward or kept for a beacon.

## Magmari (mag-MAR-ee): the lava-sea folk

- **Body:** about 2.5 blocks (2.9 with crest), hunched, long backward-bending legs for wading lava, short heavy tail.
- **Skin:** magma crust with glowing seams; a crest of basalt columns, each a little vent.
- **Heat:** lava doesn't hurt them, cold does. Away from lava or fire they cool like striders: seams go dark, crust
  turns ash-violet (`magmari_cold` texture), slower and grumpy.
- **Temper:** peaceful toward the player: if attacked they wade into the lava where you can't follow; the Mooring
  remembers. Piglin and Withren raiders get the chain bridges pulled up and magma thrown at them.
- **Dress:** twisting-vine rope, weeping-vine hip cloth, iron chain (they are chainwrights).
- **Gender:** males one tall crest ridge and a chain sash; females (`magmari_female`) twin ridges hung with chain
  loops, a woven chest wrap and a longer hip cloth.
- **Manner:** warm, chatty, hospitable, haggling, always cold.
- **Names:** volcanic words plus their mooring's chain: Tephra of the Long Chain, Scoria of the Slow Glow.
- **Children:** about 1.3 blocks, round and stubby-legged, three crest nubs; they run hotter than adults but cool
  twice as fast (`magmari_child`, `magmari_child_cold`).
- **Jobs:** Ferrier (strider barges), Lava-angler, Chainwright, Quartzcutter, Hearthkeeper, Chartmaker.
- **Play:** strider-barge ferries across lava seas (Travel), for outsiders only, never one of the other four peoples.
  Striders are their cattle and boats; ghasts are "the sky-mourners" who saw the Wither and never stopped crying.

## Weft: the canopy weavers of the Warped Forest (my design)

- **Body:** about 2.3 blocks, slender, FOUR arms: a long upper pair to climb, a short lower pair to weave (one
  holds a shuttle). Long three-fingered hands. Straight thin legs.
- **Head:** a smooth dark face plate with six small glowing eyes (two hot ones, one above each, one low at each
  side), a long skull swept back, two dark mandibles at the jaw. No plants on the head (End Expansion's Chorrin
  already have a chorus crown).
- **Skin:** warped-stem bark: deep indigo with teal streaks.
- **Temper:** wary/neutral. They talk to visitors who leave the forest alone. Break warped stems or wart near a
  Loom and they drop vine nets (slowness, held in place) and the whole Loom turns on you.
- **Dress:** twisting-vine cloth, warped-wart teal with shroomlight-orange stitching, spools at the belt.
  Males: shoulder mantle, short kilt, woven headband. Females (`weft_female`): shawl, long skirt, headwrap with two
  long tails.
- **Manner:** patient, precise, quietly smug about four arms; click their mandibles while thinking.
- **Names:** a name plus the row of the Long Cloth woven the year they were born, said like a year: "Vesk, Row
  Twelve-Eighty-Seven". This year's row is Thirteen-Twelve; the sixty black rows are Eleven-Ninety to Twelve-Forty-Nine.
- **Children:** about 1.1 blocks, two arms only; the lower pair are nubs that grow in later.
- **Jobs:** Weaver, Spinner, Netcaster (guard), Dyer, Canopy-tender, Clothkeeper (elder).
- **The Long Cloth:** each Loom keeps a copy of their history, one row a year; sixty black rows are the Wither. Doing
  something worth remembering gets you woven into the cloth (their highest honour, a friendship milestone).
- Saying: "The forest is the warp. We are the weft."

## Ghastkin (GAST-kin): the mourners of the Basalt Deltas (my design)

- **Body:** pale slender upper body; below the waist six tentacles instead of legs; they float a little off the
  ground, about 2.25 blocks crown to tentacle tip.
- **Head:** a ghast's face at normal head size (our own texture, not the vanilla ghast): closed eyes, tear tracks,
  a small down-turned mouth.
- **Temper:** shy and peaceful. Approach while sneaking to talk; run at them or draw a weapon and they drift up out
  of reach. They never fight.
- **Tears:** they cry once, properly, for each death, then let it go. Tears close wounds and wash away memories, so
  they trade healing (ghast tears / regeneration) and are careful who they cry for.
- **Dress:** ash-silk the colour of delta smoke, a tear vial on a cord. Males: hood and short cape. Females
  (`ghastkin_female`): long sheer veil, circlet of tear beads.
- **Manner:** soft-spoken, kind, a little melancholy, with a sly streak; never finish a sad story.
- **Names:** a new name after every great grief, the old one let go: "Siel, who was Oriel".
- **Children:** about 1.2 blocks, eyes still open (they haven't learned to cry), four short tentacles.
- **Ghasts:** a Ghastkin who grieves too long swells, forgets hands and words, and drifts off as a ghast, still crying.
- **Jobs:** Tearkeeper (healer), Mender, Songwarden, Ashsweeper, Drift-watcher.

## Five peoples, no peace

Piglins attack anyone who shows no gold, and none of the other four ever wears it. On top of that, every pair has
its own grudge:

| | Withren | Magmari | Weft | Ghastkin |
|---|---|---|---|---|
| **Piglins** | The War of the Hoard and the Wither. | Gold melts on Magmari skin; Piglins raid their striders. | Piglins raid the canopy for shroomlights; Weft drop nets. | Piglins shoot them down as ghast whelps. |
| **Withren** | | The ferries that carried the Piglins; the Moorings the Wither burned. | Weft take bone from the Withren dead for needles and loom weights. | Withren keep every name; Ghastkin cry names away. |
| **Magmari** | | | Magmari raid the forest for warped fungus to drive striders. | A Ghastkin tear puts out a Magmari's glow. |
| **Weft** | | | | Weft string nets in the air to catch Ghastkin for their tears. |

## Villages (never shared, one people per biome)

| Kind | Who | Where / look |
|---|---|---|
| **Hold** | Piglins, 10-18 (2 Brutes, babies) | Crimson Forest, Nether Wastes. Crimson-timber longhouses on blackstone, the Hoard, barter ring, hoglin pens, war-horn tower. |
| **Vigil** | Withren, 6-14 | Soul Sand Valley. Basalt and bone halls, soul-lantern lanes, Hall of the Three, Name Wall, wart terraces, brewery. |
| **Mooring** | Magmari, 6-12 | Lava seas. Platforms on basalt pillars, chain bridges, magma hearths, strider pens, ferry docks. |
| **Loom** | Weft, 8-14 | Warped Forest. Platforms on giant warped-fungus caps, woven bridges and nets, the Great Loom and the Long Cloth, shroomlight dye vats, spinning walks, net-hung lookouts. |
| **Eave** | Ghastkin, 6-12 | Basalt Deltas. Homes hung like lanterns under cliffs and great basalt pillars, vine ladders, the Weeping Hall, tear-vial stores, hanging gardens, lookout perches. |

The villages are built and in the game; see `VILLAGES.md` for every building. How often they appear is data (structure sets a data pack can change). Vanilla ruins stay: Bastion Remnants (still Piglin homes; Brutes guard
the old hoards) and Nether Fortresses (the Withren's old war-forts, held by the Hollowed and the blazes set there as
wardens).

## History

1. **The Gilded Age:** five peoples, five realms, no friendship.
2. **The War of the Hoard:** Piglins want the gold under the valleys; the Magmari ferry Piglin war-bands for pay; the
   Weft strip bone from both sides' dead. Piglins wear gold to tell friend from foe, because nobody else will.
3. **The Last Answer:** the one Wither burns the bastions, the Moorings, then the Withren's own fortresses. The grief
   turns half the Ghastkin into ghasts; the Weft weave sixty black rows.
4. **Now:** five kinds of village, ten grudges. The player arrives from the world where outsiders make Withers.

## Creatures (in the game)

One beast for each people that had none (the Piglins keep hoglins, the Magmari striders). Models in
`tools/creatures/`. Each lives wild in its people's biome and is kept in its people's villages.

- **Cairnback** (Withren, Soul Sand Valley): a slow tortoise whose high, stepped shell is plated in bone like a
  cairn, with a bone bowl on top where a small soul fire burns as long as it lives. Hold a torch, lantern or campfire
  to a grown one and it comes away relit as its soul version. Bred with nether wart. Armoured and hard to knock
  about. The Withren walk them in their processions; every Hall of the Three keeps one, and they graze the wart
  terraces. About 1.1 blocks tall, 1.4 long.
- **Silkgrub** (Weft, Warped Forest): a fat teal caterpillar with glowing orange spots that lives in the wart and
  spins silk. Shear its tufts for string (and now and then a cobweb); they grow back in three minutes, or at once if
  you feed it warped roots. Bred with warped roots. It climbs like the Weft. They're kept on the Loom's decks, at
  the dye house and the Great Loom. About 0.55 blocks tall, 1.3 long.
- **Ashbell** (Ghastkin, Basalt Deltas): a pale drifter like a jellyfish of the air, half see-through, with a glow
  at its heart and glowing beads on its trailing threads. It rings faintly as it drifts. Harmless; it can be led on
  a lead. No ghast takes aim at anyone within 16 blocks of an Ashbell (a ghast that hears one remembers being kin),
  so the Ghastkin lead them across open ground. They drift round the Eave's pillars and in the Weeping Hall. Not
  bred. About 1.25 blocks from bell to thread tips.

The young of the Cairnback and Silkgrub are the grown model at half size; a young Cairnback has no fire yet and a
young Silkgrub no silk. The three peoples talk about their creatures (a topic each and some small talk).

## Conversation screen

Built: Village Friends' layout in netherrack (see `CONVERSATION_UI.md`), with every line new (`DIALOGUE.md`,
about 1,400 lines across the five peoples). Right-click a resident to talk; Piglins only while you wear gold
(or without it, once you're their Ingot-friend), Ghastkin only while you sneak. Panel `#6a2b29` over a netherrack tile, nether-brick edge `#260d0c`, bevel `#b35d53`,
ink `#fbeee2`, muted `#dcb0a2`, status gold `#f2c14e`. Friendship (six levels, named by each people) shows as
gold nuggets, soul flames, embers, spools or tears. Dock: Talk (small talk), Gift, Barter (Piglins), Goodbye. Voices
are note-block blips: Piglins bass grunts, Withren a low flute, Magmari a warm guitar (lower when cold), Weft
quick hat clicks, Ghastkin chimes that fall through the line.

Names: every resident has one from the moment the world generates them (see `world/Names.java`), with a job from
the building they live in. Walking into a village shows its name ("Gildmaw Hold", "the Mooring of the Slow Glow").

Tabs under the friendship card: **Talk**, **Story** (every grown resident's own story in four steps, 64 stories
across the five peoples, two for every job) and **Journal** (memories, About, story notes). See `STORIES.md`.

Not built yet: trading for the four new peoples, and the Ferrier's ferry (Travel).
