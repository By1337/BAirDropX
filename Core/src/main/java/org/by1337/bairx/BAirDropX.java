package org.by1337.bairx;

/*import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.npc.NPC;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;*/
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.loader.AirdropLoader;
import org.by1337.bairx.airdrop.loader.AirdropRegistry;
import org.by1337.bairx.config.adapter.AdapterGeneratorSetting;
import org.by1337.bairx.config.adapter.AdapterObserver;
import org.by1337.bairx.config.adapter.AdapterRequirement;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.effect.EffectCreator;
import org.by1337.bairx.effect.EffectLoader;
//import org.by1337.bairx.entity.metadata.MetaDataProviderArmorStand;
import org.by1337.bairx.exception.PluginInitializationException;
import org.by1337.bairx.hologram.HologramLoader;
import org.by1337.bairx.hologram.HologramManager;
import org.by1337.bairx.inventory.MenuAddItem;
import org.by1337.bairx.inventory.MenuEditChance;
import org.by1337.bairx.listener.ClickListener;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.menu.ListenersMenu;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.ObserverManager;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.schematics.SchematicsLoader;
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
/*        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);
        PacketEvents.getAPI().load();*/
    }

    @Override
    public void onEnable() {
        try {
           // PacketEvents.getAPI().getEventManager().registerListener(new PacketEventsPacketListener());
//            PacketEvents.getAPI().init();

            initCommand();
            ConfigUtil.trySave("listeners/default.yml");
            ConfigUtil.trySave("config.yml");
            timerManager = new TimerManager();

            AdapterRegistry.registerAdapter(GeneratorSetting.class, new AdapterGeneratorSetting());
            AdapterRegistry.registerAdapter(Requirement.class, new AdapterRequirement());
            AdapterRegistry.registerAdapter(Observer.class, new AdapterObserver());

            observerManager = new ObserverManager();
            EffectLoader.load();
            SchematicsLoader.load();
            HologramLoader.load();
            HologramManager.INSTANCE.registerCommands();

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

        for (AirDrop value : airDropMap.values()) {
            value.forceStop();
        }
      //  PacketEvents.getAPI().terminate();
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
        command.addSubCommand(new Command<CommandSender>("start")
                .requires(new RequiresPermission<>("bair.start"))
                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                .executor(((sender, args) -> {
                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx edit loot <id>")));
                    airDrop.forceStart(sender);
                }))
        );
        command.addSubCommand(new Command<CommandSender>("stop")
                .requires(new RequiresPermission<>("bair.stop"))
                .argument(new ArgumentSetList<>("air", () -> airDropMap.values().stream().map(air -> air.getId().getName()).toList()))
                .executor(((sender, args) -> {
                    AirDrop airDrop = airDropMap.get(new NameKey((String) args.getOrThrow("air", "&c/bairx edit loot <id>")));
                    airDrop.forceStop();
                }))
        );
        command.addSubCommand(new Command<CommandSender>("effect")
                .requires(new RequiresPermission<>("bair.effect"))
                .addSubCommand(new Command<CommandSender>("start")
                        .requires(new RequiresPermission<>("bair.effect.start"))
                        .requires((sender -> sender instanceof Player))
                        .argument(new ArgumentSetList<>("effect", () -> EffectLoader.keys().stream().toList()))
                        .executor(((sender, args) -> {
                            String effectS = (String) args.getOrThrow("effect", "Укажите effect");
                            EffectCreator creator = EffectLoader.getByName(effectS);
                            Effect effect = creator.create();
                            effect.start(((Player) sender).getLocation());
                        }))
                )
        );
        command.addSubCommand(new Command<CommandSender>("test")
                .executor(((sender, args) -> {
                    Player player = (Player) sender;
/*
                    WrapperPlayServerSpawnLivingEntity spawn = new WrapperPlayServerSpawnLivingEntity(
                            1337,
                            UUID.randomUUID(),
                            EntityTypes.ARMOR_STAND,
                            new Vector3d(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()),
                            0,
                            0,
                            0,
                            new Vector3d(0, 0, 0),
                            new ArrayList<>() // meta data
                    );

                    var channel = PacketEvents.getAPI().getPlayerManager().getChannel(player);
                    PacketEvents.getAPI().getProtocolManager().sendPacket(channel, spawn);

                    MetaDataProviderArmorStand metaData = new MetaDataProviderArmorStand();
                    metaData.init();

                    MiniMessage miniMessage = MiniMessage.miniMessage();

                    metaData.setCustomName(miniMessage.deserialize("&cCustom ArmorStand"));
                    metaData.setGlowing(true);
                    metaData.setCustomNameVisible(true);
                    metaData.setSmall(true);
                    metaData.setInvisible(true);
                    metaData.setNoBasePlate(true);
                    metaData.setSilent(true);
                    metaData.setNoGravity(true);
                    metaData.setMarker(true);

                    var list = metaData.getEntityData().build();
                    for (EntityData data : list) {
                        System.out.println(
                                "index = '" + data.getIndex() + "' " +
                                        "type = '" + data.getType().getName() + "' " +
                                        "value = '" + data.getValue() + "'"
                        );
                    }
                    WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
                            1337,
                            list
                    );

                    PacketEvents.getAPI().getProtocolManager().sendPacket(channel, metadataPacket);

                    // npc.spawn(PacketEvents.getAPI().getPlayerManager().getChannel(sender));*/
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
            getMessage().debug(message.get());
        }
    }

    @Nullable
    public static AirDrop getAirdropById(NameKey nameKey) {
        return instance.airDropMap.get(nameKey);
    }
}
