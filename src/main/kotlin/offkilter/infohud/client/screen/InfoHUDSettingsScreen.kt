package offkilter.infohud.client.screen

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import offkilter.infohud.client.InfoHUDSettings

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

        infoLinesButton = addRenderableWidget(Button(
            (width / 2) - 155,
            65,
            BUTTON_WIDTH,
            20,
            Component.translatable("offkilter.infohud.settings.infolines")
        ) {
            minecraft!!.setScreen(InfoHUDOptionsScreen(this))
        })

        doneButton = addRenderableWidget(Button(
            (width / 2) - (BUTTON_WIDTH / 2),
            height - 40,
            BUTTON_WIDTH,
            20,
            CommonComponents.GUI_DONE
        ) { onClose() })
    }

    override fun render(poseStack: PoseStack, i: Int, j: Int, f: Float) {
        renderDirtBackground(0)
        drawCenteredString(poseStack, font, title, width / 2, 8, 0xFFFFFF)
        super.render(poseStack, i, j, f)
    }

    companion object {
        private const val BUTTON_WIDTH = 150
    }
}