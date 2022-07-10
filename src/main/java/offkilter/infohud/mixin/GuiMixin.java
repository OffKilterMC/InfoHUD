package offkilter.infohud.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import offkilter.infohud.client.InfoHUDClient;
import offkilter.infohud.client.PerfCounters;
import offkilter.infohud.client.InfoHUDRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    private static @Nullable InfoHUDRenderer renderer = null;
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method="render", at=@At(value="RETURN", target="Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void HookIt(PoseStack poseStack, float f, CallbackInfo ci) {
        if (InfoHUDClient.showHUD && !this.minecraft.options.renderDebug) {
            if (renderer == null) {
                renderer = new InfoHUDRenderer(this.minecraft);
            }
            renderer.render(poseStack);
        }
        PerfCounters.INSTANCE.updateFPS();
    }
}
