package org.by1337.bairx.util.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.by1337.bairx.BAirDropX;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class PluginEnablerManager {
    private final Map<String, ThrowableRunnable> enable = new LinkedHashMap<>();
    private final Map<String, ThrowableRunnable> disable = new LinkedHashMap<>();
    private final Set<String> enabled = new HashSet<>();

    private final Plugin plugin;

    public PluginEnablerManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginEnablerManager enable(@NotNull String name, @NotNull ThrowableRunnable runnable) {
        if (enable.containsKey(name)) {
            throw new IllegalStateException("Enable block already exist! " + name);
        }
        enable.put(name, runnable);
        return this;
    }

    public PluginEnablerManager disable(@NotNull String name, @NotNull ThrowableRunnable runnable) {
        if (disable.containsKey(name)) {
            throw new IllegalStateException("Disable block already exist! " + name);
        }
        if (!enable.containsKey(name)) {
            throw new IllegalStateException("Enable block for " + name + " doesn't exist");
        }
        disable.put(name, runnable);
        return this;
    }

    public boolean onEnable() {
        for (String string : enable.keySet()) {
            var run = enable.get(string);
            BAirDropX.debug(() -> "enable: " + string);
            try {
                run.run();
                enabled.add(string);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to enable! Step: " + string, t);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return false;
            }
        }
        return true;
    }

    public void onDisable() {
        for (String string : disable.keySet()) {
            if (!enabled.contains(string)) continue;
            BAirDropX.debug(() -> "disable: " + string);
            var run = disable.get(string);
            try {
                run.run();
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to disable! Step: " + string, t);
            }
        }
        enabled.clear();
    }


    public interface ThrowableRunnable {
        void run() throws Throwable;
    }
}
