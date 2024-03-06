package org.by1337.bairx.addon;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AddonLogger extends Logger {
    private final String prefix;

    public AddonLogger(@NotNull Addon addon, @NotNull String moduleName, @NotNull Logger parent) {
        super(addon.getClass().getCanonicalName(), null);
        prefix = String.format("[%s] ", moduleName);
        setParent(parent);
        setLevel(Level.ALL);
    }

    public AddonLogger(String prefix, Class<?> clazz, @NotNull Logger parent) {
        super(clazz.getCanonicalName(), null);
        this.prefix = String.format("[%s] ", prefix);
        setParent(parent);
        setLevel(Level.ALL);
    }

    @Override
    public void log(@NotNull LogRecord logRecord) {
        logRecord.setMessage(prefix + logRecord.getMessage());
        super.log(logRecord);
    }
}
