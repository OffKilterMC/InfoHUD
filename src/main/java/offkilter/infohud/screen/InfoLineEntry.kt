package offkilter.infohud.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence


class InfoLineEntry(
    private val minecraft: Minecraft,
    private val screen: Screen,
    private val parent: InfoLineList,
    private val option: InfoLineOptionsModel.Option
) : ObjectSelectionList.Entry<InfoLineEntry?>() {
    private enum class ArrowTextureIcon(val xOffset: Int) {
        ADD(0),
        REMOVE(ICON_SIZE),
        DOWN(ICON_SIZE * 2),
        UP(ICON_SIZE * 3)
    }

    private fun getArrowTextureXY(icon: ArrowTextureIcon, isHover: Boolean): Point {
        return Point(
            icon.xOffset,
            if (isHover) ICON_SIZE else 0
        )
    }

    data class Point(
        val x: Int, val y: Int
    )

    data class Rect(
        val x: Int, val y: Int, val width: Int, val height: Int
    ) {
        val maxX = x + width
        val maxY = y + height

        fun contains(point: Point): Boolean {
            return !(point.x < this.x || point.x > this.maxX || point.y < this.y || point.y > this.maxY)
        }
    }

    private fun addRemoveButtonRect(rowRect: Rect): Rect {
        return Rect(
            rowRect.x, rowRect.y, ICON_SIZE, ICON_SIZE
        )
    }

    private fun upButtonRect(rowRect: Rect): Rect {
        return Rect(
            rowRect.maxX - (ICON_SIZE * 2), rowRect.y, ICON_SIZE, ICON_SIZE
        )
    }

    private fun downButtonRect(rowRect: Rect): Rect {
        return Rect(
            rowRect.maxX - ICON_SIZE, rowRect.y, ICON_SIZE, ICON_SIZE
        )
    }

    private fun possiblyTruncatedString(minecraft: Minecraft, component: Component, maxWidth: Int): FormattedCharSequence {
        val i = minecraft.font.width(component)
        if (i > maxWidth) {
            val formattedText = FormattedText.composite(
                minecraft.font.substrByWidth(
                    component, maxWidth - minecraft.font.width(
                        TOO_LONG_NAME_SUFFIX
                    )
                ), FormattedText.of(TOO_LONG_NAME_SUFFIX)
            )
            return Language.getInstance().getVisualOrder(formattedText)
        }
        return component.visualOrderText
    }

    override fun render(
        poseStack: PoseStack,
        index: Int,
        rowTop: Int,
        rowLeft: Int,
        rowWidth: Int,
        rowHeight: Int,
        mouseX: Int,
        mouseY: Int,
        isMouseOver: Boolean,
        f: Float
    ) {
        var hasUpButton = false
        var hasDownButton = false

        if (!isMouseOver) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, this.option.icon)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            GuiComponent.blit(poseStack, rowLeft, rowTop, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE)
        } else {
            val localMouse = Point(mouseX - rowLeft, mouseY - rowTop)
            val mouseLoc = Point(mouseX, mouseY)

            val rowRect = Rect(rowLeft, rowTop, rowWidth - 10, rowHeight)

            RenderSystem.setShaderTexture(0, ResourceLocation("infohud:textures/gui/arrows.png"))
            GuiComponent.fill(
                poseStack, rowRect.x, rowRect.y, rowRect.maxX, rowRect.maxY, 0x809090B0.toInt()
            )
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

            val iconLoc = getArrowTextureXY(
                if (this.option.canAdd) ArrowTextureIcon.ADD else ArrowTextureIcon.REMOVE,
                localMouse.x < ICON_SIZE
            )
            GuiComponent.blit(
                poseStack,
                rowRect.x,
                rowRect.y,
                iconLoc.x.toFloat(),
                iconLoc.y.toFloat(),
                ICON_SIZE,
                ICON_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE
            )

            if (this.option.canMoveUp()) {
                val rect = upButtonRect(rowRect)
                val texPt = getArrowTextureXY(ArrowTextureIcon.UP, rect.contains(mouseLoc))
                GuiComponent.blit(
                    poseStack,
                    rect.x,
                    rect.y,
                    texPt.x.toFloat(),
                    texPt.y.toFloat(),
                    ICON_SIZE,
                    ICON_SIZE,
                    TEXTURE_SIZE,
                    TEXTURE_SIZE
                )
                hasUpButton = true
            }
            if (this.option.canMoveDown()) {
                val rect = downButtonRect(rowRect)
                val texPt = getArrowTextureXY(ArrowTextureIcon.DOWN, rect.contains(mouseLoc))
                GuiComponent.blit(
                    poseStack,
                    rect.x,
                    rect.y,
                    texPt.x.toFloat(),
                    texPt.y.toFloat(),
                    ICON_SIZE,
                    ICON_SIZE,
                    TEXTURE_SIZE,
                    TEXTURE_SIZE
                )
                hasDownButton = true
            }

           // this.screen.renderTooltip(poseStack, this.option.desc, mouseX, mouseY)
        }

        var maxTextWidth = rowWidth - ICON_SIZE - 8
        if (hasUpButton) {
            maxTextWidth -= (ICON_SIZE * 2)
        } else if (hasDownButton) {
            maxTextWidth -= ICON_SIZE
        }
        val text = possiblyTruncatedString(this.minecraft, option.name, maxTextWidth)
        this.minecraft.font.drawShadow(
            poseStack, text, (rowLeft + ICON_SIZE + 4).toFloat(), (rowTop + 4).toFloat(), 0xFFFFFF
        )
    }

    override fun getNarration(): Component {
        return Component.translatable("narrator.select", option.name)
    }

    override fun mouseClicked(x: Double, y: Double, i: Int): Boolean {
        val left = this.parent.rowLeft
        val top = this.parent.getRowTop(this.parent.children().indexOf(this))
        val point = Point(x.toInt(), y.toInt())
        val rect = Rect(
            left,
            top,
            this.parent.rowWidth - 10,
            ROW_HEIGHT
        )

        if (addRemoveButtonRect(rect).contains(point)) {
            option.toggle()
            return true
        } else if (option.canMoveUp() && upButtonRect(rect).contains(point)) {
            option.moveUp()
            return true
        } else if (option.canMoveDown() && downButtonRect(rect).contains(point)) {
            option.moveDown()
            return true
        }
        return false
    }

    companion object {
        private const val ICON_SIZE = 16
        private const val TEXTURE_SIZE = 64
        const val ROW_HEIGHT = ICON_SIZE + 4
        private const val TOO_LONG_NAME_SUFFIX = "..."
    }
}