package net.da23335;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ExtraModInfoPayload(String extraData) implements CustomPayload {

    public static final Id<ExtraModInfoPayload> ID = new Id<>( Identifier.of("storage-logging-client", "extra_mod_info"));
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final PacketCodec<RegistryByteBuf, ExtraModInfoPayload> CODEC = PacketCodec.of(
            ExtraModInfoPayload::encode, ExtraModInfoPayload::decode
    );

    public void encode(RegistryByteBuf buf) {
        buf.writeString(extraData);
    }

    public static ExtraModInfoPayload decode(RegistryByteBuf buf) {
        return new ExtraModInfoPayload(buf.readString(32767));
    }


}
