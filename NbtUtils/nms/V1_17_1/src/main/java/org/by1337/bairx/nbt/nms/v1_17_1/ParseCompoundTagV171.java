package org.by1337.bairx.nbt.nms.v1_17_1;

import net.minecraft.nbt.*;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.impl.*;
import org.by1337.bairx.nbt.api.ParseCompoundTag;

import java.util.Map;

public class ParseCompoundTagV171 implements ParseCompoundTag {
    @Override
    public org.by1337.bairx.nbt.impl.CompoundTag copy(ItemStack itemStack) {
        var nmsItem = CraftItemStack.asNMSCopy(itemStack);
        var nmsTags = new net.minecraft.nbt.CompoundTag();
        nmsItem.save(nmsTags);
        org.by1337.bairx.nbt.impl.CompoundTag compoundTag = new org.by1337.bairx.nbt.impl.CompoundTag();
        copyAsApiType(nmsTags, compoundTag);
        return compoundTag;
    }

    @Override
    public ItemStack create(org.by1337.bairx.nbt.impl.CompoundTag compoundTag) {
        var nms = new net.minecraft.nbt.CompoundTag();
        copyAsNms(compoundTag, nms);
        return CraftItemStack.asBukkitCopy(
                net.minecraft.world.item.ItemStack.of(nms)
        );
    }

    private void copyAsNms(org.by1337.bairx.nbt.impl.CompoundTag compoundTag, net.minecraft.nbt.CompoundTag nms) {
        for (Map.Entry<String, NBT> entry : compoundTag.entrySet()) {
            NBT nbt = entry.getValue();
            String key = entry.getKey();
            nms.put(key, convert(nbt));
        }
    }

    private Tag convert(NBT nbt) {
        if (nbt instanceof ByteArrNBT) {
            return new ByteArrayTag(((ByteArrNBT) nbt).getValue());
        } else if (nbt instanceof IntArrNBT) {
            return new IntArrayTag(((IntArrNBT) nbt).getValue());
        } else if (nbt instanceof LongArrNBT) {
            return new LongArrayTag(((LongArrNBT) nbt).getValue());
        } else if (nbt instanceof ByteNBT) {
            return ByteTag.valueOf(((ByteNBT) nbt).getValue());
        } else if (nbt instanceof DoubleNBT) {
            return DoubleTag.valueOf(((DoubleNBT) nbt).getValue());
        } else if (nbt instanceof FloatNBT) {
            return FloatTag.valueOf(((FloatNBT) nbt).getValue());
        } else if (nbt instanceof IntNBT) {
            return IntTag.valueOf(((IntNBT) nbt).getValue());
        } else if (nbt instanceof LongNBT) {
            return LongTag.valueOf(((LongNBT) nbt).getValue());
        } else if (nbt instanceof ShortNBT) {
            return ShortTag.valueOf(((ShortNBT) nbt).getValue());
        } else if (nbt instanceof StringNBT) {
            return StringTag.valueOf(((StringNBT) nbt).getValue());
        } else if (nbt instanceof org.by1337.bairx.nbt.impl.CompoundTag) {
            net.minecraft.nbt.CompoundTag subNms = new net.minecraft.nbt.CompoundTag();
            copyAsNms((org.by1337.bairx.nbt.impl.CompoundTag) nbt, subNms);
            return subNms;
        } else if (nbt instanceof ListNBT listNBT) {
            ListTag listTag = new ListTag();
            for (NBT element : listNBT.getList()) {
                listTag.add(convert(element));
            }
            return listTag;
        } else {
            throw new UnsupportedOperationException("Unsupported tag type: " + nbt.getClass().getSimpleName());
        }
    }


    private void copyAsApiType(net.minecraft.nbt.CompoundTag nms, org.by1337.bairx.nbt.impl.CompoundTag compoundTag) {
        for (String key : nms.getAllKeys()) {
            var tag = nms.get(key);
            compoundTag.putTag(key, convertFromNms(tag));
        }
    }

    private NBT convertFromNms(net.minecraft.nbt.Tag tag) {
        if (tag instanceof net.minecraft.nbt.ByteArrayTag byteTags) {
            return new ByteArrNBT(byteTags.getAsByteArray());
        } else if (tag instanceof net.minecraft.nbt.IntArrayTag intTags) {
            return new IntArrNBT(intTags.getAsIntArray());
        } else if (tag instanceof net.minecraft.nbt.LongArrayTag longTags) {
            return new LongArrNBT(longTags.getAsLongArray());
        } else if (tag instanceof net.minecraft.nbt.ByteTag byteTag) {
            return ByteNBT.valueOf(byteTag.getAsByte());
        } else if (tag instanceof net.minecraft.nbt.CompoundTag nmsTags) {
            org.by1337.bairx.nbt.impl.CompoundTag compoundTag1 = new org.by1337.bairx.nbt.impl.CompoundTag();
            copyAsApiType(nmsTags, compoundTag1);
            return compoundTag1;
        } else if (tag instanceof net.minecraft.nbt.DoubleTag doubleTag) {
            return new DoubleNBT(doubleTag.getAsDouble());
        } else if (tag instanceof net.minecraft.nbt.FloatTag floatTag) {
            return new FloatNBT(floatTag.getAsFloat());
        } else if (tag instanceof net.minecraft.nbt.IntTag intTag) {
            return IntNBT.valueOf(intTag.getAsInt());
        } else if (tag instanceof net.minecraft.nbt.LongTag longTag) {
            return LongNBT.valueOf(longTag.getAsLong());
        } else if (tag instanceof net.minecraft.nbt.ShortTag shortTag) {
            return ShortNBT.valueOf(shortTag.getAsShort());
        } else if (tag instanceof net.minecraft.nbt.StringTag stringTag) {
            return new StringNBT(stringTag.getAsString());
        } else if (tag instanceof ListTag listTag) {
            ListNBT listNBT = new ListNBT();
            for (Tag value : listTag) {
                listNBT.add(convertFromNms(value));
            }
            return listNBT;
        } else {
            throw new UnsupportedOperationException("Unsupported tag type: " + tag.getClass().getSimpleName());
        }
    }

}
