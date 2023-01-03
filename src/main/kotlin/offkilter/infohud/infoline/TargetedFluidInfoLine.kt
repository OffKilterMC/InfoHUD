package offkilter.infohud.infoline

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class TargetedFluidInfoLine : InfoLineBase("targeted-fluid", SettingsCategory.BLOCK) {
    override fun getInfoString(env: InfoLineEnvironment): Component? {
        val targetedFluid = env.camera.pick(20.0, 0.0f, true)
        if (targetedFluid.type == HitResult.Type.BLOCK) {
            val targetedBlockPos = (targetedFluid as BlockHitResult).blockPos
            val fluidState = env.level.getFluidState(targetedBlockPos)
            if (fluidState != null) {
                val fluidType = BuiltInRegistries.FLUID.getKey(fluidState.type)
                if (fluidType.toString() != "minecraft:empty") {
                    return Component.literal(
                        "Targeted Fluid: " + targetedBlockPos.x + ", " + targetedBlockPos.y + ", " + targetedBlockPos.z + " " + BuiltInRegistries.FLUID.getKey(
                            fluidState.type
                        )
                    )
                }
            }
        }
        return null
    }


}