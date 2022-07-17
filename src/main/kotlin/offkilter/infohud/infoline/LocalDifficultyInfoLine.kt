package offkilter.infohud.infoline

import net.minecraft.world.DifficultyInstance
import java.util.*

class LocalDifficultyInfoLine : InfoLine {
    override val key = "local-difficulty"
    override val name = "offkilter.infohud.localdifficulty.name"
    override val description = "offkilter.infohud.localdifficulty.desc"
    override val category = SettingsCategory.GAMEPLAY

    override fun getInfoString(env: InfoLineEnvironment): String? {
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
            return String.format(
                Locale.ROOT,
                "Local Difficulty: %.2f | %.2f (Day %d)",
                java.lang.Float.valueOf(difficultyInstance.effectiveDifficulty),
                java.lang.Float.valueOf(difficultyInstance.specialMultiplier),
                level.dayTime / 24000L
            )
        } else {
            return null
        }
    }
}