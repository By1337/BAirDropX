package org.by1337.bairx.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.inventory.pipeline.ItemHandler;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.PipelineManager;
import org.by1337.bairx.nbt.NBT;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.impl.ListNBT;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class InventoryManager implements PipelineHandler<ItemStack> {
    private final Random random = new Random();
    private List<InventoryItem> items = new ArrayList<>();
    private PipelineManager<ItemStack> pipeline;
    private int genItemCount;
    private int invSize;
    private String invName;
    private Inventory inventory;

    public InventoryManager(int invSize, String invName) {
        this(invSize, invSize, invName);
    }

    public InventoryManager(int genItemCount, int invSize, String invName) {
        this.genItemCount = genItemCount;
        this.invSize = invSize;
        this.invName = invName;
        inventory = Bukkit.createInventory(null, invSize, BAirDropX.getMessage().messageBuilder(invName));
        rebuildPipeline();
    }

    public static InventoryManager load(CompoundTag compoundTag) {
        int genItemCount = compoundTag.getAsInt("genItemCount");
        int invSize = compoundTag.getAsInt("invSize");
        String invName = compoundTag.getAsString("invName");
        ListNBT listNBT = (ListNBT) compoundTag.getOrThrow("items");
        List<InventoryItem> items = new ArrayList<>();
        for (NBT nbt : listNBT) {
            items.add(InventoryItem.load((CompoundTag) nbt));
        }
        InventoryManager manager = new InventoryManager(genItemCount, invSize, invName);
        manager.setItems(items);
        return manager;
    }

    public void save(CompoundTag compoundTag) {
        // CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("genItemCount", genItemCount);
        compoundTag.putInt("invSize", invSize);
        compoundTag.putString("invName", invName);
        ListNBT listNBT = new ListNBT();
        for (InventoryItem item : items) {
            CompoundTag compoundTag1 = new CompoundTag();
            item.save(compoundTag1);
            listNBT.add(compoundTag1);
        }
        compoundTag.putTag("items", listNBT);
        //  return compoundTag;
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
