package org.by1337.bairx.hologram.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.hologram.Hologram;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.configuration.YamlContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DHHologram implements Hologram {
    private static final AtomicInteger counter = new AtomicInteger();
    private List<String> lines;
    private Vector offsets;
    private String name;
    private final boolean canUse;

    public DHHologram() {
        canUse = Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
        if (!canUse){
            BAirDropX.getMessage().error("Вы не можете использовать голограммы от DecentHolograms без DecentHolograms!");
        }
    }

    @Override
    public void load(YamlContext context) {
        lines = context.getList("lines", String.class);
        offsets = context.getAs("offsets", Vector.class);
        name = this.getClass().getSimpleName() + "-id:" + counter.getAndIncrement();
    }

    @Override
    public void spawn(Location location, Placeholderable placeholderable) {
        if (!canUse){
            return;
        }
        Validate.notNullAll(lines, offsets, name);
        List<String> list = new ArrayList<>(lines);
        list.replaceAll(placeholderable::replace);
        var loc = location.clone();
        loc.add(offsets);
        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name);
        if (hologram != null) {
            hologram.setLocation(loc);
            DHAPI.setHologramLines(hologram, list);
            hologram.realignLines();
        } else {
            DHAPI.createHologram(name, loc, list);
        }
    }

    @Override
    public void update(Placeholderable placeholderable) {
        if (!canUse){
            return;
        }
        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name);
        if (hologram != null) {
            List<String> list = new ArrayList<>(lines);
            list.replaceAll(placeholderable::replace);
            DHAPI.setHologramLines(hologram, list);
            hologram.realignLines();
        }
    }

    @Override
    public void remove() {
        if (!canUse){
            return;
        }
        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram(name);
        if (hologram != null)
            DHAPI.removeHologram(name);
    }
}
