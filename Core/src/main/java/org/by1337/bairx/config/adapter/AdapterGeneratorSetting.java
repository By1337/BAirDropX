package org.by1337.bairx.config.adapter;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.ClassAdapter;
import org.by1337.blib.world.BlockPosition;
import org.by1337.blib.world.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterGeneratorSetting implements ClassAdapter<GeneratorSetting> {
    @Override
    public ConfigurationSection serialize(GeneratorSetting obj, YamlContext context) {
        context.set("has-block", obj.hasBlock);
        context.set("has-no-block", obj.hasNoBlock);
        context.set("ignore-blocks", obj.ignoreBlocks);
        context.set("white-list-blocks", obj.whiteListBlocks);
        context.set("white-list-biomes", obj.whiteListBiomes);
        context.set("offsets", obj.offsets);
        context.set("center", obj.center);
        context.set("radius", obj.radius);
        context.set("max-y", obj.maxY);
        context.set("min-y", obj.minY);
        context.set("region-radius", obj.regionRadius);
        context.set("check-region", obj.checkRegion);

        List<String> list = new ArrayList<>();

        for (Map.Entry<Flag<?>, String> entry : obj.flags.entrySet()) {
            list.add(entry.getKey().getName() + " " + entry.getValue());
        }

        context.set("flags", list);

        return context.getHandle();
    }

    @Override
    public GeneratorSetting deserialize(YamlContext context) {
        GeneratorSetting generatorSetting = new GeneratorSetting();
        generatorSetting.hasBlock = context.getList("has-block", BlockPosition.class, new ArrayList<>());
        generatorSetting.hasNoBlock = context.getList("has-no-block", BlockPosition.class, new ArrayList<>());
        generatorSetting.ignoreBlocks = context.getList("ignore-blocks", Material.class, new ArrayList<>());
        generatorSetting.whiteListBlocks = context.getList("white-list-blocks", Material.class, new ArrayList<>());
        generatorSetting.whiteListBiomes = context.getList("white-list-biomes", Biome.class, new ArrayList<>());
        generatorSetting.offsets = context.getAs("offsets", BlockPosition.class);
        generatorSetting.center = context.getAs("center", Vector2D.class);
        generatorSetting.radius = context.getAsInteger("radius");
        generatorSetting.maxY = context.getAsInteger("max-y");
        generatorSetting.minY = context.getAsInteger("min-y");
        generatorSetting.regionRadius = context.getAs("region-radius", BlockPosition.class);
        generatorSetting.checkRegion = context.getAsBoolean("check-region");
        var flags = context.getList("flags", String.class, new ArrayList<>());

        generatorSetting.flags = new HashMap<>();

        for (String flag : flags) {
            String name = flag.split(" ")[0];
            String val = flag.substring(name.length() + 1);
            Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(name);
            if (f == null){
                BAirDropX.getMessage().error("unknown flag: " + name);
                continue;
            }
            generatorSetting.flags.put(f, val);
        }

        return generatorSetting;
    }
}
