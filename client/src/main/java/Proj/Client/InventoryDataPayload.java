package Proj.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Map;

public record InventoryDataPayload(Map<String, Object> inventoryData) implements CustomPayload {

    public static final CustomPayload.Id<InventoryDataPayload> ID = new CustomPayload.Id<>(Identifier.of("Storage_Logging", "inventory_data_MoD"));
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static final PacketCodec<PacketByteBuf, InventoryDataPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, InventoryDataPayload payload) {

            String json = serializeData(payload.inventoryData());
            if (json != null) {
                buf.writeString(json);
            }
        }

        @Override
        public InventoryDataPayload decode(PacketByteBuf buf) {
            String json = buf.readString(32767);
            Map<String, Object> inventoryData = deserializeData(json);
            return new InventoryDataPayload(inventoryData);
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
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
    public static Map<String, Object> deserializeData(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
