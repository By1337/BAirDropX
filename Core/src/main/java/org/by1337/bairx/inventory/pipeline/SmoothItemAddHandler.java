package org.by1337.bairx.inventory.pipeline;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.BAirDropX;
import org.by1337.blib.configuration.YamlContext;

import java.util.Objects;
import java.util.Random;

public class SmoothItemAddHandler implements PipelineHandler<ItemStack> {
    private final Random random;
    private final Config config;

    public SmoothItemAddHandler(Random random, Config config) {
        this.random = random;
        this.config = config;
    }

    @Override
    public void process(ItemStack val, PipelineManager<ItemStack> manager) {
        new BukkitRunnable() {
            @Override
            public void run() {
                manager.processNext(val, SmoothItemAddHandler.this);
            }
        }.runTaskLaterAsynchronously(BAirDropX.getInstance(), random.nextInt(config.insertTickMax - config.insertTickMin) + config.insertTickMin);
    }

    public static class Config {
        public boolean enable = false;
        public int insertTickMax = 40;
        public int insertTickMin = 20;

        public Config() {
        }

        public void load(YamlContext context) {
            Objects.requireNonNull(context, "YamlContext cannot be null");

            enable = context.getAsBoolean("enable");
            insertTickMax = context.getAsInteger("insertTickMax", insertTickMax);
            insertTickMin = context.getAsInteger("insertTickMin", insertTickMin);

            if (insertTickMax < insertTickMin || insertTickMin < 0) {
                throw new IllegalStateException("insertTickMax должен быть больше insertTickMin и больше чем 0!");
            }
        }

        public YamlContext save() {
            YamlContext context = new YamlContext(new YamlConfiguration());

            context.set("enable", enable);
            context.set("insertTickMax", insertTickMax);
            context.set("insertTickMin", insertTickMin);
            return context;
        }
    }
}
