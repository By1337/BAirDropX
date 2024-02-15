package org.by1337.bairx;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Placeholder;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.io.IOException;

public class AirDrop extends Placeholder {
    private final NameKey id;
    private final File dataFolder;
    private World world;
    private GeneratorSetting generatorSetting;
    private CompoundTag compoundTag;

    private AirDrop(NameKey id, File dataFolder) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.dataFolder = dataFolder;
        generatorSetting = new GeneratorSetting();
        generatorSetting.applyDefaultFlags();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File genSetting = new File(dataFolder + "/generator_setting.yml");
        if (genSetting.exists()) genSetting.delete();
        genSetting.createNewFile();

        YamlConfig genSettingCfg = new YamlConfig(genSetting);
        genSettingCfg.set("setting", genSetting);
        genSettingCfg.save();

        File config = new File(dataFolder + "/config.yml");
        if (config.exists()) config.delete();
        config.createNewFile();



    }

    public World getWorld() {
        return world;
    }
}
