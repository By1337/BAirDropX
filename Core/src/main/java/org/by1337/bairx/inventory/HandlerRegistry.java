package org.by1337.bairx.inventory;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.SmoothAddItemHandler;
import org.by1337.bairx.inventory.pipeline.click.AntiSteal;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class HandlerRegistry {
    private static final Map<String, HandlerRegistry> types = new HashMap<>();
    public static final HandlerRegistry SMOOTH_ADD_ITEM = register(new SmoothAddItemHandler.Creator());
    public static final HandlerRegistry ANTI_STEAL = register(new AntiSteal.Creator());

    private final HandlerCreator<?> creator;

    private HandlerRegistry(HandlerCreator<?> creator) {
        this.creator = creator;
    }

    public HandlerCreator<?> getCreator() {
        return creator;
    }

    @Nullable
    public static HandlerRegistry getByName(String name) {
        return types.get(name);
    }

    public static int size() {
        return types.size();
    }

    public static Set<Map.Entry<String, HandlerRegistry>> entrySet() {
        return types.entrySet();
    }

    public static HandlerRegistry[] values() {
        return types.values().toArray(new HandlerRegistry[0]);
    }


    public static <T> HandlerRegistry register(HandlerCreator<T> creator) {
        return register(creator.name(), creator);
    }

    public static <T> HandlerRegistry register(String key, HandlerCreator<T> creator) {
        if (!types.containsKey(key)) {
            var v = new HandlerRegistry(creator);
            types.put(key, v);
            return v;
        } else {
            throw new IllegalArgumentException("Handler with key '" + key + "' is already registered.");
        }
    }

    public static void unregister(String key) {
        types.remove(key);
    }

    public static final class Type<T> {
        public static final Type<ItemStack> ITEM = new Type<>();
        public static final Type<InventoryEvent> CLICK = new Type<>();

        private Type() {
        }
    }
}
