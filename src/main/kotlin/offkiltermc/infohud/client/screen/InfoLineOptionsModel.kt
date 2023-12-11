package offkiltermc.infohud.client.screen

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import offkiltermc.infohud.client.InfoHUDSettings
import offkiltermc.infohud.infoline.InfoLine
import offkiltermc.infohud.infoline.InfoLineRegistry

class InfoLineOptionsModel(private val onListChanged: Runnable) {
    private val selectedInfoLines = mutableListOf<InfoLine>()
    private val unselectedInfoLines = mutableListOf<InfoLine>()

    init {
        val all = InfoLineRegistry.allInfoLines.sortedBy { it.name }
        val current = InfoHUDSettings.INSTANCE.infoLines

        selectedInfoLines.addAll(current)

        unselectedInfoLines.addAll(all)
        unselectedInfoLines.removeAll(current)
    }

    fun getSelectedOptions(): List<Option> {
        return selectedInfoLines.map { i -> SelectedOption(i) }
    }

    fun getUnselectedOptions(): List<Option> {
        return unselectedInfoLines.map { i -> UnselectedOption(i) }
    }

    fun commit() {
        InfoHUDSettings.INSTANCE.setActiveInfoLines(selectedInfoLines)
    }

    private fun selectItem(infoLine: InfoLine) {
        unselectedInfoLines.remove(infoLine)
        selectedInfoLines.add(0, infoLine)
        onListChanged.run()
    }

    private fun unselectItem(infoLine: InfoLine) {
        selectedInfoLines.remove(infoLine)
        unselectedInfoLines.add(0, infoLine)
        unselectedInfoLines.sortBy { it.name }
        onListChanged.run()
    }

    interface Option {
        val name: Component
        val desc: Component
        val icon: ResourceLocation
        val canAdd: Boolean
        val canRemove: Boolean

        fun canMoveUp(): Boolean {
            return false
        }

        fun canMoveDown(): Boolean {
            return false
        }

        fun moveUp() {}
        fun moveDown() {}

        fun toggle()
    }

    private inner class SelectedOption(private val infoLine: InfoLine) : Option {
        override val name: Component = Component.translatable(infoLine.name)
        override val desc: Component = Component.translatable(infoLine.description)
        override val icon = infoLine.category.iconResource
        override val canAdd = false
        override val canRemove = true

        override fun canMoveUp(): Boolean {
            val idx = selectedInfoLines.indexOf(infoLine)
            return (idx != -1 && idx > 0)
        }

        override fun canMoveDown(): Boolean {
            val idx = selectedInfoLines.indexOf(infoLine)
            return (idx != -1 && idx < (selectedInfoLines.size - 1))
        }

        override fun moveUp() {
            val idx = selectedInfoLines.indexOf(infoLine)
            val itemAbove = selectedInfoLines[idx - 1]
            selectedInfoLines[idx - 1] = infoLine
            selectedInfoLines[idx] = itemAbove
            onListChanged.run()
        }

        override fun moveDown() {
            val idx = selectedInfoLines.indexOf(infoLine)
            val itemBelow = selectedInfoLines[idx + 1]
            selectedInfoLines[idx + 1] = infoLine
            selectedInfoLines[idx] = itemBelow
            onListChanged.run()
        }

        override fun toggle() {
            unselectItem(infoLine)
        }
    }

    private inner class UnselectedOption(private val infoLine: InfoLine) : Option {
        override val name: Component = Component.translatable(infoLine.name)
        override val desc: Component = Component.translatable(infoLine.description)
        override val icon = infoLine.category.iconResource
        override val canAdd = true
        override val canRemove = true

        override fun toggle() {
            selectItem(infoLine)
        }
    }
}