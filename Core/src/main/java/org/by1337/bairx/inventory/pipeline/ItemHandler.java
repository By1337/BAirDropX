package org.by1337.bairx.inventory.pipeline;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemHandler implements PipelineHandler<ItemStack> {
    private final Inventory inventory;
    private final Random random;

    public ItemHandler(Inventory inventory, Random random) {
        this.inventory = inventory;
        this.random = random;
    }

    @Override
    public void process(ItemStack val, PipelineManager<ItemStack> manager) {
        inventory.setItem(getRandomEmptySlot(), val);
    }

    private int getRandomEmptySlot(){
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            var item = inventory.getItem(i);
            if (item == null || !item.hasItemMeta()){
                emptySlots.add(i);
            }
        }
        if (emptySlots.isEmpty()){
            return 0;
        }
        return emptySlots.get(random.nextInt(emptySlots.size()));
    }
}
