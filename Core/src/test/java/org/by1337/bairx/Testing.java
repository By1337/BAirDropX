package org.by1337.bairx;

import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.by1337.bairx.effect.impl.Circle;
import org.by1337.bairx.effect.particle.DefaultParticle;
import org.by1337.bairx.nbt.NBTParser;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.blib.math.MathParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class Testing {
    @Test
    public void run() {
//        Circle.Creator cfg = new Circle.Creator(2, 0.05, Circle.Type.XZ, new Vector(1, 0, 1), new Vector(0, 0, 0), 0, 0, false, 5, 20, "test", new DefaultParticle(Particle.CLOUD));
//
//        CompoundTag nbt = new CompoundTag();
//
//        cfg.save(nbt);
//
//        System.out.println(nbt.toString());
//
//        Circle.Creator v = new Circle.Creator(nbt);
//
//        CompoundTag nbt1 = new CompoundTag();
//        v.save(nbt1);
//        Assert.assertEquals(nbt1.toString(), nbt.toString());

//        long l = System.currentTimeMillis();
//        long nanos = System.nanoTime();
//        for (int i = 0; i < 50; i++) {
//            MathParser.mathSave("10 + 10 + 10 + (((((((((((((123)))) + 89)))))))))");
//        }
//        System.out.println(System.currentTimeMillis() - l);
//        System.out.println(TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - nanos));
    }
}