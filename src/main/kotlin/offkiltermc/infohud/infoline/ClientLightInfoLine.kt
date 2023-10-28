package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component
import net.minecraft.world.level.LightLayer

class ClientLightInfoLine : InfoLineBase("client-light", SettingsCategory.LIGHT) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        return if (env.clientChunk != null && !env.clientChunk.isEmpty) {
            val level = env.level
            val blockPos = env.blockPos
            val darkness = if (level.isThundering) 10 else 0
            val i = level.getRawBrightness(blockPos, darkness)
            val j = level.getBrightness(LightLayer.SKY, blockPos)
            val k = level.getBrightness(LightLayer.BLOCK, blockPos)
            return Component.literal("Client Light: $i ($j sky, $k block, $darkness darkness)")
        } else {
            null
        }
    }
}