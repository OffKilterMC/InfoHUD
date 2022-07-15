package offkilter.infohud.infoline

class LocationInfoLine : InfoLineBase("location", SettingsCategory.LOCATION) {
    override fun getInfoString(env: InfoLineEnvironment): String {
        val camera = env.camera
        return String.format("X: %.3f Y: %.5f Z: %.3f", camera.x, camera.y, camera.z)
    }
}
