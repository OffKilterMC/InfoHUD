package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component
import net.minecraft.world.level.LightLayer
import offkiltermc.infohud.client.InfoHUDClient

class ServerLightInfoLine : InfoLineBase("server-light", SettingsCategory.LIGHT) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        return if (env.clientChunk != null && !env.clientChunk.isEmpty) {
            val level = env.level
            val blockPos = env.blockPos
            val darkness = InfoHUDClient.skyDarken
            val i = level.getRawBrightness(blockPos, darkness)
            val j = level.getBrightness(LightLayer.SKY, blockPos)
            val k = level.getBrightness(LightLayer.BLOCK, blockPos)
            return Component.literal("Server Light: $i ($j sky, $k block, $darkness darkness)")
        } else {
            null
        }
    }
}