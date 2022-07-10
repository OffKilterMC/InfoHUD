package offkilter.infohud.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.Util

@Environment(value = EnvType.CLIENT)
object PerfCounters {
    private var frameCount = 0
    private var lastTime: Long = 0

    var fps = 0
    var mspt: Long = 0
        private set
    var tps: Long = 0
        private set

    fun setPerfInfo(mspt: Long, tps: Long) {
        this.mspt = mspt
        this.tps = tps
    }

    fun updateFPS() {
        frameCount += 1
        while (Util.getMillis() >= lastTime + 1000L) {
            fps = frameCount
            lastTime += 1000L
            frameCount = 0
        }
    }
}