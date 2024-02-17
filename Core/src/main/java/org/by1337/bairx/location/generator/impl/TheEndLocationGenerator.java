package org.by1337.bairx.location.generator.impl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.location.generator.GeneratorSetting;

public class TheEndLocationGenerator extends OverworldLocationGenerator {
    public TheEndLocationGenerator(GeneratorSetting setting, AirDrop airDrop) {
        super(setting, airDrop);
    }

    @Override
    protected Location generate() {
        Chunk chunk = getRandomChunk();
        if (chunk.getBlock(8, 50, 8).getType().isAir()) return null;
        return process(chunk);
    }
}
