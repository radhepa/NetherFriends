package dev.netherexpansion.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** What the peoples' models animate from. */
public class ResidentRenderState extends LivingEntityRenderState {
    /** Which of the renderer's looks (model and texture) to draw. */
    public int look;
    /** 0..1 progress of a melee swing. */
    public float swing;
    /** A Weft on a wall. */
    public boolean climbing;
    /** A Ghastkin drifting away from someone. */
    public boolean frightened;
    /** A Magmari gone cold. */
    public boolean cold;
}
