package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component

class MoonInfoLine : InfoLineBase("moon", SettingsCategory.GAMEPLAY) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        val phase = env.level.dimensionType().moonPhase(env.level.dayTime())
        val phaseDesc = arrayOf(
            "offkilter.infohud.moon.full",
            "offkilter.infohud.moon.waning-gibbous",
            "offkilter.infohud.moon.third-quarter",
            "offkilter.infohud.moon.waning-crescent",
            "offkilter.infohud.moon.new",
            "offkilter.infohud.moon.waxing-crescent",
            "offkilter.infohud.moon.first-quarter",
            "offkilter.infohud.moon.waxing-gibbous"
        )

        return if (phase >= 0 && phase <= phaseDesc.size - 1) {
            Component.translatable(
                "offkilter.infohud.moon.infoline",
                Component.translatable(phaseDesc[phase]),
                "%.02f".format(env.level.moonBrightness)
            )
        } else {
            null
        }
    }
}