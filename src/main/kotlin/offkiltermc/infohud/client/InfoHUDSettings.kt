package offkiltermc.infohud.client

import com.google.gson.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import offkiltermc.infohud.infoline.InfoLine
import offkiltermc.infohud.infoline.InfoLineRegistry
import java.io.Reader
import java.io.Writer
import java.nio.file.Path

@Environment(value = EnvType.CLIENT)
class InfoHUDSettings(private val helper: FileHelper) {
    interface FileHelper {
        fun getReader(): Reader
        fun getWriter(): Writer
    }

    enum class Direction {
        UP,
        DOWN
    }

    enum class Position(val key: String) {
        TOP_LEFT("top-left"),
        TOP_RIGHT("top-right")
    }

    interface Listener {
        fun infoLineAdded(infoLine: InfoLine)
        fun infoLineRemoved(infoLine: InfoLine)
        fun infoLinesChanged()
    }

    data class Config(var position: Position = defaultPosition,
                      var scale: Int = defaultScale,
                      val infoLines:MutableList<InfoLine> = defaultInfoLines.toMutableList())

    private val listeners: MutableSet<Listener> = mutableSetOf<Listener>()

    var scale: Int
        get() = config.scale
        set(value) = setScaleInternal(value)

    var position: Position
        get() = config.position
        set(value) = setPositionInternal(value)

    val infoLines: List<InfoLine>
        get() = config.infoLines

    private val config: Config by lazy {
        load() ?: Config()
    }

    private fun setScaleInternal(value: Int) {
        config.scale = value
        save()
    }

    private fun setPositionInternal(value: Position) {
        config.position = value
        save()
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun announceAdded(infoLine: InfoLine) {
        listeners.forEach {
            it.infoLineAdded(infoLine)
        }
    }

    private fun announceRemoved(infoLine: InfoLine) {
        listeners.forEach {
            it.infoLineRemoved(infoLine)
        }
    }

    private fun announceChanged() {
        listeners.forEach {
            it.infoLinesChanged()
        }
    }

    fun add(infoLine: InfoLine) {
        if (!config.infoLines.contains(infoLine)) {
            config.infoLines.add(infoLine)
            save()

            announceAdded(infoLine)
        }
    }

    fun remove(infoLine: InfoLine) {
        if (config.infoLines.contains(infoLine)) {
            config.infoLines.remove(infoLine)
            save()

            announceRemoved(infoLine)
        }
    }

    fun move(infoLine: InfoLine, direction: Direction) {
        val idx = config.infoLines.indexOf(infoLine)
        if (idx == -1) {
            return
        }

        when (direction) {
            Direction.UP -> {
                if (idx != 0) {
                    val temp = config.infoLines[idx - 1]
                    config.infoLines[idx - 1] = infoLine
                    config.infoLines[idx] = temp
                    save()
                }
            }
            Direction.DOWN -> {
                if (idx < (config.infoLines.size - 1)) {
                    val temp = config.infoLines[idx + 1]
                    config.infoLines[idx + 1] = infoLine
                    config.infoLines[idx] = temp
                    save()
                }
            }
        }
    }

    fun setActiveInfoLines(infoLines: List<InfoLine>) {
        config.infoLines.clear()
        config.infoLines.addAll(infoLines)
        save()
        announceChanged()
    }

    private fun save() {
        val root = JsonObject()
        val list = JsonArray()
        config.infoLines.forEach { t -> list.add(t.key) }
        root.add(overlayKey, list)
        root.add(scaleKey, JsonPrimitive(config.scale))
        root.add(positionKey, JsonPrimitive(config.position.key))

        try {
            helper.getWriter().use { writer ->
                val gson = GsonBuilder().setPrettyPrinting().create()
                gson.toJson(root, writer)
            }
        } catch (_: Exception) {

        }
    }

    private fun readAndValidateScale(obj: JsonObject): Int {
        val scale = obj.getAsJsonPrimitive(scaleKey)?.asInt ?: defaultScale
        return if (scale < minScale || scale > maxScale) {
            println("[InfoHUD] Invalid value for scale ($scale). Using default.")
            defaultScale
        } else {
            scale
        }
    }

    private fun readAndValidatePosition(obj: JsonObject): Position {
        return obj.getAsJsonPrimitive(positionKey)?.asString?.let { position ->
            for (v in Position.values()) {
                if (position == v.key) {
                    return v
                }
            }
            println("[InfoHUD] Invalid value for position ($position). Using default.")
            return defaultPosition
        } ?: run {
            defaultPosition
        }
    }

    private fun load(): Config? {
        val overlays: MutableList<InfoLine> = ArrayList()
        try {
            val root = JsonParser.parseReader(helper.getReader())
            val obj = root.asJsonObject
            val overlayList = obj.getAsJsonArray(overlayKey)
            if (overlayList != null) {
                for (e in overlayList) {
                    val value = e.asJsonPrimitive.asString
                    InfoLineRegistry.infoLineWithKey(value)?.let { infoLine ->
                        overlays.add(infoLine)
                    } ?: run {
                        println("[InfoHUD] Ignoring unknown info-overlay name: $value")
                    }
                }
            }
            val scale = readAndValidateScale(obj)
            val position = readAndValidatePosition(obj)
            return Config(scale = scale, position = position, infoLines = overlays)
        } catch (e: Exception) {
            println("[InfoHUD] Unable to read config file. Using defaults. (${e.message})")
        }
        return null
    }

    private class DefaultFileHelper: FileHelper {
        override fun getReader(): Reader {
            return file.toFile().reader()
        }
        override fun getWriter(): Writer {
            return file.toFile().writer()
        }
    }
    companion object {
        // our json keys
        private const val overlayKey = "info-overlays"
        private const val scaleKey = "scale"
        private const val positionKey = "position"
        private val defaultPosition = Position.TOP_LEFT
        private const val defaultScale = 2
        private const val minScale = 0
        private const val maxScale = 6
        private val defaultInfoLines = listOf(
            InfoLineRegistry.FPS,
            InfoLineRegistry.LOCATION,
            InfoLineRegistry.BLOCK,
            InfoLineRegistry.DIRECTION,
            InfoLineRegistry.BIOME,
            InfoLineRegistry.CLIENT_LIGHT,
            InfoLineRegistry.TARGETED_BLOCK,
            InfoLineRegistry.TARGETED_FLUID
        )

        private val file: Path by lazy {
            FabricLoader.getInstance().configDir.resolve("infohud.json")
        }

        val INSTANCE: InfoHUDSettings by lazy {
            InfoHUDSettings(DefaultFileHelper())
        }
    }

}