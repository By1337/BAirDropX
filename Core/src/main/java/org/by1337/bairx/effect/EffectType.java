package org.by1337.bairx.effect;

import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EffectType {
    private final static Map<NameKey, EffectType> types = new HashMap<>();

    private final NameKey id;
    private final EffectCreator creator;

    public static EffectType register(String id, EffectCreator effectCreator) {
        return register(new NameKey(id), effectCreator);
    }

    public static EffectType register(NameKey id, EffectCreator effectCreator) {
        if (types.containsKey(id)) {
            throw new IllegalStateException("effect already exist");
        }
        return new EffectType(id, effectCreator);
    }

    @Nullable
    public static EffectType getById(NameKey id) {
        return types.get(id);
    }

    private EffectType(NameKey id, EffectCreator creator) {
        this.id = id;
        this.creator = creator;
        types.put(id, this);
    }

    public Effect create(CompoundTag compoundTag, @Nullable File file) {
        return creator.create(compoundTag, file);
    }

    public NameKey getId() {
        return id;
    }

    public EffectCreator getCreator() {
        return creator;
    }

    public interface EffectCreator {
        Effect create(CompoundTag compoundTag, @Nullable File file);
    }
}
