package org.by1337.bairx.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverLoader;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListenersMenu extends CodeMenu {
    private final AirDrop airDrop;
    @Nullable
    private final String nameSpace;
    private int page = 0;

    public ListenersMenu(AirDrop airDrop, Player player, @Nullable String nameSpace) {
        super(player, "&7Listeners: " + (nameSpace == null ? "all" : nameSpace));
        this.airDrop = airDrop;
        this.nameSpace = nameSpace;
    }

    private ItemStack buildItem(Observer observer) {
        ItemStack icon = observer.getIcon();

        SpacedNameKey name = observer.getName();
        List<String> lore = new ArrayList<>();
        boolean singled = name != null && airDrop.getSignedListeners().contains(name);
        lore.add("&fОписание: &f" + observer.getDescription());
        lore.add("&fПодписан: &f" + (name == null ? "&cНевозможно подписаться" : singled));
        lore.add("&fИвент: &f" + observer.getEventType().getNameKey().getName());
        lore.add("&fЛКМ - подписаться/отписаться");
        lore.add("&fКоманды: (" + observer.getCommands().size() + ")");
        for (String cmd : observer.getCommands()) {
            lore.add("&f" + cmd);
        }
        lore.add("&fDeny Commands: (" + observer.getDenyCommands().size() + ")");
        for (String cmd : observer.getDenyCommands()) {
            lore.add("&f" + cmd);
        }

        var meta = icon.getItemMeta();
        meta.setDisplayName(BAirDropX.getMessage().messageBuilder("&7" + name));
        if (singled) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(trimAndApplyColor(lore));
        icon.setItemMeta(meta);
        return icon;
    }

    public List<String> trimAndApplyColor(List<String> in) {
        List<String> out = new ArrayList<>();
        for (String s : in) {
            var s1 = BAirDropX.getMessage().messageBuilder(s);
            if (s1.length() > 75) {
                s1 = s1.substring(0, 72);
                s1 += "...";
            }
            out.add(s1);
        }
        return out;
    }


    public void generateMenu() {
        getItems().clear();
        var list = getObservers();
        int startPos = 45 * page;

        int slot = 0;
        for (int i = startPos; i < startPos + 45; i++) {
            if (list.size() <= i) break;
            var observer = list.get(i);
            addItem(new MenuItem<>(observer, this::buildItem, this::clickByObserver, slot));
            slot++;
        }

        {
            ItemStack arrowBack = new ItemStack(Material.ARROW);
            var meta = arrowBack.getItemMeta();
            meta.setDisplayName(BAirDropX.getMessage().messageBuilder("&cНазад"));
            arrowBack.setItemMeta(meta);
            addItem(new MenuItem<>(null, obj -> arrowBack, click -> {
                if (page > 0) {
                    page--;
                    generateMenu();
                }
            }, 45));
        }
        {
            ItemStack arrowNext = new ItemStack(Material.ARROW);
            var meta = arrowNext.getItemMeta();
            meta.setDisplayName(BAirDropX.getMessage().messageBuilder("&aВперёд"));
            arrowNext.setItemMeta(meta);
            addItem(new MenuItem<>(null, obj -> arrowNext, click -> {
                page++;
                generateMenu();
            }, 53));
        }
        generateMenu();
    }

    private List<Observer> getObservers() {
        List<Observer> list = new ArrayList<>();
        ObserverManager manager = BAirDropX.getObserverManager();
        if (nameSpace == null) {
            for (ObserverLoader loader : manager.getLoaders().values()) {
                list.addAll(loader.getObserverList());
            }
        } else {
            ObserverLoader loader = manager.getLoaders().get(new NameKey(nameSpace));
            if (loader != null) {
                list.addAll(loader.getObserverList());
            }
        }
        return list;
    }

    private void clickByObserver(Object obj) {
        if (!(obj instanceof Observer observer)) return;
        if (observer.getName() == null) return;
        if (!airDrop.getSignedListeners().remove(observer.getName())) {
            airDrop.getSignedListeners().add(observer.getName());
        }
        generateMenu();
    }

    @Override
    protected void onClose(InventoryCloseEvent e) {
        super.onClose(e);
        airDrop.trySave();
    }

    @Deprecated
    public CodeMenu getMenu() {
        return this;
    }
}
