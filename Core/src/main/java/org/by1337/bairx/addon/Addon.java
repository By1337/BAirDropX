package org.by1337.bairx.addon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface Addon {
    @NotNull
    File getDataFolder();
    @Nullable
    InputStream getResource(@NotNull String file);
    void saveResource(@NotNull String file, boolean replace);
    @NotNull
    Logger getLogger();
    @NotNull
    String getName();
    boolean isEnabled();
    void onLoad();
    void onEnable();
    void onDisable();
    @NotNull
    AddonDescriptionFile getDescription();
}
