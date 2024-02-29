package org.by1337.bairx.menu;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemCreator<T> {
    ItemStack build(T raw);
}
