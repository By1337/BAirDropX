package org.by1337.bairx.nbt.impl;

import org.by1337.bairx.nbt.*;
import org.by1337.bairx.nbt.NbtType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CompoundTag extends NBT {
    private final Map<String, NBT> tags = new HashMap<>();

    public CompoundTag() {

    }

    public void putTag(String id, NBT nbt) {
        tags.put(id, nbt);
    }

    public void putString(String id, String s) {
        tags.put(id, new StringNBT(s));
    }

    public void putBoolean(String id, boolean v) {
        tags.put(id, ByteNBT.valueOf((byte) (v ? 1 : 0)));
    }

    public void putByteArray(String id, byte[] byteArray) {
        tags.put(id, new ByteArrNBT(byteArray));
    }

    public void putByte(String id, byte v) {
        tags.put(id, ByteNBT.valueOf(v));
    }

    public void putDouble(String id, double v) {
        tags.put(id, new DoubleNBT(v));
    }

    public void putFloat(String id, float v) {
        tags.put(id, new FloatNBT(v));
    }

    public void putIntArray(String id, int[] intArray) {
        tags.put(id, new IntArrNBT(intArray));
    }

    public void putInt(String id, int v) {
        tags.put(id, IntNBT.valueOf(v));
    }

    public void putList(String id, List<NBT> list) {
        tags.put(id, new ListNBT(list));
    }

    public void putLongArray(String id, long[] longArray) {
        tags.put(id, new LongArrNBT(longArray));
    }

    public void putLong(String id, long v) {
        tags.put(id, LongNBT.valueOf(v));
    }

    public void putShort(String id, short v) {
        tags.put(id, ShortNBT.valueOf(v));
    }


    public boolean has(String name){
        return get(name) != null;
    }
    @Nullable
    public NBT get(String name) {
        return tags.get(name);
    }

    public NBT getOrDefault(String name, NBT def) {
        return tags.getOrDefault(name, def);
    }

    public byte getAsByte(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (byte) v.getAsObject();
    }

    public boolean getAsBoolean(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (byte) v.getAsObject() == 1;
    }

    public double getAsDouble(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (double) v.getAsObject();
    }

    public float getAsFloat(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (float) v.getAsObject();
    }

    public int getAsInt(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (int) v.getAsObject();
    }

    public long getAsLong(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (long) v.getAsObject();
    }

    public short getAsShort(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (short) v.getAsObject();
    }

    public String getAsString(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (String) v.getAsObject();
    }
    public CompoundTag getAsCompoundTag(String name) {
        var v = get(name);
        if (v == null) throw new NullPointerException("unknown tag " + name);
        return (CompoundTag) v;
    }

    public boolean isEmpty(){
        return tags.isEmpty();
    }
    @Override
    public Object getAsObject() {
        return tags;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (Map.Entry<String, NBT> entry : tags.entrySet()) {
            String name = entry.getKey();
            sb.append(quoteAndEscape(name)).append(":");
            sb.append(entry.getValue());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        ;
        sb.append("}");
        return sb.toString();
    }

    public String toStringBeautifier(int lvl) {
        StringBuilder sb = new StringBuilder("{\n");

        for (Map.Entry<String, NBT> entry : tags.entrySet()) {
            NBT nbt = entry.getValue();
            String name = entry.getKey();
            sb.append(" ".repeat(lvl + 4)).append(quoteAndEscape(name)).append(": ").append(nbt.toStringBeautifier(lvl + 4)).append(",\n");
        }
        sb.setLength(sb.length() - 2);
        sb.append("\n").append(" ".repeat(lvl)).append("}");
        return sb.toString();
    }

    @Override
    public String quoteAndEscape(String name) {
        return name.contains("'") ||
                name.contains("\"") ||
                name.contains("\\") ||
                name.contains("{") ||
                name.contains("}") ||
                name.contains(":") ||
                name.contains("[") ||
                name.contains("]") ||
                name.contains(";") ||
                name.contains(" ") ||
                name.contains(",") ? super.quoteAndEscape(name) : name;
    }

    @Override
    public NbtType getType() {
        return NbtType.COMPOUND;
    }


    public Map<String, NBT> getTags() {
        return tags;
    }

    public Set<Map.Entry<String, NBT>> entrySet() {
        return tags.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundTag that = (CompoundTag) o;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }


}
