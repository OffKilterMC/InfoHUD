package offkilter.infohud.infoline

import net.minecraft.network.chat.Component

interface InfoLine {
    fun getInfoString(env: InfoLineEnvironment): Component?
    val key: String
    val name: String
    val description: String
    val category: SettingsCategory
}