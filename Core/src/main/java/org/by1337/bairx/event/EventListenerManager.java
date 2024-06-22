package org.by1337.bairx.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.blib.BLib;
import org.by1337.blib.util.collection.LockableList;

import java.util.HashMap;
import java.util.List;

public class EventListenerManager implements Listener {
    private static final LockableList<EventListener> listeners = LockableList.createThreadSaveList();

    @Deprecated
    public static void register(Plugin plugin, EventListener listener) {
        register(listener);
    }
    public static void register(EventListener listener) {
        BLib.catchOp("async listener registering");
        listeners.add(listener);
    }

    @Deprecated
    public static void unregister(Plugin plugin, EventListener listener) {
        unregister(listener);
    }
    public static void unregister(EventListener listener) {
        BLib.catchOp("async listener unregistering");
        listeners.remove(listener);
    }

    public static void call(Event event, AirDrop airDrop) {
        listeners.lock();
        for (EventListener listener : listeners) {
            listener.onEvent(event, airDrop);
        }
        listeners.unlock();
    }
    public static void close(){
        listeners.clear();
    }

}
