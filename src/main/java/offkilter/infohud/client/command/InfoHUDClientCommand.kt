package offkilter.infohud.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import offkilter.infohud.client.InfoHUDSettings
import offkilter.infohud.infoline.InfoLine

@Environment(value = EnvType.CLIENT)
object InfoHUDClientCommand {
    @JvmStatic
    fun register(commandDispatcher: CommandDispatcher<FabricClientCommandSource?>) {
        val builder = ClientCommandManager.literal("infohud")
            .requires { commandSourceStack: FabricClientCommandSource -> commandSourceStack.hasPermission(2) }
            .then(
                ClientCommandManager.literal("show")
                    .then(
                        ClientCommandManager.argument("infoLine", InfoLineArgumentType.infoLine())
                            .executes { context: CommandContext<FabricClientCommandSource?> ->
                                setInfoLineVisible(
                                    InfoLineArgumentType.getInfoLine(context, "infoLine"),
                                    true,
                                    context.source
                                )
                            })
            )
            .then(
                ClientCommandManager.literal("hide")
                    .then(
                        ClientCommandManager.argument("infoLine", InfoLineArgumentType.infoLine())
                            .executes { context: CommandContext<FabricClientCommandSource?> ->
                                setInfoLineVisible(
                                    InfoLineArgumentType.getInfoLine(context, "infoLine"),
                                    false,
                                    context.source
                                )
                            })
            )
            .then(
                ClientCommandManager.literal("move")
                    .then(
                        ClientCommandManager.argument("infoLine", InfoLineArgumentType.infoLine())
                            .then(ClientCommandManager.argument("direction", DirectionArgumentType.direction())
                                .executes { context: CommandContext<FabricClientCommandSource?> ->
                                    moveInfoLine(
                                        InfoLineArgumentType.getInfoLine(context, "infoLine"),
                                        DirectionArgumentType.getDirection(context, "direction"),
                                        context.source
                                    )
                                })
                    )
            )
        commandDispatcher.register(builder)
    }

    private fun setInfoLineVisible(
        infoLine: InfoLine,
        visible: Boolean,
        @Suppress("UNUSED_PARAMETER") sourceStack: FabricClientCommandSource?
    ): Int {
        if (visible) {
            InfoHUDSettings.INSTANCE.add(infoLine)
        } else {
            InfoHUDSettings.INSTANCE.remove(infoLine)
        }
        return 0
    }

    private fun moveInfoLine(
        infoLine: InfoLine,
        direction: InfoHUDSettings.Direction,
        @Suppress("UNUSED_PARAMETER") sourceStack: FabricClientCommandSource?
    ): Int {
        InfoHUDSettings.INSTANCE.move(infoLine, direction)
        return 0
    }
}