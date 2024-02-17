package org.by1337.bairx.airdrop;

import org.by1337.bairx.nbt.NbtType;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.io.ByteBuffer;

import java.util.Arrays;

public class AirDropMetaData {
    private static final byte[] MAGIC_NUMBERS = new byte[]{13, 37, 0, 123};
    private int version;
    private String type;
    private CompoundTag extra;

    public AirDropMetaData(int version, String type, CompoundTag extra) {
        this.version = version;
        this.type = type;
        this.extra = extra;
    }

    public AirDropMetaData() {
    }

    private AirDropMetaData(boolean empty) {
        version = 0;
        type = "";
        extra = new CompoundTag();
    }

    public static AirDropMetaData createEmpty() {
        return new AirDropMetaData(true);
    }

    public void write(ByteBuffer buffer) {
        buffer.writeBytes(MAGIC_NUMBERS);
        buffer.writeVarInt(version);
        buffer.writeUtf(type);
        extra.write(buffer);
    }

    public static AirDropMetaData read(ByteBuffer buffer) {
        if (buffer.readableBytes() < MAGIC_NUMBERS.length) {
            throw new IllegalArgumentException("Файл не начинается с магических чисел!");
        }
        byte[] arr = new byte[MAGIC_NUMBERS.length];
        buffer.readBytes(arr);

        if (!Arrays.equals(MAGIC_NUMBERS, arr)) {
            throw new IllegalArgumentException("Это не формат AirDropMetaData!");
        }

        int version = buffer.readVarInt();
        String type = buffer.readUtf();
        CompoundTag extra = (CompoundTag) NbtType.COMPOUND.read(buffer);

        if (buffer.readableBytes() > 0) {
            throw new IllegalArgumentException("Файл имеет дополнительные байты!");
        }
        return new AirDropMetaData(version, type, extra);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExtra(CompoundTag extra) {
        this.extra = extra;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public CompoundTag getExtra() {
        return extra;
    }
}
