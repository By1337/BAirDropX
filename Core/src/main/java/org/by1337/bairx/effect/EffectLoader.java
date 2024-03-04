package org.by1337.bairx.effect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.nbt.NBTParser;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.StringNBT;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.bairx.util.FileUtil;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;

public class EffectLoader {
    private static Map<String, EffectCreator> creatorMap = new HashMap<>();

    @Nullable
    public static EffectCreator getByName(String name) {
        return creatorMap.get(name);
    }

    public static Set<String> keys() {
        return creatorMap.keySet();
    }

    public static void load() {
        creatorMap.clear();
        File folder = new File(BAirDropX.getInstance().getDataFolder(), "effects");
        if (!folder.exists()) {
            folder.mkdir();
            ConfigUtil.trySave("effects/circle.snbt");
        }
        List<File> files = FileUtil.findFiles(folder, file -> file.getName().endsWith(".snbt"));

        for (File file : files) {
            try {
                String data = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                CompoundTag compoundTag = NBTParser.parse(data);

                String id = ((StringNBT) Validate.notNull(compoundTag.get("effect-type"), "В Файле отсутствует тип эффекта! `effect-type`")).getValue();

                EffectCreatorType effectCreatorType = EffectCreatorType.getById(new NameKey(id));
                Validate.notNull(effectCreatorType, "Неизвестный тип " + id);

                EffectCreator creator = effectCreatorType.getCreator().create(compoundTag);

                creatorMap.put(creator.name(), creator);

                BAirDropX.debug(() -> String.format("Загружен эффект %s из файла %s", creator.name(), file.getPath()));

            } catch (Throwable e) {
                BAirDropX.getMessage().error("Не удалось загрузить эффект из файла '%s'", e, file.getPath());
            }
        }
    }

}
