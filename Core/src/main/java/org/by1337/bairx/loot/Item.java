package org.by1337.bairx.loot;

import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.nms.ParseCompoundTagManager;

public class Item {
    private final ItemStack itemStack;
    private int chance;
    private int maxAmount;
    private int minAmount;
    private boolean randomAmount;

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item(ItemStack itemStack, int chance) {
        this.itemStack = itemStack;
        this.chance = chance;
    }

    public Item(ItemStack itemStack, int chance, int maxAmount, int minAmount, boolean randomAmount) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.randomAmount = randomAmount;
    }
    public void save(CompoundTag compoundTag){
        compoundTag.putTag("item", ParseCompoundTagManager.get().copy(itemStack));
        compoundTag.putInt("chance", chance);
        compoundTag.putInt("maxAmount", maxAmount);
        compoundTag.putInt("minAmount", minAmount);
        compoundTag.putBoolean("randomAmount", randomAmount);
    }
    public static Item load(CompoundTag compoundTag){
        ItemStack itemStack = ParseCompoundTagManager.get().create(compoundTag.getAsCompoundTag("item"));
        int chance = compoundTag.getAsInt("chance");
        int maxAmount = compoundTag.getAsInt("maxAmount");
        int minAmount = compoundTag.getAsInt("minAmount");
        boolean randomAmount = compoundTag.getAsBoolean("randomAmount");
        return new Item(itemStack, chance, maxAmount, minAmount, randomAmount);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public boolean isRandomAmount() {
        return randomAmount;
    }

    public void setRandomAmount(boolean randomAmount) {
        this.randomAmount = randomAmount;
    }
}
