package dev.netherexpansion.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import dev.netherexpansion.NetherExpansion;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/** The peoples' entity types, each with default attributes and a spawn egg. */
public final class NetherEntities {
    private NetherEntities() {}

    public record Entry(String id, EntityType<? extends Resident> type, Supplier<AttributeSupplier.Builder> attributes) {}

    private static final List<Entry> ALL = new ArrayList<>();
    private static final List<Item> EGGS = new ArrayList<>();

    public static final EntityType<Withren> WITHREN = register("withren", Withren::new, Withren::createAttributes,
            b -> b.sized(0.6F, 1.95F).eyeHeight(1.62F).fireImmune());
    public static final EntityType<Magmari> MAGMARI = register("magmari", Magmari::new, Magmari::createAttributes,
            b -> b.sized(0.8F, 2.7F).eyeHeight(2.35F).fireImmune());
    public static final EntityType<Weft> WEFT = register("weft", Weft::new, Weft::createAttributes,
            b -> b.sized(0.7F, 2.25F).eyeHeight(1.95F));
    public static final EntityType<Ghastkin> GHASTKIN = register("ghastkin", Ghastkin::new, Ghastkin::createAttributes,
            b -> b.sized(0.7F, 2.1F).eyeHeight(1.8F).fireImmune());

    private static <T extends Resident> EntityType<T> register(String id, EntityType.EntityFactory<T> factory,
                                                               Supplier<AttributeSupplier.Builder> attributes,
                                                               UnaryOperator<EntityType.Builder<T>> edit) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NetherExpansion.id(id));
        EntityType.Builder<T> builder = edit.apply(EntityType.Builder.of(factory, MobCategory.MISC).clientTrackingRange(10));
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        ALL.add(new Entry(id, type, attributes));
        return type;
    }

    public static List<Entry> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Attributes and a spawn egg for every people. */
    public static void init() {
        for (Entry e : ALL) {
            FabricDefaultAttributeRegistry.register(e.type(), e.attributes().get());
            ResourceKey<Item> egg = ResourceKey.create(Registries.ITEM, NetherExpansion.id(e.id() + "_spawn_egg"));
            EGGS.add(Registry.register(BuiltInRegistries.ITEM, egg, new SpawnEggItem(new Item.Properties().setId(egg).spawnEgg(e.type()))));
        }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(out -> EGGS.forEach(out::accept));
    }
}
