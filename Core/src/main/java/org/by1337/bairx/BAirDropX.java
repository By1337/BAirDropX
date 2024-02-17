package org.by1337.bairx;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.menu.MenuItemBuilder;
import org.by1337.bairx.menu.MenuLoader;
import org.by1337.bairx.menu.adapter.AdapterCustomItemStack;
import org.by1337.bairx.menu.adapter.AdapterIRequirement;
import org.by1337.bairx.menu.adapter.AdapterRequirements;
import org.by1337.bairx.menu.requirement.IRequirement;
import org.by1337.bairx.menu.requirement.Requirements;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.util.Objects;
import java.util.function.Supplier;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;
    private ObserverManager observerManager;
    private static boolean debug = true;
    private MenuLoader menuLoader;

    @Override
    public void onLoad() {
        setInstance(this);
        message = new Message(getLogger());
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        if (!new File(getDataFolder() + "listeners/default.yml").exists()){
            saveResource("listeners/default.yml", false);
        }

        AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
        AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
        AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());
        AdapterRegistry.registerAdapter(MenuItemBuilder.class, new AdapterCustomItemStack());
        AdapterRegistry.registerAdapter(IRequirement.class, new AdapterIRequirement());
        AdapterRegistry.registerAdapter(Requirements.class, new AdapterRequirements());


        menuLoader = new MenuLoader(this);

        observerManager = new ObserverManager();
        AirDrop airDrop = ClassicAirDrop.createNew(new NameKey("default"), new File(getDataFolder() + "/default"));

        new BukkitRunnable() {
            @Override
            public void run() {
                airDrop.tick();
            }
        }.runTaskTimer(this, 10, 10);
    }

    @Override
    public void onDisable() {
        AdapterRegistry.unregisterAdapter(GeneratorSetting.class);
        AdapterRegistry.unregisterAdapter(Requirement.class);
        AdapterRegistry.unregisterAdapter(Observer.class);
        AdapterRegistry.unregisterAdapter(MenuItemBuilder.class);
        AdapterRegistry.unregisterAdapter(IRequirement.class);
        AdapterRegistry.unregisterAdapter(Requirements.class);
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

    public static void debug(Supplier<String> message){
        if (debug){
            getMessage().logger(message.get());
        }
    }
}
