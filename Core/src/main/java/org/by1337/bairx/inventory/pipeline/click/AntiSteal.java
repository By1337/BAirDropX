package org.by1337.bairx.inventory.pipeline.click;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.inventory.HandlerCreator;
import org.by1337.bairx.inventory.HandlerRegistry;
import org.by1337.bairx.inventory.Saveable;
import org.by1337.bairx.inventory.Releasable;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.PipelineManager;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AntiSteal implements PipelineHandler<InventoryEvent>, Releasable, Saveable {
    private Map<UUID, ChestStealData> chestStealDataMap = new HashMap<>();
    public int cooldown = 200;
    public int maxWarnings = 5;
    public int interval = 5;
    public int minIntervalToIgnore = 250;
    public String command = "close_inventory";

    public AntiSteal(YamlContext context) {
        load(context);
    }

    public void load(YamlContext context) {
        Objects.requireNonNull(context, "YamlContext cannot be null");

        cooldown = context.getAsInteger("cooldown", cooldown);
        maxWarnings = context.getAsInteger("maxWarnings", maxWarnings);
        interval = context.getAsInteger("interval", interval);
        minIntervalToIgnore = context.getAsInteger("minIntervalToIgnore", minIntervalToIgnore);
        command = context.getAsString("command", command);
    }
    public YamlContext save() {
        YamlContext context = new YamlContext(new YamlConfiguration());
        context.set("cooldown", cooldown);
        context.set("maxWarnings", maxWarnings);
        context.set("interval", interval);
        context.set("minIntervalToIgnore", minIntervalToIgnore);
        context.set("command", command);
        return context;
    }
    public static YamlContext saveDefault() {
        YamlContext context = new YamlContext(new YamlConfiguration());
        context.set("cooldown", 200);
        context.set("maxWarnings", 5);
        context.set("interval", 5);
        context.set("minIntervalToIgnore", 250);
        context.set("command", "close_inventory");
        return context;
    }

    @Override
    public void process(InventoryEvent val, PipelineManager<InventoryEvent> manager) {
        if (!(val instanceof InventoryClickEvent event)) return;
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();

        ChestStealData chestStealData = chestStealDataMap.getOrDefault(player.getUniqueId(), new ChestStealData(interval, minIntervalToIgnore));
        long currentTime = System.currentTimeMillis();

        if (chestStealData.getLastTime() == 0) {
            chestStealData.setLastTime(currentTime);
        } else {
            long lastActionTime = chestStealData.getLastTime();
            long interval = currentTime - lastActionTime;
            if (interval != 0) {
                chestStealData.addTime(interval);
            }
            if (chestStealData.getLastSteal() != -1 && currentTime - chestStealData.getLastSteal() <= cooldown) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    player.setCooldown(event.getCurrentItem().getType(), Math.abs(cooldown / 50));
                }
            } else {
                chestStealData.setLastSteal(currentTime);
            }
            if (chestStealData.getWarnings() >= maxWarnings) {
                if (command.equals("close_inventory")){
                    player.closeInventory();
                }else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BAirDropX.getMessage().messageBuilder(command, player));
                }
                player.updateInventory();
                chestStealData.reset();
                event.setCancelled(true);
            } else {
                chestStealData.setLastTime(currentTime);
            }
        }
        chestStealDataMap.put(player.getUniqueId(), chestStealData);
        if (!event.isCancelled()) {
            manager.processNext(val, this);
        }
    }

    public Map<UUID, ChestStealData> getChestStealDataMap() {
        return chestStealDataMap;
    }

    @Override
    public void release() {
        chestStealDataMap.clear();
    }

    public static class Creator implements HandlerCreator<InventoryEvent> {

        @Override
        public PipelineHandler<InventoryEvent> create(YamlContext context) {
            return new AntiSteal(context);
        }

        @Override
        public HandlerRegistry.Type<InventoryEvent> getType() {
            return HandlerRegistry.Type.CLICK;
        }

        @Override
        public @Nullable String addBefore() {
            return "handler";
        }

        @Override
        public @NotNull YamlContext saveDefault() {
            return AntiSteal.saveDefault();
        }

        @Override
        public @NotNull String name() {
            return "anti_steal";
        }
    }

    public static class ChestStealData {
        private int interval; // 5
        private int minIntervalToIgnore; // 250
        private int warnings = 0;
        private long lastInterval = -1;
        private long lastSteal = -1;
        private long lastTime;

        public ChestStealData(int interval, int minIntervalToIgnore) {
            this.interval = interval;
            this.minIntervalToIgnore = minIntervalToIgnore;
        }

        public int getWarnings() {
            return warnings;
        }

        public void addTime(long time) {
            if (time > minIntervalToIgnore) {
                reset();
                return;
            }
            if (lastInterval != -1) {
                if (Math.abs(lastInterval - time) < interval) {
                    warnings++;
                } else
                    reset();
            } else {
                lastInterval = time;
            }
        }

        public void setLastSteal(long lastSteal) {
            this.lastSteal = lastSteal;
        }

        public long getLastSteal() {
            return lastSteal;
        }

        public void reset() {
            warnings = 0;
            lastInterval = -1;
        }

        public long getLastTime() {
            return lastTime;
        }

        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }
    }

}
