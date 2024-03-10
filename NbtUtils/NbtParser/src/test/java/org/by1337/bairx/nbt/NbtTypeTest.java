package org.by1337.bairx.nbt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.*;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.bairx.nbt.io.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class NbtTypeTest {


    @Test
    public void test() throws IOException {
        var nms = buildNmsCompoundTag();
        System.out.println(nms);
        CompoundTag parsed = NBTParser.parseAsCompoundTag(nms.toString());

        var v = parsed.toStringBeautifier(0);

        System.out.println(v);

        CompoundTag parsed1 = NBTParser.parseAsCompoundTag(v);

        assertEqualsCustom(parsed1, parsed);

    }


    @Test
    public void write() throws CommandSyntaxException {

        var nms = buildNmsCompoundTag();
//        if (true){
//            System.out.println(nms);
//            return;
//        }
        CompoundTag parsed = NBTParser.parseAsCompoundTag(nms.toString());

        ByteBuffer buffer = new ByteBuffer();
        parsed.getType().write(buffer, parsed);


        CompoundTag compoundTag = (CompoundTag) NbtType.COMPOUND.read(new ByteBuffer(buffer.toByteArray()));

        assertEqualsCustom(nms, compoundTag);

        assertEqualsCustom(TagParser.parseTag(compoundTag.toString()), parsed);

        //  System.out.println(compoundTag.toStringBeautifier(0));
    }


    public void assertEqualsCustom(Tag nms, NBT nbt) {
        if (nms instanceof net.minecraft.nbt.CompoundTag compoundTag) {
            for (String key : compoundTag.getAllKeys()) {
                assertEqualsCustom(
                        compoundTag.get(key),
                        ((CompoundTag) nbt).get(key)
                );
            }
        } else if (nms instanceof ListTag listTag) {
            var iterator = listTag.iterator();
            var iterator1 = ((ListNBT) nbt).iterator();
            while (iterator.hasNext()) {
                var nmsTag = iterator.next();
                var tag = iterator1.next();

                assertEqualsCustom(
                        nmsTag,
                        tag
                );
            }
        } else {
            Assert.assertEquals(
                    nms.toString(),
                    nbt.toString()
            );
        }
    }

    public void assertEqualsCustom(NBT nms, NBT nbt) {
        if (nms instanceof CompoundTag compoundTag) {
            for (String key : compoundTag.getTags().keySet()) {
                assertEqualsCustom(
                        compoundTag.get(key),
                        ((CompoundTag) nbt).get(key)
                );
            }
        } else if (nms instanceof ListNBT listTag) {
            var iterator = listTag.iterator();
            var iterator1 = ((ListNBT) nbt).iterator();
            while (iterator.hasNext()) {
                var nmsTag = iterator.next();
                var tag = iterator1.next();

                assertEqualsCustom(
                        nmsTag,
                        tag
                );
            }
        } else {
            Assert.assertEquals(
                    nms.toString(),
                    nbt.toString()
            );
        }
    }


    public net.minecraft.nbt.CompoundTag buildNmsCompoundTag() {
        net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();

        compoundTag.putByteArray("byte_arr", new byte[]{0, 127, 89});
        compoundTag.putByte("byte", (byte) 127);

        compoundTag.putDouble("Double", 45D);
        compoundTag.putDouble("Double2", 45.5D);

        compoundTag.putFloat("float", 2);
        compoundTag.putFloat("float2", 2.5f);

        compoundTag.putIntArray("int_arr", new int[]{99, 345, 211});
        compoundTag.putInt("int", 43);

        ListTag listTag = new ListTag();

        listTag.add(IntTag.valueOf(123));
        listTag.add(IntTag.valueOf(123));


        compoundTag.put("list", listTag);

        compoundTag.putLongArray("long_arr", new long[]{882883, 34213, 4322});
        compoundTag.putLong("long", 123);

        compoundTag.putShort("short", (short) 34);


        compoundTag.putString("string", "str");
        compoundTag.putString("string1", "asa'as");
        compoundTag.putString("string2", "asa\"as");
        compoundTag.putString("string2", "asa\"a's");

        compoundTag.putString("s'tring2", "asa\"a's");
        compoundTag.putString("str\"ing2", "asa\"a's");
        compoundTag.putString("str\"in'g2", "asa\"a's");


        ListTag strList = new ListTag();
        strList.add(StringTag.valueOf("string"));
        strList.add(StringTag.valueOf("string1"));
        strList.add(StringTag.valueOf("string2"));

        ListTag floatList = new ListTag();
        floatList.add(FloatTag.valueOf(1));
        floatList.add(FloatTag.valueOf(1.1f));
        floatList.add(FloatTag.valueOf(23.5f));
        compoundTag.put("floatList", floatList);

        ListTag longList = new ListTag();
        longList.add(LongTag.valueOf(1));
        longList.add(LongTag.valueOf(10));
        longList.add(LongTag.valueOf(256));
        compoundTag.put("longList", longList);


        ListTag arrList = new ListTag();
        arrList.add(new LongArrayTag(new long[]{123, 123, 321}));
        arrList.add(new LongArrayTag(new long[]{4123, 123, 4231}));
        arrList.add(new LongArrayTag(new long[]{123, 123, 321}));

        compoundTag.put("arr_list", arrList);

        compoundTag.put("strList", strList);

        net.minecraft.nbt.CompoundTag compoundTag2 = new net.minecraft.nbt.CompoundTag();

        compoundTag2.put("arr_list", arrList);
        compoundTag2.putDouble("Double", 45D);
        compoundTag2.putDouble("Double2", 45.5D);

        compoundTag2.putFloat("float", 2);
        compoundTag2.putFloat("float2", 2.5f);

        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf("{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð’Ñ�Ñ‘ Ð¿Ð¾Ð´Ñ€Ñ�Ð´\"}],\"text\":\"\"}"));
        lore.add(StringTag.valueOf("{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð˜Ð½Ñ�Ñ‚Ñ€ÑƒÐ¼ÐµÐ½Ñ‚Ñ‹\"}],\"text\":\"\"}"));
        lore.add(StringTag.valueOf("{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð�Ð»Ñ…Ð¸Ð¼Ð¸Ñ�\"}],\"text\":\"\"}"));
        lore.add(StringTag.valueOf("{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð”Ð¾Ð½Ð°Ñ‚Ð½Ñ‹Ðµ Ð¿Ñ€ÐµÐ´Ð¼ÐµÑ‚Ñ‹\"}],\"text\":\"\"}"));

        compoundTag2.put("Lore", lore);


        ListTag last = new ListTag();
        last.add(new LongArrayTag(new long[]{123, 123, 321}));
        for (int i = 0; i < 10; i++) {
            ListTag list = new ListTag();
            list.add(last);
            last = list;
        }

        compoundTag2.put("list_in_list", last);


        compoundTag.put("tags", compoundTag2);

        ListTag listTags = new ListTag();
        listTags.add(compoundTag2);
        listTags.add(compoundTag2);
        listTags.add(compoundTag2);

        compoundTag.put("list_compound_tag", listTags);

        compoundTag.putBoolean("b_1", true);
        compoundTag.putBoolean("b_2", false);
        compoundTag.putBoolean("b_3", false);
        compoundTag.putBoolean("b_4", true);

        compoundTag.put("empty_list", new ListTag());
        compoundTag.put("empty_list1", new ListTag());
        compoundTag.put("empty_list2", new ListTag());
        compoundTag.put("empty_list3", new ListTag());
        compoundTag.put("empty_arr", new ByteArrayTag(new ArrayList<>()));

        return compoundTag;
    }

}