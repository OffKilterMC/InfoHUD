package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GameClockInfoLine: InfoLineBase("game-clock", SettingsCategory.GAMEPLAY) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        if (env.level.dimensionType().fixedTime.isPresent) {
            return null
        }

        val time = env.level.dayTime

        val tod = time % 24000L
        val hour = (tod / 1000L + 6) % 24 // 0000 is 6am, noon is 6000, etc.
        val minuteTicks = tod - (tod / 1000L * 1000L)
        val minute = minuteTicks / 1000.0 * 60
        val days = time / 24000L

        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val d = LocalTime.of(hour.toInt(), minute.toInt(), 0, 0)
        val t = d.format(formatter)
        return Component.literal(String.format("Game time: %s, day %d", t, days))
    }
}