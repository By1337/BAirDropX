package org.by1337.bairx.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.location.generator.LocationManager;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.serialize.ByteBuffer;
import org.by1337.bairx.util.Placeholder;
import org.by1337.blib.configuration.YamlConfig;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.world.BLocation;
import org.by1337.blib.world.BlockPosition;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AirDrop extends Placeholder {
    private final NameKey id;
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
    private int requiredNumberOfPlayersOnline;
    private List<String> signedListeners;
    private AirDropMetaData metaData;
    private boolean enable;
    private boolean started;
    private boolean opened;
    private boolean clicked;
    private Location location;
    private LocationManager locationManager;

    public static AirDrop createNew(NameKey id, File dataFolder) {
        try {
            return new AirDrop(id, dataFolder);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private AirDrop(NameKey id, File dataFolder) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.dataFolder = dataFolder;
        generatorSetting = new GeneratorSetting();
        generatorSetting.applyDefaultFlags();

        locationManager = new LocationManager(generatorSetting, this);

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

        setDefaultValues();
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

        cfg.set("required-number-of-players-online", requiredNumberOfPlayersOnline);
        cfg.set("enable", enable);

        cfg.set("signed-listeners", signedListeners);

        cfg.save();

        metaData = AirDropMetaData.createEmpty();

        metaData.setType("classic");
        metaData.getExtra().putString("config", "config.yml");
        metaData.getExtra().putString("genSettings", "generator_setting.yml");
        metaData.setVersion(1);

        ByteBuffer buffer = new ByteBuffer();
        metaData.write(buffer);


        File metaData = new File(dataFolder + "/desc.metadata");
        if (metaData.exists()) metaData.delete();
        metaData.createNewFile();

        Files.write(metaData.toPath(), buffer.toByteArray());
    }


    private void setDefaultValues() {
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
        requiredNumberOfPlayersOnline = 1;
        signedListeners = new ArrayList<>();
        enable = true;
        started = false;
        opened = false;
        clicked = false;
    }

    public void tick() {
        if (!enable) return;

        if (timeToStart < 600) {
            generateLocation();
        }

        if (!started) {
            if (Bukkit.getOnlinePlayers().size() < requiredNumberOfPlayersOnline) return;
            timeToStart--;
            if (timeToStart == 0) {
                started = true;
                location.getBlock().setType(materialWhenClosed);
                callEvent(null, EventType.START);
            }
        } else if (!opened && (!triggeredTimeToOpen || clicked)) {
            timeToOpen--;
            if (timeToOpen == 0) {
                opened = true;
                location.getBlock().setType(materialWhenOpened);
                callEvent(null, EventType.OPEN);
            }
        }
        if (timeToOpen == 0 || independentTimeToEnd) {
            timeToEnd--;
            if (timeToEnd == 0) {
                started = false;
                opened = false;
                timeToStart = timeToStartConst;
                timeToOpen = timeToOpenConst;
                timeToEnd = timeToEndConst;
                location.getBlock().setType(Material.AIR);
                location = null;
                callEvent(null, EventType.END);
            }
        }
        callEvent(null, EventType.TICK);
    }

    private void generateLocation(){
        if (location != null) return;
        if (useStaticLoc){
            location = new Location(world, staticLoc.getX(), staticLoc.getY(), staticLoc.getZ());
        }else {
            location = locationManager.generate();
        }
    }
    public void callEvent(@Nullable Player player, EventType eventType) {
        Event event = new Event(this, player, eventType);

        for (String listener : signedListeners) {
            BAirDropX.getObserverManager().invoke(listener, event);
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public String toString() {
        return "AirDrop{" +
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
        registerPlaceholder("{world}", () -> world.getName());
        registerPlaceholder("{air_name}", () -> airName);
        registerPlaceholder("{time_to_start}", () -> timeToStart);
        registerPlaceholder("{time_to_open}", () -> timeToOpen);
        registerPlaceholder("{time_to_end}", () -> timeToEnd);
        registerPlaceholder("{time_to_start_const}", () -> timeToStartConst);
        registerPlaceholder("{time_to_open_const}", () -> timeToOpenConst);
        registerPlaceholder("{time_to_end_const}", () -> timeToEndConst);
        registerPlaceholder("{use_static_loc}", () -> useStaticLoc);
        registerPlaceholder("{triggered_time_to_open}", () -> triggeredTimeToOpen);
        registerPlaceholder("{independent_time_to_end}", () -> independentTimeToEnd);
        registerPlaceholder("{material_when_closed}", () -> materialWhenClosed.name());
        registerPlaceholder("{material_when_opened}", () -> materialWhenOpened.name());
        registerPlaceholder("{required_number_of_players_online}", () -> requiredNumberOfPlayersOnline);
        registerPlaceholder("{enable}", () -> enable);
        registerPlaceholder("{started}", () -> started);
        registerPlaceholder("{opened}", () -> opened);
        registerPlaceholder("{clicked}", () -> opened);
        registerPlaceholder("{airdrop_type}", () -> "classic");

        registerPlaceholder("{x}", () -> location == null ? "?" : location.getBlockX());
        registerPlaceholder("{y}", () -> location == null ? "?" : location.getBlockY());
        registerPlaceholder("{z}", () -> location == null ? "?" : location.getBlockZ());
    }
}
