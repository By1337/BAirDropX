package org.by1337.bairx.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.by1337.bairx.menu.requirement.Requirements;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleMenu extends Menu{
    public SimpleMenu(MenuSetting setting, Player player, @Nullable Menu previousMenu) {
        super(setting, player, previousMenu);
    }

    public SimpleMenu(List<MenuItemBuilder> items, String title, int size, @Nullable Requirements viewRequirement, Player player, InventoryType type, @Nullable Menu previousMenu) {
        super(items, title, size, viewRequirement, player, type, previousMenu);
    }

    @Override
    protected void generate() {
    }

}
