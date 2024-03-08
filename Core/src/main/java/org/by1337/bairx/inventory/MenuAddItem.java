package org.by1337.bairx.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.inventory.item.InventoryItem;
import org.by1337.bairx.menu.AsyncClickListener;
import org.by1337.bairx.menu.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class MenuAddItem extends AsyncClickListener {
    private final int itemSlots = 45;
    private final InventoryManager inventoryManager;
    private final AirDrop airDrop;
    private final Player player;
    private int page = 0;
    private Map<Integer, InventoryItem> itemsOnScreen;

    public MenuAddItem(InventoryManager inventoryManager, AirDrop airDrop, Player player) {
        super(player, false);
        this.inventoryManager = inventoryManager;
        this.airDrop = airDrop;
        this.player = player;
        createInventory(54, BAirDropX.getMessage().messageBuilder("&7Добавление предметов"));
        generate();
    }


    private void generate() {
        inventory.clear();
        inventory.setItem(45, new ItemBuilder().setMaterial(Material.ARROW).setName("&cНазад").setLore("").build());
        inventory.setItem(53, new ItemBuilder().setMaterial(Material.ARROW).setName("&aВперёд").setLore("").build());
        ItemStack gui = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").setLore("").build();
        for (int i = 46; i < 53; i++) {
            inventory.setItem(i, gui);
        }
        int startPos = itemSlots * page;
        var list = inventoryManager.getItems();
        itemsOnScreen = new HashMap<>();
        for (int i = startPos, slot = 0; i < startPos + itemSlots; i++, slot++) {
            if (list.size() <= i) break;
            var item = list.get(i);
            var itemStack = item.getItemStack();
            itemsOnScreen.put(i, item);
            inventory.setItem(slot, itemStack);
        }
    }

    private void update() {
        for (int i = 0; i < 45; i++) {
            var itemStack = inventory.getItem(i);
            InventoryItem item = itemsOnScreen.get(i);

            if (item == null) {
                if (itemStack != null) {
                    inventoryManager.getItems().add(new InventoryItem(itemStack, 100, false, 1, 64));
                }
            } else {
                if (itemStack == null) {
                    inventoryManager.getItems().remove(item);
                } else if (!item.getItemStack().equals(itemStack)) {
                    inventoryManager.getItems().remove(item);
                    inventoryManager.getItems().add(new InventoryItem(itemStack, 100, false, 1, 64));
                }
            }
        }
        generate();
    }


    @Override
    protected void onClose(InventoryCloseEvent e) {
        airDrop.trySave();
    }

    @Override
    protected void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != inventory) {
            e.setCancelled(false);
            return;
        }
        if (e.getSlot() < 44) {
            syncUtil(this::update, 1);
            e.setCancelled(false);
            return;
        }
        if (e.getSlot() == 45) {
            if (page > 0) {
                page--;
                generate();
            }
        } else if (e.getSlot() == 53) {
            page++;
            generate();
        }
    }

    @Override
    protected void onClick(InventoryDragEvent e) {
        if (e.getRawSlots().stream().anyMatch(slot -> slot >= 45 && slot <= 53)) return;
        e.setCancelled(false);
        syncUtil(this::update, 1);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
