package org.by1337.bairx.effect.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface SpawnableParticle {

    default void spawnParticle(World world, Location location, int count) {
        spawnParticle(world, location.getX(), location.getY(), location.getZ(), count);
    }

    default void spawnParticle(World world, double x, double y, double z, int count) {
        spawnParticle(world, x, y, z, count, 0, 0, 0);
    }

    default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    default void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(world, x, y, z, count, offsetX, offsetY, offsetZ, 1);
    }

    default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    default void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(world, x, y, z, count, offsetX, offsetY, offsetZ, extra, false);
    }

    default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, boolean force) {
        spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, force);
    }

    default void spawnParticle(World world, Vector pos, int count, Vector offsets, double extra, boolean force) {
        spawnParticle(world, pos.getX(), pos.getY(), pos.getZ(), count, offsets.getX(), offsets.getY(), offsets.getZ(), extra, force);
    }


    void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, boolean force);

    void save(CompoundTag compoundTag);
}
