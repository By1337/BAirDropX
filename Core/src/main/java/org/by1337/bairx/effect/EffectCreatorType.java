package org.by1337.bairx.effect;

import org.by1337.bairx.effect.impl.*;
import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EffectCreatorType {
    private final static Map<NameKey, EffectCreatorType> types = new HashMap<>();

    public static final EffectCreatorType CIRCLE = register(new NameKey("circle"), Circle.Config::new);
    public static final EffectCreatorType EXPANDING_CIRCLE = register(new NameKey("expanding_circle"), ExpandingCircle.Config::new);
    public static final EffectCreatorType HELIX = register(new NameKey("helix"), Helix.Config::new);
    public static final EffectCreatorType PARTICLE_EXPLOSION = register(new NameKey("particle_explosion"), ParticleExplosion.Config::new);
    public static final EffectCreatorType RANDOM_PARTICLE = register(new NameKey("random_particle"), RandomParticle.Config::new);
    private final NameKey id;
    private final EffectCfg creator;

    public static EffectCreatorType register(String id, EffectCfg effectCfg) {
        return register(new NameKey(id), effectCfg);
    }

    public static EffectCreatorType register(NameKey id, EffectCfg effectCfg) {
        if (types.containsKey(id)) {
            throw new IllegalStateException("effect already exist");
        }
        return new EffectCreatorType(id, effectCfg);
    }

    @Nullable
    public static EffectCreatorType getById(NameKey id) {
        return types.get(id);
    }

    private EffectCreatorType(NameKey id, EffectCfg creator) {
        this.id = id;
        this.creator = creator;
        types.put(id, this);
    }


    public NameKey getId() {
        return id;
    }

    public EffectCfg getCreator() {
        return creator;
    }

    @FunctionalInterface
    public interface EffectCfg {
        EffectCreator create(CompoundTag compoundTag);
    }
}
