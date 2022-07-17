package offkilter.infohud.infoline

import net.minecraft.core.Registry
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class TargetedBlockInfoLine : InfoLineBase("targeted-block", SettingsCategory.BLOCK) {
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
}