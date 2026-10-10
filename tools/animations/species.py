"""Which peoples have animation packs, the models (looks) each pack must fit, and the tags clips may use."""
MOD_ID = "netherfriends"
MIN_CLIPS = 50
GEOMETRY_DIRS = ["src/client/resources/assets/netherfriends/geometry", "tools/design/reference"]

SPECIES = {
    "piglin": {"name": "Piglins", "looks": ["piglin", "piglin_baby", "piglin_brute"]},
    "withren": {"name": "Withren", "looks": ["withren", "withren_remembrancer", "withren_ashwarden", "withren_child"]},
    "magmari": {"name": "Magmari", "looks": ["magmari", "magmari_female", "magmari_ferrier", "magmari_child"]},
    "weft": {"name": "Weft", "looks": ["weft", "weft_female", "weft_child"]},
    "ghastkin": {"name": "Ghastkin", "looks": ["ghastkin", "ghastkin_female", "ghastkin_child"]},
}

JOBS = {
    "piglin": ["bartermaster", "hunter", "forager", "smelter", "goldsmith", "hoglin_herder", "brute", "bastion_dweller", "wanderer"],
    "withren": ["remembrancer", "ashwarden", "lantern_keeper", "wart_grower", "bonecarver", "brewer"],
    "magmari": ["ferrier", "hearthkeeper", "lava_angler", "chainwright", "quartzcutter", "chartmaker"],
    "weft": ["clothkeeper", "weaver", "spinner", "canopy_tender", "dyer", "netcaster"],
    "ghastkin": ["tearkeeper", "songwarden", "mender", "ashsweeper", "drift_watcher"],
}

# adult/child: age; social: a chat partner is near; talking: in a conversation with the player;
# job:<job>: their job (children: job:child); male/female (not Withren, not Piglins); cold: a Magmari away from heat;
# brute: a Piglin Brute
TAGS = {
    "common": {"adult", "child", "social", "talking", "job:child"},
    "piglin": {"brute"},
    "magmari": {"cold", "male", "female"},
    "weft": {"male", "female"},
    "ghastkin": {"male", "female"},
}
for _s, _jobs in JOBS.items():
    TAGS[_s] = set(TAGS.get(_s, ())) | {"job:" + j for j in _jobs}
