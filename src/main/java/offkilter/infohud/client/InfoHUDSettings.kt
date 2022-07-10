package offkilter.infohud.client

import com.google.gson.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.nio.file.Path

@Environment(value = EnvType.CLIENT)
object InfoHUDSettings {
    enum class Direction {
        UP,
        DOWN
    }

    private val file: Path
        get() = FabricLoader.getInstance().configDir.resolve("infohud.json")

    private val mutableInfoLines: MutableList<InfoLine> by lazy {
        readInfoOverlays()
    }

    val currentInfoLines: List<InfoLine>
        get() = mutableInfoLines

    fun add(infoLine: InfoLine) {
        if (!mutableInfoLines.contains(infoLine)) {
            mutableInfoLines.add(infoLine)

            // later: come up with something more generic. A listener, e.g.
            if (infoLine == InfoLine.TICK_PERF) {
                InfoHUDClient.syncTickPerfEnabled()
            } else if (infoLine == InfoLine.SERVER_LIGHT) {
                InfoHUDClient.syncServerLight()
            }

            save()
        }
    }

    fun remove(infoLine: InfoLine) {
        mutableInfoLines.remove(infoLine)

        // later: come up with something more generic. A listener, e.g.
        if (infoLine == InfoLine.TICK_PERF) {
            InfoHUDClient.syncTickPerfEnabled()
        } else if (infoLine == InfoLine.SERVER_LIGHT) {
            InfoHUDClient.syncServerLight()
        }

        save()
    }

    fun move(infoLine: InfoLine, direction: Direction) {
        val idx = mutableInfoLines.indexOf(infoLine)
        if (idx == -1) {
            return
        }

        when (direction) {
            Direction.UP -> {
                if (idx != 0) {
                    val temp = mutableInfoLines[idx - 1]
                    mutableInfoLines[idx - 1] = infoLine
                    mutableInfoLines[idx] = temp
                    save()
                }
            }
            Direction.DOWN -> {
                if (idx < (mutableInfoLines.size - 1)) {
                    val temp = mutableInfoLines[idx + 1]
                    mutableInfoLines[idx + 1] = infoLine
                    mutableInfoLines[idx] = temp
                    save()
                }
            }
        }
    }

    fun setActiveInfoLines(infoLines: List<InfoLine>) {
        mutableInfoLines.clear()
        mutableInfoLines.addAll(infoLines)

        InfoHUDClient.syncTickPerfEnabled()
        InfoHUDClient.syncServerLight()

        save()
    }

    private fun save() {
        val root = JsonObject()
        val list = JsonArray()
        mutableInfoLines.forEach { t -> list.add(t.key) }
        root.add("info-overlays", list)

        try {
            file.toFile().writer().use { writer ->
                val gson = GsonBuilder().setPrettyPrinting().create()
                gson.toJson(root, writer)
            }
        } catch (_: Exception) {

        }
    }

    private fun readInfoOverlays(): MutableList<InfoLine> {
        val overlays: MutableList<InfoLine> = ArrayList()
        try {
            val root = JsonParser.parseReader(FileReader(file.toFile()))
            val obj = root.asJsonObject
            val overlayList = obj.getAsJsonArray("info-overlays")
            if (overlayList != null) {
                for (e in overlayList) {
                    val value = e.asJsonPrimitive.asString
                    val infoLine = InfoLine.BY_NAME[value]
                    if (infoLine != null) {
                        overlays.add(infoLine)
                    } else {
                        println("[InfoHUD] Ignoring unknown info name: $value")
                    }
                }
            }
        } catch (e: Exception) {
            println("[InfoHUD] Unable to read config file. Using defaults.")
            overlays.addAll(java.util.List.of(*defaultInfoLines))
        }
        return overlays
    }

    private val defaultInfoLines = arrayOf(
        InfoLine.FPS,
        InfoLine.LOCATION,
        InfoLine.BLOCK,
        InfoLine.DIRECTION,
        InfoLine.BIOME,
        InfoLine.CLIENT_LIGHT,
        InfoLine.TARGETED_BLOCK,
        InfoLine.TARGETED_FLUID
    )
}