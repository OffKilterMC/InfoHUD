package offkiltermc.infohud.infoline

import net.minecraft.network.chat.Component
import offkiltermc.infohud.client.PerfCounters

class TickPerfInfoLine : InfoLineBase("tick-perf", SettingsCategory.PERF) {
    override fun getInfoString(env: InfoLineEnvironment): Component {
        val mspt = PerfCounters.mspt / 1000000.0f
        val tps = PerfCounters.tps.toFloat() //1000.0f / Math.max(50.0f, mspt);
        return Component.literal(String.format("TPS: %.1f, MSPT: %.1f", tps, mspt))
    }
}