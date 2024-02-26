package org.by1337.bairx.nbt.impl;

import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.NbtType;

import java.util.Arrays;

public class ByteArrNBT extends NBT {
    private final byte[] value;

    public ByteArrNBT(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return arrToString();
    }

    public String arrToString() {
        StringBuilder sb = new StringBuilder("[B;");
        for (Byte b : value) {
            sb.append(b).append("B,");
        }
        if (value.length != 0)
            sb.setLength(sb.length() - 1);
        return sb.append("]").toString();
    }

    @Override
    public Object getAsObject() {
        return value;
    }
    @Override
    public NbtType getType() {
        return NbtType.BYTE_ARR;
    }

    @Override
    public String toStringBeautifier(int lvl) {
        String space = " ".repeat(lvl + 4);
        StringBuilder sb = new StringBuilder("[ B;\n");
        for (Byte b : value) {
            sb.append(space).append(b).append("B,\n");
        }
        sb.setLength(sb.length() - 2);
        return sb.append("\n").append(" ".repeat(lvl)).append("]").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrNBT that = (ByteArrNBT) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
