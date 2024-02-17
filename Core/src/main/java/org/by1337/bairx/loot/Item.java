package org.by1337.bairx.loot;

import org.bukkit.inventory.ItemStack;

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
