package offkilter.infohud.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import offkilter.infohud.client.InfoHUDClient;
import offkilter.infohud.client.InfoHUDRenderer;
import offkilter.infohud.client.PerfCounters;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    private static @Nullable InfoHUDRenderer renderer = null;
    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow public abstract DebugScreenOverlay getDebugOverlay();

    @Inject(method="render", at=@At(value="RETURN", target="Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void HookIt(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (InfoHUDClient.showHUD && !this.getDebugOverlay().showDebugScreen()) {
            if (renderer == null) {
                renderer = new InfoHUDRenderer(this.minecraft);
            }
            renderer.render(guiGraphics);
        }
        PerfCounters.INSTANCE.updateFPS();
    }
}
