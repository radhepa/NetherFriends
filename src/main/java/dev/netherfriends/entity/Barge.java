package dev.netherfriends.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import dev.netherfriends.world.Sea;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LinearInterpolationHandler;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * A Magmari strider barge: a raft of basalt slabs bound with chain, towed by a strider. It only exists for a
 * crossing (the ferry, talk.Ferry, makes and steers it), follows the way it's given at a strider's pace, takes one
 * passenger, can't be hurt, and is never saved: a barge left behind by an unloaded chunk is simply gone.
 */
public class Barge extends Entity {
    private static final EntityDataAccessor<Boolean> UNDER_WAY = SynchedEntityData.defineId(Barge.class, EntityDataSerializers.BOOLEAN);

    /** Right-clicking a barge: (barge, player). Set by the ferry, which decides whether you may board. */
    public static BiConsumer<Barge, ServerPlayer> onBoard = (barge, player) -> {};

    /** How far ahead of the barge's middle its strider walks, in blocks. */
    public static final double TOW = 4.4;

    private final List<Vec3> way = new ArrayList<>();
    private int next;
    private @Nullable UUID passenger;
    /** Client only: the strider drawn towing the barge (never added to the world; the game's own strider, drawn as one). */
    private @Nullable Strider tower;

    public Barge(EntityType<? extends Barge> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Who may board (the player who booked it), or null for no one. */
    public void bookFor(@Nullable UUID player) {
        this.passenger = player;
    }

    public boolean bookedFor(Player player) {
        return player.getUUID().equals(this.passenger);
    }

    /** Sail along these waypoints, from wherever the barge is now. */
    public void follow(List<Vec3> waypoints) {
        this.way.clear();
        this.way.addAll(waypoints);
        this.next = 0;
        this.entityData.set(UNDER_WAY, !waypoints.isEmpty());
    }

    /** Through every waypoint it was given (or given none). */
    public boolean arrived() {
        return this.next >= this.way.size();
    }

    /** How far along its way it is, 0 to 1. */
    public float progress() {
        return this.way.isEmpty() ? 1 : Math.min(1F, this.next / (float) this.way.size());
    }

    /** For the renderer: the strider walks while the barge is under way. */
    public boolean underWay() {
        return this.entityData.get(UNDER_WAY);
    }

    /** The strider towing this barge, for the renderer; null on the server. */
    public @Nullable Strider tower() {
        return this.tower;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.towTick();
            return;
        }
        // a frozen game (the /tick command) holds the barge still too, though it carries a player
        if (!this.level().tickRateManager().runsNormally()) return;
        if (this.next < this.way.size()) {
            Vec3 here = this.position(), target = this.way.get(this.next);
            Vec3 to = target.subtract(here);
            double flat = Math.sqrt(to.x * to.x + to.z * to.z);
            if (flat > 0.05) {
                // turn toward the next waypoint, a few degrees a tick, as a towed raft would
                float want = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90.0F;
                this.setYRot(Mth.approachDegrees(this.getYRot(), want, 6.0F));
            }
            if (flat <= Sea.SPEED) {
                this.setPos(target.x, target.y, target.z);
                this.next++;
                if (this.next >= this.way.size()) this.entityData.set(UNDER_WAY, false);
            } else {
                this.setPos(here.x + to.x / flat * Sea.SPEED, target.y, here.z + to.z / flat * Sea.SPEED);
            }
        }
    }

    /** Keep the towing strider ahead of the bow, facing the way the barge faces, walking while it moves. */
    private void towTick() {
        if (this.tower == null) {
            this.tower = EntityTypes.STRIDER.create(this.level(), EntitySpawnReason.LOAD);
            if (this.tower == null) return;
            this.tower.setId(-1 - this.getId());          // its own id, out of the world's range: the renderer asks for one
            this.tower.snapTo(this.ahead(), this.getYRot(), 0.0F);
        }
        Strider s = this.tower;
        Vec3 to = this.ahead();
        double moved = to.distanceTo(s.position());
        s.xo = s.getX();
        s.yo = s.getY();
        s.zo = s.getZ();
        s.yRotO = s.getYRot();
        s.yBodyRotO = s.yBodyRot;
        s.yHeadRotO = s.yHeadRot;
        s.setPos(to);
        s.setYRot(this.getYRot());
        s.yBodyRot = this.getYRot();
        s.yHeadRot = this.getYRot();
        s.walkAnimation.update((float) Math.min(moved * 4.0, 1.0), 0.4F, 1.0F);
        s.tickCount++;
    }

    private Vec3 ahead() {
        float yaw = this.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(this.getX() - Mth.sin(yaw) * TOW, this.getY() + 0.1, this.getZ() + Mth.cos(yaw) * TOW);
    }

    @Override
    protected InterpolationHandler createInterpolationHandler() {
        return LinearInterpolationHandler.create(this, 3);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (this.isVehicle() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player instanceof ServerPlayer sp) onBoard.accept(this, sp);
        return InteractionResult.SUCCESS;
    }

    /**
     * One seat on the bench, a little aft of the middle. The bench stands high enough that a sitting player's feet
     * (0.6 below the seat) stay clear of the lava the raft floats in.
     */
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, 0.8, -0.7).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        super.positionRider(passenger, move);
        float turn = Mth.wrapDegrees(this.getYRot() - this.yRotO);
        if (turn != 0.0F) {
            passenger.setYRot(passenger.getYRot() + turn);
            passenger.setYHeadRot(passenger.getYHeadRot() + turn);
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return !this.isVehicle();
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    /** Never written to disk: a barge belongs to one crossing, and is gone with it. */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(UNDER_WAY, false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}
}
