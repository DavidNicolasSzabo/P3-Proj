package Proj.Client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;


public record ExtraModInfoPayload(String extraData) implements CustomPayload {

    public static final CustomPayload.Id<ExtraModInfoPayload> ID = new CustomPayload.Id<>(Identifier.of("Storage_Logging", "Extra_mod_info"));

    public static final PacketCodec<PacketByteBuf, ExtraModInfoPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, ExtraModInfoPayload payload) {
            buf.writeString(payload.extraData());
        }

        @Override
        public ExtraModInfoPayload decode(PacketByteBuf buf) {
            String extraData = buf.readString(32767);
            return new ExtraModInfoPayload(extraData);
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
