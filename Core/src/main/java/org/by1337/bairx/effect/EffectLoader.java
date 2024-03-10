package org.by1337.bairx.effect;

import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.NBTParser;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
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

public class EffectLoader {
    private static final Map<String, EffectCreator> creatorMap = new HashMap<>();

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
            ConfigUtil.trySave("effects/expandingCircle.snbt");
            ConfigUtil.trySave("effects/helix.snbt");
            ConfigUtil.trySave("effects/particleExplosion.snbt");
            ConfigUtil.trySave("effects/randomParticle.snbt");
        }
        List<File> files = FileUtil.findFiles(folder, file -> file.getName().endsWith(".snbt"));

        for (File file : files) {
            try {
                String data = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                NBT nbt = NBTParser.parseNBT(data);

                List<CompoundTag> effects = new ArrayList<>();
                if (nbt instanceof ListNBT listNBT) {
                    for (NBT nbt1 : listNBT) {
                        effects.add((CompoundTag) nbt1);
                    }
                } else if (nbt instanceof CompoundTag compoundTag) {
                    effects.add(compoundTag);
                } else {
                    throw new IllegalAccessException("Ожидался ListNBT или CompoundTag, а получен " + nbt.getType());
                }
                for (CompoundTag effect : effects) {
                    String id = ((StringNBT) Validate.notNull(effect.get("effect-type"), "В Файле отсутствует тип эффекта! `effect-type`")).getValue();

                    EffectCreatorType effectCreatorType = EffectCreatorType.getById(new NameKey(id));
                    Validate.notNull(effectCreatorType, "Неизвестный тип " + id);

                    EffectCreator creator = effectCreatorType.getCreator().create(effect);

                    if (creatorMap.containsKey(creator.name())){
                        BAirDropX.getMessage().error("Эффект с именем %s уже существует!", creator.name());
                        continue;
                    }
                    creatorMap.put(creator.name(), creator);

                    BAirDropX.debug(() -> String.format("Загружен эффект %s из файла %s", creator.name(), file.getPath()));
                }

            } catch (Throwable e) {
                BAirDropX.getMessage().error("Не удалось загрузить эффект из файла '%s'", e, file.getPath());
            }
        }
    }

}
