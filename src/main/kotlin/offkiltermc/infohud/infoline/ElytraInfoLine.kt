package offkiltermc.infohud.infoline

import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.roundToInt

class ElytraInfoLine : InfoLineBase("elytra", SettingsCategory.GAMEPLAY) {
    private fun getElytra(p: LocalPlayer): ItemStack? {
        val stack = p.getItemBySlot(EquipmentSlot.CHEST)
        if (stack != null) {
            if (stack.`is`(Items.ELYTRA)) {
                return stack
            }
        }
        return null
    }

    private fun getElytraLifeLeft(elytra: ItemStack): Float {
        return (elytra.maxDamage - elytra.damageValue) / elytra.maxDamage.toFloat()
    }

    override fun getInfoString(env: InfoLineEnvironment): Component? {
        env.minecraft.player?.let { p ->
            getElytra(p)?.let { elytra ->
                val value = getElytraLifeLeft(elytra)
                if (value != (-1).toFloat()) {
                    val statusText = Component.literal(String.format("%d%%", (value * 100.0f).roundToInt()))
                        .withStyle(Style.EMPTY.withColor(elytra.barColor))
                    return Component.translatable("Elytra: %s", statusText)
                }
            }
        }
        return null
    }
}