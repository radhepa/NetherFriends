package dev.netherfriends.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import dev.netherfriends.NetherFriends;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * The peoples' entity types and their three creatures (the Withren's Cairnback, the Weft's Silkgrub, the Ghastkin's
 * Ashbell), each with default attributes and a spawn egg. The creatures also spawn in the wild, each in its people's
 * biome.
 */
public final class NetherEntities {
    private NetherEntities() {}

    public record Entry(String id, EntityType<? extends Resident> type, Supplier<AttributeSupplier.Builder> attributes) {}

    /** A creature: its type, attributes, spawn rule and the biome it lives in. */
    public record CreatureEntry<T extends Mob>(String id, EntityType<T> type, Supplier<AttributeSupplier.Builder> attributes,
                                               SpawnPlacements.SpawnPredicate<T> spawnRule, ResourceKey<Biome> biome, int weight,
                                               int minGroup, int maxGroup) {}

    private static final List<Entry> ALL = new ArrayList<>();
    private static final List<CreatureEntry<?>> CREATURES = new ArrayList<>();
    private static final List<Item> EGGS = new ArrayList<>();

    public static final EntityType<Withren> WITHREN = register("withren", Withren::new, Withren::createAttributes,
            b -> b.sized(0.6F, 1.95F).eyeHeight(1.62F).fireImmune());
    public static final EntityType<Magmari> MAGMARI = register("magmari", Magmari::new, Magmari::createAttributes,
            b -> b.sized(0.8F, 2.7F).eyeHeight(2.35F).fireImmune());
    public static final EntityType<Weft> WEFT = register("weft", Weft::new, Weft::createAttributes,
            b -> b.sized(0.7F, 2.25F).eyeHeight(1.95F));
    public static final EntityType<Ghastkin> GHASTKIN = register("ghastkin", Ghastkin::new, Ghastkin::createAttributes,
            b -> b.sized(0.7F, 2.1F).eyeHeight(1.8F).fireImmune());

    public static final EntityType<Cairnback> CAIRNBACK = creature("cairnback", Cairnback::new, Cairnback::createAttributes,
            b -> b.sized(1.2F, 1.1F).eyeHeight(0.7F).fireImmune(), Cairnback::checkSpawnRules, Biomes.SOUL_SAND_VALLEY, 6, 1, 2);
    public static final EntityType<Silkgrub> SILKGRUB = creature("silkgrub", Silkgrub::new, Silkgrub::createAttributes,
            b -> b.sized(0.7F, 0.55F).eyeHeight(0.35F), Silkgrub::checkSpawnRules, Biomes.WARPED_FOREST, 8, 2, 4);
    public static final EntityType<Ashbell> ASHBELL = creature("ashbell", Ashbell::new, Ashbell::createAttributes,
            b -> b.sized(0.8F, 1.25F).eyeHeight(0.9F).fireImmune(), Ashbell::checkSpawnRules, Biomes.BASALT_DELTAS, 5, 1, 3);

    /**
     * The Magmari's strider barge: made by the ferry for a crossing, never summoned. Not saved either, but by the barge
     * itself (Barge.shouldBeSaved): a type marked noSave can't be ridden.
     */
    public static final EntityType<Barge> BARGE = barge();

    private static EntityType<Barge> barge() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NetherFriends.id("barge"));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, EntityType.Builder.<Barge>of(Barge::new, MobCategory.MISC)
                .sized(3.0F, 0.6F).clientTrackingRange(10).updateInterval(1).fireImmune().noSummon().build(key));
    }

    private static <T extends Mob> EntityType<T> creature(String id, EntityType.EntityFactory<T> factory,
                                                          Supplier<AttributeSupplier.Builder> attributes,
                                                          UnaryOperator<EntityType.Builder<T>> edit, SpawnPlacements.SpawnPredicate<T> rule,
                                                          ResourceKey<Biome> biome, int weight, int minGroup, int maxGroup) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NetherFriends.id(id));
        EntityType.Builder<T> builder = edit.apply(EntityType.Builder.of(factory, MobCategory.CREATURE).clientTrackingRange(10));
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        CREATURES.add(new CreatureEntry<>(id, type, attributes, rule, biome, weight, minGroup, maxGroup));
        return type;
    }

    private static <T extends Resident> EntityType<T> register(String id, EntityType.EntityFactory<T> factory,
                                                               Supplier<AttributeSupplier.Builder> attributes,
                                                               UnaryOperator<EntityType.Builder<T>> edit) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NetherFriends.id(id));
        EntityType.Builder<T> builder = edit.apply(EntityType.Builder.of(factory, MobCategory.MISC).clientTrackingRange(10));
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        ALL.add(new Entry(id, type, attributes));
        return type;
    }

    public static List<Entry> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static List<CreatureEntry<?>> creatures() {
        return Collections.unmodifiableList(CREATURES);
    }

    /** Attributes and a spawn egg for every people and creature; the creatures' wild spawns. */
    public static void init() {
        for (Entry e : ALL) {
            FabricDefaultAttributeRegistry.register(e.type(), e.attributes().get());
            egg(e.id(), e.type());
        }
        for (CreatureEntry<?> c : CREATURES) {
            FabricDefaultAttributeRegistry.register(c.type(), c.attributes().get());
            egg(c.id(), c.type());
            placement(c);
            BiomeModifications.addSpawn(BiomeSelectors.includeByKey(c.biome()), MobCategory.CREATURE, c.type(), c.weight(), c.minGroup(), c.maxGroup());
        }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(out -> EGGS.forEach(out::accept));
    }

    private static void egg(String id, EntityType<? extends Mob> type) {
        ResourceKey<Item> egg = ResourceKey.create(Registries.ITEM, NetherFriends.id(id + "_spawn_egg"));
        EGGS.add(Registry.register(BuiltInRegistries.ITEM, egg, new SpawnEggItem(new Item.Properties().setId(egg).spawnEgg(type))));
    }

    private static <T extends Mob> void placement(CreatureEntry<T> c) {
        SpawnPlacements.register(c.type(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, c.spawnRule());
    }
}
