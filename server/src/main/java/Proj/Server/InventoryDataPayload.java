package Proj.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Map;

public record InventoryDataPayload(Map<String, Object> inventoryData) implements CustomPayload {

    public static final Id<InventoryDataPayload> ID = new Id<>(Identifier.of("Storage_Logging", "inventory_data_MoD"));
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Custom PacketCodec for serializing and deserializing the inventory data
    public static final PacketCodec<PacketByteBuf, InventoryDataPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, InventoryDataPayload payload) {
            // Serialize the Map<String, Object> to JSON and write to the buffer
            String json = serializeData(payload.inventoryData());
            if (json != null) {
                buf.writeString(json);
            }
        }

        @Override
        public InventoryDataPayload decode(PacketByteBuf buf) {
            // Read the JSON string from the buffer and deserialize it into a Map<String, Object>
            String json = buf.readString(32767); // Max length for the string
            Map<String, Object> inventoryData = deserializeData(json);
            return new InventoryDataPayload(inventoryData);
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // Method to serialize Map<String, Object> to JSON
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
            return objectMapper.readValue(jsonData, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
