package org.by1337.bairx.effect.impl;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.effect.EffectCreator;
import org.by1337.bairx.effect.EffectCreatorType;
import org.by1337.bairx.effect.particle.ParticleType;
import org.by1337.bairx.effect.particle.SpawnableParticle;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;

public class ParticleExplosion implements Effect {

    private final Config config;
    private BukkitRunnable runnable;

    public ParticleExplosion(Config config) {
        this.config = config;
    }

    @Override
    public void start(@NotNull Location location) {
        Validate.notNull(location, "location is null!");
        Validate.notNull(location.getWorld(), "World is null!");
        runnable = new BukkitRunnable() {
            int x;

            @Override
            public void run() {
                config.particle.spawnParticle(location.getWorld(), location.add(config.offsets), config.count, config.radius, config.radius, config.radius, config.maxSpeed, true);
                if (config.repeat && x >= config.repeatCount) {
                    cancel();
                }
                x++;
            }
        };
        if (config.repeat) {
            runnable.runTaskTimerAsynchronously(BAirDropX.getInstance(), 0, config.period);
        } else {
            runnable.runTaskAsynchronously(BAirDropX.getInstance());
        }
    }

    @Override
    public void stop() {
        Validate.notNull(runnable, "Unable to stop the effect as it was never initiated!");
        runnable.cancel();
        runnable = null;
    }

    @Override
    public boolean isStopped() {
        return runnable == null || runnable.isCancelled();
    }

    public static class Config implements EffectCreator {
        final int period;
        final int repeatCount;
        final boolean repeat;
        final float maxSpeed;
        final int count;
        final Vector offsets;
        final String name;
        final SpawnableParticle particle;
        final double radius;

        public Config(int period, int repeatCount, boolean repeat, float maxSpeed, int count, Vector offsets, String name, SpawnableParticle particle, double radius) {
            this.period = period;
            this.repeatCount = repeatCount;
            this.repeat = repeat;
            this.maxSpeed = maxSpeed;
            this.count = count;
            this.offsets = offsets;
            this.name = name;
            this.particle = particle;
            this.radius = radius;
        }

        public Config(CompoundTag nbt) {
            radius = nbt.getAsDouble("radius");
            offsets = new Vector(
                    nbt.getAsDouble("offsets.x"),
                    nbt.getAsDouble("offsets.y"),
                    nbt.getAsDouble("offsets.z")
            );
            maxSpeed = nbt.getAsFloat("maxSpeed");
            count = nbt.getAsInt("count");
            period = nbt.getAsInt("period");
            repeatCount = nbt.getAsInt("repeatCount");
            repeat = nbt.getAsBoolean("repeat");
            name = nbt.getAsString("name");
            CompoundTag p = nbt.getAsCompoundTag("particle");
            var t = ParticleType.getById(new NameKey(p.getAsString("type")));
            particle = t.getCreator().create(p);
        }

        @Override
        public void save(CompoundTag nbt) {
            nbt.putDouble("radius", radius);
            nbt.putString("effect-type", EffectCreatorType.PARTICLE_EXPLOSION.getId().getName());
            nbt.putDouble("offsets.x", offsets.getX());
            nbt.putDouble("offsets.y", offsets.getY());
            nbt.putDouble("offsets.z", offsets.getZ());
            nbt.putFloat("maxSpeed", maxSpeed);
            nbt.putInt("count", count);
            nbt.putInt("period", period);
            nbt.putInt("repeatCount", repeatCount);
            nbt.putBoolean("repeat", repeat);
            nbt.putString("name", name);
            CompoundTag p = new CompoundTag();
            particle.save(p);
            nbt.putTag("particle", p);
        }

        @Override
        public Effect create() {
            return new ParticleExplosion(this);
        }

        @Override
        public String name() {
            return name;
        }
    }
}
