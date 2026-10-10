package dev.netherfriends.talk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.People;
import dev.netherfriends.entity.Resident;
import dev.netherfriends.entity.Withren;
import dev.netherfriends.world.Names;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

/**
 * Who a resident is: their name and the rest of it (a Piglin's clan, the dead a Withren keeps, a Magmari's
 * chain, a Weft's row, a Ghastkin's old name), their job, the house they live in and their village. A resident
 * a village spawns gets this the moment the world generates them, from the building they stand in; anyone
 * else (a Bastion Piglin, a spawn egg) gets it the first time someone talks to them.
 */
public final class Folk {
    private Folk() {}

    /**
     * {@code kin} is the people's own part of the name: clan, kept name, chain, row or former name. {@code place}
     * is "village", "bastion" or "wild".
     */
    public record Profile(String name, String kin, String job, int gender, Optional<BlockPos> home, Optional<BlockPos> village, String place) {
        public static final Codec<Profile> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("name").forGetter(Profile::name),
                Codec.STRING.optionalFieldOf("kin", "").forGetter(Profile::kin),
                Codec.STRING.fieldOf("job").forGetter(Profile::job),
                Codec.INT.optionalFieldOf("gender", Resident.MALE).forGetter(Profile::gender),
                BlockPos.CODEC.optionalFieldOf("home").forGetter(Profile::home),
                BlockPos.CODEC.optionalFieldOf("village").forGetter(Profile::village),
                Codec.STRING.optionalFieldOf("place", "wild").forGetter(Profile::place)
        ).apply(i, Profile::new));
    }

    public static final AttachmentType<Profile> PROFILE = AttachmentRegistry.create(NetherFriends.id("profile"),
            b -> b.persistent(Profile.CODEC));

    /** The job, synced to clients so the animations can follow it. Not saved: it is copied from the profile
     *  whenever a resident with one is given it or loads (see {@link #init}). */
    public static final AttachmentType<String> JOB = AttachmentRegistry.create(NetherFriends.id("job"),
            b -> b.syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.all()));

    public static final String CHILD = "Child";

    /** The jobs a resident can be given when nothing about where they live says otherwise. */
    static final Map<People, String[]> ORDINARY_JOBS = Map.of(
            People.PIGLIN, new String[]{"Hunter", "Forager", "Smelter"},
            People.WITHREN, new String[]{"Wart-grower", "Bonecarver", "Lantern-keeper", "Brewer"},
            People.MAGMARI, new String[]{"Lava-angler", "Chainwright", "Quartzcutter", "Chartmaker"},
            People.WEFT, new String[]{"Weaver", "Spinner", "Canopy-tender"},
            People.GHASTKIN, new String[]{"Mender", "Ashsweeper", "Drift-watcher"});

    /** Every job each people has, for the dialogue checker and anything else that needs the list. */
    public static final Map<People, List<String>> JOBS = Map.of(
            People.PIGLIN, List.of("Bartermaster", "Hunter", "Forager", "Smelter", "Goldsmith", "Hoglin-herder", "Brute", "Bastion-dweller", "Wanderer"),
            People.WITHREN, List.of("Remembrancer", "Ashwarden", "Lantern-keeper", "Wart-grower", "Bonecarver", "Brewer"),
            People.MAGMARI, List.of("Ferrier", "Hearthkeeper", "Lava-angler", "Chainwright", "Quartzcutter", "Chartmaker"),
            People.WEFT, List.of("Clothkeeper", "Weaver", "Spinner", "Canopy-tender", "Dyer", "Netcaster"),
            People.GHASTKIN, List.of("Tearkeeper", "Songwarden", "Mender", "Ashsweeper", "Drift-watcher"));

    /** The job (or choice of jobs, split by "|") each kind of building gives the residents it spawns. */
    static final Map<String, String> JOB_AT = Map.ofEntries(
            Map.entry("hold/ring", "Bartermaster"),
            Map.entry("hold/longhouse", "Hunter|Forager|Smelter"),
            Map.entry("hold/hut", "Forager|Hunter"),
            Map.entry("hold/forge", "Goldsmith"),
            Map.entry("hold/pen", "Hoglin-herder"),
            Map.entry("vigil/hall", "Lantern-keeper"),
            Map.entry("vigil/home", "Wart-grower|Bonecarver|Lantern-keeper"),
            Map.entry("vigil/brewery", "Brewer"),
            Map.entry("vigil/terrace", "Wart-grower"),
            Map.entry("mooring/hearth", "Hearthkeeper"),
            Map.entry("mooring/home", "Lava-angler|Chainwright|Quartzcutter|Chartmaker"),
            Map.entry("loom/great", "Weaver"),
            Map.entry("loom/tree", "Weaver|Spinner|Canopy-tender"),
            Map.entry("eave/hall", "Songwarden"),
            Map.entry("eave/pillar", "Mender|Ashsweeper|Drift-watcher"));

    public static @Nullable String jobAt(String kind) {
        return JOB_AT.get(kind);
    }

    // -- giving and reading profiles -------------------------------------------------------------------------

    /**
     * Give a resident spawned by a village its profile: {@code job} (a job, a choice of jobs split by "|", or null
     * for one of its people's ordinary jobs), the house it lives in and its village's centre. A role the resident
     * already has (Brute, Remembrancer, Ashwarden, Ferrier) wins over the building's job.
     */
    public static Profile settle(Mob mob, @Nullable String job, @Nullable BlockPos home, @Nullable BlockPos village) {
        People people = People.of(mob);
        if (people == null) throw new IllegalArgumentException("not one of the peoples: " + mob);
        Profile profile = make(mob, people, job, home, village, village != null ? "village" : "wild");
        mob.setAttached(PROFILE, profile);
        mob.setAttached(JOB, profile.job());
        name(mob, profile, people);
        return profile;
    }

    /** This resident's profile, made now if they never had one (a Bastion Piglin, a spawn egg). */
    /** Registers the attachments before any world (and its residents) loads, and syncs jobs as residents load. */
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            Profile profile = entity.getAttached(PROFILE);
            if (profile != null && !profile.job().equals(entity.getAttached(JOB))) entity.setAttached(JOB, profile.job());
        });
    }

    public static Profile of(LivingEntity entity) {
        Profile profile = entity.getAttached(PROFILE);
        if (profile != null) return profile;
        People people = People.of(entity);
        if (people == null) throw new IllegalArgumentException("not one of the peoples: " + entity);
        // anyone a village spawned already has a profile, so this one lives outside any village
        String place = "wild";
        if (people == People.PIGLIN && entity.level() instanceof ServerLevel level && level.structureManager()
                .getStructureWithPieceAt(entity.blockPosition(), holder -> holder.is(BuiltinStructures.BASTION_REMNANT)).isValid()) {
            place = "bastion";
        }
        profile = make(entity, people, null, null, null, place);
        entity.setAttached(PROFILE, profile);
        entity.setAttached(JOB, profile.job());
        name(entity, profile, people);
        return profile;
    }

    private static Profile make(LivingEntity entity, People people, @Nullable String job, @Nullable BlockPos home, @Nullable BlockPos village,
                                String place) {
        RandomSource random = RandomSource.create(entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits());
        boolean child = entity.isBaby();
        int gender = entity instanceof Resident r ? r.gender() : random.nextBoolean() ? Resident.FEMALE : Resident.MALE;
        boolean female = gender == Resident.FEMALE;
        String role = roleJob(entity);
        if (child) job = CHILD;
        else if (role != null) job = role;
        else if (job != null && job.contains("|")) {
            String[] jobs = job.split("\\|");
            job = jobs[random.nextInt(jobs.length)];
        }
        else if (job == null) job = defaultJob(people, place, random);
        long n = random.nextLong();
        String name = switch (people) {
            case PIGLIN -> Names.piglin(n, female);
            case WITHREN -> Names.withren(n);
            case MAGMARI -> Names.magmari(n, female);
            case WEFT -> Names.weft(n, female);
            case GHASTKIN -> Names.ghastkin(n, female);
        };
        String kin = switch (people) {
            // a household shares a clan
            case PIGLIN -> Names.clan(home != null ? Names.seed(home) : random.nextLong());
            // the kept are the names on the Vigil's own Name Wall
            case WITHREN -> child ? "" : village != null ? Names.wallName(village, random.nextInt(Names.WALL_SIZE)) : Names.withren(random.nextLong());
            // a Mooring shares a chain
            case MAGMARI -> Names.chain(village != null ? Names.seed(village) : random.nextLong());
            case WEFT -> Names.row(child ? Names.THIS_ROW - 1 - random.nextInt(12)
                    : job.equals("Clothkeeper") ? 1140 + random.nextInt(50) : 1190 + random.nextInt(106));
            case GHASTKIN -> child ? "" : Names.ghastkin(random.nextLong(), random.nextBoolean());
        };
        return new Profile(name, kin, job, gender, Optional.ofNullable(home), Optional.ofNullable(village), place);
    }

    private static @Nullable String roleJob(LivingEntity entity) {
        if (entity instanceof PiglinBrute) return "Brute";
        if (entity instanceof Withren w && w.role() == Withren.REMEMBRANCER) return "Remembrancer";
        if (entity instanceof Withren w && w.role() == Withren.ASHWARDEN) return "Ashwarden";
        if (entity instanceof Magmari m && m.role() == Magmari.FERRIER) return "Ferrier";
        return null;
    }

    private static String defaultJob(People people, String place, RandomSource random) {
        if (people == People.PIGLIN) return place.equals("bastion") ? "Bastion-dweller" : "Wanderer";
        String[] jobs = ORDINARY_JOBS.get(people);
        return jobs[random.nextInt(jobs.length)];
    }

    /** Show the name over their head when looked at (a name tag still renames them). */
    private static void name(LivingEntity entity, Profile profile, People people) {
        if (entity.getCustomName() == null) entity.setCustomName(Component.literal(people == People.PIGLIN ? fullName(profile, people) : profile.name()));
    }

    // -- how they're spoken of ------------------------------------------------------------------------------

    /** The name they go by in the conversation window: a name tag's name if they've been given one. */
    public static String name(LivingEntity entity) {
        Profile p = of(entity);
        Component custom = entity.getCustomName();
        People people = People.of(entity);
        if (custom != null && !custom.getString().equals(fullName(p, people)) && !custom.getString().equals(p.name())) return custom.getString();
        return p.name();
    }

    public static String fullName(Profile p, @Nullable People people) {
        return people == People.PIGLIN && !p.kin().isEmpty() ? p.name() + " " + p.kin() : p.name();
    }

    /** The rest of their name: "Red-Tusk", "who keeps Anwen", "of the Long Chain", "Row Twelve-Eighty", "who was Oriel". */
    public static String epithet(Profile p, People people, boolean child) {
        return switch (people) {
            case PIGLIN -> p.kin();
            case WITHREN -> child ? "who keeps no one yet" : "who keeps " + p.kin();
            case MAGMARI -> "of " + p.kin();
            case WEFT -> "Row " + p.kin();
            case GHASTKIN -> p.kin().isEmpty() ? "" : "who was " + p.kin();
        };
    }

    public static boolean female(LivingEntity entity) {
        if (entity instanceof Resident r) return r.isFemale();
        return of(entity).gender() == Resident.FEMALE;
    }

    /** Their village's name, or null when they have none. */
    public static @Nullable String village(Profile p, People people) {
        return p.village().map(v -> Names.village(people, v)).orElse(null);
    }

    /** The names of others of their people who share their village (or are nearby, for those with none). */
    public static List<String> neighbours(LivingEntity entity) {
        People people = People.of(entity);
        Profile mine = of(entity);
        List<String> names = new ArrayList<>();
        AABB box = mine.village().map(v -> new AABB(v).inflate(56, 32, 56)).orElse(entity.getBoundingBox().inflate(32));
        Class<? extends LivingEntity> kind = people == People.PIGLIN ? AbstractPiglin.class : Resident.class;
        for (LivingEntity other : entity.level().getEntitiesOfClass(kind, box, e -> e != entity && People.of(e) == people)) {
            Profile theirs = other.getAttached(PROFILE);
            if (theirs != null && theirs.village().equals(mine.village())) names.add(theirs.name());
        }
        return names;
    }
}
