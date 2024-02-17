package org.by1337.bairx.location.generator.impl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.ClassicAirDrop;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.location.generator.LocationGenerator;
import org.by1337.blib.world.BlockPosition;

public class OverworldLocationGenerator extends LocationGenerator {
    public OverworldLocationGenerator(GeneratorSetting setting, AirDrop airDrop) {
        super(setting, airDrop);
    }

    @Override
    protected Location generate() {
        Chunk chunk = getRandomChunk();
        return process(chunk);
    }
    protected Location process(Chunk chunk){
        int y = getHighestBlock(chunk, 8, 8);

        if (y < setting.minY) return null;

        BlockPosition pos = new BlockPosition(8, y, 8);

        Block block = chunk.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (!setting.whiteListBiomes.contains(block.getBiome()))
            return null;

        pos = pos.add(setting.offsets);

//        if (!isRegionEmpty(setting.regionRadius, block.getLocation()))
//            return null;

        for (BlockPosition blockPosition : setting.hasBlock) {
            if (!hasBlock(chunk, blockPosition.add(pos))) {
                return null;
            }
        }
        for (BlockPosition blockPosition : setting.hasNoBlock) {
            if (hasBlock(chunk, blockPosition.add(pos))) {
                return null;
            }
        }

        return chunk.getBlock(pos.getX(), pos.getY(), pos.getZ()).getLocation();
    }
}
