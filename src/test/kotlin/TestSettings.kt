import offkilter.infohud.client.InfoHUDSettings
import offkilter.infohud.infoline.InfoLineRegistry
import java.io.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSettings {
    private inner class TestFileAccess(private val name: String) : InfoHUDSettings.FileHelper {
        val writer = StringWriter()

        override fun getReader(): Reader {
            val url = Thread.currentThread().contextClassLoader.getResource(name)
            val file = File(url!!.path)
            return file.reader()
        }

        override fun getWriter(): Writer {
           return writer
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
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

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
        val settings = InfoHUDSettings(TestFileAccess("notthere"))

        val items = settings.infoLines.map { it.key }

        assertEquals(items, expectedDefaultInfoLines)
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }

    @Test
    fun testDropsUnknownKeys() {
        val settings = InfoHUDSettings(TestFileAccess("badkeys.json"))

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
        val settings = InfoHUDSettings(TestFileAccess("badfile.json"))

        val items = settings.infoLines.map { it.key }

        // since it's unreadable, we should get the defaults.
        assertEquals(items, expectedDefaultInfoLines)
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }

    @Test
    fun testTryMovingTopItemUp() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val origFirst = settings.infoLines.first()

        // moving top level up should do nothing
        settings.move(origFirst, InfoHUDSettings.Direction.UP)

        val firstItemKey = settings.infoLines.first().key
        assertEquals(firstItemKey, origFirst.key)
    }

    @Test
    fun testTryMovingBottomItemDown() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val origLast = settings.infoLines.last()

        // moving should do nothing
        settings.move(origLast, InfoHUDSettings.Direction.DOWN)

        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, origLast.key)
    }

    @Test
    fun testTryMovingTopItemDown() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val secondItem = settings.infoLines[1]

        // moving down should swap with second
        settings.move(settings.infoLines.first(), InfoHUDSettings.Direction.DOWN)

        val firstItemKey = settings.infoLines.first().key
        assertEquals(firstItemKey, secondItem.key)
    }

    @Test
    fun testTryMovingBottomItemUp() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val secondToLastItem = settings.infoLines[settings.infoLines.size - 2]
        // moving up should swap with second-to-last
        settings.move(settings.infoLines.last(), InfoHUDSettings.Direction.UP)

        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, secondToLastItem.key)
    }

    @Test
    fun testAdd() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        settings.add(InfoLineRegistry.MOOD)
        val lastItemKey = settings.infoLines.last().key
        assertEquals(lastItemKey, InfoLineRegistry.MOOD.key)
    }

    @Test
    fun testAddExistingShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val count = settings.infoLines.size

        // add something we know is there
        settings.add(InfoLineRegistry.FPS)
        assertEquals(count, settings.infoLines.size)
    }

    @Test
    fun testRemove() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        settings.remove(InfoLineRegistry.FPS)
        assertEquals(-1, settings.infoLines.indexOf(InfoLineRegistry.FPS))
    }

    @Test
    fun testRemoveNonexistentShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val count = settings.infoLines.size

        settings.remove(InfoLineRegistry.MOOD)
        assertEquals(count, settings.infoLines.size)
    }

    @Test
    fun testSetActiveInfoLines() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val itemsToSet = listOf(
            InfoLineRegistry.BIOME,
            InfoLineRegistry.LOCATION
        )

        settings.setActiveInfoLines(itemsToSet)
        assertEquals(settings.infoLines, itemsToSet)
    }

    @Test
    fun testBadScaleAndPositionValue() {
        val settings = InfoHUDSettings(TestFileAccess("badvalues.json"))

        // should be the default
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }

    @Test
    fun testBadScaleAndPositionValueType() {
        val settings = InfoHUDSettings(TestFileAccess("badvaluetypes.json"))

        // should be the default
        assertEquals(settings.scale, 2)
        assertEquals(settings.position, InfoHUDSettings.Position.TOP_LEFT)
    }
}