package org.by1337.bairx.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.inventory.pipeline.ItemHandler;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.PipelineManager;
import org.by1337.bairx.inventory.pipeline.click.ClickHandler;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryManager implements PipelineHandler<ItemStack>, Listener {
    private final Random random = new Random();
    private List<InventoryItem> items = new ArrayList<>();
    private PipelineManager<ItemStack> pipeline;
    private PipelineManager<InventoryEvent> clickPipeline;
    private int genItemCount;
    private int invSize;
    private String invName;
    private Inventory inventory;
    private int emptySlotChance;
    private Map<HandlerCreator<?>, PipelineHandler<?>> loadedExtensions = new HashMap<>();

    public InventoryManager(int invSize, String invName) {
        this(invSize, invSize, invName, 0);
    }

    public InventoryManager(int genItemCount, int invSize, String invName, int emptySlotChance) {
        this.emptySlotChance = emptySlotChance;
        this.genItemCount = genItemCount;
        this.invSize = invSize;
        this.invName = invName;
        inventory = Bukkit.createInventory(null, invSize, BAirDropX.getMessage().messageBuilder(invName));
        rebuildPipeline();
        reloadExtensions();
        Bukkit.getPluginManager().registerEvents(this, BAirDropX.getInstance());
    }

    public static InventoryManager load(@Nullable CompoundTag compoundTag, YamlConfig cfg) {
        int genItemCount = cfg.getAsInteger("genItemCount");
        int invSize = cfg.getAsInteger("invSize");
        int emptySlotChance = cfg.getAsInteger("emptySlotChance");
        String invName = cfg.getAsString("invName");

        InventoryManager manager = new InventoryManager(genItemCount, invSize, invName, emptySlotChance);
        if (compoundTag != null) {
            ListNBT listNBT = (ListNBT) compoundTag.getOrThrow("items");
            List<InventoryItem> items = new ArrayList<>();
            for (NBT nbt : listNBT) {
                items.add(InventoryItem.load((CompoundTag) nbt));
            }
            manager.setItems(items);
        }

        manager.sortItems();

        Map<String, YamlContext> extensionMap = cfg.getMap("extensions", YamlContext.class, new HashMap<>());
        for (Map.Entry<String, YamlContext> entry : extensionMap.entrySet()) {
            YamlContext context = entry.getValue();
            boolean enable = context.getAsBoolean("enable");
            String name = context.getAsString("name");
            if (enable) {
                var registry = HandlerRegistry.getByName(name);
                if (registry == null) {
                    BAirDropX.getMessage().warning("Не найдено расширение для инвентаря под именем '%s'", name);
                    continue;
                }
                var handler = registry.getCreator().create(context);
                manager.loadedExtensions.put(registry.getCreator(), handler);
            }
        }
        if (extensionMap.size() != HandlerRegistry.size()) {
            for (Map.Entry<String, HandlerRegistry> entry : HandlerRegistry.entrySet()) {
                if (!extensionMap.containsKey(entry.getKey())) {
                    var context = entry.getValue().getCreator().saveDefault();
                    context.set("enable", false);
                    context.set("name", entry.getValue().getCreator().name());
                    cfg.set("extensions." + entry.getKey(), context);
                }
            }
            cfg.trySave();
        }
        manager.reloadExtensions();
        return manager;
    }

    public void save(CompoundTag compoundTag, YamlContext cfg) {
        Map<String, YamlContext> contextMap = new HashMap<>();
        for (Map.Entry<HandlerCreator<?>, PipelineHandler<?>> extension : loadedExtensions.entrySet()) {
            YamlContext context;
            if (extension.getValue() instanceof Saveable saveable) {
                context = saveable.save();
            } else {
                context = extension.getKey().saveDefault();
            }
            context.set("enable", true);
            context.set("name", extension.getKey().name());
            contextMap.put(extension.getKey().name(), context);
        }
        for (Map.Entry<String, HandlerRegistry> entry : HandlerRegistry.entrySet()) {
            if (!contextMap.containsKey(entry.getKey())) {
                YamlContext context = entry.getValue().getCreator().saveDefault();
                context.set("enable", false);
                context.set("name", entry.getValue().getCreator().name());
                contextMap.put(entry.getKey(), context);
            }
        }
        cfg.set("extensions", contextMap);
        cfg.set("genItemCount", genItemCount);
        cfg.set("invSize", invSize);
        cfg.set("invName", invName);
        cfg.set("emptySlotChance", emptySlotChance);
        ListNBT listNBT = new ListNBT();
        for (InventoryItem item : items) {
            CompoundTag compoundTag1 = new CompoundTag();
            item.save(compoundTag1);
            listNBT.add(compoundTag1);
        }
        compoundTag.putTag("items", listNBT);
    }

    public void sortItems() {
        items.sort(Comparator.comparingInt(InventoryItem::getChance));
    }

    public void generateItems() {
        process(null, pipeline);
    }

    public void release() {
        inventory.clear();
        for (PipelineHandler<?> value : loadedExtensions.values()) {
            if (value instanceof Releasable releasable){
                releasable.release();
            }
        }
        for (HumanEntity viewer : inventory.getViewers().toArray(new HumanEntity[0])) {
            viewer.closeInventory();
        }
    }

    public void close() {
        release();
        HandlerList.unregisterAll(this);
    }

    public void reloadExtensions() {
        loadedExtensions.forEach((k, v) -> {
            if (k.getType() == HandlerRegistry.Type.ITEM) {
                String before = k.addBefore();
                before = before == null ? "handler" : before;
                pipeline.addBefore(
                        before,
                        k.name(),
                        (PipelineHandler<ItemStack>) v
                );
            } else {
                String before = k.addBefore();
                if (before != null) {
                    clickPipeline.addBefore(
                            before,
                            k.name(),
                            (PipelineHandler<InventoryEvent>) v
                    );
                } else {
                    clickPipeline.add(
                            k.name(),
                            (PipelineHandler<InventoryEvent>) v
                    );
                }
            }
        });
    }

    public void rebuildPipeline() {
        clickPipeline = new PipelineManager<>();
        pipeline = new PipelineManager<>();
        pipeline
                .add("generator", this)
                .add("handler", new ItemHandler(inventory, random));
        clickPipeline.add("handler", new ClickHandler());

    }

    @Override
    public void process(@Nullable ItemStack val, PipelineManager<ItemStack> manager) {
        for (int i = 0; i < genItemCount; i++) {
            if (random.nextInt(100) < emptySlotChance) continue;
            for (InventoryItem inventoryItem : items) {
                ItemStack item = inventoryItem.getItemStack(random);
                if (item != null) {
                    manager.processNext(item, this);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == inventory) {
            clickPipeline.processFirst(e);
        }
    }

    @EventHandler
    public void onClick(InventoryDragEvent e) {
        if (e.getInventory() == inventory) {
            clickPipeline.processFirst(e);
        }
    }


    public void setGenItemCount(int genItemCount) {
        this.genItemCount = genItemCount;
    }

    public void setInvSize(int invSize) {
        this.invSize = invSize;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public Random getRandom() {
        return random;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public PipelineManager<ItemStack> getPipeline() {
        return pipeline;
    }

    public int getGenItemCount() {
        return genItemCount;
    }

    public int getInvSize() {
        return invSize;
    }

    public String getInvName() {
        return invName;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public void setPipeline(PipelineManager<ItemStack> pipeline) {
        this.pipeline = pipeline;
    }

    public int getEmptySlotChance() {
        return emptySlotChance;
    }

    public void setEmptySlotChance(int emptySlotChance) {
        this.emptySlotChance = emptySlotChance;
    }
}
