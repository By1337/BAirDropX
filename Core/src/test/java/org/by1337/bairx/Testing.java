package org.by1337.bairx;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.by1337.bairx.effect.impl.Circle;
import org.by1337.bairx.effect.impl.Helix;
import org.by1337.bairx.effect.impl.ParticleExplosion;
import org.by1337.bairx.effect.impl.RandomParticle;
import org.by1337.bairx.effect.particle.DefaultParticle;
import org.by1337.bairx.effect.particle.RedStoneParticle;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.junit.Test;

import java.util.List;

public class Testing {
    @Test
    public void run() {
        RandomParticle.Config cfg = new RandomParticle.Config(
                0,
                true,
                2,
                9999,
                "random_particle",
                0,
                new Vector(-10, -10, -10),
                new Vector(10, 10, 10),
                List.of(
                        new DefaultParticle(Particle.FLAME),
                        new DefaultParticle(Particle.PORTAL),
                        new DefaultParticle(Particle.FALLING_OBSIDIAN_TEAR),
                        new DefaultParticle(Particle.SOUL_FIRE_FLAME),
                        new DefaultParticle(Particle.ENCHANTMENT_TABLE),
                        new DefaultParticle(Particle.TOTEM)
                )
        );

        CompoundTag nbt = new CompoundTag();

        cfg.save(nbt);


        System.out.println(nbt.toString());
//
//

        //System.out.println(((ListNBT)NBTParser.parseNBT("[{},{},{}]")).getInnerType());
    }
}
