package dev.netherfriends.entity;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * The Weft: four-armed weavers of the Warped Forest canopy. They climb the stems like spiders and live in
 * Looms on the giant fungus caps. Wary: leave their forest whole and they'll talk; cut it near a Loom and
 * every Weft around drops on you, and their blows tangle you in vine net (slowness).
 */
public class Weft extends Resident {
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(Weft.class, EntityDataSerializers.BOOLEAN);
    /** How long a Weft stays angry with someone who cut their forest (ticks). */
    public static final int GRUDGE_TICKS = 20 * 60;
    private @Nullable UUID grudge;
    private int grudgeTicks;

    public Weft(EntityType<? extends Weft> type, Level level) {
        super(type, level);
    }

    @Override
    public People people() {
        return People.WEFT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createResidentAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5) {
            @Override public boolean canUse() { return Weft.this.isBaby() && super.canUse(); }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true) {
            @Override public boolean canUse() { return !Weft.this.isBaby() && super.canUse(); }
        });
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Weft.class));
        this.targetSelector.addGoal(2, new GrudgeTargetGoal());
    }

    // -- climbing, as a spider climbs ---------------------------------------------------------------------

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) this.entityData.set(CLIMBING, this.horizontalCollision);
    }

    @Override
    public boolean onClimbable() {
        return this.entityData.get(CLIMBING);
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING);
    }

    // -- the grudge -------------------------------------------------------------------------------------------

    /** Someone cut the forest near this Weft's Loom: hold it against them for a minute. */
    public void holdGrudge(Player player) {
        if (this.isBaby() || player.isCreative() || player.isSpectator()) return;
        this.grudge = player.getUUID();
        this.grudgeTicks = GRUDGE_TICKS;
    }

    public boolean hasGrudgeAgainst(Entity entity) {
        return this.grudge != null && this.grudgeTicks > 0 && this.grudge.equals(entity.getUUID());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.grudgeTicks > 0 && --this.grudgeTicks == 0) {
            this.grudge = null;
            if (this.getTarget() instanceof Player) this.setTarget(null);
        }
    }

    /** Their blows wrap you in vine net. */
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), this);
        }
        return hit;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.grudge != null && this.grudgeTicks > 0) {
            output.putString("Grudge", this.grudge.toString());
            output.putInt("GrudgeTicks", this.grudgeTicks);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.grudge = input.getString("Grudge").map(UUID::fromString).orElse(null);
        this.grudgeTicks = input.getIntOr("GrudgeTicks", 0);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.SPIDER_STEP, 0.1F, 1.4F); }
    @Override protected float getSoundVolume() { return 0.35F; }
    @Override public float getVoicePitch() { return (this.isBaby() ? 2.0F : 1.55F) + this.random.nextFloat() * 0.1F; }
    @Override public int getAmbientSoundInterval() { return 200; }

    /** Targets whoever this Weft holds a grudge against, while they're in reach. */
    class GrudgeTargetGoal extends TargetGoal {
        GrudgeTargetGoal() {
            super(Weft.this, false);
        }

        @Override
        public boolean canUse() {
            if (Weft.this.grudge == null || Weft.this.grudgeTicks <= 0 || !(Weft.this.level() instanceof ServerLevel level)) return false;
            Player player = level.getPlayerByUUID(Weft.this.grudge);
            if (player == null || !player.isAlive() || player.isCreative() || player.isSpectator()
                    || player.distanceToSqr(Weft.this) > 32 * 32) {
                return false;
            }
            this.targetMob = player;
            return true;
        }

        @Override
        public void start() {
            Weft.this.setTarget(this.targetMob);
            super.start();
        }
    }
}
