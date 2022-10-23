package offkilter.infohud.infoline

import net.minecraft.network.chat.Component
import net.minecraft.world.DifficultyInstance
import java.util.*

class LocalDifficultyInfoLine : InfoLineBase("local-difficulty", SettingsCategory.GAMEPLAY) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        val blockPos = env.blockPos
        val level = env.level
        val serverChunk = env.serverChunk
        if (blockPos.y >= level.minBuildHeight && blockPos.y < level.maxBuildHeight) {
            var l = 0L
            var h = 0.0f
            if (serverChunk != null) {
                h = level.moonBrightness
                l = serverChunk.inhabitedTime
            }
            val difficultyInstance = DifficultyInstance(level.difficulty, level.dayTime, l, h)
            return Component.literal(
                String.format(
                    Locale.ROOT,
                    "Local Difficulty: %.2f | %.2f (Day %d)",
                    java.lang.Float.valueOf(difficultyInstance.effectiveDifficulty),
                    java.lang.Float.valueOf(difficultyInstance.specialMultiplier),
                    level.dayTime / 24000L
                )
            )
        } else {
            return null
        }
    }
}
