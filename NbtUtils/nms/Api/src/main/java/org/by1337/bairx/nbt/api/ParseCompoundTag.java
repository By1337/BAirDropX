package org.by1337.bairx.nbt.api;

import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.nbt.impl.CompoundTag;

public interface ParseCompoundTag {
    CompoundTag copy(ItemStack itemStack);
    ItemStack create(CompoundTag compoundTag);
}
