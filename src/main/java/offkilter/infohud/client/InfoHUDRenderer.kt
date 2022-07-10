package offkilter.infohud.client

import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.DebugScreenOverlay
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.LevelChunk
import offkilter.infohud.client.InfoHUDSettings.currentInfoLines

@Environment(value = EnvType.CLIENT)
class InfoHUDRenderer(private val minecraft: Minecraft) {
    private val font = minecraft.font
    private var clientChunk: LevelChunk? = null
    private var lastChunkPos: ChunkPos? = null

    private fun updateChunkPos(pos: BlockPos) {
        val chunkPos = ChunkPos(pos)
        if (chunkPos != lastChunkPos) {
            lastChunkPos = chunkPos
            clientChunk = null
        }
    }

    private fun getClientChunk(level: ClientLevel): LevelChunk {
        if (clientChunk == null) {
            clientChunk = level.getChunk(lastChunkPos!!.x, lastChunkPos!!.z)
        }
        return clientChunk!!
    }

    fun render(poseStack: PoseStack) {
        val camera = minecraft.getCameraEntity()
        val level = minecraft.level

        if (camera == null || level == null) {
            return
        }

        val blockPos = camera.blockPosition()
        updateChunkPos(blockPos)
        val levelChunk = getClientChunk(level)
        val env = InfoLineEnvironment(minecraft, level, blockPos, camera, levelChunk)

        val list: MutableList<String> = Lists.newArrayList()
        for (infoLine in currentInfoLines) {
            val result = infoLine.getInfoString(env)
            if (result != null) {
                list.add(result)
            }
        }

        poseStack.pushPose()
        poseStack.scale(0.5f, 0.5f, 1.0f)
        for (i in list.indices) {
            val string = list[i]
            if (Strings.isNullOrEmpty(string)) continue
            val j = font.lineHeight + 1
            val k = font.width(string)
            val l = 5
            val m = 5 + j * i
            DebugScreenOverlay.fill(poseStack, l - 1, m - 1, l + k + 1, m + j - 1, -1873784752)
            font.draw(poseStack, string, l.toFloat(), m.toFloat(), 0xE0E0E0)
        }
        poseStack.popPose()
    }
}