package org.by1337.bairx.hologram;

import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HologramLoader {
    private static final Map<String, YamlContext> loaded = new HashMap<>();
    public static void load() throws IOException, InvalidConfigurationException {
        loaded.clear();
        File f = new File(BAirDropX.getInstance().getDataFolder(), "holograms.yml");
        if (!f.exists()) {
            ConfigUtil.trySave("holograms.yml");
        }
        YamlConfig config = new YamlConfig(f);

        Map<String, YamlContext> map = config.getMap("holograms", YamlContext.class);

        for (YamlContext value : map.values()) {
            String name = Validate.notNull(value.getAsString("name"));
            BAirDropX.debug(() -> "load " + name + " hologram");
            loaded.put(name, value);
        }
    }

    public static Hologram create(String name) {
        var context = loaded.get(name);
        if (context == null) {
            throw new IllegalStateException("Голограмма " + name + " не найдена!");
        }
        var provider = context.getAsString("provider");
        HologramRegistry registry = HologramRegistry.getByName(provider);
        if (registry == null) {
            throw new IllegalStateException("Поставщик " + provider + " для голограмм не найден!");
        }
        var holo = registry.getCreator().get();
        holo.load(context);
        return holo;
    }
}
