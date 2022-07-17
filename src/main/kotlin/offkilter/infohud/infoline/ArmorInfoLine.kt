package offkilter.infohud.infoline

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import kotlin.math.roundToInt

class ArmorInfoLine : InfoLineBase("armor", SettingsCategory.GAMEPLAY) {
    private fun getItemStackLifeLeft(stack: ItemStack): Float {
        return (stack.maxDamage - stack.damageValue) / stack.maxDamage.toFloat()
    }

    private fun getItemStatus(stack: ItemStack?): Component {
        return if (stack != null && stack.isDamageableItem) {
            Component.literal(String.format("%d%%", (getItemStackLifeLeft(stack) * 100.0f).roundToInt()))
                .withStyle(Style.EMPTY.withColor(stack.barColor))
        } else {
            Component.literal("--")
        }
    }

    override fun getInfoString(env: InfoLineEnvironment): Component? {
        env.minecraft.player?.let { p ->
            return Component.translatable(
                "Armor: H: %s C: %s L: %s B: %s",
                getItemStatus(p.getItemBySlot(EquipmentSlot.HEAD)),
                getItemStatus(p.getItemBySlot(EquipmentSlot.CHEST)),
                getItemStatus(p.getItemBySlot(EquipmentSlot.LEGS)),
                getItemStatus(p.getItemBySlot(EquipmentSlot.FEET))
            )
        }

        return null
    }

}