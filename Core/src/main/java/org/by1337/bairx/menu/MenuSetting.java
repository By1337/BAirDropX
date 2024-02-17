package org.by1337.bairx.menu;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.by1337.bairx.menu.requirement.Requirements;
import org.by1337.blib.BLib;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class MenuSetting {
    private final List<MenuItemBuilder> items;
    private final String title;
    private final int size;
    private final Requirements viewRequirement;
    private final InventoryType type;
    private final List<String> openCommands;
    private final YamlContext context;

    public MenuSetting(List<MenuItemBuilder> items, String title, int size, Requirements viewRequirement, InventoryType type , List<String> openCommands, YamlContext context) {
        this.items = items;
        this.title = title;
        this.size = size;
        this.viewRequirement = viewRequirement;
        this.type = type;
        this.openCommands = openCommands;
        this.context = context;
    }

    public YamlContext getContext() {
        return context;
    }

    public List<String> getOpenCommands() {
        return openCommands;
    }

    public List<MenuItemBuilder> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Requirements getViewRequirement() {
        return viewRequirement;
    }

    public InventoryType getType() {
        return type;
    }


    public void open(Player player, @Nullable Menu backMenu) {
        SimpleMenu simpleMenu = new SimpleMenu(MenuSetting.this, player, backMenu);
        simpleMenu.open();
    }
}
