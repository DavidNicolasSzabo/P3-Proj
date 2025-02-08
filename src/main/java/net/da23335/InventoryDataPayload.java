package net.da23335;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Map;

public record InventoryDataPayload(Map<String, Object> inventoryData) implements CustomPayload {

    public static final Id<InventoryDataPayload> ID = new Id<>(Identifier.of("storage-logging-server", "inventory_data_mod"));
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final PacketCodec<RegistryByteBuf, InventoryDataPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(RegistryByteBuf buf, InventoryDataPayload payload) {

            String json = serializeData(payload.inventoryData());
            if (json != null) {
                buf.writeString(json);
            }
        }
        @Override
        public InventoryDataPayload decode(RegistryByteBuf buf) {
            String json = buf.readString(32767);
            Map<String, Object> inventoryData = deserializeData(json);
            return new InventoryDataPayload(inventoryData);
        }
    };
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static String serializeData(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to deserialize JSON to Map<String, Object>
    public static Map<String, Object> deserializeData(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
