package offkilter.infohud.client

import com.google.gson.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import offkilter.infohud.infoline.InfoLine
import offkilter.infohud.infoline.InfoLineRegistry
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

    interface Listener {
        fun infoLineAdded(infoLine: InfoLine)
        fun infoLineRemoved(infoLine: InfoLine)
        fun infoLinesChanged()
    }

    private val listeners: MutableSet<Listener> = mutableSetOf<Listener>()

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

    private val mutableInfoLines: MutableList<InfoLine> by lazy {
        readInfoOverlays()
    }

    val currentInfoLines: List<InfoLine>
        get() = mutableInfoLines

    fun add(infoLine: InfoLine) {
        if (!mutableInfoLines.contains(infoLine)) {
            mutableInfoLines.add(infoLine)
            save()

            announceAdded(infoLine)
        }
    }

    fun remove(infoLine: InfoLine) {
        if (mutableInfoLines.contains(infoLine)) {
            mutableInfoLines.remove(infoLine)
            save()

            announceRemoved(infoLine)
        }
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
        save()
        announceChanged()
    }

    private fun save() {
        val root = JsonObject()
        val list = JsonArray()
        mutableInfoLines.forEach { t -> list.add(t.key) }
        root.add("info-overlays", list)

        try {
            helper.getWriter().use { writer ->
                val gson = GsonBuilder().setPrettyPrinting().create()
                gson.toJson(root, writer)
            }
        } catch (_: Exception) {

        }
    }

    private fun readInfoOverlays(): MutableList<InfoLine> {
        val overlays: MutableList<InfoLine> = ArrayList()
        try {
            val root = JsonParser.parseReader(helper.getReader())
            val obj = root.asJsonObject
            val overlayList = obj.getAsJsonArray("info-overlays")
            if (overlayList != null) {
                for (e in overlayList) {
                    val value = e.asJsonPrimitive.asString
                    InfoLineRegistry.infoLineWithKey(value)?.let { infoLine ->
                        overlays.add(infoLine)
                    } ?: run {
                        println("[InfoHUD] Ignoring unknown info name: $value")
                    }
                }
            }
        } catch (e: Exception) {
            println("[InfoHUD] Unable to read config file. Using defaults.")
            overlays.addAll(defaultInfoLines)
        }
        return overlays
    }

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

    private class DefaultFileHelper: FileHelper {
        override fun getReader(): Reader {
            return file.toFile().reader()
        }
        override fun getWriter(): Writer {
            return file.toFile().writer()
        }
    }
    companion object {
        private val file: Path by lazy {
            FabricLoader.getInstance().configDir.resolve("infohud.json")
        }

        val INSTANCE: InfoHUDSettings by lazy {
            InfoHUDSettings(DefaultFileHelper())
        }
    }

}