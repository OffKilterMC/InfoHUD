package offkiltermc.infohud.infoline

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component

class TargetedEntityInfoLine : InfoLineBase("targeted-entity", SettingsCategory.BLOCK) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        return env.minecraft.crosshairPickEntity?.let { entity ->
            Component.literal("Targeted Entity: " + BuiltInRegistries.ENTITY_TYPE.getKey(entity.type))
        }
    }
}