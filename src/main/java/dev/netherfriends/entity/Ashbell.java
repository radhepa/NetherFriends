package dev.netherfriends.entity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * The Ghastkin's beast: a pale drifter of the Basalt Deltas, like a jellyfish of the air, ringing faintly as it
 * goes. Harmless; it can be led on a lead. A ghast that hears one remembers being kin and holds its fire: no ghast
 * targets anyone within {@link #CALM} blocks of an Ashbell (see {@link #soothes} and MobMixin).
 */
public class Ashbell extends PathfinderMob {
    /** How near an Ashbell you must be for ghasts to leave you alone. */
    public static final double CALM = 16.0;
    /** The height over the floor it likes to drift at. */
    static final double HOVER = 2.5;

    public Ashbell(EntityType<? extends Ashbell> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl<>(this, 6, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.FLYING_SPEED, 0.1)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    /** Basalt or blackstone underfoot: the deltas. */
    public static boolean checkSpawnRules(EntityType<Ashbell> type, LevelAccessor level, EntitySpawnReason reason, BlockPos pos,
                                         RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.BASALT) || below.is(Blocks.BLACKSTONE) || below.is(Blocks.SMOOTH_BASALT);
    }

    /** True when an Ashbell is close enough to {@code target} that ghasts won't fire at it. */
    public static boolean soothes(Entity target) {
        return !target.level().getEntitiesOfClass(Ashbell.class, target.getBoundingBox().inflate(CALM), Entity::isAlive).isEmpty();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6));
        this.goalSelector.addGoal(5, new DriftGoal());
    }

    @Override
    public void travel(Vec3 input) {
        this.travelFlying(input, this.getSpeed());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
        // ghasts already firing at someone near it lose interest
        if (this.level() instanceof ServerLevel level && this.tickCount % 10 == 0) {
            for (Ghast ghast : level.getEntitiesOfClass(Ghast.class, this.getBoundingBox().inflate(80))) {
                LivingEntity target = ghast.getTarget();
                if (target != null && target.distanceToSqr(this) < CALM * CALM) ghast.setTarget(null);
            }
        }
        // a slow bob, a little lift whenever it's sinking too near the floor
        if (!this.level().isClientSide() && this.getNavigation().isDone() && !this.isLeashed()) {
            double gap = this.heightAboveFloor(6);
            if (gap < HOVER - 0.6) this.setDeltaMovement(this.getDeltaMovement().add(0, 0.015, 0));
            else if (gap > HOVER + 1.5) this.setDeltaMovement(this.getDeltaMovement().add(0, -0.008, 0));
        }
    }

    /** Blocks of air below it (up to {@code max}). */
    double heightAboveFloor(int max) {
        BlockPos.MutableBlockPos p = this.blockPosition().mutable();
        for (int i = 0; i <= max; i++) {
            BlockState state = this.level().getBlockState(p);
            if (!state.getCollisionShape(this.level(), p).isEmpty() || !state.getFluidState().isEmpty()) return this.getY() - (p.getY() + 1);
            p.move(0, -1, 0);
        }
        return max;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override protected @Nullable SoundEvent getAmbientSound() { return SoundEvents.AMETHYST_BLOCK_CHIME; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.AMETHYST_BLOCK_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.AMETHYST_BLOCK_BREAK; }
    @Override protected float getSoundVolume() { return 0.5F; }
    @Override public float getVoicePitch() { return 0.6F + this.random.nextFloat() * 0.3F; }
    @Override public int getAmbientSoundInterval() { return 160; }

    /** Drift to a nearby spot over open ground and hang there a while. */
    class DriftGoal extends Goal {
        private int cooldown;

        DriftGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (Ashbell.this.isLeashed() || --this.cooldown > 0 || !Ashbell.this.getNavigation().isDone()) return false;
            this.cooldown = 60 + Ashbell.this.random.nextInt(140);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !Ashbell.this.getNavigation().isDone();
        }

        @Override
        public void start() {
            Ashbell a = Ashbell.this;
            for (int tries = 0; tries < 10; tries++) {
                BlockPos from = a.hasHome() && !a.isWithinHome() ? a.getHomePosition() : a.blockPosition();
                int x = from.getX() + a.random.nextInt(15) - 7, z = from.getZ() + a.random.nextInt(15) - 7;
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, a.getBlockY() + a.random.nextInt(7) - 3, z);
                if (!a.level().getBlockState(p).isAir()) continue;
                int drop = 0;
                while (drop < 16 && a.level().getBlockState(p.below()).isAir()) {
                    p.move(0, -1, 0);
                    drop++;
                }
                if (drop >= 16 || !a.level().getFluidState(p.below()).isEmpty()) continue;
                int up = 2 + a.random.nextInt(3);
                boolean open = true;
                for (int i = 1; i <= up + 1; i++) open &= a.level().getBlockState(p.above(i)).isAir();
                if (!open) continue;
                p.move(0, up, 0);
                if (a.hasHome() && !a.isWithinHome(p)) continue;
                a.getNavigation().moveTo(p.getX() + 0.5, p.getY() + 0.3, p.getZ() + 0.5, 1.0);
                return;
            }
        }
    }
}
