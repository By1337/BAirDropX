package org.by1337.bairx.airdrop.loader;

import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.AirDropMetaData;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.timer.strategy.TimerCreator;
import org.by1337.bairx.timer.strategy.TimerRegistry;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AirdropRegistry {
    private static final Map<NameKey, AirdropRegistry> types = new HashMap<>();
    public static final AirdropRegistry CLASSIC = register(new NameKey(ClassicAirDrop.TYPE), ClassicAirDrop::load);
    private final NameKey id;
    private final AirDropCreator creator;

    private AirdropRegistry(NameKey id, AirDropCreator creator) {
        this.id = id;
        this.creator = creator;
    }

    public static AirdropRegistry byId(NameKey id){
        return types.get(id);
    }

    public NameKey getId() {
        return id;
    }

    public AirDropCreator getCreator() {
        return creator;
    }

    public static AirdropRegistry register(NameKey id, AirDropCreator creator) {
        if (types.containsKey(id)) {
            throw new IllegalStateException(String.format("There is already a airdrop associated with the %s identifier.", id));
        }
        AirdropRegistry airdropRegistry = new AirdropRegistry(id, creator);
        types.put(id, airdropRegistry);
        return airdropRegistry;
    }
    public interface AirDropCreator {
        AirDrop create(File dataFolder, AirDropMetaData metaData);
    }
}
