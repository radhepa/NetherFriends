package dev.netherexpansion.mixin;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Sets a sign's text while its chunk is still being generated (the public setters need a live world). */
@Mixin(SignBlockEntity.class)
public interface SignAccess {
    @Accessor("frontText")
    void netherexpansion$setFrontText(SignText text);

    @Accessor("isWaxed")
    void netherexpansion$setWaxed(boolean waxed);
}
