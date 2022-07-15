package offkilter.infohud.infoline

import offkilter.infohud.mixin.MinecraftMixin

class FPSInfoLine : InfoLineBase("fps", SettingsCategory.PERF) {
    override fun getInfoString(env: InfoLineEnvironment): String {
        return "FPS: " + MinecraftMixin.getFps() + if (env.minecraft.options.enableVsync().get()) " vsync" else ""
    }
}
