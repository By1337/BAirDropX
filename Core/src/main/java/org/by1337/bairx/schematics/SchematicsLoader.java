package org.by1337.bairx.schematics;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.util.Vector;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.bairx.util.FileUtil;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchematicsLoader {
    private static final Map<String, File> schematics = new HashMap<>();
    private static final Map<String, SchematicsConfig> schematicsCfg = new HashMap<>();

    public static void load() {
        schematics.clear();
        File home = BAirDropX.getInstance().getDataFolder().getParentFile();

        File bairxSchematicsFolder = new File(home + "/BAirDropX/schematics");
        File weSchematicsFolder = new File(home + "/WorldEdit/schematics");
        File faweSchematicsFolder = new File(home + "/FastAsyncWorldEdit/schematics");


        if (bairxSchematicsFolder.exists()) {
            loadFromFolder(bairxSchematicsFolder);
        } else {
            bairxSchematicsFolder.mkdir();
            ConfigUtil.trySave("schematics/example.txt");
        }
        if (weSchematicsFolder.exists()) {
            loadFromFolder(weSchematicsFolder);
        }
        if (faweSchematicsFolder.exists()) {
            loadFromFolder(faweSchematicsFolder);
        }

        for (File file : FileUtil.findFiles(bairxSchematicsFolder, f -> f.getName().endsWith(".yml"))) {
            try {
                BAirDropX.debug(() -> String.format("load schematic cfg '%s'", file.getPath()));
                YamlConfig config = new YamlConfig(file);
                SchematicsConfig cfg = new SchematicsConfig(config);
                schematicsCfg.put(cfg.name, cfg);
            } catch (IOException | InvalidConfigurationException e) {
                BAirDropX.getMessage().error(Component.translatable("schematics-loader.failed"), e);
            }
        }
    }

    @Nullable
    public static File getSchematic(String key) {
        return schematics.get(key);
    }

    @Nullable
    public static SchematicsConfig getSchematicsConfig(String key) {
        return schematicsCfg.get(key);
    }

    private static void loadFromFolder(File file) {
        for (File schem : FileUtil.findFiles(file, f -> true)) {
            BAirDropX.debug(() -> String.format("detected schematics file in '%s'", schem.getPath()));
            schematics.put(schem.getName(), schem);
        }
    }

    public static class SchematicsConfig {
        private final String name;
        private final String schemFileName;
        private final boolean ignoreAirBlocks;
        private final Vector offsets;

        public SchematicsConfig(YamlContext context) {
            name = context.getAsString("name");
            schemFileName = context.getAsString("schematics-file");
            ignoreAirBlocks = context.getAsBoolean("ignore-air-blocks");
            offsets = new Vector(
                    context.getAsInteger("offsets-x"),
                    context.getAsInteger("offsets-y"),
                    context.getAsInteger("offsets-z")
            );
            Validate.notNull(name, "missing `name`");
            Validate.notNull(schemFileName, "missing `schemFileName`");
        }

        public String getName() {
            return name;
        }

        public String getSchemFileName() {
            return schemFileName;
        }

        public boolean isIgnoreAirBlocks() {
            return ignoreAirBlocks;
        }

        public Vector getOffsets() {
            return offsets;
        }
    }
}
