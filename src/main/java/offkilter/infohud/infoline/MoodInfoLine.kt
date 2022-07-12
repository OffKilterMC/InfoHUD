package offkilter.infohud.infoline

import kotlin.math.roundToInt

class MoodInfoLine : InfoLineBase("mood", SettingsCategory.GAMEPLAY) {
    override fun getInfoString(env: InfoLineEnvironment): String? {
        return env.minecraft.player?.let { p ->
            String.format("Mood: %d%%", (p.currentMood * 100.0f).roundToInt())
        }
    }
}