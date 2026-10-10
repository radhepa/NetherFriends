package dev.netherexpansion.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The Magmari: tall wading people of the lava seas, with molten seams in their stony hide. They walk on
 * lava as striders do and live on Moorings out on the open sea. Peaceful toward the player. Kept away from
 * heat for long, their seams go dark and they turn "cold" (grey, slow, unhappy) until they warm again.
 */
public class Magmari extends Resident {
    public static final int RESIDENT = 0, FERRIER = 1;
    private static final EntityDataAccessor<Boolean> COLD = SynchedEntityData.defineId(Magmari.class, EntityDataSerializers.BOOLEAN);
    /** Seconds away from heat before the seams go dark. */
    static final int COOLING_SECONDS = 45;
    /** Seconds of warmth since the last check; counts down away from heat. */
    private int warmth = COOLING_SECONDS;

    public Magmari(EntityType<? extends Magmari> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, 0.0F);
        this.setPathfindingMalus(PathType.FIRE, 0.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    public People people() {
        return People.MAGMARI;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createResidentAttributes()
                .add(Attributes.MAX_HEALTH, 34.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLD, false);
    }

    public boolean isCold() { return this.entityData.get(COLD); }
    public void setCold(boolean cold) { this.entityData.set(COLD, cold); }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.7));
    }

    // -- lava -------------------------------------------------------------------------------------------------

    @Override
    public boolean canStandOnFluid(FluidState fluid) {
        return fluid.is(FluidTags.LAVA);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new LavaWalkerNavigation(this, level);
    }

    /** They like the lava a little better than the deck, so they wade about the Mooring. */
    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).getFluidState().is(FluidTags.LAVA) ? 2.0F : 0.0F;
    }

    // -- warmth -----------------------------------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            if (this.isNearHeat()) {
                this.warmth = Math.min(COOLING_SECONDS, this.warmth + 5);
            } else if (this.warmth > 0) {
                this.warmth--;
            }
            boolean cold = this.warmth <= 0;
            if (cold != this.isCold()) {
                this.setCold(cold);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(cold ? 0.18 : 0.26);
                this.playSound(cold ? SoundEvents.FIRE_EXTINGUISH : SoundEvents.BLAZE_SHOOT, 0.4F, cold ? 0.7F : 1.4F);
            }
        }
    }

    /** Lava, magma, fire or a lit campfire within a few blocks (standing in lava counts). */
    public boolean isNearHeat() {
        if (this.isInLava()) return true;
        BlockPos at = this.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(at.offset(-3, -2, -3), at.offset(3, 2, 3))) {
            BlockState state = this.level().getBlockState(p);
            if (state.getFluidState().is(FluidTags.LAVA) || state.is(Blocks.MAGMA_BLOCK) || state.is(BlockTags.FIRE)
                    || state.is(Blocks.LAVA_CAULDRON) || state.is(BlockTags.CAMPFIRES)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Warmth", this.warmth);
        output.putBoolean("Cold", this.isCold());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.warmth = input.getIntOr("Warmth", COOLING_SECONDS);
        this.setCold(input.getBooleanOr("Cold", false));
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.CAMPFIRE_CRACKLE; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.MAGMA_CUBE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.MAGMA_CUBE_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 0.25F, 0.7F);
    }
    @Override protected float getSoundVolume() { return 0.6F; }
    @Override public float getVoicePitch() { return (this.isBaby() ? 1.4F : 0.75F) * (this.isCold() ? 0.8F : 1.0F) + this.random.nextFloat() * 0.1F; }

    /** Ground navigation that treats lava as solid footing, as a strider's does. */
    static class LavaWalkerNavigation extends GroundPathNavigation {
        LavaWalkerNavigation(Magmari mob, Level level) {
            super(mob, level);
        }

        @Override
        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }

        @Override
        public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).is(Blocks.LAVA) || super.isStableDestination(pos);
        }
    }
}
