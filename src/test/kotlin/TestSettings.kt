import offkilter.infohud.client.InfoHUDSettings
import offkilter.infohud.infoline.InfoLineRegistry
import java.io.File
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSettings {
    private fun resourceFileSpec(name: String): Lazy<File> {
        return lazy {
            val url = Thread.currentThread().contextClassLoader.getResource("$name.json")
            File(url!!.path)
        }
    }

    private fun tempFileSpec(name: String) : Lazy<File> {
        return lazy {
            val tempFile: File = File.createTempFile(name, ".json")
            tempFile.deleteOnExit()
            tempFile
        }
    }

    private open inner class TestFileAccess(val readFile: Lazy<File>, val writeFile: Lazy<File>? = null) :
        InfoHUDSettings.FileHelper {
        override fun getReader(): Reader {
            return readFile.value.reader()
        }

        override fun getWriter(): Writer {
            return writeFile?.value?.writer() ?: StringWriter()
        }
    }

    private val expectedDefaultInfoLines = listOf(
        "fps",
        "location",
        "block",
        "direction",
        "biome",
        "client-light",
        "targeted-block",
        "targeted-fluid"
    )

    @Test
    fun testBasicFileReading() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val items = settings.infoLines.map { it.key }

        val expected = listOf(
            "fps",
            "location",
            "block",
            "direction",
            "biome",
            "client-light",
            "targeted-block",
            "targeted-fluid",
            "tick-perf",
            "local-difficulty"
        )

        assertEquals(items, expected)
    }

    @Test
    fun testDefaults() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("notthere")))

        val items = settings.infoLines.map { it.key }

        assertEquals(items, expectedDefaultInfoLines)
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }

    @Test
    fun testDropsUnknownKeys() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("badkeys")))

        val items = settings.infoLines.map { it.key }

        val expected = listOf(
            "fps",
            "location",
            "block",
            "biome",
            "client-light",
            "targeted-fluid",
            "tick-perf"
        )

        assertEquals(items, expected)
    }

    @Test
    fun testInvalidJSON() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("badfile")))

        val items = settings.infoLines.map { it.key }

        // since it's unreadable, we should get the defaults.
        assertEquals(items, expectedDefaultInfoLines)
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }

    @Test
    fun testTryMovingTopItemUp() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val origFirst = settings.infoLines.first()

        // moving top level up should do nothing
        settings.move(origFirst, InfoHUDSettings.Direction.UP)

        val firstItemKey = settings.infoLines.first().key
        assertEquals(firstItemKey, origFirst.key)
    }

    @Test
    fun testTryMovingBottomItemDown() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val origLast = settings.infoLines.last()

        // moving should do nothing
        settings.move(origLast, InfoHUDSettings.Direction.DOWN)

        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, origLast.key)
    }

    @Test
    fun testTryMovingTopItemDown() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val secondItem = settings.infoLines[1]

        // moving down should swap with second
        settings.move(settings.infoLines.first(), InfoHUDSettings.Direction.DOWN)

        val firstItemKey = settings.infoLines.first().key
        assertEquals(firstItemKey, secondItem.key)
    }

    @Test
    fun testTryMovingBottomItemUp() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val secondToLastItem = settings.infoLines[settings.infoLines.size - 2]
        // moving up should swap with second-to-last
        settings.move(settings.infoLines.last(), InfoHUDSettings.Direction.UP)

        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, secondToLastItem.key)
    }

    @Test
    fun testAdd() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        settings.add(InfoLineRegistry.MOOD)
        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, InfoLineRegistry.MOOD.key)
    }

    @Test
    fun testAddExistingShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("test")))

        val count = settings.infoLines.size

        // add something we know is there
        settings.add(InfoLineRegistry.FPS)
        assertEquals(count, settings.infoLines.size)
    }

    @Test
    fun testRemove() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        settings.remove(InfoLineRegistry.FPS)
        assertEquals(-1, settings.infoLines.indexOf(InfoLineRegistry.FPS))
    }

    @Test
    fun testRemoveNonexistentShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val count = settings.infoLines.size

        settings.remove(InfoLineRegistry.MOOD)
        assertEquals(count, settings.infoLines.size)
    }

    @Test
    fun testSetActiveInfoLines() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("testfile")))

        val itemsToSet = listOf(
            InfoLineRegistry.BIOME,
            InfoLineRegistry.LOCATION
        )

        settings.setActiveInfoLines(itemsToSet)
        assertEquals(settings.infoLines, itemsToSet)
    }

    @Test
    fun testBadScaleAndPositionValue() {
        val settings = InfoHUDSettings(TestFileAccess(resourceFileSpec("badvalues")))

        // should be the default
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }


    @Test
    fun testSetScaleAndPositionAndReReadFile() {
        val tempFileSpec = tempFileSpec("InfoHUDSettings")
        val settings = InfoHUDSettings(
            TestFileAccess(
                resourceFileSpec("testfile"),
                tempFileSpec
            )
        )

        settings.scale = 4
        settings.position = InfoHUDSettings.Position.TOP_RIGHT

        val settings2 = InfoHUDSettings(TestFileAccess(tempFileSpec, null))
        assertEquals(4, settings2.scale)
        assertEquals(InfoHUDSettings.Position.TOP_RIGHT, settings2.position)
    }
}