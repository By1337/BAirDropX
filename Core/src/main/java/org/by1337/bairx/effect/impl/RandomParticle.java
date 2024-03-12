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
import org.by1337.blib.nbt.NBT;
import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.blib.nbt.impl.ListNBT;
import org.by1337.bairx.util.NBTUtil;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomParticle implements Effect {
    private final Config config;
    private BukkitRunnable runnable;
    private final Random random;

    public RandomParticle(Config config) {
        this.config = config;
        random = new Random();
    }

    @Override
    public void start(@NotNull Location location) {
        Validate.notNull(location, "location is null!");
        Validate.notNull(location.getWorld(), "World is null!");
        runnable = new BukkitRunnable() {
            int x;
            final double minX = location.getX() + config.pos1.getX();
            final double minY = location.getY() + config.pos1.getY();
            final double minZ = location.getZ() + config.pos1.getZ();

            final double maxX = location.getX() + config.pos2.getX();
            final double maxY = location.getY() + config.pos2.getY();
            final double maxZ = location.getZ() + config.pos2.getZ();

            @Override
            public void run() {
                for (SpawnableParticle particle : config.particles) {
                    double randomX = minX + (maxX - minX) * random.nextDouble();
                    double randomY = minY + (maxY - minY) * random.nextDouble();
                    double randomZ = minZ + (maxZ - minZ) * random.nextDouble();
                    particle.spawnParticle(location.getWorld(), randomX, randomY, randomZ, config.count, config.velocity, config.velocity, config.velocity, config.maxSpeed, true);
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

        final int count;
        final boolean repeat;
        final int period;
        final int repeatCount;
        final String name;
        final float maxSpeed;
        final Vector pos1;
        final Vector pos2;
        final List<SpawnableParticle> particles;
        final double velocity;

        public Config(int count, boolean repeat, int period, int repeatCount, String name, float maxSpeed, Vector pos1, Vector pos2, List<SpawnableParticle> particles, double velocity) {
            this.count = count;
            this.repeat = repeat;
            this.period = period;
            this.repeatCount = repeatCount;
            this.name = name;
            this.maxSpeed = maxSpeed;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.particles = particles;
            this.velocity = velocity;
        }

        public Config(CompoundTag nbt) {
            pos1 = NBTUtil.getAsVector(nbt.getAsCompoundTag("pos1"));
            pos2 = NBTUtil.getAsVector(nbt.getAsCompoundTag("pos2"));

            maxSpeed = nbt.getAsFloat("maxSpeed");
            velocity = nbt.getAsDouble("velocity");
            count = nbt.getAsInt("count");
            period = nbt.getAsInt("period");
            repeatCount = nbt.getAsInt("repeatCount");
            repeat = nbt.getAsBoolean("repeat");
            name = nbt.getAsString("name");

            ListNBT particlesNBT = (ListNBT) nbt.get("particles");
            Validate.notNull(particlesNBT);
            particles = new ArrayList<>();
            for (NBT nbt1 : particlesNBT) {
                CompoundTag p = (CompoundTag) nbt1;
                var t = ParticleType.getById(new NameKey(p.getAsString("type")));
                particles.add(t.getCreator().create(p));
            }

        }

        @Override
        public Effect create() {
            return new RandomParticle(this);
        }

        @Override
        public void save(CompoundTag nbt) {
            nbt.putString("effect-type", EffectCreatorType.RANDOM_PARTICLE.getId().getName());
            nbt.putFloat("maxSpeed", maxSpeed);
            nbt.putInt("count", count);
            nbt.putInt("period", period);
            nbt.putInt("repeatCount", repeatCount);
            nbt.putBoolean("repeat", repeat);
            nbt.putString("name", name);
            nbt.putDouble("velocity", velocity);
            nbt.putTag("pos1", NBTUtil.setVector(pos1));
            nbt.putTag("pos2", NBTUtil.setVector(pos2));
            ListNBT listNBT = new ListNBT();
            for (SpawnableParticle particle : particles) {
                CompoundTag p = new CompoundTag();
                particle.save(p);
                listNBT.add(p);
            }
            nbt.putTag("particles", listNBT);
        }

        @Override
        public String name() {
            return name;
        }
    }
}
