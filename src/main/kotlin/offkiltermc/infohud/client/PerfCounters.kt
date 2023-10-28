package offkiltermc.infohud.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.Util
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

@Environment(value = EnvType.CLIENT)
object PerfCounters {
    private var frameCount = 0
    private var lastTime: Long = 0
    private var lastLocation: Vec3 = Vec3(0.0, 0.0, 0.0)
    private var lastSpeedTime: Long = 0

    var fps = 0
    var mspt: Long = 0
        private set
    var tps: Long = 0
        private set
    var speed = 0.0
        private set

    fun setPerfInfo(mspt: Long, tps: Long) {
        PerfCounters.mspt = mspt
        PerfCounters.tps = tps
    }

    fun updateFPS() {
        frameCount += 1
        while (Util.getMillis() >= lastTime + 1000L) {
            fps = frameCount
            lastTime += 1000L
            frameCount = 0
        }
    }

    fun updateSpeed(player: LocalPlayer) {
        val now = Util.getMillis()
        while (now >= lastSpeedTime + 500L) {
            val current = player.position()
            speed = if (!current.equals(lastLocation)) {
                val dx = current.x - lastLocation.x
                val dy = current.y - lastLocation.y
                val dz = current.z - lastLocation.z

                val dist = sqrt(dx * dx + dy * dy + dz * dz)
                lastLocation = current
                dist * 2
            } else {
                0.0
            }
            lastSpeedTime = now
        }
    }
}