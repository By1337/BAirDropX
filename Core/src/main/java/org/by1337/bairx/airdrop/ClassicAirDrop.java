package org.by1337.bairx.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventListenerManager;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.inventory.InventoryManager;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.location.generator.LocationManager;
import org.by1337.bairx.util.NumberUtil;
import org.by1337.blib.command.argument.ArgumentBoolean;
import org.by1337.blib.nbt.NBTParser;
import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.blib.nbt.impl.StringNBT;
import org.by1337.blib.io.ByteBuffer;
import org.by1337.bairx.random.RandomPlaceholders;
import org.by1337.bairx.summon.Summoner;
import org.by1337.bairx.util.Placeholder;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.argument.ArgumentIntegerAllowedMath;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.by1337.blib.world.BlockPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ClassicAirDrop extends Placeholder implements AirDrop, Summonable {
    public static final String TYPE = "classic";
    private NameKey id;
    private final File dataFolder;
    private World world;
    private GeneratorSetting generatorSetting;
    private CompoundTag compoundTag;
    private String airName;
    private int timeToStart;
    private int timeToOpen;
    private int timeToEnd;
    private int timeToStartConst;
    private int timeToOpenConst;
    private int timeToEndConst;
    private boolean useStaticLoc;
    private BlockPosition staticLoc;
    private boolean triggeredTimeToOpen;
    private boolean independentTimeToEnd;
    private Material materialWhenClosed;
    private Material materialWhenOpened;
    private Set<SpacedNameKey> signedListeners;
    private AirDropMetaData metaData;
    private boolean enable;
    private boolean started;
    private boolean opened;
    private boolean wasOpened;
    private boolean clicked;
    private Location location;
    private LocationManager locationManager;
    private boolean useDefaultTimer;
    private InventoryManager inventoryManager;
    private final Map<String, Effect> loadedEffects = new HashMap<>();
    private boolean lockSave;
    private boolean summoned;
    private String summonerName;
    private boolean remove;
    private Command<Event> command;
    private boolean unloaded;

    public static AirDrop createNew(NameKey id, File dataFolder) {
        try {
            return new ClassicAirDrop(id, dataFolder);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassicAirDrop load(File dataFolder, AirDropMetaData metaData) {
        try {
            return new ClassicAirDrop(dataFolder, metaData);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AirDrop createTempMirror(NameKey id) {
        try {
            ClassicAirDrop airDrop = new ClassicAirDrop(dataFolder, metaData);
            airDrop.id = id;
            airDrop.lockSave = true;
            airDrop.remove = true;
            return airDrop;
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Error while creating AirDrop mirror", e);
        }
    }

    private ClassicAirDrop(File dataFolder, AirDropMetaData metaData) throws IOException, InvalidConfigurationException {
        this.dataFolder = dataFolder;
        this.metaData = metaData;

        File cfgFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("config"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `config`").getValue()
        );
        File genSettingFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("genSettings"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `genSettings`").getValue()
        );
        File invItemsFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("invItems"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `invItems`").getValue()
        );

        File invManagerCfgFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("invManagerCfg"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `invManagerCfg`").getValue()
        );
        if (!cfgFile.exists()) {
            throw new IllegalArgumentException("Файл конфига отсутствует!");
        }
        if (!genSettingFile.exists()) {
            generatorSetting = new GeneratorSetting();
            generatorSetting.applyDefaultFlags();
            genSettingFile.createNewFile();
            YamlConfig config = new YamlConfig(genSettingFile);
            config.set("setting", generatorSetting);
            config.save();
        }

        if (!invManagerCfgFile.exists()) {
            inventoryManager = new InventoryManager(54, "&7AirDrop inventory");
            invManagerCfgFile.createNewFile();
            YamlConfig invCfg = new YamlConfig(invManagerCfgFile);
            CompoundTag compoundTag1 = new CompoundTag();
            inventoryManager.save(compoundTag1, invCfg);
            invCfg.save(invManagerCfgFile);
        }

        YamlConfig gen = new YamlConfig(genSettingFile);

        generatorSetting = gen.getAs("setting", GeneratorSetting.class);
        locationManager = new LocationManager(generatorSetting, this);


        YamlConfig invCfg = new YamlConfig(invManagerCfgFile);

        if (invItemsFile.exists()) {
            CompoundTag compoundTag1 = NBTParser.parseAsCompoundTag(Files.readString(invItemsFile.toPath()));
            inventoryManager = InventoryManager.load(compoundTag1, invCfg);
        } else {
            inventoryManager = InventoryManager.load(null, invCfg);
        }


        YamlConfig cfg = new YamlConfig(cfgFile);

        id = cfg.getAsNameKey("id");
        load(cfg);
        registerPlaceholders();
        initCommand();
        callEvent(null, EventType.LOAD);
    }

    private ClassicAirDrop(NameKey id, File dataFolder) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        } else if (!dataFolder.isDirectory()) {
            throw new IllegalArgumentException(dataFolder + " должен быть папкой!");
        }
        generatorSetting = new GeneratorSetting();
        generatorSetting.applyDefaultFlags();
        locationManager = new LocationManager(generatorSetting, this);
        setDefaultValues();
        registerPlaceholders();

        metaData = AirDropMetaData.createEmpty();
        metaData.setType(TYPE);
        metaData.getExtra().putString("config", "config.yml");
        metaData.getExtra().putString("genSettings", "generator_setting.yml");
        metaData.getExtra().putString("invItems", "items.snbt");
        metaData.getExtra().putString("invManagerCfg", "inventory_config.yml");
        metaData.setVersion(1);

        ByteBuffer buffer = new ByteBuffer();
        metaData.write(buffer);

        File metaData = new File(dataFolder, "desc.metadata");
        if (metaData.exists()) metaData.delete();
        metaData.createNewFile();
        Files.write(metaData.toPath(), buffer.toByteArray());
        save();
        initCommand();
        callEvent(null, EventType.LOAD);
    }

    private void load(YamlContext context) {
        airName = context.getAsString("air-name");
        world = Bukkit.getWorld(context.getAsString("world"));
        timeToStartConst = context.getAsInteger("time-to-start");
        timeToOpenConst = context.getAsInteger("time-to-open");
        timeToEndConst = context.getAsInteger("time-to-end");
        timeToEnd = timeToEndConst;
        timeToOpen = timeToOpenConst;
        timeToStart = timeToStartConst;
        useStaticLoc = context.getAsBoolean("use-static-loc");
        staticLoc = context.getAs("static-loc", BlockPosition.class);
        triggeredTimeToOpen = context.getAsBoolean("triggered-time-to-open");
        independentTimeToEnd = context.getAsBoolean("independent-time-to-end");
        materialWhenClosed = context.getAsMaterial("material-when-closed");
        materialWhenOpened = context.getAsMaterial("material-when-opened");
        enable = context.getAsBoolean("enable");
        signedListeners = new HashSet<>();
        for (String s : context.getList("signed-listeners", String.class)) {
            signedListeners.add(new SpacedNameKey(s));
        }
        useDefaultTimer = context.getAsBoolean("use-default-timer");
    }

    public void close() {
        callEvent(null, EventType.UNLOAD);
        inventoryManager.close();
        unloaded = true;
    }

    public void trySave() {
        try {
            save();
        } catch (IOException | InvalidConfigurationException e) {
            BAirDropX.getMessage().error("Не удалось сохранить аирдроп!", e);
        }
    }

    public void save() throws IOException, InvalidConfigurationException {
        if (lockSave) return;
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File genSetting = new File(dataFolder + "/generator_setting.yml");
        if (genSetting.exists()) genSetting.delete();
        genSetting.createNewFile();

        YamlConfig genSettingCfg = new YamlConfig(genSetting);
        genSettingCfg.set("setting", generatorSetting);
        genSettingCfg.save();

        File config = new File(dataFolder + "/config.yml");
        if (config.exists()) config.delete();
        config.createNewFile();

        YamlConfig cfg = new YamlConfig(config);
        cfg.set("air-name", airName);
        cfg.set("id", id);
        cfg.set("world", world.getName());
        cfg.set("time-to-start", timeToStartConst);
        cfg.set("time-to-open", timeToOpenConst);
        cfg.set("time-to-end", timeToEndConst);
        cfg.set("use-static-loc", useStaticLoc);
        cfg.set("static-loc", staticLoc);
        cfg.set("triggered-time-to-open", triggeredTimeToOpen); // AKA start-countdown-after-click
        cfg.set("independent-time-to-end", independentTimeToEnd); // AKA time-stop-event-must-go
        cfg.set("material-when-closed", materialWhenClosed);
        cfg.set("material-when-opened", materialWhenOpened);
        cfg.set("enable", enable);
        cfg.set("signed-listeners", signedListeners);
        cfg.set("use-default-timer", useDefaultTimer);
        cfg.save();
        File invItemsFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("invItems"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `invItems`").getValue()
        );
        if (invItemsFile.exists()) {
            invItemsFile.delete();
        }
        invItemsFile.createNewFile();
        File invManagerCfgFile = new File(dataFolder,
                Validate.tryMap(metaData.getExtra().get("invManagerCfg"), nbt -> (StringNBT) nbt, "В метаданных аирдропа отсутствует параметр `invManagerCfg`").getValue()
        );
        if (invManagerCfgFile.exists()) {
            invManagerCfgFile.delete();
        }
        invManagerCfgFile.createNewFile();
        YamlConfig invCfg = new YamlConfig(invManagerCfgFile);

        CompoundTag compoundTag1 = new CompoundTag();
        inventoryManager.save(compoundTag1, invCfg);

        Files.writeString(invItemsFile.toPath(), compoundTag1.toString());
        invCfg.save();
    }

    private void setDefaultValues() {
        inventoryManager = new InventoryManager(54, "&7AirDrop inventory");
        world = Bukkit.getWorld("world") == null ? Bukkit.getWorlds().get(0) : Bukkit.getWorld("world");
        airName = "&7AirDrop";
        timeToStart = 120;
        timeToOpen = 120;
        timeToEnd = 120;
        timeToStartConst = 120;
        timeToOpenConst = 120;
        timeToEndConst = 120;
        useStaticLoc = false;
        staticLoc = new BlockPosition();
        triggeredTimeToOpen = false;
        independentTimeToEnd = false;
        materialWhenClosed = Material.RESPAWN_ANCHOR;
        materialWhenOpened = Material.ENDER_CHEST;
        signedListeners = new HashSet<>();
        //signedListeners.add(new SpacedNameKey("default:test"));
        enable = true;
        started = false;
        opened = false;
        clicked = false;
        useDefaultTimer = true;

    }

    private BukkitTask forceStartTask;

    public void forceStart(CommandSender sender, @Nullable Location loc) {
        if (started) {
            BAirDropX.getMessage().sendMsg(sender, "&cАирдоп уже запущен!");
            return;
        }
        if (forceStartTask != null) {
            forceStartTask.cancel();
            BAirDropX.getMessage().sendMsg(sender, "&cПринудительный запуск отменён!");
            return;
        }
        if (loc != null) {
            location = loc;
        }
        forceStartTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (location == null) {
                    generateLocation0();
                } else {
                    start();
                    cancel();
                    forceStartTask = null;
                }
            }
        }.runTaskTimer(BAirDropX.getInstance(), 0, 1);
    }

    public void forceStop() {
        if (!started) return;
        end();
    }



    public void tick() {
        if (!enable) return;

        if (timeToStart < 600) {
            generateLocation();
        }

        if (!started) {
            timeToStart--;
            if (timeToStart <= 0) {
                timeToStart = 0;
                start();
            }
        } else if (!opened && (!triggeredTimeToOpen || clicked)) {
            timeToOpen--;
            if (timeToOpen <= 0) {
                timeToOpen = 0;
                unlock();
            }
        }
        if (started && (timeToOpen == 0 || independentTimeToEnd)) {
            timeToEnd--;
            if (timeToEnd <= 0) {
                end();
            }
        }
        callEvent(null, EventType.TICK);
    }

    private void start() {
        if (location == null) {
            BAirDropX.getMessage().warning("%s не успел найти локацию для спавна!", id.getName());
            do {
                generateLocation0();
            } while (location == null);
        }
        timeToStart = 0;
        started = true;
        location.getBlock().setType(materialWhenClosed);
        callEvent(null, EventType.START);
    }

    private void unlock() {
        timeToOpen = 0;
        opened = true;
        location.getBlock().setType(materialWhenOpened);
        callEvent(null, EventType.OPEN);
    }

    private void end() {
        callEvent(null, EventType.END);
        started = false;
        opened = false;
        timeToStart = timeToStartConst;
        timeToOpen = timeToOpenConst;
        timeToEnd = timeToEndConst;
        location.getBlock().setType(Material.AIR);
        location = null;
        summoned = false;
        clicked = false;
        wasOpened = false;
        summonerName = null;
        if (remove) {
            BAirDropX.unregisterAirDrop(id);
        }
    }

    private BukkitTask generateTask;

    private void generateLocation0() {
        if (location != null) return;
        if (useStaticLoc) {
            location = new Location(world, staticLoc.getX(), staticLoc.getY(), staticLoc.getZ());
            return;
        }
        location = locationManager.generate();

    }

    private void generateLocation() {
        if (generateTask == null) {
            generateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    generateLocation0();
                    generateTask = null;
                }
            }.runTaskAsynchronously(BAirDropX.getInstance());
        }
    }

    @Override
    public void callEvent(@NotNull Event event) {
        if (event.getEventType() == EventType.CLICK) {
            if (opened) {
                if (!wasOpened) {
                    wasOpened = true;
                    callEvent(event.getAs(this, event.getPlayer(), EventType.FIRST_OPEN));
                } else {
                    callEvent(event.getAs(this, event.getPlayer(), EventType.CLICK_OPEN));
                }
            } else if (clicked || !triggeredTimeToOpen) {
                callEvent(event.getAs(this, event.getPlayer(), EventType.CLICK_CLOSE));
            }
            if (!clicked) {
                clicked = true;
                callEvent(event.getAs(this, event.getPlayer(), EventType.ACTIVATE));
            }
        }
        EventListenerManager.call(event, this);
        for (SpacedNameKey listener : signedListeners) {
            BAirDropX.getObserverManager().invoke(listener, event);
        }
    }

    @Override
    public void callEvent(@Nullable Player player, EventType eventType) {
        callEvent(new Event(this, player, eventType));
    }

    public World getWorld() {
        return world;
    }

    @Override
    public String toString() {
        return "ClassicAirDrop{" +
                "timeToStart=" + timeToStart +
                ", timeToOpen=" + timeToOpen +
                ", timeToEnd=" + timeToEnd +
                ", enable=" + enable +
                ", started=" + started +
                ", opened=" + opened +
                ", location=" + location +
                '}';
    }

    private void registerPlaceholders() {
        registerPlaceholders(RandomPlaceholders.INSTANCE);
        registerPlaceholder("{world}", world::getName);
        registerPlaceholder("{air_name}", () -> airName);
        registerPlaceholder("{time_to_start}", () -> timeToStart);
        registerPlaceholder("{time_to_start_format}", () -> NumberUtil.format(timeToStart));
        registerPlaceholder("{time_to_open}", () -> timeToOpen);
        registerPlaceholder("{time_to_open_format}", () -> NumberUtil.format(timeToOpen));
        registerPlaceholder("{time_to_end}", () -> timeToEnd);
        registerPlaceholder("{time_to_end_format}", () -> NumberUtil.format(timeToEnd));
        registerPlaceholder("{time_to_start_const}", () -> timeToStartConst);
        registerPlaceholder("{time_to_open_const}", () -> timeToOpenConst);
        registerPlaceholder("{time_to_end_const}", () -> timeToEndConst);
        registerPlaceholder("{use_static_loc}", () -> useStaticLoc);
        registerPlaceholder("{triggered_time_to_open}", () -> triggeredTimeToOpen);
        registerPlaceholder("{independent_time_to_end}", () -> independentTimeToEnd);
        registerPlaceholder("{material_when_closed}", materialWhenClosed::name);
        registerPlaceholder("{material_when_opened}", materialWhenOpened::name);
        registerPlaceholder("{enable}", () -> enable);
        registerPlaceholder("{started}", () -> started);
        registerPlaceholder("{opened}", () -> opened);
        registerPlaceholder("{clicked}", () -> clicked);
        registerPlaceholder("{airdrop_type}", () -> TYPE);
        registerPlaceholder("{id}", id::getName);
        registerPlaceholder("{summoned}", () -> summoned);
        registerPlaceholder("{summoner_name}", () -> summonerName);
        registerPlaceholder("{remove}", () -> remove);
        registerPlaceholder("{was_opened}", () -> wasOpened);
        registerPlaceholder("{minecraft_world}", () -> world.getName().equals("world") ? "overworld" : world.getName());
        registerPlaceholder("{current_world}", () -> location == null ? "none" : location.getWorld().getName());
        registerPlaceholder("{minecraft_current_world}", () -> location == null ? "none" : location.getWorld().getName().equals("world") ? "overworld" : world.getName());

        registerPlaceholder("{x}", () -> location == null ? "?" : location.getBlockX());
        registerPlaceholder("{y}", () -> location == null ? "?" : location.getBlockY());
        registerPlaceholder("{z}", () -> location == null ? "?" : location.getBlockZ());
    }

    @Override
    public void addEffectAndStart(String id, Effect effect) {
        if (loadedEffects.containsKey(id)) {
            throw new IllegalStateException("Эффект " + id + " уже загружен!");
        }
        effect.start(location);
        loadedEffects.put(id, effect);
    }

    @Override
    public void stopEffect(String id) {
        var ef = loadedEffects.remove(id);
        if (ef != null) {
            ef.stop();
        } else {
            throw new IllegalStateException("Эффект " + id + " не был загружен!");
        }
    }

    @Override
    public void stopAllEffects() {
        loadedEffects.values().forEach(Effect::stop);
        loadedEffects.clear();
    }

    @Override
    public Set<SpacedNameKey> getSignedListeners() {
        return signedListeners;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public NameKey getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isUseDefaultTimer() {
        return useDefaultTimer;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public GeneratorSetting getGeneratorSetting() {
        return generatorSetting;
    }

    @Override
    public Summoner.Result canBeSummoned(@NotNull Player player, @NotNull Summoner summoner) {
        if (!started || summoner.isSpawnMirror()) {
            return new Summoner.Result(Summoner.ResultStatus.SUCCESSFULLY, null);
        } else {
            return new Summoner.Result(Summoner.ResultStatus.FAILED, "&cВы не можете призвать аирдроп так как он уже запущен!");
        }
    }

    @Override
    public void summon(@NotNull Player player, @Nullable Location location, @NotNull Summoner summoner) {
        if (started) {
            throw new IllegalStateException("Аирдроп не может быть вызван так как он уже запущен!");
        }
        summoned = true;
        summonerName = player.getName();
        if (summoner.isUsePlayerLoc())
            this.location = location;
    }

    @Override
    public void executeCustomCommand(Event event, String cmd) {
        try {
            command.process(event, cmd.split(" "));
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> tabCompleter(Event event, String cmd) {
        return command.getTabCompleter(event, cmd.split(" "));
    }

    @Override
    public boolean hasCommand(String command) {
        return this.command.getSubcommands().containsKey(command);// todo aliases?
    }

    private void initCommand() {
        command = new Command<>("air");
        command.addSubCommand(new Command<Event>("[SET_TIME_TO_START]")
                .argument(new ArgumentIntegerAllowedMath<>("time"))
                .executor((pair, args) -> {
                    timeToStart = (int) args.getOrThrow("time", "[SET_TIME_TO_START] <time>");
                })
        );
        command.addSubCommand(new Command<Event>("[SET_TIME_TO_OPEN]")
                .argument(new ArgumentIntegerAllowedMath<>("time"))
                .executor((pair, args) -> {
                    timeToOpen = (int) args.getOrThrow("time", "[SET_TIME_TO_OPEN] <time>");
                })
        );
        command.addSubCommand(new Command<Event>("[SET_TIME_TO_END]")
                .argument(new ArgumentIntegerAllowedMath<>("time"))
                .executor((pair, args) -> {
                    timeToEnd = (int) args.getOrThrow("time", "[SET_TIME_TO_END] <time>");
                })
        );
        command.addSubCommand(new Command<Event>("[START]")
                .executor((pair, args) -> {
                    if (started) {
                        throw new IllegalArgumentException("Аирдроп уже запущен!");
                    }
                    start();
                })
        );
        command.addSubCommand(new Command<Event>("[UNLOCK]")
                .executor((pair, args) -> {
                    if (!started) {
                        throw new IllegalArgumentException("Аирдроп не запущен!");
                    }
                    unlock();
                })
        );
        command.addSubCommand(new Command<Event>("[SET_CLICKED]")
                .argument(new ArgumentBoolean<>("flag"))
                .executor((pair, args) -> {
                    clicked = (boolean) args.getOrDefault("flag", !clicked);
                })
        );
        command.addSubCommand(new Command<Event>("[STOP]")
                .executor((pair, args) -> {
                    if (!started) {
                        throw new IllegalArgumentException("Аирдроп не запущен!");
                    }
                    end();
                })
        );
        command.addSubCommand(new Command<Event>("[SET_LOC_X]")
                .argument(new ArgumentIntegerAllowedMath<>("x"))
                .executor((pair, args) -> {
                    int x = (int) args.getOrThrow("x", "[SET_LOC_X] <x>");
                    if (location == null) {
                        location = new Location(world, 0, 0, 0);
                    }
                    location.setX(x);
                })
        );
        command.addSubCommand(new Command<Event>("[SET_LOC_Y]")
                .argument(new ArgumentIntegerAllowedMath<>("y"))
                .executor((pair, args) -> {
                    int y = (int) args.getOrThrow("y", "[SET_LOC_Y] <y>");
                    if (location == null) {
                        location = new Location(world, 0, 0, 0);
                    }
                    location.setY(y);
                })
        );
        command.addSubCommand(new Command<Event>("[SET_LOC_Z]")
                .argument(new ArgumentIntegerAllowedMath<>("z"))
                .executor((pair, args) -> {
                    int z = (int) args.getOrThrow("z", "[SET_LOC_Z] <z>");
                    if (location == null) {
                        location = new Location(world, 0, 0, 0);
                    }
                    location.setZ(z);
                })
        );
        command.addSubCommand(new Command<Event>("[SET_ENABLE]")
                .argument(new ArgumentBoolean<>("flag"))
                .executor((pair, args) -> {
                    enable = (boolean) args.getOrDefault("flag", !enable);
                })
        );

    }

    @Override
    public boolean isSummoned() {
        return summoned;
    }

    @Override
    @Nullable
    public String getSummonerName() {
        return summonerName;
    }

    public boolean isUnloaded() {
        return unloaded;
    }

    @Override
    public void openEditMenu(Player player) {

    }

    @Override
    public int getToSpawn() {
        return timeToStart;
    }
}
