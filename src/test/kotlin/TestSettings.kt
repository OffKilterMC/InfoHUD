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

    private val expectedDefaults = listOf(
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

        val items = settings.currentInfoLines.map { it.key }

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

        val items = settings.currentInfoLines.map { it.key }

        assertEquals(items, expectedDefaults)
    }

    @Test
    fun testDropsUnknownKeys() {
        val settings = InfoHUDSettings(TestFileAccess("badkeys.json"))

        val items = settings.currentInfoLines.map { it.key }

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

        val items = settings.currentInfoLines.map { it.key }

        // since it's unreadable, we should get the defaults.
        assertEquals(items, expectedDefaults)
    }

    @Test
    fun testTryMovingTopItemUp() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val origFirst = settings.currentInfoLines.first()

        // moving top level up should do nothing
        settings.move(origFirst, InfoHUDSettings.Direction.UP)

        val firstItemKey = settings.currentInfoLines.first().key
        assertEquals(firstItemKey, origFirst.key)
    }

    @Test
    fun testTryMovingBottomItemDown() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val origLast = settings.currentInfoLines.last()

        // moving should do nothing
        settings.move(origLast, InfoHUDSettings.Direction.DOWN)

        val lastItemKey = settings.currentInfoLines.last().key
        assertEquals(lastItemKey, origLast.key)
    }

    @Test
    fun testTryMovingTopItemDown() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val secondItem = settings.currentInfoLines[1]

        // moving down should swap with second
        settings.move(settings.currentInfoLines.first(), InfoHUDSettings.Direction.DOWN)

        val firstItemKey = settings.currentInfoLines.first().key
        assertEquals(firstItemKey, secondItem.key)
    }

    @Test
    fun testTryMovingBottomItemUp() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val secondToLastItem = settings.currentInfoLines[settings.currentInfoLines.size - 2]
        // moving up should swap with second-to-last
        settings.move(settings.currentInfoLines.last(), InfoHUDSettings.Direction.UP)

        val lastItemKey = settings.currentInfoLines.last().key
        assertEquals(lastItemKey, secondToLastItem.key)
    }

    @Test
    fun testAdd() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        settings.add(InfoLineRegistry.MOOD)
        val lastItemKey = settings.currentInfoLines.last().key
        assertEquals(lastItemKey, InfoLineRegistry.MOOD.key)
    }

    @Test
    fun testAddExistingShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val count = settings.currentInfoLines.size

        // add something we know is there
        settings.add(InfoLineRegistry.FPS)
        assertEquals(count, settings.currentInfoLines.size)
    }

    @Test
    fun testRemove() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        settings.remove(InfoLineRegistry.FPS)
        assertEquals(-1, settings.currentInfoLines.indexOf(InfoLineRegistry.FPS))
    }

    @Test
    fun testRemoveNonexistentShouldFail() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val count = settings.currentInfoLines.size

        settings.remove(InfoLineRegistry.MOOD)
        assertEquals(count, settings.currentInfoLines.size)
    }

    @Test
    fun testSetActiveInfoLines() {
        val settings = InfoHUDSettings(TestFileAccess("testfile.json"))

        val itemsToSet = listOf(
            InfoLineRegistry.BIOME,
            InfoLineRegistry.LOCATION
        )

        settings.setActiveInfoLines(itemsToSet)
        assertEquals(settings.currentInfoLines, itemsToSet)
    }
}