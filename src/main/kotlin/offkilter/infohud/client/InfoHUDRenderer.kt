package offkilter.infohud.client

import com.google.common.collect.Lists
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.datafixers.util.Either
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ChunkHolder.ChunkLoadingFailure
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.LevelChunk
import offkilter.infohud.infoline.InfoLineEnvironment
import java.util.concurrent.CompletableFuture

@Environment(value = EnvType.CLIENT)
class InfoHUDRenderer(private val minecraft: Minecraft) : GuiComponent() {
    private val font = minecraft.font
    private var lastChunkPos: ChunkPos? = null
    private var clientChunk: LevelChunk? = null
    private var serverChunk: CompletableFuture<LevelChunk>? = null
    private var lastGuiScale = -1.0
    private var lastScale = -1

    private fun updateChunkPos(pos: BlockPos) {
        val chunkPos = ChunkPos(pos)
        if (chunkPos != lastChunkPos) {
            lastChunkPos = chunkPos
            clientChunk = null
            serverChunk = null
        }
    }

    private fun getClientChunk(): LevelChunk? {
        if (clientChunk == null) {
            clientChunk = minecraft.level?.let { level ->
                lastChunkPos?.let { pos ->
                    level.getChunk(pos.x, pos.z)
                }
            }
        }
        return clientChunk
    }

    private fun getServerLevel(): ServerLevel? {
        val integratedServer = minecraft.singleplayerServer
        val level = minecraft.level
        if (integratedServer != null && level != null) {
            return integratedServer.getLevel(level.dimension())
        }
        return null
    }

    private fun getServerChunk(): LevelChunk? {
        if (serverChunk == null) {
            serverChunk = getServerLevel()?.let { serverLevel ->
                lastChunkPos?.let { lastChunkPos ->
                    serverLevel.chunkSource.getChunkFuture(lastChunkPos.x, lastChunkPos.z, ChunkStatus.FULL, false)
                        .thenApply { either: Either<ChunkAccess?, ChunkLoadingFailure?> ->
                            either.map(
                                { chunkAccess: ChunkAccess? -> chunkAccess as LevelChunk? },
                                { _: ChunkLoadingFailure? -> null }
                            )
                        }
                }
            } ?: run {
                CompletableFuture.completedFuture(getClientChunk())
            }
        }
        return serverChunk?.getNow(null)
    }

    private fun reallyDetermineScale(): Int {
        val scale = InfoHUDSettings.INSTANCE.scale
        if (scale != 0) {
            // if our desired scale is larger than we'd normally allow right now,
            // cap it to the max
            val maxScale = minecraft.window.calculateScale(0, minecraft.isEnforceUnicode)
            if (scale > maxScale) {
                return maxScale
            }
        }
        return scale
    }

    private fun determineScale(): Int {
        if (lastGuiScale != minecraft.window.guiScale) {
            lastScale = reallyDetermineScale()
        }
        return lastScale
    }

    fun render(poseStack: PoseStack) {
        val camera = minecraft.getCameraEntity()
        val level = minecraft.level

        if (camera == null || level == null) {
            return
        }

        val blockPos = camera.blockPosition()
        updateChunkPos(blockPos)
        val env = InfoLineEnvironment(minecraft, level, blockPos, camera, getClientChunk(), getServerChunk())

        val list: MutableList<Component> = Lists.newArrayList()
        for (infoLine in InfoHUDSettings.INSTANCE.infoLines) {
            val result = infoLine.getInfoString(env)
            if (result != null) {
                list.add(result)
            }
        }

        poseStack.pushPose()

        val scale = determineScale()
        if (scale != 0) {
            val guiScale = minecraft.window.guiScale.toFloat()
            if (guiScale.toInt() != scale) {
                poseStack.scale(
                    scale / guiScale,
                    scale / guiScale,
                    scale / guiScale
                )
            }
        }

        for (i in list.indices) {
            val string = list[i]
            val j = font.lineHeight + 1
            val k = font.width(string)
            val l = 5
            val m = 5 + j * i
            fill(poseStack, l - 1, m - 1, l + k + 1, m + j - 1, 0x90505050.toInt())
            font.draw(poseStack, string, l.toFloat(), m.toFloat(), 0xE0E0E0)
        }
        poseStack.popPose()
    }
}