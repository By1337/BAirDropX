package org.by1337.bairx.effect.impl;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.effect.EffectCreatorType;
import org.by1337.bairx.effect.particle.SpawnableParticle;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.jetbrains.annotations.NotNull;


public class Helix implements Effect {
    private final Config config;
    private BukkitRunnable runnable;

    public Helix(Config config) {
        this.config = config;
    }

    @Override
    public void stop() {
        Validate.notNull(runnable, "Unable to stop the effect as it was never initiated!");
        runnable.cancel();
        runnable = null;
    }

    @Override
    public void start(@NotNull Location location) {
        Validate.notNull(location, "location is null!");
        Validate.notNull(location.getWorld(), "World is null!");
        runnable = new BukkitRunnable() {
            int x;

            @Override
            public void run() {
                for (double y = 0; y <= config.height * 2; y += config.step) {
                    Vector pos = config.type.apply(location, y, config.radius);
                    pos.add(config.offsets);
                    pos.add(new Vector(0, y, 0));
                    config.particle.spawnParticle(location.getWorld(), pos, config.count, config.velocity, config.maxSpeed, true);
                }
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
    public boolean isStopped() {
        return runnable == null || runnable.isCancelled();
    }

    public static class Config extends Circle.Config {
        final double height;

        public Config(CompoundTag nbt) {
            super(nbt);
            height = nbt.getAsDouble("height");
        }

        @Override
        public void save(CompoundTag nbt) {
            super.save(nbt);
            nbt.putString("effect-type", EffectCreatorType.HELIX.getId().getName());
            nbt.putDouble("height", height);
        }

        @Override
        public Effect create() {
            return new Helix(this);
        }

        public Config(double radius, double step, Circle.Type type, Vector offsets, Vector velocity, float maxSpeed, int count, boolean repeat, int period, int repeatCount, String name, SpawnableParticle particle, double height) {
            super(radius, step, type, offsets, velocity, maxSpeed, count, repeat, period, repeatCount, name, particle);
            this.height = height;
        }
    }

}
