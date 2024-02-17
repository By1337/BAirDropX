package org.by1337.bairx.nbt.impl;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.NbtType;
import java.util.Objects;

public class IntNBT extends NBT{

    private final int value;

    private IntNBT(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public NbtType getType() {
        return NbtType.INT;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toStringBeautifier(int lvl) {
        return toString();
    }

    @Override
    public Object getAsObject() {
        return value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntNBT intNBT = (IntNBT) o;
        return value == intNBT.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static IntNBT valueOf(int i) {
        return i >= -128 && i <= 1024 ? Cache.cache[i + 128] : new IntNBT(i);
    }


    static class Cache {
        private static final int HIGH = 1024;
        private static final int LOW = -128;
        static final IntNBT[] cache = new IntNBT[HIGH - LOW + 1];

        private Cache() {
        }

        static {
            for(int i = 0; i < cache.length; ++i) {
                cache[i] = new IntNBT(LOW + i);
            }

        }
    }
}