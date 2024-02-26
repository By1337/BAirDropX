package org.by1337.bairx.effect.impl;

import org.by1337.bairx.nbt.impl.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class DefaultParticle implements SpawnableParticle{
    @Override
    public <T> void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {

    }

    @Override
    public CompoundTag save() {
        return null;
    }
}
