package org.by1337.bairx.nbt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.*;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.NBTParser;
import org.by1337.bairx.nbt.NbtType;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.bairx.nbt.io.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class NbtTypeTest {

    private String nbt = "{id:\"minecraft:light_blue_shulker_box\",tag:{BlockEntityTag:{Items:[{Slot:0b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:1b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:2b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:3b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:4b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:5b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:6b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:7b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:8b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:9b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:10b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:11b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:12b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:13b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:14b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:15b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:16b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:17b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:18b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:19b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:20b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:21b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:22b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:23b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:24b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:25b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"},{Slot:26b,tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"Ð¡Ð¾Ñ€Ñ‚Ð¸Ñ€Ð¾Ð²ÐºÐ°\"}],\"text\":\"\"}',Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"text\":\"  \"},{\"italic\":false,\"color\":\"gold\",\"text\":\"âœ” Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ð¾Ð²Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ñ�Ñ‚Ð°Ñ€Ñ‹Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"â�º Ð¡Ð½Ð°Ñ‡Ð°Ð»Ð° Ð½Ðµ Ð´Ð¾Ñ€Ð¾Ð³Ð¸Ðµ Ð·Ð° ÑˆÑ‚ÑƒÐºÑƒ\"}],\"text\":\"\"}']},PublicBukkitValues:{\"minecraft:bauc_menu_item\":1870876720}},Count:1b,id:\"minecraft:hopper\"}]}},Count:1b}";

    @Test
    public void test() throws IOException {
        var nms = buildNmsCompoundTag();
        CompoundTag parsed = NBTParser.parse(nms.toString());

        var v = parsed.toStringBeautifier(0);

       // System.out.println(v);

        CompoundTag parsed1 = NBTParser.parse(v);

        assertEquals(parsed1, parsed);
    }


    @Test
    public void write() throws CommandSyntaxException {

        var nms = buildNmsCompoundTag();
//        if (true){
//            System.out.println(nms);
//            return;
//        }
        CompoundTag parsed = NBTParser.parse(nms.toString());

        ByteBuffer buffer = new ByteBuffer();
        parsed.getType().write(buffer, parsed);


        CompoundTag compoundTag = (CompoundTag) NbtType.COMPOUND.read(new ByteBuffer(buffer.toByteArray()));

        assertEquals(nms, compoundTag);

        assertEquals(TagParser.parseTag(compoundTag.toString()), parsed);

        //  System.out.println(compoundTag.toStringBeautifier(0));
    }


    public void assertEquals(Tag nms, NBT nbt) {
        if (nms instanceof net.minecraft.nbt.CompoundTag compoundTag) {
            for (String key : compoundTag.getAllKeys()) {
                assertEquals(
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

                assertEquals(
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

    public void assertEquals(NBT nms, NBT nbt) {
        if (nms instanceof CompoundTag compoundTag) {
            for (String key : compoundTag.getTags().keySet()) {
                assertEquals(
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

                assertEquals(
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

        return compoundTag;
    }

}