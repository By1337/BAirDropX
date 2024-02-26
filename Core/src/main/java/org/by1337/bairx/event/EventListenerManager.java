package org.by1337.bairx.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.*;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.blib.BLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListenerManager implements Listener {
    private static final HashMap<Plugin, List<EventListener>> listeners = new HashMap<>();

    public static void load() {
        Bukkit.getPluginManager().registerEvents(new EventListenerManager(), BAirDropX.getInstance());
    }

    public static void register(Plugin plugin, EventListener listener) {
        BLib.catchOp("async listener registering");
        listeners.computeIfAbsent(plugin, k -> new ArrayList<>()).add(listener);
    }

    public static void unregister(Plugin plugin, EventListener listener) {
        BLib.catchOp("async listener unregistering");
        List<EventListener> pluginListeners = listeners.get(plugin);
        if (pluginListeners != null) {
            pluginListeners.remove(listener);
            if (pluginListeners.isEmpty()) {
                listeners.remove(plugin);
            }
        }
    }

    public static void call(Event event, AirDrop airDrop) {
        for (List<EventListener> list : listeners.values().toArray(new List[0])) {
            for (EventListener listener : list.toArray(new EventListener[0])) {
                listener.onEvent(event, airDrop);
            }
        }
    }

    @EventHandler
    public void onPluginUnload(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (listeners.containsKey(plugin)) {
            List<EventListener> pluginListeners = listeners.get(plugin);
            for (EventListener listener : pluginListeners) {
                unregister(plugin, listener);
            }
        }
    }
}
