package offkilter.infohud.client

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.commands.CommandBuildContext
import net.minecraft.network.FriendlyByteBuf
import offkilter.infohud.InfoHUDNetworking
import offkilter.infohud.client.command.InfoHUDClientCommand
import offkilter.infohud.client.screen.InfoHUDOptionsScreen
import offkilter.infohud.infoline.InfoLine
import offkilter.infohud.infoline.InfoLineRegistry

@Environment(EnvType.CLIENT)
class InfoHUDClient : ClientModInitializer,InfoHUDSettings.Listener {
    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource?>, _: CommandBuildContext ->
            InfoHUDClientCommand.register(dispatcher)
        })

        // don't register for our server info until the user has officially joined
        ClientPlayConnectionEvents.JOIN.register(
            ClientPlayConnectionEvents.Join { _: ClientPacketListener?, _: PacketSender?, _: Minecraft? ->
                syncTickPerfEnabled()
                syncServerLight()
            })
        val hotKey = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.offkilter.microhud",
                InputConstants.KEY_H,
                "key.categories.misc"
            )
        )
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            while (hotKey.consumeClick()) {
                if (Screen.hasControlDown()) {
                    client.setScreen(InfoHUDOptionsScreen(null))
                } else {
                    showHUD = !showHUD
                }
            }
        })

        InfoHUDSettings.INSTANCE.addListener(this)
    }

    override fun infoLineAdded(infoLine: InfoLine) {
        if (infoLine == InfoLineRegistry.TICK_PERF) {
           syncTickPerfEnabled()
        } else if (infoLine == InfoLineRegistry.SERVER_LIGHT) {
            syncServerLight()
        }
    }

    override fun infoLineRemoved(infoLine: InfoLine) {
        if (infoLine == InfoLineRegistry.TICK_PERF) {
           syncTickPerfEnabled()
        } else if (infoLine == InfoLineRegistry.SERVER_LIGHT) {
            syncServerLight()
        }
    }

    override fun infoLinesChanged() {
        syncTickPerfEnabled()
        syncServerLight()
    }

    companion object {
        @JvmField
        var showHUD = true
        var skyDarken = 0
        fun syncTickPerfEnabled() {
            val wantsTickPerf = InfoHUDSettings.INSTANCE.currentInfoLines.contains(InfoLineRegistry.TICK_PERF)
            val channels = ClientPlayNetworking.getReceived()
            val listeningToTickPerf = channels.contains(InfoHUDNetworking.TICK_PERF)
            if (wantsTickPerf != listeningToTickPerf) {
                if (wantsTickPerf) {
                    ClientPlayNetworking.registerReceiver(InfoHUDNetworking.TICK_PERF) { client: Minecraft, _: ClientPacketListener?, buf: FriendlyByteBuf, _: PacketSender? ->
                        val mspt = buf.readLong()
                        val tps = buf.readLong()
                        client.execute {
                           PerfCounters.setPerfInfo(
                                mspt,
                                tps
                            )
                        }
                    }
                } else {
                    ClientPlayNetworking.unregisterReceiver(InfoHUDNetworking.TICK_PERF)
                }
            }
        }

        fun syncServerLight() {
            val wantsTickPerf = InfoHUDSettings.INSTANCE.currentInfoLines.contains(InfoLineRegistry.SERVER_LIGHT)
            val channels = ClientPlayNetworking.getReceived()
            val listeningToTickPerf = channels.contains(InfoHUDNetworking.SERVER_LIGHT)
            if (wantsTickPerf != listeningToTickPerf) {
                if (wantsTickPerf) {
                    ClientPlayNetworking.registerReceiver(InfoHUDNetworking.SERVER_LIGHT) { _: Minecraft?, _: ClientPacketListener?, buf: FriendlyByteBuf, _: PacketSender? ->
                        skyDarken = buf.readInt()
                    }
                } else {
                    ClientPlayNetworking.unregisterReceiver(InfoHUDNetworking.SERVER_LIGHT)
                }
            }
        }
    }
}