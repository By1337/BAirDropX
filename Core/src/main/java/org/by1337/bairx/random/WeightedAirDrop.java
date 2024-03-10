package org.by1337.bairx.random;

import org.by1337.blib.util.NameKey;

public class WeightedAirDrop implements WeightedItem {
    private final NameKey id;
    private final int chance;

    public WeightedAirDrop(NameKey id, int chance) {
        this.id = id;
        this.chance = chance;
    }

    @Override
    public int getWeight() {
        return chance;
    }

    public NameKey getId() {
        return id;
    }

    public int getChance() {
        return chance;
    }
}