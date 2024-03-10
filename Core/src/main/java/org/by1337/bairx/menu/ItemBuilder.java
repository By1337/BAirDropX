package org.by1337.bairx.menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.hook.wg.basehead.BaseHeadHook;
import org.by1337.blib.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ItemBuilder {
    private List<String> lore;
    private String name;
    private String material;
    private List<Pair<NamespacedKey, String>> nbts = new ArrayList<>();
    private int modelData = 0;

    public ItemBuilder lore(String... lore) {
        return lore(new ArrayList<>(List.of(lore)));
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder replaceLore(UnaryOperator<String> operator) {
        this.lore.replaceAll(operator);
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder putNbt(NamespacedKey name, String value) {
        nbts.add(Pair.of(name, value));
        return this;
    }

    public ItemBuilder material(String material) {
        this.material = material;
        return this;
    }

    public ItemBuilder material(Material material) {
        this.material = material.name();
        return this;
    }

    public ItemBuilder modelData(int modelData) {
        this.modelData = modelData;
        return this;
    }

    public ItemStack build() {
        return build(new ItemStack(BaseHeadHook.getItem(material)));
    }

    public ItemStack build(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        if (name != null)
            meta.setDisplayName(BAirDropX.getMessage().messageBuilder(name));
        List<String> lore0 = new ArrayList<>();
        if (meta.getLore() != null) {
            lore0.addAll(meta.getLore());
        }
        lore0.addAll(lore);
        lore0.replaceAll(BAirDropX.getMessage()::messageBuilder);
        meta.setLore(lore0);
        if (!nbts.isEmpty()) {
            var pdc = meta.getPersistentDataContainer();
            for (Pair<NamespacedKey, String> nbt : nbts) {
                pdc.set(nbt.getLeft(), PersistentDataType.STRING, nbt.getRight());
            }
        }
        if (modelData != 0){
            meta.setCustomModelData(modelData);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
