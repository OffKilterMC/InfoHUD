package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component

class BlockInfoLine : InfoLineBase("block", SettingsCategory.LOCATION) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        val blockPos = env.blockPos
        return Component.literal(
            String.format(
                "Block: %d %d %d, in sub-chunk %d %d %d",
                blockPos.x,
                blockPos.y,
                blockPos.z,
                blockPos.x and 0xF,
                blockPos.y and 0xF,
                blockPos.z and 0xF
            )
        )
    }
}
