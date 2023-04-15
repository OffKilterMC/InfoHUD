package offkilter.infohud

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.*
import net.minecraft.Util
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import java.util.*

class InfoHUD : ModInitializer {
    private var lastTickPerfSend: Long = 0
    private var lastServerLightSend: Long = 0
    private var tickCount = 0
    val logger = LogManager.getLogger("infohud")
    private val tickPerfClients: MutableSet<UUID> = HashSet()
    private val serverLightClients: MutableSet<UUID> = HashSet()
    override fun onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server: MinecraftServer ->
            serverTickEnd(
                server
            )
        })
        S2CPlayChannelEvents.REGISTER.register(S2CPlayChannelEvents.Register { handler: ServerGamePacketListenerImpl, sender: PacketSender?, server: MinecraftServer?, channels: List<ResourceLocation> ->
            handleRegistrationAdded(
                handler,
                channels
            )
        })
        S2CPlayChannelEvents.UNREGISTER.register(S2CPlayChannelEvents.Unregister { handler: ServerGamePacketListenerImpl, sender: PacketSender?, server: MinecraftServer?, channels: List<ResourceLocation> ->
            handleRegistrationRemoved(
                handler,
                channels
            )
        })
        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEvents.Disconnect { handler: ServerGamePacketListenerImpl, server: MinecraftServer ->
            playerDisconnected(
                handler,
                server
            )
        })
    }

    private fun handleRegistrationAdded(handler: ServerGamePacketListenerImpl, channels: List<ResourceLocation>) {
        if (channels.contains(InfoHUDNetworking.TICK_PERF)) {
            val oldCount = tickPerfClients.size.toLong()
            tickPerfClients.add(handler.getPlayer().uuid)
            if (oldCount == 0L) {
                logger.info("Tick perf now enabled")
            }
        }
        if (channels.contains(InfoHUDNetworking.SERVER_LIGHT)) {
            val oldCount = serverLightClients.size.toLong()
            serverLightClients.add(handler.getPlayer().uuid)
            if (oldCount == 0L) {
                logger.info("Server light now enabled")
            }
        }
    }

    private fun handleRegistrationRemoved(handler: ServerGamePacketListenerImpl, channels: List<ResourceLocation>) {
        if (channels.contains(InfoHUDNetworking.TICK_PERF)) {
            tickPerfClients.remove(handler.getPlayer().uuid)
            if (tickPerfClients.size == 0) {
                logger.info("Tick perf now disabled")
            }
        }
        if (channels.contains(InfoHUDNetworking.SERVER_LIGHT)) {
            serverLightClients.remove(handler.getPlayer().uuid)
            if (serverLightClients.size == 0) {
                logger.info("Server light now disabled")
            }
        }
    }

    private fun playerDisconnected(handler: ServerGamePacketListenerImpl, server: MinecraftServer) {
        if (tickPerfClients.remove(handler.getPlayer().uuid) && tickPerfClients.size == 0) {
            logger.info("Tick perf now disabled")
        }
        if (serverLightClients.remove(handler.getPlayer().uuid) && serverLightClients.size == 0) {
            logger.info("Server light now disabled")
        }
    }

    private fun serverTickEnd(server: MinecraftServer) {
        if (!tickPerfClients.isEmpty()) {
            tickCount += 1
            val now = Util.getMillis()
            if (now - lastTickPerfSend >= 1000) {
                val buf = PacketByteBufs.create()
                buf.writeLong(server.tickTimes.toList().average().toLong())
                buf.writeLong(tickCount.toLong())
                for (p in server.playerList.players) {
                    if (ServerPlayNetworking.canSend(p, InfoHUDNetworking.TICK_PERF)) {
                        ServerPlayNetworking.send(p, InfoHUDNetworking.TICK_PERF, buf)
                    }
                }
                lastTickPerfSend = now
                tickCount = 0
            }
        }
        if (!serverLightClients.isEmpty()) {
            val now = Util.getMillis()
            if (now - lastServerLightSend >= 500) {
                val level = server.getLevel(Level.OVERWORLD)
                if (level != null) {
                    val buf = PacketByteBufs.create()
                    buf.writeInt(if (level.isThundering) 10 else level.skyDarken)
                    for (p in server.playerList.players) {
                        if (ServerPlayNetworking.canSend(p, InfoHUDNetworking.SERVER_LIGHT)) {
                            ServerPlayNetworking.send(p, InfoHUDNetworking.SERVER_LIGHT, buf)
                        }
                    }
                }
                lastServerLightSend = now
            }
        }
    }
}