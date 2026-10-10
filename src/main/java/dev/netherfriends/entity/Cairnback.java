package dev.netherfriends.entity;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * The Withren's beast: a slow tortoise of the soul sand valleys whose stepped shell of bone plates carries a small
 * soul fire that never goes out while it lives. Hold a torch, lantern or campfire to a grown one and it comes away
 * relit with soul fire. Bred with nether wart. The Withren walk them in their processions; wild ones graze the
 * valleys in ones and twos.
 */
public class Cairnback extends Animal {
    /** What its fire relights, and as what. */
    static final Map<Item, Item> RELIGHTS = Map.of(
            Items.TORCH, Items.SOUL_TORCH,
            Items.LANTERN, Items.SOUL_LANTERN,
            Items.CAMPFIRE, Items.SOUL_CAMPFIRE);

    public Cairnback(EntityType<? extends Cairnback> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.13)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    /** Soul sand or soul soil underfoot: the valleys. */
    public static boolean checkSpawnRules(EntityType<Cairnback> type, LevelAccessor level, EntitySpawnReason reason, BlockPos pos,
                                         RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.SOUL_SAND) || below.is(Blocks.SOUL_SOIL);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.NETHER_WART);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        Item relit = RELIGHTS.get(held.getItem());
        if (relit != null && !this.isBaby()) {
            if (this.level() instanceof ServerLevel server) {
                held.consume(1, player);
                ItemStack soul = new ItemStack(relit);
                if (!player.getInventory().add(soul)) player.spawnAtLocation(server, soul);
                server.playSound(null, this, SoundEvents.SOUL_ESCAPE.value(), SoundSource.NEUTRAL, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
                server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + this.getBbHeight() + 0.3, this.getZ(),
                        8, 0.15, 0.2, 0.15, 0.01);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // the fire on its back
        if (this.level().isClientSide() && !this.isBaby() && this.random.nextInt(12) == 0) {
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + (this.random.nextDouble() - 0.5) * 0.15,
                    this.getY() + this.getBbHeight() + 0.25, this.getZ() + (this.random.nextDouble() - 0.5) * 0.15, 0, 0.02, 0);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        Cairnback young = NetherEntities.CAIRNBACK.create(level, EntitySpawnReason.BREEDING);
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

    @Override protected @Nullable SoundEvent getAmbientSound() { return SoundEvents.ARMADILLO_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ARMADILLO_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ARMADILLO_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.ARMADILLO_STEP, 0.3F, 0.6F); }
    @Override public float getVoicePitch() { return (this.isBaby() ? 1.2F : 0.6F) + this.random.nextFloat() * 0.1F; }
    @Override public int getAmbientSoundInterval() { return 300; }
}
