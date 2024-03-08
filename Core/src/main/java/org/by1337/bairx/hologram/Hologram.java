package org.by1337.bairx.hologram;

import org.bukkit.Location;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.SpacedNameKey;

public interface Hologram {
    void load(YamlContext context);
    void spawn(Location location, Placeholderable placeholderable, SpacedNameKey name);
    void update(Placeholderable placeholderable);
    void remove();

}
