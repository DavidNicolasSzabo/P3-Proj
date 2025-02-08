package net.da23335;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private static final Logger LOGGER = LoggerFactory.getLogger("storage-logging-client");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, Object> map;
    private boolean connected = false;




    public void onInitializeClient() {
        LOGGER.info("Initializing Storage Logging Client...");

        PayloadTypeRegistry.playC2S().register(ExtraModInfoPayload.ID, ExtraModInfoPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(InventoryDataPayload.ID, InventoryDataPayload.CODEC);
        if (Client_Keybinding.Client_Keybind == null) {
            Client_Keybinding.registerKeybinding();
        }
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            boolean modExists = FabricLoader.getInstance().isModLoaded("storage-logging-server");
            connected = true;
            if (modExists) {
                registerPackets();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            connected = false;
            unregisterPackets();
        });
        try {
            String json = "{\"key\":\"value\"}";
            map = objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse JSON", e);
        }
    }

    private void registerPackets() {

        ClientPlayNetworking.registerGlobalReceiver(InventoryDataPayload.ID, (payload, context) -> {
            try {
                if (payload != null && payload.inventoryData() != null) {
                    map = payload.inventoryData();
                    LOGGER.info("Received and decoded InventoryDataPayload successfully.");
                } else {
                    LOGGER.error("Failed to decode InventoryDataPayload: Payload is null or missing data.");
                }
            } catch (Exception e) {
                LOGGER.error("Exception while handling InventoryDataPayload", e);
            }
        });
    }
    private void unregisterPackets() {
        ClientPlayNetworking.unregisterGlobalReceiver(InventoryDataPayload.ID.id());
        ClientPlayNetworking.unregisterGlobalReceiver(ExtraModInfoPayload.ID.id());
    }
    public static Map<String, Object> getObjectMapper() {
        return map;
    }
    public boolean isConnected() {
        return connected;
    }
}

