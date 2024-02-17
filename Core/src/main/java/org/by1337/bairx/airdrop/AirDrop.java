package org.by1337.bairx.airdrop;

import org.bukkit.World;
import org.by1337.blib.chat.Placeholderable;

public interface AirDrop extends Placeholderable {
    World getWorld();
    void tick();
}
