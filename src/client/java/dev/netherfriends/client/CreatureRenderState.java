package dev.netherfriends.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** What the creatures' model animates from. */
public class CreatureRenderState extends LivingEntityRenderState {
    /** 0..1: how far a Silkgrub's shorn silk has grown back. */
    public float silk = 1.0F;
    /** A Silkgrub on a wall. */
    public boolean climbing;
}
