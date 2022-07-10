package offkilter.infohud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InfoHUD implements ModInitializer {
    private long lastTickPerfSend = 0;
    private long lastServerLightSend = 0;
    private int tickCount = 0;
    public final Logger logger = LogManager.getLogger("infohud");

    private final Set<UUID> tickPerfClients = new HashSet<>();
    private final Set<UUID> serverLightClients = new HashSet<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::serverTickEnd);
        S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> handleRegistrationAdded(handler, channels));
        S2CPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> handleRegistrationRemoved(handler, channels));
        ServerPlayConnectionEvents.DISCONNECT.register(this::playerDisconnected);
    }

    private void handleRegistrationAdded(ServerGamePacketListenerImpl handler, List<ResourceLocation> channels) {
        if (channels.contains(InfoHUDNetworking.TICK_PERF)) {
            long oldCount = tickPerfClients.size();
            tickPerfClients.add(handler.getPlayer().getUUID());
            if (oldCount == 0) {
                logger.info("Tick perf now enabled");
            }
        }
        if (channels.contains(InfoHUDNetworking.SERVER_LIGHT)) {
            long oldCount = serverLightClients.size();
            serverLightClients.add(handler.getPlayer().getUUID());
            if (oldCount == 0) {
                logger.info("Server light now enabled");
            }
        }
    }

    private void handleRegistrationRemoved(ServerGamePacketListenerImpl handler, List<ResourceLocation> channels) {
        if (channels.contains(InfoHUDNetworking.TICK_PERF)) {
            tickPerfClients.remove(handler.getPlayer().getUUID());
            if (tickPerfClients.size() == 0) {
                logger.info("Tick perf now disabled");
            }
        }
        if (channels.contains(InfoHUDNetworking.SERVER_LIGHT)) {
            serverLightClients.remove(handler.getPlayer().getUUID());
            if (serverLightClients.size() == 0) {
                logger.info("Server light now disabled");
            }
        }
    }

    private void playerDisconnected(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        if (tickPerfClients.remove(handler.getPlayer().getUUID()) && (tickPerfClients.size() == 0)) {
            logger.info("Tick perf now disabled");
        }
        if (serverLightClients.remove(handler.getPlayer().getUUID()) && (serverLightClients.size() == 0)) {
            logger.info("Server light now disabled");
        }
    }

    private void serverTickEnd(@NotNull MinecraftServer server) {
        if (!tickPerfClients.isEmpty()) {
            tickCount += 1;
            long now = Util.getMillis();
            if ((now - lastTickPerfSend) >= 1000) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeLong((long) Mth.average(server.tickTimes));
                buf.writeLong(tickCount);

                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    if (ServerPlayNetworking.canSend(p, InfoHUDNetworking.TICK_PERF)) {
                        ServerPlayNetworking.send(p, InfoHUDNetworking.TICK_PERF, buf);
                    }
                }

                lastTickPerfSend = now;
                tickCount = 0;
            }
        }

        if (!serverLightClients.isEmpty()) {
            long now = Util.getMillis();
            if ((now - lastServerLightSend) >= 500) {
                ServerLevel level = server.getLevel(Level.OVERWORLD);
                if (level != null) {
                    FriendlyByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(level.isThundering() ? 10 : level.getSkyDarken());
                    for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                        if (ServerPlayNetworking.canSend(p, InfoHUDNetworking.SERVER_LIGHT)) {
                            ServerPlayNetworking.send(p, InfoHUDNetworking.SERVER_LIGHT, buf);
                        }
                    }
                }
                lastServerLightSend = now;
            }
        }
    }
}
