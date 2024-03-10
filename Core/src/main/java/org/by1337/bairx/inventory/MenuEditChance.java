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

import java.util.ArrayList;
import java.util.List;

public class MenuEditChance extends AsyncClickListener {
    private final int itemSlots = 45;
    private final InventoryManager inventoryManager;
    private final AirDrop airDrop;
    private final Player player;
    private int page = 0;
    private final List<InventoryItem> itemsOnScreen = new ArrayList<>();

    public MenuEditChance(InventoryManager inventoryManager, AirDrop airDrop, Player player) {
        super(player, false);
        this.inventoryManager = inventoryManager;
        this.airDrop = airDrop;
        this.player = player;
        createInventory(54, BAirDropX.getMessage().messageBuilder("&7Редактирования шансов появления дропа"));
        generate();
    }

    private void generate() {
        inventory.clear();
        inventory.setItem(45, new ItemBuilder().material(Material.ARROW).name("&cНазад").lore("").build());
        inventory.setItem(53, new ItemBuilder().material(Material.ARROW).name("&aВперёд").lore("").build());
        ItemStack gui = new ItemBuilder().material(Material.BLACK_STAINED_GLASS_PANE).name(" ").lore("").build();
        for (int i = 46; i < 53; i++) {
            inventory.setItem(i, gui);
        }
        int startPos = itemSlots * page;
        var list = inventoryManager.getItems();
        itemsOnScreen.clear();
        for (int i = startPos, slot = 0; i < startPos + itemSlots; i++, slot++) {
            if (list.size() <= i) break;
            var item = list.get(i);
            var itemStack = item.getItemStack();
            itemsOnScreen.add(item);
            inventory.setItem(slot, new ItemBuilder()
                    .lore(
                            "&aШанс появления:&f {chance}",
                            "&aРандомизация количества:&f {enable-random-count}",
                            "&aМинимальное количество:&f {min-count}",
                            "&aМаксимальное количество:&f {max-count}",
                            "&aЛКМ&f - +1 к шансу появления",
                            "&aShift + ЛКМ&f - +10 к шансу появления",
                            "&aПКМ&f - -1 к шансу появления",
                            "&aShift + ПКМ&f - -10 к шансу появления",
                            "&aQ&f - Включить/выключить рандомизацию количества",
                            "&a1&f - +1 к минимальному количеству",
                            "&a2&f - +10 к минимальному количеству",
                            "&c3&f - -1 к минимальному количеству",
                            "&c4&f - -10 к минимальному количеству",
                            "&a5&f - +1 к максимальному количеству",
                            "&a6&f - +10 к максимальному количеству",
                            "&c7&f - -1 к максимальному количеству",
                            "&c8&f - -10 к максимальному количеству"
                    )
                    .replaceLore(item::replace)
                    .build(itemStack)
            );
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent e) {
        inventoryManager.sortItems();
        airDrop.trySave();
    }

    @Override
    protected void onClick(InventoryClickEvent e) {
        if (e.getSlot() == 45) {
            if (page > 0) {
                page--;
                generate();
            }
        } else if (e.getSlot() == 53) {
            page++;
            generate();
        } else {
            if (e.getSlot() > itemsOnScreen.size() - 1) return;

            var item = itemsOnScreen.get(e.getSlot());
            if (item == null) return;
            switch (e.getClick()) {
                case LEFT -> {
                    int chance = item.getChance() + 1;
                    item.setChance(Math.min(chance, 100));
                    generate();
                }
                case SHIFT_LEFT -> {
                    int chance = item.getChance() + 10;
                    item.setChance(Math.min(chance, 100));
                    generate();
                }
                case RIGHT -> {
                    int chance = item.getChance() - 1;
                    item.setChance(Math.max(chance, 0));
                    generate();
                }
                case SHIFT_RIGHT -> {
                    int chance = item.getChance() - 10;
                    item.setChance(Math.max(chance, 0));
                    generate();
                }
                case DROP -> {
                    item.setRandomAmount(!item.isRandomAmount());
                    generate();
                }
                case NUMBER_KEY -> {
                    switch (e.getHotbarButton()) {
                        case 0 -> item.setMinAmount(item.getMinAmount() + 1);
                        case 1 -> item.setMinAmount(item.getMinAmount() + 10);
                        case 2 -> item.setMinAmount(item.getMinAmount() - 1);
                        case 3 -> item.setMinAmount(item.getMinAmount() - 10);
                        case 4 -> item.setMaxAmount(item.getMaxAmount() + 1);
                        case 5 -> item.setMaxAmount(item.getMaxAmount() + 10);
                        case 6 -> item.setMaxAmount(item.getMaxAmount() - 1);
                        case 7 -> item.setMaxAmount(item.getMaxAmount() - 10);
                    }
                    generate();
                }
            }

        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    protected void onClick(InventoryDragEvent e) {

    }
}
