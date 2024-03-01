package org.by1337.bairx.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.inventory.pipeline.ItemHandler;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.PipelineManager;
import org.by1337.bairx.inventory.pipeline.click.AntiSteal;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class InventoryManager implements PipelineHandler<ItemStack> {
    private final Random random = new Random();
    private List<InventoryItem> items = new ArrayList<>();
    private PipelineManager<ItemStack> pipeline;
    private PipelineManager<InventoryEvent> clickPipeline;
    private int genItemCount;
    private int invSize;
    private String invName;
    private Inventory inventory;
    private AntiSteal.Config antiStealCfg;

    public InventoryManager(int invSize, String invName) {
        this(invSize, invSize, invName);
    }

    public InventoryManager(int genItemCount, int invSize, String invName) {
        this.genItemCount = genItemCount;
        this.invSize = invSize;
        this.invName = invName;
        inventory = Bukkit.createInventory(null, invSize, BAirDropX.getMessage().messageBuilder(invName));
        antiStealCfg = new AntiSteal.Config();
        rebuildPipeline();
        reloadExtensions();
    }

    public static InventoryManager load(CompoundTag compoundTag, YamlContext cfg) {
        int genItemCount = cfg.getAsInteger("genItemCount");
        int invSize = cfg.getAsInteger("invSize");
        String invName = cfg.getAsString("invName");

        ListNBT listNBT = (ListNBT) compoundTag.getOrThrow("items");
        List<InventoryItem> items = new ArrayList<>();
        for (NBT nbt : listNBT) {
            items.add(InventoryItem.load((CompoundTag) nbt));
        }
        InventoryManager manager = new InventoryManager(genItemCount, invSize, invName);
        manager.setItems(items);
        manager.sortItems();
        manager.antiStealCfg.load(cfg.getAs("extensions.anti-steal", YamlContext.class, new YamlContext(new YamlConfiguration())));
        manager.reloadExtensions();
        return manager;
    }

    public void save(CompoundTag compoundTag, YamlContext cfg) {
        cfg.set("extensions.anti-steal", antiStealCfg.save());
        cfg.set("genItemCount", genItemCount);
        cfg.set("invSize", invSize);
        cfg.set("invName", invName);
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

    public void clearInventory() {
        inventory.clear();
    }

    public void reloadExtensions(){
        clickPipeline = new PipelineManager<>();
        if (antiStealCfg.enable){
            clickPipeline.add("anti-steal", new AntiSteal(antiStealCfg));
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
            for (InventoryItem inventoryItem : items) {
                ItemStack item = inventoryItem.getItemStack(random);
                if (item != null) {
                    manager.processNext(item, this);
                    break;
                }
            }
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

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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
}
