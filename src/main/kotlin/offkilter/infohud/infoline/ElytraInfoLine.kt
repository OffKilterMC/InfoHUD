package offkilter.infohud.infoline

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import kotlin.math.roundToInt

class ElytraInfoLine : InfoLineBase("elytra", SettingsCategory.GAMEPLAY) {
    private fun getPlayerElytraDurability(p: LocalPlayer): Float? {
        val stack = p.getItemBySlot(EquipmentSlot.CHEST)
        if (stack != null) {
            if (stack.`is`(Items.ELYTRA)) {
                return (stack.maxDamage - stack.damageValue) / stack.maxDamage.toFloat()
            }
        }
        return null
    }

    override fun getInfoString(env: InfoLineEnvironment): String? {
        env.minecraft.player?.let { p ->
            val value = getPlayerElytraDurability(p)
            if (value != null && value != (-1).toFloat()) {
                return String.format("Elytra: %d%%", (value * 100.0f).roundToInt())
            }
        }
        return null
    }
}