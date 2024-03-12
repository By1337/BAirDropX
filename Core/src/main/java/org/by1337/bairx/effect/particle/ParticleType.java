package org.by1337.bairx.effect.particle;

import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;

import java.util.HashMap;
import java.util.Map;

public class ParticleType {
    private static final Map<NameKey, ParticleType> types = new HashMap<>();
    public static final ParticleType DEFAULT = register(new NameKey("default"), DefaultParticle::new);
    public static final ParticleType REDSTONE = register(new NameKey("redstone"), RedStoneParticle::new);
    private final NameKey id;
    private final ParticleCreator creator;

    private ParticleType(NameKey id, ParticleCreator creator) {
        this.id = id;
        this.creator = creator;
        types.put(id, this);
    }

    public static SpawnableParticle create(CompoundTag compoundTag){
        NameKey nameKey = new NameKey(compoundTag.getAsString("type"));
        var type = types.get(nameKey);
        Validate.notNull(type, "Неизвестный тип партикла " + nameKey.getName());
        return type.creator.create(compoundTag);
    }
    public static ParticleType getById(NameKey id){
        return types.get(id);
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
