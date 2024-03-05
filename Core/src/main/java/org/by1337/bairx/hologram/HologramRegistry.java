package org.by1337.bairx.hologram;

import org.by1337.bairx.util.Validate;
import org.by1337.bairx.hologram.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class HologramRegistry {
    private static final Map<String, HologramRegistry> types = new HashMap<>();
    public static final HologramRegistry DECENT_HOLOGRAMS = register("DecentHolograms", DHHologram::new);
    private final Supplier<Hologram> creator;
    private final String name;

    private HologramRegistry(Supplier<Hologram> creator, String name) {
        this.creator = creator;
        this.name = name;
    }

    public static HologramRegistry register(@NotNull String name, @NotNull Supplier<Hologram> creator) {
        Validate.notNull(name, "name is null!");
        Validate.notNull(creator, "creator is null!");

        if (types.containsKey(name)) {
            throw new IllegalStateException("Попытка зарегистрировать тип \"" + name + "\", который уже существует!");
        }
        HologramRegistry registry = new HologramRegistry(creator, name);
        types.put(name, registry);
        return registry;
    }

    public Supplier<Hologram> getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static HologramRegistry getByName(String name) {
        return types.get(name);
    }

    public Set<String> keySet() {
        return types.keySet();
    }
}
