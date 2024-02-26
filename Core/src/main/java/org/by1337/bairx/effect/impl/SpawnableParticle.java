package org.by1337.bairx.effect.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface SpawnableParticle {
    default void spawnParticle(Location location, int count) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count);
    }

    default void spawnParticle(double x, double y, double z, int count) {
        spawnParticle(x, y, z, count, null);
    }

    default <T> void spawnParticle(Location location, int count, @Nullable T data) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, data);
    }

    default <T> void spawnParticle(double x, double y, double z, int count, @Nullable T data) {
        spawnParticle(x, y, z, count, 0, 0, 0, data);
    }

    default void spawnParticle(Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    default void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    default <T> void spawnParticle(Location location, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    default <T> void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data) {
        spawnParticle(x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    default void spawnParticle(Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    default void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    default <T> void spawnParticle(Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    default <T> void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) {
        spawnParticle(x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
    }

    default <T> void spawnParticle(Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {
        spawnParticle(location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, force);
    }

    <T> void spawnParticle(double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);

    CompoundTag save();
}
