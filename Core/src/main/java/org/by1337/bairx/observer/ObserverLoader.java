package org.by1337.bairx.observer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObserverLoader {
    private Map<NameKey, Observer> observers;

    public ObserverLoader(File file) throws IOException, InvalidConfigurationException {
        YamlConfig config = new YamlConfig(file);

        observers = config.getContext().getMap("listeners", Observer.class, NameKey.class, new HashMap<>());

    }
    @Nullable
    public Observer getByName(NameKey nameKey){
        return observers.get(nameKey);
    }

    public Collection<Observer> values(){
        return observers.values();
    }
}
