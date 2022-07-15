package offkilter.infohud.infoline

object InfoLineRegistry {
    private val registry: MutableMap<String, InfoLine> = mutableMapOf()

    private fun register(infoLine: InfoLine): InfoLine {
        registry[infoLine.key] = infoLine
        return infoLine
    }

    fun infoLineWithKey(key: String): InfoLine? {
        return registry[key]
    }

    val allInfoLines: List<InfoLine>
        get() = registry.values.toList()


    val FPS = register(FPSInfoLine())
    val BIOME = register(BiomeInfoLine())
    val LOCATION = register(LocationInfoLine())
    val DIRECTION = register(DirectionInfoLine())
    val BLOCK = register(BlockInfoLine())
    val CLIENT_LIGHT = register(ClientLightInfoLine())
    val TARGETED_BLOCK = register(TargetedBlockInfoLine())
    val TARGETED_FLUID = register(TargetedFluidInfoLine())
    val SERVER_LIGHT = register(ServerLightInfoLine())
    val TICK_PERF = register(TickPerfInfoLine())
    val LOCAL_DIFFICULTY = register(LocalDifficultyInfoLine())
    val MOOD = register(MoodInfoLine())
}
