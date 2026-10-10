package dev.netherfriends.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * The Weft's beast: a fat teal caterpillar of the warped canopy that spins silk. Shear its tufts for string; they
 * grow back in a few minutes, or at once if you feed it warped roots. It climbs as the Weft do. Bred with warped
 * roots. The Weft farm them on their decks; wild ones crawl the warped forest floor and stems.
 */
public class Silkgrub extends Animal implements Shearable {
    private static final EntityDataAccessor<Integer> REGROW = SynchedEntityData.defineId(Silkgrub.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(Silkgrub.class, EntityDataSerializers.BOOLEAN);
    /** Ticks for shorn silk to grow back. */
    public static final int REGROW_TICKS = 20 * 60 * 3;

    public Silkgrub(EntityType<? extends Silkgrub> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18);
    }

    /** Warped nylium or wart underfoot: the warped forest. */
    public static boolean checkSpawnRules(EntityType<Silkgrub> type, LevelAccessor level, EntitySpawnReason reason, BlockPos pos,
                                         RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.WARPED_NYLIUM) || below.is(Blocks.WARPED_WART_BLOCK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REGROW, 0);
        builder.define(CLIMBING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 5.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    // -- silk ---------------------------------------------------------------------------------------------------

    /** 0 just shorn, 1 full: how far its silk has grown back (the model shows the tufts at this size). */
    public float silk() {
        return 1.0F - this.entityData.get(REGROW) / (float) REGROW_TICKS;
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby() && this.entityData.get(REGROW) == 0;
    }

    @Override
    public void shear(ServerLevel level, SoundSource source, ItemStack tool) {
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.3F);
        this.spawnAtLocation(level, new ItemStack(Items.STRING, 2 + this.random.nextInt(3)), 0.5F);
        if (this.random.nextInt(6) == 0) this.spawnAtLocation(level, new ItemStack(Items.COBWEB), 0.5F);
        this.entityData.set(REGROW, REGROW_TICKS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.SHEARS) && this.readyForShearing()) {
            if (this.level() instanceof ServerLevel server) {
                this.shear(server, SoundSource.PLAYERS, held);
                this.gameEvent(GameEvent.SHEAR, player);
                held.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return InteractionResult.SUCCESS;
        }
        // a shorn grub fed warped roots spins its silk back at once
        if (this.isFood(held) && this.entityData.get(REGROW) > 0 && !this.isBaby()) {
            if (!this.level().isClientSide()) {
                held.consume(1, player);
                this.entityData.set(REGROW, 0);
                this.playSound(SoundEvents.SNIFFER_EAT, 0.6F, 1.6F);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WARPED_ROOTS);
    }

    // -- climbing, as the Weft climb ----------------------------------------------------------------------------

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.entityData.set(CLIMBING, this.horizontalCollision);
            int regrow = this.entityData.get(REGROW);
            if (regrow > 0) this.entityData.set(REGROW, regrow - 1);
        }
    }

    @Override
    public boolean onClimbable() {
        return this.entityData.get(CLIMBING);
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING);
    }

    // -- life ---------------------------------------------------------------------------------------------------

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        Silkgrub young = NetherEntities.SILKGRUB.create(level, EntitySpawnReason.BREEDING);
        if (young != null && this.hasHome()) young.setHomeTo(this.getHomePosition(), this.getHomeRadius());
        return young;
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Regrow", this.entityData.get(REGROW));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(REGROW, input.getIntOr("Regrow", 0));
    }

    @Override protected @Nullable SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_SQUISH_SMALL; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH_SMALL; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.SILVERFISH_STEP, 0.1F, 0.7F); }
}
