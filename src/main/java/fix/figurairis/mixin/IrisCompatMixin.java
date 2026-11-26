package fix.figurairis.mixin;

import fix.figurairis.FixHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Runs right after Figura has registered its own pipeline with Iris (only when Iris is installed). */
@Mixin(targets = "org.figuramc.figura.compat.IrisCompat", remap = false)
public class IrisCompatMixin {
    @Inject(method = "assignPipelinesToIrisPrograms", at = @At("TAIL"), remap = false)
    private static void figuraIrisFix$warmUp(CallbackInfo ci) {
        FixHelper.warmUp();
    }
}
