package dev.netherfriends.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.jspecify.annotations.Nullable;

/**
 * The five peoples of the Nether. Piglins are vanilla mobs with new behaviour; the other four are this
 * mod's {@link Resident}s. No two peoples share a village or get along (see docs/PEOPLE.md).
 */
public enum People {
    PIGLIN("Piglins", true),
    WITHREN("Withren", false),
    MAGMARI("Magmari", true),
    WEFT("Weft", true),
    GHASTKIN("Ghastkin", true);

    public final String plural;
    /** Male and female like Village Friends; the Withren are genderless. */
    public final boolean gendered;

    People(String plural, boolean gendered) {
        this.plural = plural;
        this.gendered = gendered;
    }

    /** Which people an entity belongs to, or null for anyone else (players, animals, monsters). */
    public static @Nullable People of(@Nullable Entity entity) {
        if (entity instanceof Resident r) return r.people();
        if (entity instanceof AbstractPiglin) return PIGLIN;
        return null;
    }

    /** True when the two belong to different peoples, which in the Nether means they are enemies. */
    public static boolean enemies(@Nullable Entity a, @Nullable Entity b) {
        People pa = of(a), pb = of(b);
        return pa != null && pb != null && pa != pb;
    }
}
