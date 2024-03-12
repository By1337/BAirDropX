package org.by1337.bairx.effect;

import org.by1337.blib.nbt.impl.CompoundTag;

public interface EffectCreator {
    Effect create();
    void save(CompoundTag compoundTag);
    String name();
}
