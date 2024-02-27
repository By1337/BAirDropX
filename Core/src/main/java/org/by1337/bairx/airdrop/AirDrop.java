package org.by1337.bairx.airdrop;

import org.bukkit.Location;
import org.bukkit.World;
import org.by1337.bairx.timer.Timer;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface AirDrop extends Placeholderable {
    World getWorld();
    void tick();
    Set<SpacedNameKey> getSignedListeners();
    boolean isStarted();
    NameKey getId();
   boolean isUseDefaultTimer();
   Location getLocation();
}
