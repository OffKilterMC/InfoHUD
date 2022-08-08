/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package offkilter.infohud.client.screen

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
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
        doneButton = addRenderableWidget(Button(
            (width / 2) - (DONE_BUTTON_WIDTH / 2), height - 40, DONE_BUTTON_WIDTH, 20, CommonComponents.GUI_DONE
        ) { onClose() })
        availableInfoLines =
            InfoLineList(minecraft!!, LIST_WIDTH, height, Component.translatable("pack.available.title"))
        availableInfoLines.setLeftPos(width / 2 - 4 - 200)
        addWidget(availableInfoLines)
        selectedInfoLines =
            InfoLineList(minecraft!!, LIST_WIDTH, height, Component.translatable("pack.selected.title"))
        selectedInfoLines.setLeftPos(width / 2 + 4)
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

    override fun render(poseStack: PoseStack, i: Int, j: Int, f: Float) {
        renderDirtBackground(0)
        availableInfoLines.render(poseStack, i, j, f)
        selectedInfoLines.render(poseStack, i, j, f)
        drawCenteredString(poseStack, font, title, width / 2, 8, 0xFFFFFF)
        super.render(poseStack, i, j, f)
    }

}