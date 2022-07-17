package offkilter.infohud.infoline

import net.minecraft.network.chat.Component
import offkilter.infohud.mixin.MinecraftMixin

class FPSInfoLine : InfoLineBase("fps", SettingsCategory.PERF) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        return Component.literal(
            "FPS: " + MinecraftMixin.getFps() + if (env.minecraft.options.enableVsync().get()) " vsync" else ""
        )
    }
}
