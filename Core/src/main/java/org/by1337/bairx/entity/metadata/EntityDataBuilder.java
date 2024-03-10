/*
package org.by1337.bairx.entity.metadata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import org.by1337.blib.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityDataBuilder {
    private final List<Pair<IndexedEntityDataType<?>, ?>> data = new ArrayList<>();

    public <T> void register(IndexedEntityDataType<T> type, T value) {
        data.add(Pair.of(type, value));
    }

    public <T> T get(IndexedEntityDataType<T> type) {
        for (Pair<IndexedEntityDataType<?>, ?> pair : data) {
            if (pair.getLeft().equals(type)) {
                return (T) pair.getRight();
            }
        }
        throw new IllegalStateException("Не найден тип " + type);
    }

    public <T> void set(IndexedEntityDataType<T> type, T value) {
        for (int i = 0; i < data.size(); i++) {
            Pair<IndexedEntityDataType<?>, ?> pair = data.get(i);
            if (pair.getLeft().equals(type)) {
                data.set(i, Pair.of(type, value));
                return;
            }
        }
        throw new IllegalStateException("Не найден тип " + type);
    }

    public List<EntityData> build() {
        List<EntityData> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Pair<IndexedEntityDataType<?>, ?> pair = data.get(i);
            System.out.println(pair.getLeft().getId());
            list.add(new EntityData(pair.getLeft().getId(), pair.getLeft().getType(), pair.getRight()));
        }
        return list;
    }
}
*/
