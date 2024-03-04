package org.by1337.bairx;

import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.by1337.bairx.effect.impl.Circle;
import org.by1337.bairx.effect.particle.DefaultParticle;
import org.by1337.bairx.nbt.NBTParser;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Testing {
    @Test
    public void run() {
        Circle.Config cfg = new Circle.Config(2, 0.05, Circle.Type.XZ, new Vector(1, 0, 1), new Vector(0, 0, 0), 0, 0, false, 5, 20, "test", new DefaultParticle(Particle.CLOUD));

        CompoundTag nbt = new CompoundTag();

        cfg.save(nbt);

        System.out.println(nbt.toString());

        Circle.Config v = new Circle.Config(nbt);

        CompoundTag nbt1 = new CompoundTag();
        v.save(nbt1);
        Assert.assertEquals(nbt1.toString(), nbt.toString());

    }
}
