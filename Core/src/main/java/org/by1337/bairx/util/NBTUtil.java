package org.by1337.bairx.util;

import org.bukkit.util.Vector;
import org.by1337.bairx.nbt.impl.CompoundTag;

public class NBTUtil {
    public static Vector getAsVector(CompoundTag compoundTag){
        return new Vector(
                compoundTag.getAsDouble("x"),
                compoundTag.getAsDouble("y"),
                compoundTag.getAsDouble("z")
        );
    }
    public static CompoundTag setVector(Vector vector){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putDouble("x", vector.getX());
        compoundTag.putDouble("y", vector.getY());
        compoundTag.putDouble("z", vector.getZ());
        return compoundTag;
    }
}
