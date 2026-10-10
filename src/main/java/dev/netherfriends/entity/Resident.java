package dev.netherfriends.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import dev.netherfriends.talk.Talk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * One of the Nether's people (everyone but the Piglins, who stay vanilla mobs). A resident has a gender
 * (none for the Withren), a role in its village, and a home: the village centre it keeps near. Children
 * stay children, so a village keeps its young. Residents never despawn.
 */
public abstract class Resident extends AgeableMob {
    public static final int MALE = 0, FEMALE = 1, NONE = 2;
    private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(Resident.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ROLE = SynchedEntityData.defineId(Resident.class, EntityDataSerializers.INT);
    /** How far from its village centre a resident wanders when no radius was given. */
    public static final int DEFAULT_HOME_RADIUS = 28;

    protected Resident(EntityType<? extends Resident> type, Level level) {
        super(type, level);
    }

    public abstract People people();

    /** Health, speed and reach every resident shares; each people adjusts its own. */
    public static AttributeSupplier.Builder createResidentAttributes() {
        return AgeableMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GENDER, MALE);
        builder.define(ROLE, 0);
    }

    // -- who they are ------------------------------------------------------------------------------------

    public int gender() { return this.entityData.get(GENDER); }
    public boolean isFemale() { return this.gender() == FEMALE; }

    public void setGender(int gender) {
        this.entityData.set(GENDER, this.people().gendered ? (gender == FEMALE ? FEMALE : MALE) : NONE);
    }

    /** The resident's job; each people numbers its own roles (0 is always an ordinary resident). */
    public int role() { return this.entityData.get(ROLE); }
    public void setRole(int role) { this.entityData.set(ROLE, role); }

    /** Make this resident live in the village around {@code centre}, staying within {@code radius} blocks. */
    public void settle(BlockPos centre, int radius) {
        this.setHomeTo(centre, radius);
        this.setPersistenceRequired();
    }

    /** Right-click to talk (see {@link Talk}); a name tag or spawn egg still does what it always does. */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || player.isSpectator() || held.is(Items.NAME_TAG) || held.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(player, hand);
        }
        if (player instanceof ServerPlayer server) Talk.open(server, this);
        return InteractionResult.SUCCESS;
    }

    // -- goals ----------------------------------------------------------------------------------------------

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if (this.avoidsEnemies()) {
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, LivingEntity.class, 10.0F, 1.0, 1.25,
                    e -> People.enemies(this, e)));
        }
        this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    /** Everyone keeps clear of the other peoples except those whose job is to fight them. */
    protected boolean avoidsEnemies() {
        return true;
    }

    // -- life -------------------------------------------------------------------------------------------------

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData data) {
        this.setGender(this.random.nextBoolean() ? MALE : FEMALE);
        if (!this.hasHome()) this.setHomeTo(this.blockPosition(), DEFAULT_HOME_RADIUS);
        // AgeableMob would make every other spawn in a group a baby; children are chosen by the village instead
        return super.finalizeSpawn(level, difficulty, reason, data == null ? new AgeableMob.AgeableMobGroupData(false) : data);
    }

    /** Children stay children, so a village keeps its young. */
    @Override
    public boolean canAgeUp() {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Gender", this.gender());
        output.putInt("Role", this.role());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setGender(input.getIntOr("Gender", MALE));
        this.setRole(input.getIntOr("Role", 0));
    }
}
