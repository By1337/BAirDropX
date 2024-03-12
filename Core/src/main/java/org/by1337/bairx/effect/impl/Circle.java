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


public class Circle implements Effect {
    private final Config config;
    private BukkitRunnable runnable;

    public Circle(Config config) {
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
                for (double i = 0; i <= Math.PI * 2; i += config.step) {
                    Vector pos = config.type.apply(location, i, config.radius);
                    pos.add(config.offsets);
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
        return runnable.isCancelled();
    }


    public static class Config implements EffectCreator {
        final double radius;
        final double step;
        final Type type;
        final Vector offsets;
        final Vector velocity;
        final float maxSpeed;
        final int count;
        final boolean repeat;
        final int period;
        final int repeatCount;
        final String name;
        final SpawnableParticle particle;

        public Config(CompoundTag nbt) {
            radius = nbt.getAsDouble("radius");
            step = nbt.getAsDouble("step");
            type = Type.valueOf(nbt.getAsString("type"));
            offsets = new Vector(
                    nbt.getAsDouble("offsets.x"),
                    nbt.getAsDouble("offsets.y"),
                    nbt.getAsDouble("offsets.z")
            );
            velocity = new Vector(
                    nbt.getAsDouble("velocity.x"),
                    nbt.getAsDouble("velocity.y"),
                    nbt.getAsDouble("velocity.z")
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

        public Config(double radius, double step, Type type, Vector offsets, Vector velocity, float maxSpeed, int count, boolean repeat, int period, int repeatCount, String name, SpawnableParticle particle) {
            this.radius = radius;
            this.step = step;
            this.type = type;
            this.offsets = offsets;
            this.velocity = velocity;
            this.maxSpeed = maxSpeed;
            this.count = count;
            this.repeat = repeat;
            this.period = period;
            this.repeatCount = repeatCount;
            this.name = name;
            this.particle = particle;
        }

        @Override
        public Effect create() {
            return new Circle(this);
        }

        @Override
        public void save(CompoundTag nbt) {
            nbt.putString("effect-type", EffectCreatorType.CIRCLE.getId().getName());
            nbt.putDouble("radius", radius);
            nbt.putDouble("step", step);
            nbt.putString("type", type.name());
            nbt.putDouble("offsets.x", offsets.getX());
            nbt.putDouble("offsets.y", offsets.getY());
            nbt.putDouble("offsets.z", offsets.getZ());
            nbt.putDouble("velocity.x", velocity.getX());
            nbt.putDouble("velocity.y", velocity.getY());
            nbt.putDouble("velocity.z", velocity.getZ());
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
        public String name() {
            return name;
        }
    }

    public static enum Type {
        XY((loc, i, radius) -> new Vector(loc.getX() + (radius * Math.cos(i)), loc.getY() + (radius * Math.sin(i)), loc.getZ())),
        YX((loc, i, radius) -> new Vector(loc.getX() + (radius * Math.sin(i)), loc.getY() + (radius * Math.cos(i)), loc.getZ())),
        XZ((loc, i, radius) -> new Vector(loc.getX() + (radius * Math.cos(i)), loc.getY(), loc.getZ() + (radius * Math.sin(i)))),
        ZX((loc, i, radius) -> new Vector(loc.getX() + (radius * Math.sin(i)), loc.getY(), loc.getZ() + (radius * Math.cos(i)))),
        YZ((loc, i, radius) -> new Vector(loc.getX(), loc.getY() + (radius * Math.cos(i)), loc.getZ() + (radius * Math.sin(i)))),
        ZY((loc, i, radius) -> new Vector(loc.getX(), loc.getY() + (radius * Math.sin(i)), loc.getZ() + (radius * Math.cos(i))));
        private final FunctionToVector function;

        Type(FunctionToVector function) {
            this.function = function;
        }

        public Vector apply(Location location, double i, double radius) {
            return function.apply(location, i, radius);
        }

        private interface FunctionToVector {
            Vector apply(Location location, double i, double radius);
        }
    }
}
