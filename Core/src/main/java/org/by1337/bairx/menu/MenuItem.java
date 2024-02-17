package org.by1337.bairx.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.click.ClickType;
import org.by1337.bairx.menu.click.IClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuItem {
    private int[] slots;
    private ItemStack itemStack;
    private final Map<ClickType, IClick> clicks;
    private final int id;
    private Placeholderable placeholderable;

    public MenuItem(int[] slots, ItemStack itemStack, Map<ClickType, IClick> clicks, int id, Placeholderable placeholderable) {
        this.slots = slots;
        this.itemStack = itemStack;
        this.clicks = clicks;
        this.id = id;
        this.placeholderable = placeholderable;
    }


    public List<String> getCommands(InventoryClickEvent e, Menu menu) {
        List<String> list = new ArrayList<>();
        ClickType clickType1 = ClickType.adapter(e);
        if (clickType1 == null) return list;
        IClick click = clicks.get(clickType1);
        if (click != null) {
            list.addAll(click.run(placeholderable, menu));
        }
        IClick click1 = clicks.get(ClickType.ANY_CLICK);
        if (click1 != null) {
            list.addAll(click1.run(placeholderable, menu));
        }
        list.replaceAll(placeholderable::replace);
        return list;
    }

    public int[] getSlots() {
        return slots;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getId() {
        return id;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
