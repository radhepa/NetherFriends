package dev.netherfriends.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Withren: the Wither's own people. A player's body and height under the Wither's head, genderless,
 * neutral: they leave you be unless you strike first. They keep the soul valleys, remember every name,
 * and guard the knowledge of the Last Answer. Ashwardens fight anyone of another people who comes near.
 */
public class Withren extends Resident {
    public static final int VILLAGER = 0, REMEMBRANCER = 1, ASHWARDEN = 2;

    public Withren(EntityType<? extends Withren> type, Level level) {
        super(type, level);
    }

    @Override
    public People people() {
        return People.WITHREN;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createResidentAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    public boolean isAshwarden() {
        return this.role() == ASHWARDEN && !this.isBaby();
    }

    @Override
    public void setRole(int role) {
        super.setRole(role);
        // the wardens are the village's soldiers: tougher, and they hit like a wither skeleton
        boolean warden = role == ASHWARDEN;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(warden ? 40.0 : 26.0);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(warden ? 7.0 : 4.0);
        this.getAttribute(Attributes.ARMOR).setBaseValue(warden ? 6.0 : 0.0);
        if (this.getHealth() > this.getMaxHealth()) this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4) {
            @Override public boolean canUse() { return Withren.this.isBaby() && super.canUse(); }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
            @Override public boolean canUse() { return !Withren.this.isBaby() && super.canUse(); }
        });
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        // strike one Withren and the valley answers
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Withren.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target, level) -> this.isAshwarden() && (People.enemies(this, target) || target instanceof Hoglin || target instanceof Zoglin)));
    }

    @Override
    protected boolean avoidsEnemies() {
        return this.role() != ASHWARDEN || this.isBaby();
    }

    /** The Wither's people can't be withered. */
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return !effect.is(MobEffects.WITHER) && super.canBeAffected(effect);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && this.isAshwarden() && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100), this);
        }
        return hit;
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WITHER_SKELETON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.WITHER_SKELETON_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WITHER_SKELETON_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.WITHER_SKELETON_STEP, 0.12F, 0.9F); }
    @Override protected float getSoundVolume() { return 0.45F; }
    @Override public float getVoicePitch() { return (this.isBaby() ? 1.5F : 0.8F) + this.random.nextFloat() * 0.1F; }
    @Override public int getAmbientSoundInterval() { return 240; }
}
