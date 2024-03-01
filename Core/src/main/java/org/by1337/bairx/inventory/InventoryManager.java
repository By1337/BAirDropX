package org.by1337.bairx.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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
import org.by1337.bairx.inventory.pipeline.SmoothItemAddHandler;
import org.by1337.bairx.inventory.pipeline.click.AntiSteal;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.Pair;
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
    private AntiSteal.Config antiStealCfg;
    private SmoothItemAddHandler.Config smoothItemAddCfg;
    private int emptySlotChance;

    public InventoryManager(int invSize, String invName) {
        this(invSize, invSize, invName, 0);
    }

    public InventoryManager(int genItemCount, int invSize, String invName, int emptySlotChance) {
        this.emptySlotChance = emptySlotChance;
        this.genItemCount = genItemCount;
        this.invSize = invSize;
        this.invName = invName;
        inventory = Bukkit.createInventory(null, invSize, BAirDropX.getMessage().messageBuilder(invName));
        antiStealCfg = new AntiSteal.Config();
        smoothItemAddCfg = new SmoothItemAddHandler.Config();
        rebuildPipeline();
        reloadExtensions();
        Bukkit.getPluginManager().registerEvents(this, BAirDropX.getInstance());
    }

    public static InventoryManager load(CompoundTag compoundTag, YamlContext cfg) {
        int genItemCount = cfg.getAsInteger("genItemCount");
        int invSize = cfg.getAsInteger("invSize");
        int emptySlotChance = cfg.getAsInteger("emptySlotChance");
        String invName = cfg.getAsString("invName");

        ListNBT listNBT = (ListNBT) compoundTag.getOrThrow("items");
        List<InventoryItem> items = new ArrayList<>();
        for (NBT nbt : listNBT) {
            items.add(InventoryItem.load((CompoundTag) nbt));
        }
        InventoryManager manager = new InventoryManager(genItemCount, invSize, invName, emptySlotChance);
        manager.setItems(items);
        manager.sortItems();
        manager.antiStealCfg.load(cfg.getAs("extensions.anti-steal", YamlContext.class, new YamlContext(new YamlConfiguration())));
        manager.smoothItemAddCfg.load(cfg.getAs("extensions.smooth-iem-add", YamlContext.class, new YamlContext(new YamlConfiguration())));
        manager.reloadExtensions();
        return manager;
    }

    public void save(CompoundTag compoundTag, YamlContext cfg) {
        cfg.set("extensions.anti-steal", antiStealCfg.save());
        cfg.set("extensions.smooth-iem-add", smoothItemAddCfg.save());
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
        if (clickPipeline != null) {
            for (Pair<String, PipelineHandler<InventoryEvent>> handler : clickPipeline.getHandlers()) {
                if (handler.getRight() instanceof AntiSteal antiSteal) {
                    antiSteal.getChestStealDataMap().clear();
                }
            }
        }
    }

    public void close() {
        release();
        HandlerList.unregisterAll(this);
    }

    public void reloadExtensions() {
        clickPipeline = new PipelineManager<>();
        if (antiStealCfg.enable) {
            clickPipeline.add("anti-steal", new AntiSteal(antiStealCfg));
        }
        if (smoothItemAddCfg.enable){
            pipeline.addBefore(
                    "handler",
                    "smooth_item_add_handler",
                    new SmoothItemAddHandler(random, smoothItemAddCfg)
            );
        }
    }

    public void rebuildPipeline() {
        pipeline = new PipelineManager<>();
        pipeline
                .add("generator", this)
                .add("handler", new ItemHandler(inventory, random));

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
    public void onClick(InventoryClickEvent e){
        if (e.getClickedInventory() == inventory){
            clickPipeline.processFirst(e);
        }
    }
    @EventHandler
    public void onClick(InventoryDragEvent e){
        if (e.getInventory() == inventory){
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
