package org.by1337.bairx.inventory;

import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.exception.ConfigurationReadException;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.nbt.nms.ParseCompoundTagManager;
import org.by1337.bairx.util.Placeholder;
import org.by1337.bairx.util.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class InventoryItem extends Placeholder {
    private final ItemStack itemStack;
    private int chance;
    private boolean randomAmount;
    private int minAmount;
    private int maxAmount;

    public InventoryItem(ItemStack itemStack, int chance, boolean randomAmount, int minAmount, int maxAmount) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.randomAmount = randomAmount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        registerPlaceholder("{chance}", this::getChance);
        registerPlaceholder("{enable-random-count}", this::isRandomAmount);
        registerPlaceholder("{min-count}", this::getMinAmount);
        registerPlaceholder("{max-count}", this::getMaxAmount);
    }

    public static InventoryItem load(CompoundTag compoundTag) {
        int chance = Validate.invokeAndHandleException(() -> compoundTag.getAsInt("chance"),
                NullPointerException.class,
                ConfigurationReadException::new,
                "Не удалось загрузить предмет так как параметр `chance` отсутствует");
        int minAmount = Validate.invokeAndHandleException(() -> compoundTag.getAsInt("minAmount"),
                NullPointerException.class,
                ConfigurationReadException::new,
                "Не удалось загрузить предмет так как параметр `minAmount` отсутствует");
        int maxAmount = Validate.invokeAndHandleException(() -> compoundTag.getAsInt("maxAmount"),
                NullPointerException.class,
                ConfigurationReadException::new,
                "Не удалось загрузить предмет так как параметр `maxAmount` отсутствует");
        boolean randomAmount = Validate.invokeAndHandleException(() -> compoundTag.getAsBoolean("randomAmount"),
                NullPointerException.class,
                ConfigurationReadException::new,
                "Не удалось загрузить предмет так как параметр `randomAmount` отсутствует");
        ItemStack itemStack = ParseCompoundTagManager.get().create(
                (CompoundTag) Validate.invokeAndHandleException(() -> compoundTag.getOrThrow("item"),
                        NullPointerException.class,
                        ConfigurationReadException::new,
                        "Не удалось загрузить предмет так как параметр `item` отсутствует")
        );
        return new InventoryItem(itemStack, chance, randomAmount, minAmount, maxAmount);
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putTag("item", ParseCompoundTagManager.get().copy(itemStack));
        compoundTag.putInt("chance", chance);
        compoundTag.putInt("minAmount", minAmount);
        compoundTag.putInt("maxAmount", maxAmount);
        compoundTag.putBoolean("randomAmount", randomAmount);
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Nullable
    public ItemStack getItemStack(Random random){
        if (chance < random.nextInt(100)){
            return null;
        }
        if (randomAmount){
            var item = getItemStack();
            item.setAmount(Math.min(item.getType().getMaxStackSize(), random.nextInt(Math.max(1, maxAmount - minAmount)) + minAmount));
            return item;
        }
        return getItemStack();
    }
    public int getChance() {
        return chance;
    }

    public boolean isRandomAmount() {
        return randomAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public void setRandomAmount(boolean randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = Math.max(1, Math.min(minAmount, itemStack.getMaxStackSize()));
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = Math.max(1, Math.min(maxAmount, itemStack.getMaxStackSize()));
    }

}
