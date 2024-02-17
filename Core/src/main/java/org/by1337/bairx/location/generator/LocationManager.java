package org.by1337.bairx.location.generator;

import org.bukkit.Location;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.location.generator.impl.*;
import org.jetbrains.annotations.Nullable;

public class LocationManager   {
    private final GeneratorSetting setting;
    private final LocationGenerator overworld;
    private final LocationGenerator theEnd;
    private final LocationGenerator theNether;
    private final AirDrop airDrop;

    public LocationManager(GeneratorSetting setting, AirDrop airDrop) {
        this.setting = setting;
        overworld = new OverworldLocationGenerator(setting, airDrop);
        theEnd = new TheEndLocationGenerator(setting, airDrop);
        theNether = new TheNetherLocationGenerator(setting, airDrop);
        this.airDrop = airDrop;
    }

    @Nullable
    public Location generate() {
        return switch (airDrop.getWorld().getEnvironment()) {
            case NORMAL, CUSTOM -> overworld.generate();
            case NETHER -> theNether.generate();
            case THE_END -> theEnd.generate();
        };
    }
}
