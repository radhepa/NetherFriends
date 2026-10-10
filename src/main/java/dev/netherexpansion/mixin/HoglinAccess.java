package dev.netherexpansion.mixin;

import net.minecraft.world.entity.monster.hoglin.Hoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Penned hoglins in a Hold are livestock the Piglins don't hunt (vanilla's own flag, set from code). */
@Mixin(Hoglin.class)
public interface HoglinAccess {
    @Invoker("setCannotBeHunted")
    void netherexpansion$setCannotBeHunted(boolean cannotBeHunted);
}
