package org.by1337.bairx.effect.particle;

import org.bukkit.Particle;
import org.bukkit.World;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.chat.ChatColor;
import org.jetbrains.annotations.Nullable;

public class RedStoneParticle implements SpawnableParticle {
    private final Particle particle;
    private final Particle.DustOptions options;

    public RedStoneParticle(CompoundTag compoundTag) {
        particle = Validate.invokeAndHandleException(
                () -> Particle.valueOf(compoundTag.getAsString("particle")),
                IllegalArgumentException.class,
                IllegalArgumentException::new,
                "Не удалось загрузить партикл так как он не указан! " + compoundTag.toString()
        );
        if (particle.getDataType() != Particle.DustOptions.class) {
            throw new IllegalStateException("не удалось загрузить партикл так как он не поддерживается этим типом! " + compoundTag);
        }
        ChatColor color = new ChatColor(compoundTag.getAsString("hex"));
        float size = compoundTag.getAsFloat("size");

        options = new Particle.DustOptions(color.toBukkitColor(), size);

    }


    @Override
    public void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, boolean force) {
        world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, options, force);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.putString("type", ParticleType.REDSTONE.getId().getName());
        compoundTag.putString("particle", particle.name());
        compoundTag.putString("hex",
                new ChatColor(
                        new java.awt.Color(options.getColor().getRed(), options.getColor().getGreen(), options.getColor().getBlue())
                ).toHex()
        );
        compoundTag.putFloat("size", options.getSize());
    }
}
