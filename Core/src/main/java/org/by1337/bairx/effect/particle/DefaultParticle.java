package org.by1337.bairx.effect.particle;

import org.bukkit.Particle;
import org.bukkit.World;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.jetbrains.annotations.Nullable;

public class DefaultParticle implements SpawnableParticle {
    private final Particle particle;

    public DefaultParticle(Particle particle) {
        this.particle = particle;
    }

    public DefaultParticle(CompoundTag compoundTag) {
        particle = Validate.invokeAndHandleException(
                () -> Particle.valueOf(compoundTag.getAsString("particle")),
                IllegalArgumentException.class,
                IllegalArgumentException::new,
                "Не удалось загрузить партикл так как он не указан! " + compoundTag.toString()
        );
        if (particle.getDataType() != Void.class) {
            throw new IllegalStateException("не удалось загрузить партикл так как он не поддерживается этим типом! " + compoundTag);
        }
    }


    @Override
    public void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, boolean force) {
        world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null, force);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.putString("type", ParticleType.DEFAULT.getId().getName());
        compoundTag.putString("particle", particle.name());
    }
}
