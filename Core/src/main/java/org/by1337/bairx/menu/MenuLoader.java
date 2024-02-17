package org.by1337.bairx.menu;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.bairx.BAirDropX;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class MenuLoader {
    private final Map<String, MenuSetting> menus;

    private final Plugin plugin;

    public MenuLoader(Plugin plugin) {
        menus = new HashMap<>();
        this.plugin = plugin;
    }

    public void load() {
        menus.clear();
        File dir = new File(plugin.getDataFolder() + "/menu");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (File menu : getFiles(dir, f -> f.getName().endsWith(".yml"))) {
            BAirDropX.debug(() -> String.format("[%s] Load menu from %s.", this.getClass().getSimpleName(), menu.getName()));
            try {
                YamlConfiguration cfg = new YamlConfiguration();
                cfg.load(menu);
                YamlContext context = new YamlContext(cfg);
                MenuSetting setting = MenuFactory.create(context);
                menus.put(menu.getName().replace(".yml", ""), setting);
            } catch (Exception e) {
                BAirDropX.getMessage().error(e);
            }
        }
    }

    private List<File> getFiles(File folder, Predicate<File> filter) {
        List<File> files = new ArrayList<>();
        var list = folder.listFiles();
        if (list == null) return files;
        for (File file : list) {
            if (file.isDirectory()) {
                files.addAll(getFiles(file, filter));
            } else {
                if (filter.test(file))
                    files.add(file);
            }
        }
        return files;
    }

    @Nullable
    public MenuSetting getMenu(String fileName) {
        return menus.get(fileName);
    }

    public Set<String> getMenus() {
        return menus.keySet();
    }

    public void clearCommandMap() {
        menus.clear();
    }
}
