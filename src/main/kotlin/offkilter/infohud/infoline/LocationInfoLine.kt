package offkilter.infohud.infoline

import net.minecraft.network.chat.Component

class LocationInfoLine : InfoLineBase("location", SettingsCategory.LOCATION) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        val camera = env.camera
        return Component.literal(String.format("X: %.3f Y: %.5f Z: %.3f", camera.x, camera.y, camera.z))
    }
}
