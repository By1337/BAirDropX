package org.by1337.bairx.observer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.event.Event;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ObserverManager {
    private final Map<NameKey, ObserverLoader> loaders = new HashMap<>();

    public ObserverManager() {
        File dir = new File(BAirDropX.getInstance().getDataFolder() + "/listeners");
        if (!dir.exists()) {
            dir.mkdir();
        }
        var list = dir.listFiles();
        if (list == null) return;
        for (File file : list) {
            try {
                if (file.getName().endsWith(".yml")) {
                    NameKey nameKey = new NameKey(file.getName().replace(".yml", ""));
                    ObserverLoader loader = new ObserverLoader(file);
                    loaders.put(nameKey, loader);
                }
            } catch (Throwable e) {
                BAirDropX.getMessage().error("failed to load listeners from " + file, e);
            }
        }
    }

    public void invoke(String listener, Event event) {
        invoke(listener, event, false);
    }

    public void invoke(String listener, Event event, boolean ignoreType) {
        final NameKey loaderName;
        final NameKey observerName;
        if (listener.contains(":")) {
            String[] arr = listener.split(":");
            loaderName = new NameKey(arr[0]);
            observerName = new NameKey(arr[1]);
        } else {
            loaderName = null;
            observerName = new NameKey(listener);
        }

        if (loaderName != null) {
            var loader = loaders.get(loaderName);
            if (loader == null) {
                BAirDropX.getMessage().error("unknown listener loader! " + loaderName);
                return;
            }
            var observer = loader.getByName(observerName);
            if (observer == null) {
                BAirDropX.getMessage().error("unknown listener! " + observerName.getName());
                return;
            }
            if (observer.getEventType().equals(event.getEventType()) || ignoreType) {
                observer.update(event);
            }
        } else {
            Map<NameKey, Observer> observerMap = new HashMap<>();
            for (Map.Entry<NameKey, ObserverLoader> entry : loaders.entrySet()) {
                var observer = entry.getValue().getByName(observerName);
                if (observer != null) {
                    observerMap.put(entry.getKey(), observer);
                }
            }

            if (observerMap.isEmpty()) {
                BAirDropX.getMessage().error("unknown listener! " + observerName.getName());
            } else if (observerMap.size() == 1) {
                Observer observer = observerMap.values().iterator().next();
                if (observer.getEventType().equals(event.getEventType()) || ignoreType) {
                    observer.update(event);
                }

            } else {
                StringJoiner joiner = new StringJoiner(", ");

                for (Map.Entry<NameKey, Observer> entry : observerMap.entrySet()) {
                    joiner.add(entry.getKey().getName() + ":" + observerName.getName());
                }

                BAirDropX.getMessage().error("Неоднозначный вызов слушателя %s! Все возможные варианты: [%s]", observerName.getName(), joiner.toString());
            }
        }
    }
}
