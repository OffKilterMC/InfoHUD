/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package offkilter.infohud.client.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.network.chat.Component
import java.util.*

@Environment(value = EnvType.CLIENT)
class InfoLineList(minecraft: Minecraft, width: Int, height: Int, private val title: Component) :
    ObjectSelectionList<InfoLineEntry>(minecraft, width, height, 32, height - 55 + 4, InfoLineEntry.ROW_HEIGHT) {
    init {
        centerListVertically = false
        Objects.requireNonNull(minecraft.font)
        setRenderHeader(true, (9.0f * 1.5f).toInt())
    }

    override fun renderHeader(guiGraphics: GuiGraphics, i: Int, j: Int) {
        val component = Component.empty().append(title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD)
        guiGraphics.drawString(minecraft.font,
            component, (i + width / 2 - minecraft.font.width(component) / 2), Math.min(
                y0 + 3, j
            ), 0xFFFFFF
        )
    }

    override fun getRowWidth(): Int {
        return width
    }

    public override fun getRowTop(i: Int): Int {
        return super.getRowTop(i)
    }

    override fun getScrollbarPosition(): Int {
        return x1 - 6
    }
}