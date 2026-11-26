package fix.figurairis.mixin;

import fix.figurairis.FixHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Every pipeline variant Figura builds (flat_lighting_N, fullbright_N) is handed to Iris. */
@Mixin(targets = "org.figuramc.figura.utils.WorldRenderPipelines", remap = false)
public class WorldRenderPipelinesMixin {
    @Inject(method = "create", at = @At("RETURN"), remap = false)
    private static void figuraIrisFix$assign(CallbackInfoReturnable<Object> cir) {
        FixHelper.assign(cir.getReturnValue());
    }
}
