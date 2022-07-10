package offkilter.infohud.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import offkilter.infohud.InfoHUDNetworking;
import offkilter.infohud.screen.InfoHUDOptionsScreen;

import java.util.Set;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class InfoHUDClient implements ClientModInitializer {
    public static boolean showHUD = true;

    public static int skyDarken = 0;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> InfoHUDClientCommand.register(dispatcher));

        // don't register for our server info until the user has officially joined
        ClientPlayConnectionEvents.JOIN.register(Event.DEFAULT_PHASE, (handler, sender, client) -> {
            InfoHUDClient.syncTickPerfEnabled();
            InfoHUDClient.syncServerLight();
        });

        KeyMapping hotKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.offkilter.microhud", InputConstants.KEY_H, "key.categories.misc"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hotKey.consumeClick()) {
                if (Screen.hasControlDown()) {
                    client.setScreen(new InfoHUDOptionsScreen(null));
                } else {
                    showHUD = !showHUD;
                }
            }
        });
    }

    public static void syncTickPerfEnabled() {
        boolean wantsTickPerf = InfoHUDSettings.INSTANCE.getCurrentInfoLines().contains(InfoLine.TICK_PERF);

        Set<ResourceLocation> channels = ClientPlayNetworking.getReceived();
        boolean listeningToTickPerf = channels.contains(InfoHUDNetworking.TICK_PERF);

        if (wantsTickPerf != listeningToTickPerf) {
            if (wantsTickPerf) {
                ClientPlayNetworking.registerReceiver(InfoHUDNetworking.TICK_PERF, (client, handler, buf, responseSender) -> {
                    long mspt = buf.readLong();
                    long tps = buf.readLong();
                    client.execute(() -> PerfCounters.INSTANCE.setPerfInfo(mspt, tps));
                });
            } else {
                ClientPlayNetworking.unregisterReceiver(InfoHUDNetworking.TICK_PERF);
            }
        }
    }

    public static void syncServerLight() {
        boolean wantsTickPerf = InfoHUDSettings.INSTANCE.getCurrentInfoLines().contains(InfoLine.SERVER_LIGHT);

        Set<ResourceLocation> channels = ClientPlayNetworking.getReceived();
        boolean listeningToTickPerf = channels.contains(InfoHUDNetworking.SERVER_LIGHT);

        if (wantsTickPerf != listeningToTickPerf) {
            if (wantsTickPerf) {
                ClientPlayNetworking.registerReceiver(InfoHUDNetworking.SERVER_LIGHT, (client, handler, buf, responseSender) -> skyDarken = buf.readInt());
            } else {
                ClientPlayNetworking.unregisterReceiver(InfoHUDNetworking.SERVER_LIGHT);
            }
        }
    }
}
