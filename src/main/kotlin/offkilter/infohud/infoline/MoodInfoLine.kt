package offkilter.infohud.infoline

import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class MoodInfoLine : InfoLineBase("mood", SettingsCategory.GAMEPLAY) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        return env.minecraft.player?.let { p ->
            Component.literal(String.format("Mood: %d%%", (p.currentMood * 100.0f).roundToInt()))
        }
    }
}