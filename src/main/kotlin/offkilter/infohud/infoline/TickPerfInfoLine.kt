package offkilter.infohud.infoline

import offkilter.infohud.client.PerfCounters

class TickPerfInfoLine : InfoLineBase("tick-perf", SettingsCategory.PERF) {
    override fun getInfoString(env: InfoLineEnvironment): String {
        val mspt = PerfCounters.mspt / 1000000.0f
        val tps = PerfCounters.tps.toFloat() //1000.0f / Math.max(50.0f, mspt);
        return String.format("TPS: %.1f, MSPT: %.1f", tps, mspt)
    }
}