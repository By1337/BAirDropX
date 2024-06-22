package org.by1337.bairx;

//import net.kyori.adventure.text.minimessage.MiniMessage;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
//import net.kyori.adventure.text.BlockNBTComponent;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.event.ClickEvent;
//import net.kyori.adventure.text.event.HoverEvent;
//import net.kyori.adventure.text.format.Style;
//import net.kyori.adventure.text.format.TextColor;
//import net.kyori.adventure.text.format.TextDecoration;
//import net.kyori.adventure.text.minimessage.MiniMessage;
//import net.kyori.adventure.text.serializer.ComponentSerializer;
//import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
//import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
//import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.bairx.addon.*;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.loader.AirdropLoader;
import org.by1337.bairx.airdrop.loader.AirdropRegistry;
import org.by1337.bairx.command.bair.EffectCommand;
import org.by1337.bairx.command.bair.ExecuteCommand;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.effect.EffectLoader;
import org.by1337.bairx.event.EventListenerManager;
import org.by1337.bairx.hologram.HologramLoader;
import org.by1337.bairx.hologram.HologramManager;
import org.by1337.bairx.hook.metric.Metrics;
import org.by1337.bairx.hook.papi.PapiHook;
import org.by1337.bairx.inventory.MenuAddItem;
import org.by1337.bairx.inventory.MenuEditChance;
import org.by1337.bairx.listener.ClickListener;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.menu.ListenersMenu;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.schematics.SchematicsLoader;
import org.by1337.bairx.summon.SummonerManager;
import org.by1337.bairx.timer.TimerManager;
import org.by1337.bairx.util.ConfigUtil;
import org.by1337.bairx.util.FileUtil;
import org.by1337.bairx.util.VersionChecker;
import org.by1337.bairx.util.plugin.PluginEnablePipeline;
import org.by1337.blib.chat.util.Message;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.argument.*;
import org.by1337.blib.command.requires.RequiresPermission;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.adapter.AdapterRegistry;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class BAirDropX extends JavaPlugin {

    private static BAirDropX instance;
    private Message message;
    private ObserverManager observerManager;
    private static boolean debug = true;
    private TimerManager timerManager;
    private Command<CommandSender> command;
    private final Map<NameKey, AirDrop> airDropMap = new HashMap<>();
    private AddonLoader addonLoader;
    private YamlConfig cfg;
    private PapiHook papiHook;
    private PluginEnablePipeline enablePipeline;

    @Override
    public void onLoad() {
        setInstance(this);
        try (FileReader reader = new FileReader(ConfigUtil.trySave("translation.json"), StandardCharsets.UTF_8)) {
            message = new Message(getLogger(), reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getDataFolder().mkdir();
        File addons = new File(getDataFolder(), "addons");
        if (!addons.exists()) {
            addons.mkdir();
        }
        addonLoader = new AddonLoader(new AddonLogger("Addons", this.getClass(), getLogger()), addons);
        enablePipeline = new PluginEnablePipeline(this);
        initPipeline();
    }

    @Override
    public void onEnable() {
        enablePipeline.onEnable();
    }

    @Override
    public void onDisable() {
        enablePipeline.onDisable();
    }

    private void initPipeline() {
        enablePipeline
                .enable("register adapters", () -> {
                    AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
                    AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
                    AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());
                }).enable("load addons", () -> {
                    addonLoader.loadAll();
                }).enable("init commands", this::initCommand)
                .enable("cfg save", () -> {
                    ConfigUtil.trySave("listeners/default.yml");
                    ConfigUtil.trySave("config.yml");
                }).enable("create timer manager", () -> {
                    timerManager = new TimerManager();
                }).enable("load observers", () -> {
                    observerManager = new ObserverManager();
                    observerManager.load();
                }).enable("load effects", () -> {
                    EffectLoader.load();
                }).enable("load schematics", () -> {
                    SchematicsLoader.load();
                }).enable("load holograms", () -> {
                    HologramLoader.load();
                }).enable("holograms register commands", () -> {
                    HologramManager.INSTANCE.registerCommands();
                }).enable("load config", () -> {
                    cfg = new YamlConfig(new File(getDataFolder() + "/config.yml"));
                }).enable("enable summoner manager", () -> {
                    SummonerManager.load();
                    SummonerManager.registerBAirCommands();
                }).enable("load timer manager", () -> {
                    timerManager.load(cfg);
                }).enable("load airdrops", () -> {
                    AirdropLoader.load();
                }).enable("enable addons", () -> {
                    addonLoader.enableAll();
                }).enable("start main tick timer", () -> {
                    Bukkit.getScheduler().runTaskTimer(this, this::tick, 0, 1);
                }).enable("register listeners", () -> {
                    Bukkit.getPluginManager().registerEvents(new ClickListener(), this);
                }).enable("init papi hook", () -> {
                    papiHook = new PapiHook();
                    papiHook.register();
                }).enable("Metrics & version checker", () -> {
                    new Metrics(this, 21314);
                    new VersionChecker();
                })
                .disable("unregister", () -> {
                    Bukkit.getScheduler().cancelTasks(this);
                    HandlerList.unregisterAll(this);
                    EventListenerManager.close();
                }).disable("unregister papi hook", p -> p.isEnabled("init papi hook"), () -> {
                    papiHook.unregister();
                }).disable("disable addons", p -> p.isEnabled("enable addons"), () -> {
                    addonLoader.disableAll();
                    addonLoader.unloadAll();
                }).disable("unload airdrops", p -> p.isEnabled("load airdrops"), () -> {
                    for (AirDrop value : airDropMap.values()) {
                        value.forceStop();
                        value.close();
                    }
                    airDropMap.clear();
                }).disable("unload effects", p -> p.isEnabled("load effects"), () -> {
                    EffectLoader.close();
                }).disable("unload schematics", p -> p.isEnabled("load schematics"), () -> {
                    SchematicsLoader.close();
                }).disable("unload holograms", p -> p.isEnabled("load holograms"), () -> {
                    HologramLoader.close();
                }).disable("unregister adapters", () -> {
                    AdapterRegistry.unregisterAdapter(GeneratorSetting.class);
                    AdapterRegistry.unregisterAdapter(Requirement.class);
                    AdapterRegistry.unregisterAdapter(Observer.class);
                });
    }

    private long currentTick;

    private void tick() {
        timerManager.tick(currentTick);
        if (currentTick % 20 == 0) {
            for (AirDrop airDrop : airDropMap.values().toArray(new AirDrop[0])) {
                try {
                    if (airDrop.isUseDefaultTimer()) {
                        airDrop.tick();
                    }
                } catch (Throwable t) {
                    message.error("failed to tick airdrop %s", t, airDrop.getId());
                }
            }
        }
        currentTick++;
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
        command.addSubCommand(new Command<CommandSender>("unsafe")
                .requires(new RequiresPermission<>("bair.unsafe"))
                .addSubCommand(
                        new Command<CommandSender>("reload")
                                .requires(new RequiresPermission<>("bair.reload"))
                                .executor(((sender, args) -> {
                                    long x = System.nanoTime();
                                    enablePipeline.reload();
                                    message.sendMsg(sender, "&fReloaded in %s ms.", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - x));
                                }))
                )
        );
        command.addSubCommand(
                new Command<CommandSender>("create")
                        .requires(new RequiresPermission<>("bair.create"))
                        .argument(new ArgumentSetList<>("type", () -> Arrays.stream(AirdropRegistry.values()).map(t -> t.getId().getName()).toList()))
                        .argument(new ArgumentValidCharacters<>("name", List.of("[name]")))
                        .executor(((sender, args) -> {
                            String type = (String) args.getOrThrow("type", "&c/bairx create <type> <id>");
                            NameKey nameKey = new NameKey((String) args.getOrThrow("name", "&c/bairx load <type> <id>"));

                            if (airDropMap.containsKey(nameKey)) {
                                throw new CommandException(String.format(message.getTranslation().translate("command.create.air.already.exist"), nameKey.getName()));
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
        command.addSubCommand(new Command<CommandSender>("start")
                .requires(new RequiresPermission<>("bair.start"))
                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                .executor(((sender, args) -> {
                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx start <id>")));
                    airDrop.forceStart(sender, null);
                }))
                .addSubCommand(new Command<CommandSender>("at")
                        .requires(new RequiresPermission<>("bair.start.at"))
                        .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                        .argument(new ArgumentPosition<>("x", List.of("~", "~", "~"), ArgumentPosition.ArgumentPositionType.X))
                        .argument(new ArgumentPosition<>("y", List.of("~", "~"), ArgumentPosition.ArgumentPositionType.Y))
                        .argument(new ArgumentPosition<>("z", List.of("~"), ArgumentPosition.ArgumentPositionType.Z))
                        .argument(new ArgumentWorld<>("world"))
                        .executor(((sender, args) -> {
                            AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx start at <id> <x> <y> <z> <?world>")));
                            double x = ((Double) args.getOrThrow("x", "&c/bairx start at <id> <x> <y> <z> <?world>"));
                            double y = ((Double) args.getOrThrow("y", "&c/bairx start at <id> <x> <y> <z> <?world>"));
                            double z = ((Double) args.getOrThrow("z", "&c/bairx start at <id> <x> <y> <z> <?world>"));
                            World world = (World) args.getOrDefault("world", airDrop.getWorld());
                            airDrop.forceStart(sender, new Location(world, x, y, z).getBlock().getLocation());
                        }))
                )
        );
        command.addSubCommand(new Command<CommandSender>("stop")
                .requires(new RequiresPermission<>("bair.stop"))
                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                .executor(((sender, args) -> {
                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx stop <id>")));
                    airDrop.forceStop();
                }))
        );
        command.addSubCommand(new ExecuteCommand());

        command.addSubCommand(new Command<CommandSender>("tp")
                .requires(new RequiresPermission<>("bair.tp"))
                .requires(sender -> sender instanceof Player)
                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                .executor(((sender, args) -> {
                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx tp <id>")));
                    if (airDrop.getLocation() == null) {
                        message.sendMsg(sender, Component.translatable("command.tp.air.loc.null"));
                        return;
                    }
                    Player player = (Player) sender;
                    if (airDrop.isStarted()) {
                        player.teleport(airDrop.getLocation());
                    } else {
                        player.teleport(airDrop.getLocation());
                        message.sendMsg(sender, Component.translatable("command.tp.air.is.not.spawned"));
                    }
                }))
        );

        command.addSubCommand(new EffectCommand());
        command.addSubCommand(new Command<CommandSender>("addons")
                .requires(new RequiresPermission<>("bair.addons"))
                .addSubCommand(new Command<CommandSender>("list")
                        .requires(new RequiresPermission<>("bair.addons.list"))
                        .executor(((sender, args) -> {
                            message.sendMsg(sender, addonLoader.getAddonList());
                        }))
                )
                .addSubCommand(new Command<CommandSender>("unsafe")
                        .requires(new RequiresPermission<>("bair.addons.unsafe"))
                        .addSubCommand(new Command<CommandSender>("unload")
                                .argument(new ArgumentSetList<>("addon", () -> addonLoader.getAddons().stream().map(JavaAddon::getName).toList()))
                                .executor(((sender, args) -> {
                                    String addon = (String) args.getOrThrow("addon", "&c/addons unload <addon>");
                                    addonLoader.disable(addon);
                                    addonLoader.unload(addon);
                                    message.sendMsg(sender, "&aDone");
                                }))
                        )
                        .addSubCommand(new Command<CommandSender>("load")
                                .requires(new RequiresPermission<>("bair.addons.load"))
                                .argument(new ArgumentStrings<>("file", () -> {
                                    List<File> files = FileUtil.findFiles(addonLoader.getDir(), f -> f.getName().endsWith(".jar"));
                                    List<String> names = new ArrayList<>();
                                    for (File file : files) {
                                        try {
                                            AddonDescriptionFile descriptionFile = new AddonDescriptionFile(AddonLoader.readFileContentFromJar(file.getPath()));
                                            if (addonLoader.getAddon(descriptionFile.getName()) == null) {
                                                names.add(file.getName());
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                    return names;
                                }))
                                .executor(((sender, args) -> {
                                    String file = (String) args.getOrThrow("file", "&c/addons load <file>");
                                    File file1 = new File(addonLoader.getDir() + "/" + file + ".jar");
                                    if (!file1.exists()) {
                                        message.sendMsg(sender, Component.translatable("command.addon.load.file.does.not.exist"));
                                        return;
                                    }
                                    try {
                                        addonLoader.loadAddon(file1);
                                    } catch (IOException | InvalidAddonException e) {
                                        message.error(e);
                                        message.sendMsg(sender, e.getLocalizedMessage());
                                    }
                                }))
                        )
                )
        );
    }

    public static String translate(String key) {
        return getMessage().getTranslation().translate(key);
    }

    public static void registerBAirCommand(Command<CommandSender> command) {
        instance.command.addSubCommand(command);
    }

    public static Message getMessage() {
        return instance.message;
    }

    public static BAirDropX getInstance() {
        return instance;
    }

    public static Collection<AirDrop> airdrops() {
        return instance.airDropMap.values();
    }

    public static Set<NameKey> allIdAirdrops() {
        return instance.airDropMap.keySet();
    }

    public static void registerAirDrop(@NotNull AirDrop airDrop) {
        if (instance.airDropMap.containsKey(airDrop.getId())) {
            throw new IllegalStateException(String.format(getMessage().getTranslation().translate("register.airdrop.air.already.exist"), airDrop.getId()));
        }
        instance.airDropMap.put(airDrop.getId(), airDrop);
    }

    public static YamlConfig getCfg() {
        return instance.cfg;
    }

    @Nullable
    @CanIgnoreReturnValue
    public static AirDrop unregisterAirDrop(@NotNull NameKey airDrop) {
        var air = instance.airDropMap.remove(airDrop);
        if (air != null && !air.isUnloaded()) air.close();
        return air;
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

    public static void debug(String s, Object... objects) {
        debug(() -> String.format(s, objects));
    }
    public static void debug(Supplier<String> message) {
        if (debug) {
            getMessage().debug(message.get());
        }
    }

    public static PapiHook getPapiHook() {
        return instance.papiHook;
    }

    @Nullable
    public static AirDrop getAirdropById(NameKey nameKey) {
        return instance.airDropMap.get(nameKey);
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public AddonLoader getAddonLoader() {
        return addonLoader;
    }

    public long getCurrentTick() {
        return currentTick;
    }
    public static Collection<AirDrop> getAirDrops(){
        return instance.airDropMap.values();
    }
}
