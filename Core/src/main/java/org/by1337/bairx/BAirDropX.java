package org.by1337.bairx;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.exception.PluginInitializationException;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.menu.ListenersMenu;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.timer.TimerManager;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.Name;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;
    private ObserverManager observerManager;
    private static boolean debug = true;
    private TimerManager timerManager;

    private final Map<NameKey, AirDrop> airDropMap = new HashMap<>();

    @Override
    public void onLoad() {
        setInstance(this);
        message = new Message(getLogger());
        getDataFolder().mkdir();
    }

    @Override
    public void onEnable() {
        try {
            ConfigUtil.trySave("listeners/default.yml");
            ConfigUtil.trySave("config.yml");
            timerManager = new TimerManager();

            AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
            AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
            AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());

            observerManager = new ObserverManager();

            timerManager.load(new YamlConfig(new File(getDataFolder() + "/config.yml")));

            Bukkit.getScheduler().runTaskTimer(this, this::tick, 0, 1);

        } catch (Exception e) {
            Throwable t;
            if (e instanceof PluginInitializationException) {
                t = e;
            } else {
                t = new PluginInitializationException(e);
            }
            getLogger().log(Level.SEVERE, "failed to enable plugin!", t);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private long currentTick;
    private void tick() {
        timerManager.tick(currentTick);
        currentTick++;
    }

    @Override
    public void onDisable() {
        AdapterRegistry.unregisterAdapter(GeneratorSetting.class);
        AdapterRegistry.unregisterAdapter(Requirement.class);
        AdapterRegistry.unregisterAdapter(Observer.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        ListenersMenu listenersMenu = new ListenersMenu(airDropMap.get("default"), player, null);
        player.openInventory(listenersMenu.getMenu().getInventory());
        listenersMenu.generateMenu();
        return true;
    }

    public static Message getMessage() {
        return instance.message;
    }

    public static BAirDropX getInstance() {
        return instance;
    }

    public static void setInstance(BAirDropX instance) {
        Objects.requireNonNull(instance);
        if (BAirDropX.instance != null) {
            throw new UnsupportedOperationException();
        }
        BAirDropX.instance = instance;
    }

    public static ObserverManager getObserverManager() {
        return instance.observerManager;
    }

    public static void debug(Supplier<String> message) {
        if (debug) {
            getMessage().logger(message.get());
        }
    }

    @Nullable
    public static AirDrop getAirdropById(NameKey nameKey) {
        return instance.airDropMap.get(nameKey);
    }
}
