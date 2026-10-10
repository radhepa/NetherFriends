package dev.netherfriends.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

/**
 * One model class for the three creatures, animating whatever parts it finds (like {@link ResidentModel} for the
 * peoples). The Cairnback: four legs in a slow trot, a head that grazes now and then, its shell fire flickering.
 * The Silkgrub: a ripple running down its segments as it crawls, its silk tufts at the size they've grown back to.
 * The Ashbell: its bell pulsing, threads trailing and swaying, the whole of it bobbing on the air.
 */
public class CreatureModel extends EntityModel<CreatureRenderState> {
    private final @Nullable ModelPart head, tail, flame, bell;
    private final List<ModelPart> rightLegs = new ArrayList<>(), leftLegs = new ArrayList<>();
    private final List<ModelPart> segments = new ArrayList<>(), silk = new ArrayList<>(), threads = new ArrayList<>();
    private final float[] segmentY;

    public CreatureModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
        this.head = ResidentModel.find(root, "head");
        this.tail = ResidentModel.find(root, "tail");
        this.flame = ResidentModel.find(root, "flame");
        this.bell = ResidentModel.find(root, "bell");
        for (int i = 0; i < 8; i++) {
            add(this.rightLegs, root, "right_leg_" + i);
            add(this.leftLegs, root, "left_leg_" + i);
            add(this.segments, root, "segment_" + i);
            add(this.silk, root, "silk_" + i);
            add(this.threads, root, "thread_" + i);
            add(this.threads, root, "frill_" + i);
        }
        this.segmentY = new float[this.segments.size()];
        for (int i = 0; i < this.segmentY.length; i++) this.segmentY[i] = this.segments.get(i).y;
    }

    private static void add(List<ModelPart> list, ModelPart root, String name) {
        ModelPart part = ResidentModel.find(root, name);
        if (part != null) list.add(part);
    }

    @Override
    public void setupAnim(CreatureRenderState s) {
        super.setupAnim(s);
        float age = s.ageInTicks, speed = Math.min(1.0F, s.walkAnimationSpeed);
        float stride = Mth.cos(s.walkAnimationPos * 0.6F) * speed;
        if (this.head != null) {
            this.head.yRot += s.yRot * Mth.DEG_TO_RAD * 0.6F;
            this.head.xRot += s.xRot * Mth.DEG_TO_RAD * 0.6F;
            // now and then, standing still, the head dips to graze
            float graze = Math.max(0.0F, Mth.sin(age * 0.025F) - 0.75F) / 0.25F;
            if (speed < 0.1F) this.head.xRot += graze * 0.6F;
        }
        for (int i = 0; i < this.rightLegs.size(); i++) {
            float sign = i % 2 == 0 ? 1.0F : -1.0F;
            this.rightLegs.get(i).xRot += stride * 0.7F * sign;
            if (i < this.leftLegs.size()) this.leftLegs.get(i).xRot -= stride * 0.7F * sign;
        }
        if (this.tail != null) this.tail.yRot += Mth.sin(age * 0.06F) * 0.2F;
        if (this.flame != null) {
            float flicker = 1.0F + Mth.sin(age * 0.9F) * 0.08F + Mth.sin(age * 2.3F) * 0.05F;
            this.flame.yScale = flicker;
            this.flame.xScale = 2.0F - flicker;
            this.flame.zScale = 2.0F - flicker;
            this.flame.visible = !s.isBaby;
        }
        // the Silkgrub: a ripple from head to tail, quicker when it crawls; up walls it arches
        for (int i = 0; i < this.segments.size(); i++) {
            ModelPart seg = this.segments.get(i);
            seg.y = this.segmentY[i] - Math.max(0.0F, Mth.sin(age * (0.15F + speed * 0.5F) - i * 0.9F)) * (0.6F + speed * 1.2F);
            seg.xRot += s.climbing ? -0.25F : 0.0F;
        }
        for (ModelPart tuft : this.silk) {
            float grown = Math.clamp(s.silk, 0.0F, 1.0F);
            tuft.visible = grown > 0.05F && !s.isBaby;
            tuft.xScale = 0.4F + 0.6F * grown;
            tuft.yScale = grown;
            tuft.zScale = 0.4F + 0.6F * grown;
        }
        // the Ashbell: the bell pulses, the threads trail behind and sway
        if (this.bell != null) {
            float pulse = Mth.sin(age * 0.12F);
            this.bell.xScale = 1.0F + pulse * 0.06F;
            this.bell.zScale = 1.0F + pulse * 0.06F;
            this.bell.yScale = 1.0F - pulse * 0.05F;
            this.root().y += Mth.sin(age * 0.07F) * 1.2F;
        }
        for (int i = 0; i < this.threads.size(); i++) {
            ModelPart t = this.threads.get(i);
            t.xRot += Mth.sin(age * 0.09F + i * 0.8F) * 0.12F + speed * 0.5F;
            t.zRot += Mth.cos(age * 0.07F + i * 1.3F) * 0.1F;
        }
    }
}
