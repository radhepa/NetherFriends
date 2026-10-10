# Villages

Each of the five peoples lives in its own kind of village, in its own part of the Nether. No village is ever
shared. All five come from normal world generation in any new Nether (`/locate structure netherfriends:hold`,
or `#netherfriends:village` for all of them).

| Village | People | Where | Look |
|---|---|---|---|
| **Hold** | Piglins (vanilla), Brutes, baby Piglins, penned hoglins | Nether Wastes, Crimson Forest | Crimson longhouses on blackstone plinths round a barter ring; the Hoard; a horn tower; a forge; huts; hoglin pens |
| **Vigil** | Withren (villagers, a Remembrancer, Ashwardens, children) | Soul Sand Valley | Basalt-and-bone halls on soul-lantern lanes; the Hall of the Three; the Name Wall; wart terraces; a brewery; the Remembrancer's tower; Ashwarden posts |
| **Mooring** | Magmari (male, female, children, a Ferrier), striders | Out on the open lava sea, any biome | Round decks on basalt pillars standing in the lava; chain bridges to a central hearth; domed huts; a strider pen; a ferry dock; a causeway to the nearest shore when there is one |
| **Loom** | Weft (male, female, children) | Warped Forest | Giant warped fungi grown for living on, with decks on the caps joined by rope bridges hung with vine nets; woven huts; a dye house; a lookout; the Great Loom with the Long Cloth hanging down through its deck |
| **Eave** | Ghastkin (male, female, children) | Basalt Deltas | Great basalt pillars with lantern pods hanging from their arms; a gallery round each pillar reached by a ladder; weeping vines trailing to the ground; the Weeping Hall round a pool of the only water in the Nether |

## The buildings

### Hold (Piglins)
- **Barter ring** (centre): a round paved square, a gold block on a blackstone pedestal, four lamp posts.
- **Longhouses** (3): 9 by 15 crimson halls on a blackstone plinth, a steep crimson-stair roof, shroomlight in the
  gables, a hearth, benches, the household's gold block and stores.
- **The Hoard**: a squat blackstone vault full of gold blocks and treasure chests. A Brute stands at the gate.
  Taking the gold angers every Piglin around (vanilla).
- **Horn tower**: a blackstone tower with a crimson lookout, the war bell, and a Brute on top.
- **Forge**: an open smithy with a lava cauldron, blast furnace, smithing table and anvil.
- **Huts** (2-3) and **hoglin pens** (1-2), each with a herder at the gate. Penned hoglins are livestock: the
  Piglins don't hunt them.
- Piglins of the Hold carry crossbows or golden swords.

### Vigil (Withren)
- **Hall of the Three** (centre): a round basalt hall with a bone band and a stepped dome open over a soul fire.
  Three bone plinths carry wither skeleton skulls, each with a soul campfire and a sign naming one of the Three
  who gave themselves for the Last Answer: Ashrin, Velmir and Thalys, the same in every Vigil.
- **Name Wall**: a long blackstone wall of 39 signs, each a Withren name. These are the dead the Vigil's people
  keep: every adult Withren keeps one of the names on its own wall.
- **Homes** (4-5): smooth basalt on a bone plinth, polished basalt corners, a dark gable roof, warped doors, a
  soul-fire hearth, bedrolls (no beds: beds explode in the Nether), bookshelves, wither roses.
- **Remembrancer's tower**, **brewery**, **wart terraces** (nether wart on soul sand), two **Ashwarden posts**.
- **Cairnbacks**: one keeps its fire in the Hall of the Three; more graze the wart terraces.
- Soul-soil lanes lit by soul lanterns on bone posts.

### Mooring (Magmari)
- Decks stand two blocks over the lava on basalt pillars that go down to the sea floor.
- **Hearth** (centre): a pool of lava in a magma ring, seats, chain posts with lanterns.
- **Homes** (4-5): round decks with domed basalt huts, magma seams, three-high doorways (Magmari are tall).
- **Strider pen**: open lava fenced by a ring walk on posts, with striders (one saddled).
- **Ferry dock**: a T-shaped landing at the end of a long pier, the Ferrier, a saddled strider.
- **Causeway**: a bridge to the nearest shore within 64 blocks, if there is one.
- The Magmari walk on lava, so they wade between the platforms.

### Loom (Weft)
- The Weft grow their own giant warped fungi; decks sit on the flat caps 11-15 blocks up, joined by rope bridges
  along four straight arms. Cobweb nets hang under the bridges and round the lookout.
- **Great Loom** (centre): the biggest tree, its loom frame, and the **Long Cloth**: their history woven a row a
  year, hanging from the frame down through the deck toward the forest floor. A band of black rows is the Wither.
- **Woven huts**: round huts of teal wool and warped planks with orange stitches and cone roofs of wool stairs.
- **Dye house** (open, wool canopy, cauldrons, dye barrels) and a **lookout** (a small cap, a light, nets).
- Ladders run up the front of every stalk and come out through a hatch in the deck. The Weft just climb.
- **Silkgrubs** are kept on the home decks, at the dye house and on the Great Loom.

### Eave (Ghastkin)
- **Great pillars** (3-4): a polished basalt core in rough basalt, rising about 18 blocks. A gallery runs round
  each at 8 blocks up, reached by a ladder; two arms at the top each hold a pod on a chain.
- **Pods**: pale glass lanterns lit blue by soul lanterns. Homes (candles, white carpet, a chest), tear stores
  (brewing stands and healing tears) and hanging gardens (roots and fungi in soul soil). Weeping vines trail
  from every pod to the ground, and they can be climbed.
- **Weeping Hall** (centre): tall walls of quartz with light-blue windows and a stepped blackstone roof. Inside,
  the pool of tears: real water, the only water in the Nether.
- **Ashbells** drift under the pillars' galleries and over the pool in the Weeping Hall.

## Residents

Every resident a village spawns gets a name, a job and a home the moment the world generates them, from the
building they stand in: a Piglin in the forge is a Goldsmith, a Withren in the brewery a Brewer, a Weft at the dye
house a Dyer, a Ghastkin in a tear store a Tearkeeper. A longhouse's Piglins share a clan, a Mooring's Magmari share
its chain. Names show over their heads when you look at them. Walking into a village shows its name and how its
people expect you to behave. Bastion Piglins and residents from spawn eggs get their names the first time someone
talks to them.

## How they're made

Vanilla villages are jigsaw structures that follow a heightmap, and the Nether has no usable heightmap under its
roof. So each village kind is its own structure type (`world/village/*Village.java`):

1. **Site.** It reads the Nether's noise one column at a time (`Terrain`) to find a place that suits it: an open
   cave floor (Hold, Vigil), open lava sea (Mooring), a floor with 25+ blocks of air above for fungus trees
   (Loom), or room for tall pillars (Eave).
2. **Plan.** It places buildings on lots round the centre, each at its own floor height and facing the centre
   (`Plan`), and lays paths and bridges between them (`Ways`).
3. **Drawing.** Each building is a piece (`BuildingPiece`) stored as a kind, a seed, a position and a few
   numbers. When a chunk is generated, every piece touching it draws itself (`Builder`): clearing the hill it's
   cut into, filling foundations down to solid ground (or the lava sea floor), and placing residents and loot.
4. **Clearing.** Giant fungi, basalt columns, lava deltas and springs, fire and floor or ceiling vines are kept out
   of a village's chunks (`VillageClearing`), so the village stands in a clearing in its biome.

Villages are placed last in generation (`top_layer_modification`), and no monsters (ghasts included) spawn
naturally inside one. Spacing is data (`data/netherfriends/worldgen/structure_set`). Every village keeps 8
chunks clear of Bastions and Fortresses, and villages of different peoples never overlap: where two could
(near a biome border, or a Mooring off a shore), the later kind in the order Hold, Vigil, Loom, Eave, Mooring
gives way. A village's heart must sit on level ground, so none perches on the lip of a drop.

## Testing

`gradlew runClientGameTest -PtestHeap=3g` makes a new normal world, lines up every people's looks, then finds each
kind of village by natural generation in the Nether, checks it has at least four residents, and photographs it
(`build/run/clientGameTest/screenshots`). `-PnetherTests=Villages -PnetherVillages=loom,eave` runs only some.
