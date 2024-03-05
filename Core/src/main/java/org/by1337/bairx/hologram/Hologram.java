package org.by1337.bairx.hologram;

import org.bukkit.Location;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.configuration.YamlContext;

public interface Hologram {
    void load(YamlContext context);
    void spawn(Location location, Placeholderable placeholderable);
    void update(Placeholderable placeholderable);
    void remove();

}
