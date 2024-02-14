package org.by1337.bairx.location.generator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.by1337.bairx.AirDrop;
import org.by1337.blib.world.BlockPosition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class LocationGenerator {
    protected final GeneratorSetting setting;
    protected final AirDrop airDrop;


    protected LocationGenerator(GeneratorSetting setting, AirDrop airDrop) {
        this.setting = setting;
        this.airDrop = airDrop;
    }

    protected Chunk getRandomChunk() {
        int randomX = (int) (setting.center.x + (int) ((Math.random() * 2 - 1) * setting.radius));
        int randomZ = (int) (setting.center.z + (int) ((Math.random() * 2 - 1) * setting.radius));
        return airDrop.getWorld().getChunkAt(randomX >> 4, randomZ >> 4);
    }

    protected int getHighestBlock(Chunk chunk, int x, int z) {
        boolean upBlockIsAir = false;
        for (int y = setting.maxY; y > setting.minY; y--) {
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

    protected boolean hasBlock(Chunk chunk, BlockPosition blockPosition) {
        Material type = chunk.getBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).getType();
        return !setting.ignoreBlocks.contains(type);
    }


    protected abstract Location generate();

}
