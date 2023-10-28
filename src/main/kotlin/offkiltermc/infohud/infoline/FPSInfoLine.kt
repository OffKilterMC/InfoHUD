package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component
import offkiltermc.infohud.mixin.MinecraftMixin

class FPSInfoLine : InfoLineBase("fps", SettingsCategory.PERF) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        val limit = env.minecraft.framerateLimit
        val limitStr = if (limit == 260) "none" else limit.toString()
        val vsyncStr = if (env.minecraft.options.enableVsync().get()) "on" else "off"

        return Component.literal(
            String.format(
                "FPS: %d limit: %s vsync: %s",
                offkiltermc.infohud.mixin.MinecraftMixin.getFps(),
                limitStr,
                vsyncStr
            )
        )
    }
}
