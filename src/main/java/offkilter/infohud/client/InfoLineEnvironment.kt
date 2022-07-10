package offkilter.infohud.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.LevelChunk

@Environment(value = EnvType.CLIENT)
data class InfoLineEnvironment(
    val minecraft: Minecraft,
    val level: ClientLevel,
    val blockPos: BlockPos,
    val camera: Entity,
    val chunk: LevelChunk
)