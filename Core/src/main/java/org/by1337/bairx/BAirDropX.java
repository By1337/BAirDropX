package org.by1337.bairx;

import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.configuration.adapter.AdapterRegistry;

import java.util.Objects;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;

    @Override
    public void onLoad() {
        setInstance(this);
        message = new Message(getLogger());
    }

    @Override
    public void onEnable() {
        AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
        AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
        AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());

    }

    @Override
    public void onDisable() {
        AdapterRegistry.unregisterAdapter(GeneratorSetting.class);
        AdapterRegistry.unregisterAdapter(Requirement.class);
        AdapterRegistry.unregisterAdapter(Observer.class);
    }

    public static Message getMessage() {
        return instance.message;
    }

    public static BAirDropX getInstance() {
        return instance;
    }

    public static void setInstance(BAirDropX instance) {
        Objects.requireNonNull(instance);
        if (BAirDropX.instance != null){
            throw new UnsupportedOperationException();
        }
        BAirDropX.instance = instance;
    }
}
