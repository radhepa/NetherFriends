package dev.netherfriends.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

/**
 * One model class for all the peoples: it finds parts by name and animates whatever it finds. Head look,
 * a walk for legs and arms, the Weft's lower (weaving) arms, the Magmari's tail, the Ghastkin's tentacles
 * and drift. Animations proper are on hold; this keeps them alive in the meantime.
 */
public class ResidentModel extends EntityModel<ResidentRenderState> {
    private final @Nullable ModelPart head, rightArm, leftArm, rightLeg, leftLeg, rightLower, leftLower, tail, cape, veil;
    private final List<ModelPart> tentacles = new ArrayList<>();
    private final boolean floats;

    public ResidentModel(ModelPart root) {
        super(root);
        this.head = find(root, "head");
        this.rightArm = find(root, "right_arm");
        this.leftArm = find(root, "left_arm");
        this.rightLeg = find(root, "right_leg");
        this.leftLeg = find(root, "left_leg");
        this.rightLower = find(root, "right_lower_arm");
        this.leftLower = find(root, "left_lower_arm");
        this.tail = find(root, "tail");
        this.cape = find(root, "cape");
        this.veil = find(root, "veil");
        for (int i = 0; i < 8; i++) {
            ModelPart t = find(root, "tentacle_" + i);
            if (t != null) this.tentacles.add(t);
        }
        this.floats = !this.tentacles.isEmpty();
    }

    /** A part anywhere in the tree, or null. */
    static @Nullable ModelPart find(ModelPart part, String name) {
        if (part.hasChild(name)) return part.getChild(name);
        for (ModelPart child : part.getAllParts()) {
            if (child != part && child.hasChild(name)) return child.getChild(name);
        }
        return null;
    }

    @Override
    public void setupAnim(ResidentRenderState s) {
        super.setupAnim(s);
        float age = s.ageInTicks, pos = s.walkAnimationPos, speed = Math.min(1.0F, s.walkAnimationSpeed);
        float stride = Mth.cos(pos * 0.6662F) * speed;
        if (this.head != null) {
            this.head.yRot += s.yRot * Mth.DEG_TO_RAD;
            this.head.xRot += s.xRot * Mth.DEG_TO_RAD;
        }
        if (this.rightLeg != null && this.leftLeg != null) {
            this.rightLeg.xRot += stride * 1.1F;
            this.leftLeg.xRot -= stride * 1.1F;
        }
        float armSwing = this.floats ? 0.35F : 0.8F;
        if (this.rightArm != null) {
            this.rightArm.xRot -= stride * armSwing;
            this.rightArm.zRot += Mth.cos(age * 0.09F) * 0.04F + 0.03F;
        }
        if (this.leftArm != null) {
            this.leftArm.xRot += stride * armSwing;
            this.leftArm.zRot -= Mth.cos(age * 0.09F) * 0.04F + 0.03F;
        }
        if (s.climbing && this.rightArm != null && this.leftArm != null) {
            // reaching up the stem hand over hand
            this.rightArm.xRot = -2.6F + Mth.sin(age * 0.5F) * 0.4F;
            this.leftArm.xRot = -2.6F - Mth.sin(age * 0.5F) * 0.4F;
        }
        // the Weft's lower arms work a shuttle whenever they stand still
        float idle = 1.0F - speed;
        if (this.rightLower != null) this.rightLower.xRot += Mth.sin(age * 0.25F) * 0.25F * idle - stride * 0.3F;
        if (this.leftLower != null) this.leftLower.xRot += Mth.sin(age * 0.25F + 2.0F) * 0.25F * idle + stride * 0.3F;
        if (this.tail != null) this.tail.yRot += Mth.sin(age * 0.07F) * 0.18F;
        if (this.cape != null) this.cape.xRot += speed * 0.5F + Mth.sin(age * 0.06F) * 0.03F;
        if (this.veil != null) this.veil.xRot += speed * 0.35F + Mth.sin(age * 0.05F) * 0.03F;
        if (s.swing > 0 && this.rightArm != null) {
            float t = Mth.sin(s.swing * Mth.PI);
            this.rightArm.xRot -= t * 1.4F;
            this.rightArm.yRot += t * 0.3F;
        }
        if (this.floats) {
            // tentacles sway and trail; the whole body drifts on the air
            for (int i = 0; i < this.tentacles.size(); i++) {
                ModelPart t = this.tentacles.get(i);
                t.xRot += Mth.sin(age * 0.11F + i * 1.1F) * 0.12F + speed * 0.45F;
                t.zRot += Mth.cos(age * 0.09F + i * 1.7F) * 0.09F;
            }
            this.root().y += Mth.sin(age * 0.08F) * (s.frightened ? 1.6F : 0.8F);
            if (s.frightened && this.rightArm != null && this.leftArm != null) {
                this.rightArm.xRot -= 0.9F;
                this.leftArm.xRot -= 0.9F;
            }
        }
    }
}
