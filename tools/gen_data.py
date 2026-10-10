"""Write the mod's generated data: worldgen JSON for the five villages, biome and structure tags, chest loot
tables, entity tags, spawn eggs (texture + item model) and the English names.

  python tools/gen_data.py

Everything here is data a data pack can override; the Java side only defines the structure types.
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from mobkit import spawn_egg  # noqa: E402

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(ROOT, "src", "main", "resources", "data")
ASSETS = os.path.join(ROOT, "src", "main", "resources", "assets", "netherfriends")
NS = "netherfriends"


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


# -- villages ---------------------------------------------------------------------------------------------
# name: (biomes, spacing, separation, salt). Spacing and separation are in chunks; every village keeps
# well clear of Bastions and Fortresses (fortress halls sprawl far from where they start). A Mooring needs open lava sea at its chosen spot, so it tries more often.
VILLAGES = {
    "hold": (["minecraft:nether_wastes", "minecraft:crimson_forest"], 26, 9, 1846203117),
    "vigil": (["minecraft:soul_sand_valley"], 22, 8, 1846203118),
    "mooring": (["#minecraft:is_nether"], 20, 7, 1846203119),
    "loom": (["minecraft:warped_forest"], 20, 7, 1846203120),
    "eave": (["minecraft:basalt_deltas"], 22, 8, 1846203121),
}

for name, (biomes, spacing, separation, salt) in VILLAGES.items():
    write(os.path.join(DATA, NS, "tags", "worldgen", "biome", "has_structure", name + ".json"), {"values": biomes})
    write(os.path.join(DATA, NS, "worldgen", "structure", name + ".json"), {
        "type": f"{NS}:{name}",
        "biomes": f"#{NS}:has_structure/{name}",
        # placed last, after the fungi, basalt columns and glowstone, so nothing grows through the houses
        "step": "top_layer_modification",
        "terrain_adaptation": "none",
        # no monsters (ghasts included) spawn naturally inside a village
        "spawn_overrides": {"monster": {"bounding_box": "full", "spawns": []}},
    })
    write(os.path.join(DATA, NS, "worldgen", "structure_set", name + ".json"), {
        "structures": [{"structure": f"{NS}:{name}", "weight": 1}],
        "placement": {
            "type": "minecraft:random_spread",
            "spacing": spacing,
            "separation": separation,
            "salt": salt,
            "exclusion_zone": {"other_set": "minecraft:nether_complexes", "chunk_count": 8},
        },
    })
write(os.path.join(DATA, NS, "tags", "worldgen", "structure", "village.json"),
      {"values": [f"{NS}:{n}" for n in VILLAGES]})


# -- chest loot -------------------------------------------------------------------------------------------
def item(name, weight, lo=1, hi=1, extra=None):
    e = {"type": "minecraft:item", "name": name, "weight": weight}
    fns = []
    if hi > 1 or lo != 1:
        fns.append({"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}})
    if extra:
        fns.extend(extra)
    if fns:
        e["functions"] = fns
    return e


def potion(pid):
    return [{"function": "minecraft:set_potion", "id": pid}]


def table(*pools):
    return {"type": "minecraft:chest", "pools": [{"rolls": {"type": "minecraft:uniform", "min": lo, "max": hi},
                                                  "entries": entries} for (lo, hi, entries) in pools]}


LOOT = {
    "hold_house": table((3, 6, [
        item("minecraft:gold_nugget", 30, 3, 12), item("minecraft:gold_ingot", 10, 1, 3), item("minecraft:cooked_porkchop", 20, 1, 4),
        item("minecraft:leather", 12, 1, 3), item("minecraft:crimson_fungus", 10, 1, 3), item("minecraft:arrow", 12, 2, 8),
        item("minecraft:string", 8, 1, 4), item("minecraft:quartz", 10, 2, 6), item("minecraft:golden_carrot", 4, 1, 2)])),
    "hold_hoard": table((4, 8, [
        item("minecraft:gold_ingot", 30, 2, 8), item("minecraft:gold_block", 8, 1, 2), item("minecraft:gilded_blackstone", 10, 1, 4),
        item("minecraft:golden_apple", 6), item("minecraft:golden_sword", 6), item("minecraft:golden_axe", 5),
        item("minecraft:golden_helmet", 5), item("minecraft:golden_chestplate", 4), item("minecraft:golden_boots", 5),
        item("minecraft:crossbow", 5), item("minecraft:spectral_arrow", 6, 2, 8), item("minecraft:piglin_banner_pattern", 2),
        item("minecraft:music_disc_pigstep", 1), item("minecraft:netherite_scrap", 2)])),
    "hold_forge": table((3, 5, [
        item("minecraft:gold_ingot", 20, 1, 4), item("minecraft:gold_nugget", 25, 4, 14), item("minecraft:raw_gold", 15, 1, 4),
        item("minecraft:iron_nugget", 10, 2, 8), item("minecraft:golden_shovel", 5), item("minecraft:golden_pickaxe", 5),
        item("minecraft:magma_cream", 8, 1, 3), item("minecraft:blackstone", 10, 4, 12)])),
    "vigil_house": table((3, 6, [
        item("minecraft:bone", 25, 2, 6), item("minecraft:bone_meal", 15, 2, 6), item("minecraft:nether_wart", 20, 2, 6),
        item("minecraft:soul_torch", 12, 2, 5), item("minecraft:coal", 15, 2, 6), item("minecraft:soul_lantern", 6),
        item("minecraft:paper", 10, 2, 6), item("minecraft:book", 6), item("minecraft:quartz", 10, 2, 5)])),
    "vigil_brewery": table((3, 6, [
        item("minecraft:nether_wart", 25, 3, 9), item("minecraft:blaze_powder", 12, 1, 3), item("minecraft:glass_bottle", 18, 2, 5),
        item("minecraft:fermented_spider_eye", 8, 1, 2), item("minecraft:magma_cream", 8, 1, 2), item("minecraft:glowstone_dust", 12, 2, 6),
        item("minecraft:potion", 6, extra=potion("minecraft:fire_resistance")), item("minecraft:potion", 4, extra=potion("minecraft:strength")),
        item("minecraft:potion", 4, extra=potion("minecraft:long_fire_resistance"))])),
    "vigil_hall": table((3, 5, [
        item("minecraft:book", 15, 1, 3), item("minecraft:writable_book", 6), item("minecraft:wither_skeleton_skull", 1),
        item("minecraft:bone_block", 12, 1, 3), item("minecraft:soul_lantern", 10, 1, 2), item("minecraft:candle", 12, 1, 3),
        item("minecraft:quartz", 12, 2, 6), item("minecraft:experience_bottle", 8, 1, 3)])),
    "mooring_house": table((3, 6, [
        item("minecraft:magma_cream", 15, 1, 3), item("minecraft:warped_fungus", 12, 1, 3), item("minecraft:string", 15, 2, 6),
        item("minecraft:basalt", 12, 3, 10), item("minecraft:quartz", 12, 2, 6), item("minecraft:fire_charge", 10, 1, 3),
        item("minecraft:iron_chain", 10, 1, 4), item("minecraft:obsidian", 4, 1, 2), item("minecraft:gold_nugget", 6, 1, 5)])),
    "mooring_dock": table((3, 5, [
        item("minecraft:saddle", 8), item("minecraft:warped_fungus_on_a_stick", 10), item("minecraft:warped_fungus", 20, 2, 6),
        item("minecraft:lead", 10), item("minecraft:string", 15, 2, 5), item("minecraft:magma_cream", 10, 1, 3),
        item("minecraft:iron_chain", 10, 2, 5)])),
    "loom_house": table((3, 6, [
        item("minecraft:string", 30, 2, 8), item("minecraft:twisting_vines", 15, 2, 6), item("minecraft:warped_fungus", 12, 1, 3),
        item("minecraft:cyan_wool", 12, 1, 4), item("minecraft:light_blue_wool", 8, 1, 3), item("minecraft:orange_wool", 6, 1, 3),
        item("minecraft:shroomlight", 6, 1, 2), item("minecraft:cobweb", 6, 1, 2), item("minecraft:bone", 8, 1, 4)])),
    "loom_dyes": table((3, 6, [
        item("minecraft:cyan_dye", 20, 2, 6), item("minecraft:light_blue_dye", 12, 2, 5), item("minecraft:orange_dye", 12, 2, 5),
        item("minecraft:purple_dye", 8, 1, 4), item("minecraft:black_dye", 8, 1, 4), item("minecraft:white_dye", 8, 1, 4),
        item("minecraft:shroomlight", 8, 1, 2), item("minecraft:bone_meal", 10, 2, 6), item("minecraft:string", 12, 2, 6)])),
    "eave_house": table((3, 6, [
        item("minecraft:white_candle", 18, 1, 3), item("minecraft:light_blue_candle", 10, 1, 2), item("minecraft:string", 12, 2, 5),
        item("minecraft:quartz", 15, 2, 6), item("minecraft:soul_lantern", 8), item("minecraft:white_wool", 10, 1, 3),
        item("minecraft:glass_bottle", 12, 1, 4), item("minecraft:paper", 8, 1, 4), item("minecraft:basalt", 10, 2, 6)])),
    "eave_tears": table((2, 5, [
        item("minecraft:ghast_tear", 20, 1, 2), item("minecraft:potion", 15, extra=potion("minecraft:regeneration")),
        item("minecraft:potion", 12, extra=potion("minecraft:healing")), item("minecraft:potion", 6, extra=potion("minecraft:strong_healing")),
        item("minecraft:glass_bottle", 18, 1, 4), item("minecraft:light_blue_candle", 10, 1, 2), item("minecraft:glowstone_dust", 10, 2, 4)])),
}
for name, t in LOOT.items():
    write(os.path.join(DATA, NS, "loot_table", "chests", name + ".json"), t)

# -- creature drops ----------------------------------------------------------------------------------------
def drops(*entries):
    """An entity loot table: each entry (item, min, max) rolls once, adults only."""
    return {"type": "minecraft:entity", "pools": [{
        "rolls": 1,
        "entries": [item(name, 1, lo, hi)],
        **({"conditions": [{"condition": "minecraft:random_chance", "chance": chance}]} if chance < 1 else {}),
    } for (name, lo, hi, chance) in entries]}


CREATURE_DROPS = {
    "cairnback": drops(("minecraft:bone", 1, 3, 1), ("minecraft:bone_meal", 1, 2, 0.5)),
    "silkgrub": drops(("minecraft:string", 0, 2, 1)),
    "ashbell": drops(("minecraft:glowstone_dust", 0, 1, 1)),
}
for name, t in CREATURE_DROPS.items():
    write(os.path.join(DATA, NS, "loot_table", "entities", name + ".json"), t)

# -- entity tags ------------------------------------------------------------------------------------------
write(os.path.join(DATA, "minecraft", "tags", "entity_type", "fall_damage_immune.json"), {"values": [f"{NS}:ghastkin"]})
# the Wither leaves its own people alone
write(os.path.join(DATA, "minecraft", "tags", "entity_type", "wither_friends.json"), {"values": [f"{NS}:withren"]})
write(os.path.join(DATA, NS, "tags", "entity_type", "people.json"),
      {"values": ["minecraft:piglin", "minecraft:piglin_brute", f"{NS}:withren", f"{NS}:magmari", f"{NS}:weft", f"{NS}:ghastkin"]})

# -- spawn eggs and names ---------------------------------------------------------------------------------
EGGS = {
    "withren": ("#3b3a40", "#5ad5e0", "#d9d2bf"),     # ash, soul cyan, bone
    "magmari": ("#3a2420", "#ff7a1a", "#ffd166"),     # basalt hide, magma seams
    "weft": ("#262b52", "#17958c", "#ffb84a"),        # warped bark, teal weave, glowing eyes
    "ghastkin": ("#ebe8e2", "#a9c4cf", "#5e5b60"),    # ghast white, tear blue, ash silk
    "cairnback": ("#e3dcc6", "#5a4d45", "#6fe3f0"),   # bone plates, scaly hide, soul fire
    "silkgrub": ("#178c86", "#d9f2ee", "#ff9a3c"),    # teal hide, silk, shroomlight spots
    "ashbell": ("#d8d3e0", "#a9a4b8", "#bfe9f5"),     # smoke-pale bell, threads, glowing beads
}
for i, (mob, (base, spots, accent)) in enumerate(EGGS.items()):
    spawn_egg(mob, base, spots, accent, seed=11 + i)
    write(os.path.join(ASSETS, "items", mob + "_spawn_egg.json"),
          {"model": {"type": "minecraft:model", "model": f"{NS}:item/{mob}_spawn_egg"}})
    write(os.path.join(ASSETS, "models", "item", mob + "_spawn_egg.json"),
          {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{mob}_spawn_egg"}})

LANG = {
    "entity.netherfriends.withren": "Withren",
    "entity.netherfriends.magmari": "Magmari",
    "entity.netherfriends.weft": "Weft",
    "entity.netherfriends.ghastkin": "Ghastkin",
    "item.netherfriends.withren_spawn_egg": "Withren Spawn Egg",
    "item.netherfriends.magmari_spawn_egg": "Magmari Spawn Egg",
    "item.netherfriends.weft_spawn_egg": "Weft Spawn Egg",
    "item.netherfriends.ghastkin_spawn_egg": "Ghastkin Spawn Egg",
    "entity.netherfriends.cairnback": "Cairnback",
    "entity.netherfriends.silkgrub": "Silkgrub",
    "entity.netherfriends.ashbell": "Ashbell",
    "item.netherfriends.cairnback_spawn_egg": "Cairnback Spawn Egg",
    "item.netherfriends.silkgrub_spawn_egg": "Silkgrub Spawn Egg",
    "item.netherfriends.ashbell_spawn_egg": "Ashbell Spawn Egg",
}
write(os.path.join(ASSETS, "lang", "en_us.json"), LANG)
print("data written")
