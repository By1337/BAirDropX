package org.by1337.bairx.effect.impl;

import org.by1337.bairx.nbt.impl.CompoundTag;
import org.by1337.blib.util.NameKey;

import java.util.HashMap;
import java.util.Map;

public class ParticleType {
    private static final Map<NameKey, ParticleType> types = new HashMap<>();

    private final NameKey id;
    private final ParticleCreator creator;

    private ParticleType(NameKey id, ParticleCreator creator) {
        this.id = id;
        this.creator = creator;
        types.put(id, this);
    }

    public NameKey getId() {
        return id;
    }

    public ParticleCreator getCreator() {
        return creator;
    }

    public interface ParticleCreator {
        SpawnableParticle create(CompoundTag compoundTag);
    }

    public static ParticleType register(NameKey id, ParticleCreator creator) {
        if (id == null || creator == null) {
            throw new IllegalArgumentException("id and creator must not be null");
        }
        if (types.containsKey(id)) {
            throw new IllegalArgumentException("Particle type with id " + id + " is already registered");
        }
        return new ParticleType(id, creator);
    }

    public static void unregister(NameKey id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (!types.containsKey(id)) {
            throw new IllegalArgumentException("Particle type with id " + id + " is not registered");
        }
        types.remove(id);
    }
}
