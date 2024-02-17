package org.by1337.bairx.menu;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.CommandSyntaxError;
import org.by1337.blib.command.argument.ArgumentEnumValue;
import org.by1337.blib.command.argument.ArgumentString;
import org.by1337.blib.command.argument.ArgumentStrings;
import org.by1337.blib.util.Pair;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.menu.requirement.Requirements;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class Menu extends AsyncClickListener {

    protected final List<MenuItemBuilder> items;
    protected List<MenuItem> customItems = new ArrayList<>();
    protected List<MenuItem> currentItems = new ArrayList<>();
    protected final String title;
    protected final int size;
    protected Requirements openRequirements;
    @Nullable
    protected final Menu previousMenu;
    protected final Command<Void> commands;
    protected List<String> openCommands = new ArrayList<>();


    public Menu(MenuSetting setting, Player player, @Nullable Menu previousMenu) {
        this(setting, player, previousMenu, true);
    }

    public Menu(MenuSetting setting, Player player, @Nullable Menu previousMenu, boolean async) {
        this(setting.getItems(), setting.getTitle(), setting.getSize(), setting.getViewRequirement(), player, setting.getType(), previousMenu, async);
        openCommands = setting.getOpenCommands();
    }

    public Menu(List<MenuItemBuilder> items, String title, int size, @Nullable Requirements viewRequirement, Player player, InventoryType type, @Nullable Menu previousMenu) {
        this(items, title, size, viewRequirement, player, type, previousMenu, true);
    }

    public Menu(List<MenuItemBuilder> items, String title, int size, @Nullable Requirements viewRequirement, Player player, InventoryType type, @Nullable Menu previousMenu, boolean async) {
        super(player, async);
        commands = new Command<>("menu");
        openRequirements = viewRequirement;
        this.items = items;
        this.title = title;
        this.size = size;
        this.previousMenu = previousMenu;
        createInventory(size, replace(title), type);
        registerPlaceholder("{has-back-menu}", () -> String.valueOf(previousMenu != null));
    }


    public void open() {
        Menu menu = this;
        syncUtil(() -> {
            if (openRequirements != null && !openRequirements.check(menu, menu)) {
                List<String> list = new ArrayList<>(openRequirements.getDenyCommands());
                list.replaceAll(this::replace);
                runCommand(list);
            } else {
                if (!openCommands.isEmpty()) runCommand(openCommands);
                viewer.openInventory(inventory);
                generate0();
            }
        });
    }

    protected abstract void generate();

    protected void generate0() {
        inventory.clear();
        currentItems.clear();
        currentItems = new ArrayList<>(items.stream().map(m -> m.build(this)).filter(Objects::nonNull).toList());
        generate();
        setItems(currentItems);
        setItems(customItems);
        sendFakeTitle(replace(title));
    }

    protected void setItems(List<MenuItem> list) {
        for (MenuItem menuItem : list) {
            for (int slot : menuItem.getSlots()) {
                ItemStack item = menuItem.getItemStack();
                inventory.setItem(slot, item);
            }
        }
    }

    public void runCommand(List<String> commands) {
       //todo
    }

    @Override
    protected void onClick(InventoryDragEvent e) {
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) {
            return;
        }

        ItemStack itemStack = e.getCurrentItem();
        ItemMeta im = itemStack.getItemMeta();
        if (!im.getPersistentDataContainer().has(MenuItemBuilder.MENU_ITEM_KEY, PersistentDataType.INTEGER)) {
            inventory.clear();
            generate0();
            return;
        }
        Integer id = im.getPersistentDataContainer().get(MenuItemBuilder.MENU_ITEM_KEY, PersistentDataType.INTEGER);
        if (id == null) return;

        MenuItem menuItem = getItemById(id);

        if (menuItem == null) {
            inventory.clear();
            generate0();
            return;
        }

        runCommand(menuItem.getCommands(e, this));
    }


    public void onClose(InventoryCloseEvent e) {
    }


    @Nullable
    protected MenuItem getItemById(int id) {
        var item = findItemIn(id, customItems);
        return item == null ? findItemIn(id, currentItems) : item;
    }

    @Nullable
    protected MenuItem findItemIn(int id, List<MenuItem> list) {
        for (MenuItem item : list) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return viewer;
    }

    public void reopen() {
        if (getPlayer() == null || !getPlayer().isOnline()) {
            throw new IllegalArgumentException();
        }
        syncUtil(() -> {
            reRegister();
            if (!viewer.getOpenInventory().getTopInventory().equals(inventory))
                viewer.openInventory(getInventory());
            sendFakeTitle(replace(title));
            generate0();
        });
    }

    @Nullable
    public Menu getPreviousMenu() {
        return previousMenu;
    }

    @Override
    public final String replace(String string) {
        return super.replace(BAirDropX.getMessage().messageBuilder(string, viewer));
    }

    public void back() {
        getPreviousMenu().reopen();
    }

}