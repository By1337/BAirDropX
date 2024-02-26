package org.by1337.bairx.observer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ObserverLoader {
    private Map<NameKey, Observer> observers;
    private final NameKey nameSpace;
    private List<Observer> observerList;

    public ObserverLoader(File file, NameKey nameSpace) throws IOException, InvalidConfigurationException {
        this.nameSpace = nameSpace;
        YamlConfig config = new YamlConfig(file);

        Map<String, ?> map = YamlContext.getMemorySection(config.getHandle().get("listeners")).getValues(false);

        observers = new HashMap<>();
        observerList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            NameKey nameKey = AdapterRegistry.getAs(entry.getKey(), NameKey.class);
            Observer observer = AdapterRegistry.getAs(entry.getValue(), Observer.class);
            observers.put(nameKey, observer);
            observerList.add(observer);
        }

        observers.forEach((k, v) -> v.setName(new SpacedNameKey(nameSpace, k)));
    }
    @Nullable
    public Observer getByName(NameKey nameKey){
        return observers.get(nameKey);
    }

    public Collection<Observer> values(){
        return observers.values();
    }

    public Map<NameKey, Observer> getObservers() {
        return observers;
    }

    public List<Observer> getObserverList() {
        return observerList;
    }

    public NameKey getNameSpace() {
        return nameSpace;
    }
}
