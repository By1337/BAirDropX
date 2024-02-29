package org.by1337.bairx.menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.by1337.bairx.BAirDropX;
import org.by1337.blib.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private List<String> lore;
    private String name;
    private Material material;
    private List<Pair<NamespacedKey, String>> nbts = new ArrayList<>();
    public ItemBuilder setLore(String... lore) {
        return setLore(List.of(lore));
    }
    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }
    public ItemBuilder putNbt(NamespacedKey name, String value) {
        nbts.add(Pair.of(name, value));
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStack build(){
        ItemStack itemStack = new ItemStack(material);
        var meta = itemStack.getItemMeta();
        meta.setDisplayName(BAirDropX.getMessage().messageBuilder(name));
        List<String> lore0 = new ArrayList<>(lore);
        lore0.replaceAll(BAirDropX.getMessage()::messageBuilder);
        meta.setLore(lore0);
        if (!nbts.isEmpty()){
            var pdc = meta.getPersistentDataContainer();
            for (Pair<NamespacedKey, String> nbt : nbts) {
                pdc.set(nbt.getLeft(), PersistentDataType.STRING, nbt.getRight());
            }
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
