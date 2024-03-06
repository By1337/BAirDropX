package org.by1337.bairx.inventory.pipeline;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.inventory.Saveable;
import org.by1337.bairx.inventory.HandlerCreator;
import org.by1337.bairx.inventory.HandlerRegistry;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class SmoothAddItemHandler implements PipelineHandler<ItemStack>, Saveable {
    private final Random random;
    public int insertTickMax = 40;
    public int insertTickMin = 20;

    public SmoothAddItemHandler(YamlContext context) {
        this(new Random(), context);
    }

    public SmoothAddItemHandler(Random random, YamlContext context) {
        this.random = random;
        load(context);
    }

    @Override
    public void process(ItemStack val, PipelineManager<ItemStack> manager) {
        new BukkitRunnable() {
            @Override
            public void run() {
                manager.processNext(val, SmoothAddItemHandler.this);
            }
        }.runTaskLaterAsynchronously(BAirDropX.getInstance(), random.nextInt(insertTickMax - insertTickMin) + insertTickMin);
    }

    public void load(YamlContext context) {
        Objects.requireNonNull(context, "YamlContext cannot be null");

        insertTickMax = context.getAsInteger("insertTickMax", insertTickMax);
        insertTickMin = context.getAsInteger("insertTickMin", insertTickMin);

        if (insertTickMax < insertTickMin || insertTickMin < 0) {
            throw new IllegalStateException("insertTickMax должен быть больше insertTickMin и больше чем 0!");
        }
    }

    public static YamlContext saveDefault() {
        YamlContext context = new YamlContext(new YamlConfiguration());
        context.set("insertTickMax", 40);
        context.set("insertTickMin", 20);
        return context;
    }

    public YamlContext save() {
        YamlContext context = new YamlContext(new YamlConfiguration());
        context.set("insertTickMax", insertTickMax);
        context.set("insertTickMin", insertTickMin);
        return context;
    }

    public static class Creator implements HandlerCreator<ItemStack> {

        @Override
        public PipelineHandler<ItemStack> create(YamlContext context) {
            return new SmoothAddItemHandler(context);
        }

        @Override
        public HandlerRegistry.Type<ItemStack> getType() {
            return HandlerRegistry.Type.ITEM;
        }

        @Override
        public @Nullable String addBefore() {
            return "handler";
        }

        @Override
        public @NotNull YamlContext saveDefault() {
            return SmoothAddItemHandler.saveDefault();
        }

        @Override
        public @NotNull String name() {
            return "smooth_add_item";
        }
    }
}
