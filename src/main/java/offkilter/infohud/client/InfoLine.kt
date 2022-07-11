package offkilter.infohud.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import offkilter.infohud.mixin.MinecraftMixin
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToInt

@Environment(value = EnvType.CLIENT)
enum class InfoLine(val key: String, val displayName: String, val desc: String, val category: SettingsCategory) {
    BIOME("biome", "offkilter.infohud.biome.name", "offkilter.infohud.biome.desc", SettingsCategory.WORLD) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            return if (env.blockPos.y >= env.level.minBuildHeight && env.blockPos.y < env.level.maxBuildHeight) {
                val holder = env.level.getBiome(env.blockPos)
                "Biome: " + holder.unwrap().map({ resourceKey: ResourceKey<Biome> ->
                    resourceKey.location().toString()
                }) { biome: Biome -> "[unregistered $biome]" }
            } else {
                null
            }
        }
    },
    BLOCK("block", "offkilter.infohud.block.name", "offkilter.infohud.block.desc", SettingsCategory.LOCATION) {
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
    },
    CLIENT_LIGHT(
        "client-light",
        "offkilter.infohud.clientlight.name",
        "offkilter.infohud.clientlight.desc",
        SettingsCategory.LIGHT
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            return if (env.clientChunk != null && !env.clientChunk.isEmpty) {
                val level = env.level
                val blockPos = env.blockPos
                val darkness = if (level.isThundering) 10 else 0
                val i = level.getRawBrightness(blockPos, darkness)
                val j = level.getBrightness(LightLayer.SKY, blockPos)
                val k = level.getBrightness(LightLayer.BLOCK, blockPos)
                return "Client Light: $i ($j sky, $k block, $darkness darkness)"
            } else {
                null
            }
        }
    },
    SERVER_LIGHT(
        "server-light",
        "offkilter.infohud.serverlight.name",
        "offkilter.infohud.serverlight.desc",
        SettingsCategory.LIGHT
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            return if (env.clientChunk != null && !env.clientChunk.isEmpty) {
                val level = env.level
                val blockPos = env.blockPos
                val darkness = InfoHUDClient.skyDarken
                val i = level.getRawBrightness(blockPos, darkness)
                val j = level.getBrightness(LightLayer.SKY, blockPos)
                val k = level.getBrightness(LightLayer.BLOCK, blockPos)
                return "Server Light: $i ($j sky, $k block, $darkness darkness)"
            } else {
                null
            }
        }
    },
    DIRECTION(
        "direction",
        "offkilter.infohud.direction.name",
        "offkilter.infohud.direction.desc",
        SettingsCategory.LOCATION
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String {
            val direction = env.camera.direction
            val towards = when (direction) {
                Direction.NORTH -> "Negative Z"
                Direction.SOUTH -> "Positive Z"
                Direction.WEST -> "Negative X"
                Direction.EAST -> "Positive X"
                else -> "Unknown"
            }
            return String.format(
                "Facing: %s (%s) (%.1f / %.1f)", direction, towards,
                Mth.wrapDegrees(env.camera.yRot),
                Mth.wrapDegrees(env.camera.xRot)
            )
        }
    },
    FPS("fps", "offkilter.infohud.fps.name", "offkilter.infohud.fps.desc", SettingsCategory.PERF) {
        override fun getInfoString(env: InfoLineEnvironment): String {
            return "FPS: " + MinecraftMixin.getFps() + if (env.minecraft.options.enableVsync().get()) " vsync" else ""
        }
    },
    LOCATION(
        "location",
        "offkilter.infohud.location.name",
        "offkilter.infohud.location.desc",
        SettingsCategory.LOCATION
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String {
            val camera = env.camera
            return String.format("X: %.3f Y: %.5f Z: %.3f", camera.x, camera.y, camera.z)
        }
    },
    TARGETED_BLOCK(
        "targeted-block",
        "offkilter.infohud.targetedblock.name",
        "offkilter.infohud.targetedblock.desc",
        SettingsCategory.BLOCK
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            val targetedBlock = env.camera.pick(20.0, 0.0f, false)
            if (targetedBlock.type == HitResult.Type.BLOCK) {
                val targetedBlockPos = (targetedBlock as BlockHitResult).blockPos
                val blockState = env.level.getBlockState(targetedBlockPos)
                return "Targeted Block: " + targetedBlockPos.x + ", " + targetedBlockPos.y + ", " + targetedBlockPos.z + " " + Registry.BLOCK.getKey(
                    blockState.block
                )
            }
            return null
        }
    },
    TARGETED_FLUID(
        "targeted-fluid",
        "offkilter.infohud.targetedfluid.name",
        "offkilter.infohud.targetedfluid.desc",
        SettingsCategory.BLOCK
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            val targetedFluid = env.camera.pick(20.0, 0.0f, true)
            if (targetedFluid.type == HitResult.Type.BLOCK) {
                val targetedBlockPos = (targetedFluid as BlockHitResult).blockPos
                val fluidState = env.level.getFluidState(targetedBlockPos)
                if (fluidState != null) {
                    val fluidType = Registry.FLUID.getKey(fluidState.type)
                    if (fluidType.toString() != "minecraft:empty") {
                        return "Targeted Fluid: " + targetedBlockPos.x + ", " + targetedBlockPos.y + ", " + targetedBlockPos.z + " " + Registry.FLUID.getKey(
                            fluidState.type
                        )
                    }
                }
            }
            return null
        }
    },
    TICK_PERF(
        "tick-perf",
        "offkilter.infohud.tickperf.name",
        "offkilter.infohud.tickperf.desc",
        SettingsCategory.PERF
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String {
            val mspt = PerfCounters.mspt / 1000000.0f
            val tps = PerfCounters.tps.toFloat() //1000.0f / Math.max(50.0f, mspt);
            return String.format("TPS: %.1f, MSPT: %.1f", tps, mspt)
        }
    },
    LOCAL_DIFFICULTY(
        "local-difficulty",
        "offkilter.infohud.localdifficulty.name",
        "offkilter.infohud.localdifficulty.desc",
        SettingsCategory.GAMEPLAY
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            val blockPos = env.blockPos
            val level = env.level
            val serverChunk = env.serverChunk
            if (blockPos.y >= level.minBuildHeight && blockPos.y < level.maxBuildHeight) {
                var l = 0L
                var h = 0.0f
                if (serverChunk != null) {
                    h = level.moonBrightness
                    l = serverChunk.inhabitedTime
                }
                val difficultyInstance = DifficultyInstance(level.difficulty, level.dayTime, l, h)
                return String.format(
                    Locale.ROOT,
                    "Local Difficulty: %.2f | %.2f (Day %d)",
                    java.lang.Float.valueOf(difficultyInstance.effectiveDifficulty),
                    java.lang.Float.valueOf(difficultyInstance.specialMultiplier),
                    level.dayTime / 24000L
                )
            } else {
                return null
            }
        }
    },
    MOOD(
        "mood",
        "offkilter.infohud.mood.name",
        "offkilter.infohud.mood.desc",
        SettingsCategory.GAMEPLAY
    ) {
        override fun getInfoString(env: InfoLineEnvironment): String? {
            return env.minecraft.player?.let { p ->
                String.format("Mood: %d%%", (p.currentMood * 100.0f).roundToInt())
            }
        }
    };

    enum class SettingsCategory(val iconResource: ResourceLocation) {
        LOCATION(ResourceLocation("infohud:textures/gui/compass.png")),
        PERF(ResourceLocation("infohud:textures/gui/stopwatch.png")),
        BLOCK(ResourceLocation("infohud:textures/gui/block.png")),
        LIGHT(ResourceLocation("infohud:textures/gui/light.png")),
        WORLD(ResourceLocation("infohud:textures/gui/world.png")),
        GAMEPLAY(ResourceLocation("infohud:textures/gui/sword.png"));
    }

    abstract fun getInfoString(env: InfoLineEnvironment): String?

    companion object {
        @JvmField
        val BY_NAME: Map<String, InfoLine> = Arrays.stream(values()).collect(
            Collectors.toMap(
                { obj: InfoLine -> obj.key },
                { infoLine: InfoLine -> infoLine })
        )
    }
}
