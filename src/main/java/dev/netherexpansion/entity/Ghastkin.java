package dev.netherexpansion.entity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The Ghastkin: pale floating mourners of the Basalt Deltas, kin to the ghasts (a Ghastkin who grieves too
 * long swells into one). They drift a little above the ground on six tentacles and live in Eaves hung under
 * the basalt pillars. Shy and peaceful: anyone who comes at them without sneaking sees them drift up out of
 * reach. They never fight.
 */
public class Ghastkin extends Resident {
    /** How close a player who isn't sneaking can come before a Ghastkin drifts away. */
    public static final double SHY_DISTANCE = 6.0;
    /** How high above the floor they like to hover (blocks). */
    static final double HOVER = 0.9;
    private int frightened;

    public Ghastkin(EntityType<? extends Ghastkin> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl<>(this, 10, true);
        this.setNoGravity(true);
    }

    @Override
    public People people() {
        return People.GHASTKIN;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createResidentAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.18);
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
        super.registerGoals();
        this.goalSelector.addGoal(1, new DriftAwayGoal());
        this.goalSelector.addGoal(7, new HoverWanderGoal());
    }

    @Override
    public void travel(Vec3 input) {
        this.travelFlying(input, this.getSpeed());
    }

    @Override
    protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /** A Ghastkin is frightened for a few seconds after being hurt or rushed. */
    public boolean isFrightened() {
        return this.frightened > 0;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt) this.frightened = 100;
        return hurt;
    }

    /** The nearest player who is close and not sneaking (creative players frighten them too). */
    @Nullable Player rusher() {
        Player nearest = this.level().getNearestPlayer(this, SHY_DISTANCE);
        if (nearest == null || nearest.isSpectator() || nearest.isDiscrete()) return null;
        // the one they're talking to stood up when the window opened; that's not rushing
        if (nearest.getUUID().equals(dev.netherexpansion.talk.Talk.listeningTo(this))) return null;
        return nearest;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.frightened > 0) this.frightened--;
        this.setNoGravity(true);
        // settle gently back toward hovering height when nothing is moving them
        if (!this.level().isClientSide() && !this.isFrightened() && this.getNavigation().isDone()) {
            double gap = this.heightAboveFloor(6);
            if (gap > HOVER + 0.4) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.012, 0));
            } else if (gap < HOVER - 0.3) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02, 0));
            }
        }
    }

    /** Blocks of air below the feet (up to {@code max}); {@code max} when there's no floor that close. */
    double heightAboveFloor(int max) {
        BlockPos.MutableBlockPos p = this.blockPosition().mutable();
        for (int i = 0; i <= max; i++) {
            BlockState state = this.level().getBlockState(p);
            VoxelShape shape = state.getCollisionShape(this.level(), p);
            if (!shape.isEmpty()) return this.getY() - (p.getY() + shape.max(Direction.Axis.Y));
            if (!state.getFluidState().isEmpty()) return this.getY() - (p.getY() + state.getFluidState().getHeight(this.level(), p));
            p.move(Direction.DOWN);
        }
        return max;
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.GHAST_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GHAST_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GHAST_DEATH; }
    @Override protected float getSoundVolume() { return 0.18F; }
    @Override public float getVoicePitch() { return (this.isBaby() ? 2.0F : 1.6F) + this.random.nextFloat() * 0.15F; }
    @Override public int getAmbientSoundInterval() { return 320; }

    /** Rushed or hurt: drift up and away, out of reach, until the fright passes. */
    class DriftAwayGoal extends Goal {
        private int retarget;

        DriftAwayGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (Ghastkin.this.rusher() != null) Ghastkin.this.frightened = Math.max(Ghastkin.this.frightened, 60);
            return Ghastkin.this.isFrightened();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.retarget = 0;
        }

        @Override
        public void tick() {
            if (--this.retarget > 0) return;
            this.retarget = 15;
            Ghastkin g = Ghastkin.this;
            Player from = g.level().getNearestPlayer(g, 12);
            Vec3 away = from == null ? Vec3.ZERO : g.position().subtract(from.position()).multiply(1, 0, 1).normalize().scale(4);
            BlockPos up = BlockPos.containing(g.getX() + away.x, g.getY() + 4, g.getZ() + away.z);
            // stop under a ceiling rather than pressing into it
            while (up.getY() > g.getBlockY() && !g.level().getBlockState(up).isAir()) up = up.below();
            g.getNavigation().moveTo(up.getX() + 0.5, up.getY() + 0.2, up.getZ() + 0.5, 1.6);
        }
    }

    /** Drift to a nearby spot (inside the village) and hover just above its floor. */
    class HoverWanderGoal extends Goal {
        private int cooldown;

        HoverWanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (--this.cooldown > 0 || !Ghastkin.this.getNavigation().isDone()) return false;
            this.cooldown = 80 + Ghastkin.this.random.nextInt(120);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !Ghastkin.this.getNavigation().isDone();
        }

        @Override
        public void start() {
            Ghastkin g = Ghastkin.this;
            for (int tries = 0; tries < 10; tries++) {
                BlockPos from = g.hasHome() && !g.isWithinHome() ? g.getHomePosition() : g.blockPosition();
                int x = from.getX() + g.random.nextInt(17) - 8, z = from.getZ() + g.random.nextInt(17) - 8;
                int y = g.getBlockY() + g.random.nextInt(9) - 3;
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, y, z);
                if (!g.level().getBlockState(p).isAir()) continue;
                int drop = 0;
                while (drop < 14 && g.level().getBlockState(p.below()).isAir()) {
                    p.move(0, -1, 0);
                    drop++;
                }
                BlockState floor = g.level().getBlockState(p.below());
                if (drop >= 14 || !floor.getFluidState().isEmpty() && !floor.isSolid()) continue;
                if (!g.level().getBlockState(p.above()).isAir() || !g.level().getBlockState(p.above(2)).isAir()) continue;
                if (g.hasHome() && !g.isWithinHome(p)) continue;
                g.getNavigation().moveTo(p.getX() + 0.5, p.getY() + HOVER, p.getZ() + 0.5, 1.0);
                return;
            }
        }
    }
}
