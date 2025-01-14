package Proj.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class Client implements ClientModInitializer {
    private static Map<String, Object> objectMapper = (Map<String, Object>) new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger("Storage_Logging");
    public void onInitializeClient() {

        ClientPlayConnectionEvents.JOIN.register((JoinHandler, sender, client) -> {
            boolean modExists = FabricLoader.getInstance().isModLoaded("Storage_Logging");

            client.execute(() -> {
                if (client.player != null) {
                    if (modExists) {
                        registerPackets();
                        Client_Keybinding.registerKeybinding();
                        Client_Keybinding.onTick();
                    } else {
                    }
                }
            });
        });
    }
    public void registerPackets() {
        PayloadTypeRegistry.playS2C().register(InventoryDataPayload.ID, InventoryDataPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(InventoryDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload != null && payload.inventoryData() != null) {
                    objectMapper= payload.inventoryData();
                }
            });
        });
    }
    public static Map<String, Object> getObjectMapper() {
        return objectMapper;
    }

}
