# Nether Friends

A Fabric mod for Minecraft 26.3. The Nether stops being empty: five peoples live there, each in its own villages,
and none of them at peace with the others.

| People | Village | Where |
|---|---|---|
| **Piglins** (vanilla look, new rules: show gold and they'll talk, show none and they attack) | Hold | Nether Wastes, Crimson Forest |
| **Withren**, the Wither's own people | Vigil | Soul Sand Valley |
| **Magmari**, lava-wading folk | Mooring | the open lava sea |
| **Weft**, four-armed canopy weavers | Loom | Warped Forest |
| **Ghastkin**, the ghasts' gentle kin | Eave | Basalt Deltas |

Right-click anyone to talk: Piglins only while you wear gold, Ghastkin only if you come up quietly (sneak). Every
resident has a name and a job, and remembers how you treat them. Become a Piglin's Ingot-friend (friendship level 3)
and that Piglin leaves you be even with your gold off. Every grown resident also has a story of their own (the Story
tab: hear it, bring what they need, hear the rest, answer them), and the Journal tab keeps what you've shared.
A Mooring's Ferrier takes you across the lava sea to another Mooring on a strider barge, for a few warped fungus.

They have plenty to say, some 2,600 to 2,750 lines per people, and they move like themselves: every people (the
Piglins too, on their unchanged vanilla model) has an animation pack of 69 to 80 clips. They idle in character and
practise their trade, chat with a neighbour, greet you, gesture while they speak and listen while you choose a
reply, and react to gifts, friendship, your replies and blows.

Three creatures live alongside them: the Withren's **Cairnback** (a bone-plated tortoise whose shell fire relights
torches and lanterns as soul ones), the Weft's **Silkgrub** (a caterpillar you shear for string) and the Ghastkin's
**Ashbell** (a drifting bell that keeps ghasts from firing at anyone near it).

- `docs/PEOPLE.md`: who the peoples are, their lore and the decisions behind them.
- `docs/VILLAGES.md`: every village and building, and how the villages are generated.
- `docs/DIALOGUE.md`: conversations, friendship and the dialogue files (`python tools/check_talk.py` checks them).
- `docs/STORIES.md`: the Story and Journal tabs and the story files (`python tools/check_stories.py` checks them).
- `docs/ANIMATIONS.md`: the animation packs, how the director picks clips, and how to author them
  (`python tools/animations/compile.py --check`, `python tools/animations/sheet.py` to see clips offline).
- `docs/FERRY.md`: the Magmari lava ferry.
- `docs/CONVERSATION_UI.md`: the conversation window, taken from Village Friends and recoloured netherrack.

## Building

```
gradlew build
```

The jar lands in `build/libs`. It needs Fabric Loader 0.19.5+ and Fabric API.

## Testing

```
gradlew test
gradlew runClientGameTest -PtestHeap=3g
```

The client test makes a new world, lines up every people, holds a conversation with each of them through the real
window, tries each creature's use (relighting, shearing, calming a ghast), then finds each kind of village by natural
generation in the Nether, checks every resident has a name and a job and every village keeps its creature, and
photographs it (`build/run/clientGameTest/screenshots`). The Animations part has a row of each people act out every
clip of its pack, photographed mid-motion. `-PnetherTests=Talk` (or `Peoples`, `Creatures`, `Villages`,
`Animations`) runs one part.

## Art

The peoples and creatures are box models drawn by Python scripts (`tools/people/*.py`, `tools/creatures/*.py`,
using `tools/mobkit.py`), which write the model JSON and textures. The Withren's head is the Wither's own: the game draws it from its own texture at runtime,
so no vanilla art is copied into this mod. `python tools/gen_data.py` writes the world-generation data, loot, tags
and spawn eggs.
