package org.by1337.bairx.observer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.event.Event;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                    ObserverLoader loader = new ObserverLoader(file, nameKey);
                    loaders.put(nameKey, loader);
                }
            } catch (Throwable e) {
                BAirDropX.getMessage().error("failed to load listeners from " + file, e);
            }
        }
    }

    public void invoke(@NotNull String listener, @NotNull Event event) {
        invoke(null, new NameKey(listener), event, false);
    }

    public void invoke(@NotNull SpacedNameKey listener, @NotNull Event event) {
        invoke(listener, event, false);
    }
    public void invoke(@NotNull SpacedNameKey listener, @NotNull Event event, boolean ignoreType) {
        invoke(listener.getSpace(), listener.getName(), event, ignoreType);
    }

    public void invoke(@Nullable NameKey space, @NotNull NameKey listener, @NotNull Event event, boolean ignoreType) {
       try {
           if (space != null) {
               var loader = loaders.get(space);
               if (loader == null) {
                   BAirDropX.getMessage().error("unknown listener loader! " + space.getName());
                   return;
               }
               var observer = loader.getByName(listener);
               if (observer == null) {
                   BAirDropX.getMessage().error("unknown listener! " + listener.getName());
                   return;
               }
               if (observer.getEventType().equals(event.getEventType()) || ignoreType) {
                   observer.update(event);
               }
           } else {
               Map<NameKey, Observer> observerMap = new HashMap<>();
               for (Map.Entry<NameKey, ObserverLoader> entry : loaders.entrySet()) {
                   var observer = entry.getValue().getByName(listener);
                   if (observer != null) {
                       observerMap.put(entry.getKey(), observer);
                   }
               }

               if (observerMap.isEmpty()) {
                   BAirDropX.getMessage().error("unknown listener! " + listener.getName());
               } else if (observerMap.size() == 1) {
                   Observer observer = observerMap.values().iterator().next();
                   if (observer.getEventType().equals(event.getEventType()) || ignoreType) {
                       observer.update(event);
                   }

               } else {
                   StringJoiner joiner = new StringJoiner(", ");

                   for (Map.Entry<NameKey, Observer> entry : observerMap.entrySet()) {
                       joiner.add(entry.getKey().getName() + ":" + listener.getName());
                   }

                   BAirDropX.getMessage().error("Неоднозначный вызов слушателя %s! Все возможные варианты: [%s]", listener.getName(), joiner.toString());
               }
           }
       }catch (Exception e){
           BAirDropX.getMessage().error(e);
       }
    }

    public Map<NameKey, ObserverLoader> getLoaders() {
        return loaders;
    }
}
