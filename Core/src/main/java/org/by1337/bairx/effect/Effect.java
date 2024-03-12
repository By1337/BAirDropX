package org.by1337.bairx.effect;

import org.bukkit.Location;
import org.by1337.blib.nbt.impl.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface Effect {
    void start(@NotNull Location location);
    void stop();
    boolean isStopped();
}
