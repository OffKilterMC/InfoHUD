/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package offkiltermc.infohud.client.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

@Environment(value = EnvType.CLIENT)
class InfoHUDOptionsScreen(
    private val lastScreen: Screen?
) : Screen(Component.translatable("offkilter.infohud.settings.title")) {
    private lateinit var availableInfoLines: InfoLineList
    private lateinit var selectedInfoLines: InfoLineList
    private lateinit var doneButton: Button
    private val model = InfoLineOptionsModel(this::populateLists)

    companion object {
        private const val LIST_WIDTH = 200
        private const val DONE_BUTTON_WIDTH = 150
    }

    override fun onClose() {
        model.commit()
        minecraft!!.setScreen(lastScreen)
    }

    override fun init() {
        doneButton = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { onClose() }
            .pos((width / 2) - (DONE_BUTTON_WIDTH / 2), (height - 40)).size(DONE_BUTTON_WIDTH, 20).build())
        availableInfoLines =
            InfoLineList(minecraft!!, LIST_WIDTH, height, Component.translatable("pack.available.title"))
        availableInfoLines.x = width / 2 - 4 - 200
        addWidget(availableInfoLines)
        selectedInfoLines =
            InfoLineList(minecraft!!, LIST_WIDTH, height, Component.translatable("pack.selected.title"))
        selectedInfoLines.x = width / 2 + 4
        addWidget(selectedInfoLines)
        populateLists()
    }

    private fun populateLists() {
        updateList(availableInfoLines, model.getUnselectedOptions())
        updateList(selectedInfoLines, model.getSelectedOptions())
    }

    private fun updateList(
        infoLineList: InfoLineList?,
        options: List<InfoLineOptionsModel.Option>
    ) {
        infoLineList?.let { list ->
            list.children().clear()
            options.forEach { item ->
                list.children().add(InfoLineEntry(this.minecraft!!, this, list, item))
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)
        availableInfoLines.render(guiGraphics, i, j, f)
        selectedInfoLines.render(guiGraphics, i, j, f)
        guiGraphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        renderDirtBackground(guiGraphics)
    }
}