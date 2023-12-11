package offkiltermc.infohud.infoline

import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth

class DirectionInfoLine : InfoLineBase("direction", SettingsCategory.LOCATION) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        val direction = env.camera.direction
        val towards = when (direction) {
            Direction.NORTH -> "Negative Z"
            Direction.SOUTH -> "Positive Z"
            Direction.WEST -> "Negative X"
            Direction.EAST -> "Positive X"
            else -> "Unknown"
        }
        return Component.literal(
            String.format(
                "Facing: %s (%s) (%.1f / %.1f)", direction, towards,
                Mth.wrapDegrees(env.camera.yRot),
                Mth.wrapDegrees(env.camera.xRot)
            )
        )
    }
}