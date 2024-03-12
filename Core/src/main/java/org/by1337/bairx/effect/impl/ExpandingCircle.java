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
import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;


public class ExpandingCircle implements Effect {
    private final Config config;
    private BukkitRunnable runnable;

    public ExpandingCircle(Config config) {
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
            double r = config.radius;
            @Override
            public void run() {
                for (double i = 0; i <= Math.PI * 2; i += config.step) {
                    Vector pos = config.type.apply(location, i, r);
                    pos.add(config.offsets);
                    config.particle.spawnParticle(location.getWorld(), pos, config.count, config.velocity, config.maxSpeed, true);
                }
                if (r >= config.endRadius || config.repeat && x >= config.repeatCount) {
                    cancel();
                }
                r += config.stepRadius;
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
        final double endRadius;
        final double stepRadius;

        public Config(CompoundTag nbt) {
            super(nbt);
            endRadius = nbt.getAsDouble("endRadius");
            stepRadius = nbt.getAsDouble("stepRadius");
        }

        @Override
        public void save(CompoundTag nbt) {
            super.save(nbt);
            nbt.putString("effect-type", EffectCreatorType.EXPANDING_CIRCLE.getId().getName());
            nbt.putDouble("endRadius", endRadius);
            nbt.putDouble("stepRadius", stepRadius);

        }

        @Override
        public Effect create() {
            return new ExpandingCircle(this);
        }

        public Config(double radius, double step, Circle.Type type, Vector offsets, Vector velocity, float maxSpeed, int count, boolean repeat, int period, int repeatCount, String name, SpawnableParticle particle, double endRadius, double stepRadius) {
            super(radius, step, type, offsets, velocity, maxSpeed, count, repeat, period, repeatCount, name, particle);
            this.endRadius = endRadius;
            this.stepRadius = stepRadius;
        }
    }

}
