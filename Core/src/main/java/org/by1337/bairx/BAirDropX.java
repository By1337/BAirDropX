package org.by1337.bairx;

//import com.github.retrooper.packetevents.PacketEvents;
//import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.airdrop.loader.AirdropLoader;
import org.by1337.bairx.airdrop.loader.AirdropRegistry;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.exception.PluginInitializationException;
import org.by1337.bairx.inventory.MenuAddItem;
import org.by1337.bairx.inventory.MenuEditChance;
import org.by1337.bairx.listener.ClickListener;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.menu.ListenersMenu;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.timer.TimerManager;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.argument.ArgumentSetList;
import org.by1337.blib.command.argument.ArgumentValidCharacters;
import org.by1337.blib.command.requires.RequiresPermission;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;
    private ObserverManager observerManager;
    private static boolean debug = true;
    private TimerManager timerManager;
    private Command<CommandSender> command;
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
            initCommand();
            ConfigUtil.trySave("listeners/default.yml");
            ConfigUtil.trySave("config.yml");
            timerManager = new TimerManager();

            AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
            AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
            AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());

            observerManager = new ObserverManager();

            timerManager.load(new YamlConfig(new File(getDataFolder() + "/config.yml")));

            AirdropLoader.load();

            Bukkit.getScheduler().runTaskTimer(this, this::tick, 0, 1);

            Bukkit.getPluginManager().registerEvents(new ClickListener(), this);
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
        if (currentTick % 10 == 0) {
            airDropMap.values().stream().filter(AirDrop::isUseDefaultTimer).forEach(AirDrop::tick);
        }
        currentTick++;
    }

    @Override
    public void onDisable() {
        AdapterRegistry.unregisterAdapter(GeneratorSetting.class);
        AdapterRegistry.unregisterAdapter(Requirement.class);
        AdapterRegistry.unregisterAdapter(Observer.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, @NotNull String[] args) {
        try {
            command.process(sender, args);
        } catch (CommandException e) {
            message.sendMsg(sender, e.getMessage());
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String alias, @NotNull String[] args) {
        return command.getTabCompleter(sender, args);
    }

    private void initCommand() {
        command = new Command<>("bair");
        command.addSubCommand(
                new Command<CommandSender>("create")
                        .requires(new RequiresPermission<>("bair.create"))
                        .argument(new ArgumentSetList<>("type", () -> Arrays.stream(AirdropRegistry.values()).map(t -> t.getId().getName()).toList()))
                        .argument(new ArgumentValidCharacters<>("name", List.of("[name]")))
                        .executor(((sender, args) -> {
                            String type = (String) args.getOrThrow("type", "&c/bairx load <type> <id>");
                            NameKey nameKey = new NameKey((String) args.getOrThrow("name", "&c/bairx load <type> <id>"));

                            if (airDropMap.containsKey(nameKey)) {
                                throw new CommandException("&cАирдроп с именем %s уже существует!");
                            }

                            AirdropRegistry airdropRegistry = AirdropRegistry.byId(new NameKey(type));
                            var air = airdropRegistry.getCreator().create(nameKey, new File(getDataFolder(), "/airdrops/" + nameKey.getName()));

                            airDropMap.put(nameKey, air);
                        }))
        );
        command.addSubCommand(
                new Command<CommandSender>("edit")
                        .requires(new RequiresPermission<>("bair.edit"))
                        .addSubCommand(new Command<CommandSender>("loot")
                                .requires((sender -> sender instanceof Player))
                                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                                .executor(((sender, args) -> {
                                    Player player = (Player) sender;
                                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx edit loot <id>")));
                                    MenuAddItem menuAddItem = new MenuAddItem(airDrop.getInventoryManager(), airDrop, player);
                                    player.openInventory(menuAddItem.getInventory());
                                }))
                        )
                        .addSubCommand(new Command<CommandSender>("chance")
                                .requires((sender -> sender instanceof Player))
                                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                                .executor(((sender, args) -> {
                                    Player player = (Player) sender;
                                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx edit chance <id>")));
                                    MenuEditChance menuAddItem = new MenuEditChance(airDrop.getInventoryManager(), airDrop, player);
                                    player.openInventory(menuAddItem.getInventory());
                                }))
                        )
                        .addSubCommand(new Command<CommandSender>("listeners")
                                .requires((sender -> sender instanceof Player))
                                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                                .executor(((sender, args) -> {
                                    Player player = (Player) sender;
                                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx edit listeners <id>")));
                                    ListenersMenu listenersMenu = new ListenersMenu(airDrop, player, null);
                                    player.openInventory(listenersMenu.getInventory());
                                    listenersMenu.generateMenu();
                                }))
                        )
                        .executor(((sender, args) -> {

                        }))
        );
    }

    public static Message getMessage() {
        return instance.message;
    }

    public static BAirDropX getInstance() {
        return instance;
    }

    public static Map<NameKey, AirDrop> getAirDropMap() {
        return instance.airDropMap;
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
