package org.by1337.bairx.location.generator.impl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.by1337.bairx.AirDrop;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.bairx.location.generator.LocationGenerator;
import org.by1337.blib.world.BlockPosition;
public class TheNetherLocationGenerator extends LocationGenerator {
    public TheNetherLocationGenerator(GeneratorSetting setting, AirDrop airDrop) {
        super(setting, airDrop);
    }

    @Override
    protected Location generate() {
        Chunk chunk = getRandomChunk();
        int y;
        if (chunk.getBlock(8, 80, 8).getType().isAir()) {
            y = getHighestBlock(chunk, 8, 8, 80);
        } else if (chunk.getBlock(8, 50, 8).getType().isAir()) {
            y = getHighestBlock(chunk, 8, 8, 50);
        } else {
            return null;
        }

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

    protected int getHighestBlock(Chunk chunk, int x, int z, int start) {
        boolean upBlockIsAir = false;
        for (int y = start; y > setting.minY; y--) {
            if (!chunk.getBlock(x, y, z).getType().isAir()) {
                if (!setting.whiteListBlocks.contains(chunk.getBlock(x, y, z).getType())) {
                    return -1;
                }
                if (upBlockIsAir)
                    return y;
                else
                    return -1;
            } else {
                upBlockIsAir = true;
            }
        }
        return -1;
    }
}