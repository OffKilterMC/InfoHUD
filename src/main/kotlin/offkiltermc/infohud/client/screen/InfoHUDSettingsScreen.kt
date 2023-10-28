package offkiltermc.infohud.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import offkiltermc.infohud.client.InfoHUDSettings

class InfoHUDSettingsScreen(private val lastScreen: Screen?) :
    Screen(Component.translatable("offkilter.infohud.settings.title")) {
    private lateinit var doneButton: Button
    private lateinit var scaleButton: CycleButton<Int>
    private lateinit var positionButton: CycleButton<InfoHUDSettings.Position>
    private lateinit var infoLinesButton: Button

    override fun onClose() {
        //model.commit()
        minecraft!!.setScreen(lastScreen)
    }

    private fun stringifyScale(scale: Int): Component {
        return if (scale == 0) {
            Component.translatable("offkilter.infohud.settings.scale.auto")
        } else {
            Component.literal(scale.toString())
        }
    }

    private fun stringifyPosition(position: InfoHUDSettings.Position): Component {
        return when (position) {
            InfoHUDSettings.Position.TOP_LEFT -> Component.translatable("offkilter.infohud.settings.position.topleft")
            InfoHUDSettings.Position.TOP_RIGHT -> Component.translatable("offkilter.infohud.settings.position.topright")
        }
    }

    override fun init() {
        val scaleMax = minecraft!!.window.calculateScale(0, minecraft!!.isEnforceUnicode)
        scaleButton = addRenderableWidget(CycleButton.builder<Int> { obj -> stringifyScale(obj) }
            .withValues((0..scaleMax).toList())
            .withInitialValue(InfoHUDSettings.INSTANCE.scale)
            .create(
                (width / 2) - 155,
                40,
                BUTTON_WIDTH,
                20,
                Component.translatable("offkilter.infohud.settings.scale")
            ) { _: CycleButton<Int>?, value: Int ->
                InfoHUDSettings.INSTANCE.scale = value
            })

        positionButton = addRenderableWidget(CycleButton.builder<InfoHUDSettings.Position> { obj -> stringifyPosition(obj) }
            .withValues(InfoHUDSettings.Position.values().asList())
            .withInitialValue(InfoHUDSettings.INSTANCE.position)
            .create(
                (width / 2) + 5,
                40,
                BUTTON_WIDTH,
                20,
                Component.translatable("offkilter.infohud.settings.position")
            ) { _: CycleButton<InfoHUDSettings.Position>?, value: InfoHUDSettings.Position ->
                InfoHUDSettings.INSTANCE.position = value
            })

        infoLinesButton = addRenderableWidget(Button.builder(Component.translatable("offkilter.infohud.settings.infolines")) { minecraft!!.setScreen(InfoHUDOptionsScreen(this)) }.pos((width / 2) - 155,
            65).size(BUTTON_WIDTH, 20).build())

        doneButton = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { onClose() }
            .pos((width / 2) - (BUTTON_WIDTH / 2), height - 40)
            .size(BUTTON_WIDTH, 20).build())
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)
        guiGraphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        renderDirtBackground(guiGraphics)
    }

    companion object {
        private const val BUTTON_WIDTH = 150
    }
}