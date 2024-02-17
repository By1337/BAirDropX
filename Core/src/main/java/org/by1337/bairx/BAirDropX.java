package org.by1337.bairx;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.util.Objects;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;
    private ObserverManager observerManager;

    @Override
    public void onLoad() {
        setInstance(this);
        message = new Message(getLogger());
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
        AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
        AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());

        observerManager = new ObserverManager();
        AirDrop airDrop = AirDrop.createNew(new NameKey("default"), new File(getDataFolder() + "/default"));

        new BukkitRunnable() {
            @Override
            public void run() {
                airDrop.tick();
                System.out.println(airDrop);
            }
        }.runTaskTimer(this, 10, 10);
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

    public static ObserverManager getObserverManager() {
        return instance.observerManager;
    }
}
