package org.by1337.bairx.menu;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.function.Consumer;

public class MenuItem<T> {
    private final T raw;
    private final HashSet<Integer> slots;
    private final ItemCreator<T> builder;
    private final Consumer<T> click;

    public MenuItem(T raw, HashSet<Integer> slots, ItemCreator<T> builder, Consumer<T> click) {
        this.raw = raw;
        this.slots = slots;
        this.builder = builder;
        this.click = click;
    }

    public MenuItem(T raw, ItemCreator<T> builder, Consumer<T> click, int... slots) {
        this.raw = raw;
        this.builder = builder;
        this.click = click;
        this.slots = new HashSet<>();
        for (int slot : slots) {
            this.slots.add(slot);
        }
    }
    public ItemStack build(){
        return builder.build(raw);
    }

    public T getRaw() {
        return raw;
    }

    public HashSet<Integer> getSlots() {
        return slots;
    }

    public ItemCreator<T> getBuilder() {
        return builder;
    }
    public void click(){
        click.accept(raw);
    }
}
