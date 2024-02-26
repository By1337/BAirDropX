package org.by1337.bairx.menu;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemBuilder<T> {
    ItemStack build(T raw);
}
