package org.by1337.bairx.effect;

import org.bukkit.Location;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface Effect {
    void start(@NotNull Location location);
    void stop();
}
