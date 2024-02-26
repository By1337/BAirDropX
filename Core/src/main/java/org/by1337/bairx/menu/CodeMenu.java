package org.by1337.bairx.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.by1337.bairx.BAirDropX;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CodeMenu extends AsyncClickListener {
    private final List<MenuItem<?>> items = new ArrayList<>();
    public CodeMenu(Player viewer, String title) {
        super(viewer);
        createInventory(54, BAirDropX.getMessage().messageBuilder(title));
    }
    public void generateMenu(){
        inventory.clear();
        for (MenuItem<?> item : items) {
            var itemStack = item.build();
            for (Integer slot : item.getSlots()) {
                inventory.setItem(slot, itemStack);
            }
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent e) {
    }

    @Override
    protected void onClick(InventoryClickEvent e) {
        var item = findItem(e.getSlot());
        if (item == null){
            return;
        }
        item.click();
    }

    @Override
    protected void onClick(InventoryDragEvent e) {

    }
    @Nullable
    private MenuItem<?> findItem(int slot){
        for (MenuItem<?> item : items) {
            if (item.getSlots().contains(slot)) return item;
        }
        return null;
    }
    public Player getPlayer(){
        return viewer;
    }

    public Inventory getInventory(){
        return inventory;
    }

    public CodeMenu addItem(MenuItem<?> item){
        items.add(item);
        return this;
    }

    public List<MenuItem<?>> getItems() {
        return items;
    }
}
