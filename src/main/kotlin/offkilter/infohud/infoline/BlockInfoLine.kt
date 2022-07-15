package offkilter.infohud.infoline

class BlockInfoLine : InfoLineBase("block", SettingsCategory.LOCATION) {
    override fun getInfoString(env: InfoLineEnvironment): String {
        val blockPos = env.blockPos
        return String.format(
            "Block: %d %d %d, in sub-chunk %d %d %d",
            blockPos.x,
            blockPos.y,
            blockPos.z,
            blockPos.x and 0xF,
            blockPos.y and 0xF,
            blockPos.z and 0xF
        )
    }
}
