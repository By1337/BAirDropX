package org.by1337.bairx.event;

import org.by1337.blib.util.NameKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EventType {
    private static final Map<NameKey, EventType> byName = new HashMap<>();
    private final NameKey nameKey;

    public static final EventType START = register("start");
    public static final EventType OPEN = register("open");
    public static final EventType END = register("end");
    public static final EventType TICK = register("tick");

    private EventType(String nameKey) {
        this(new NameKey(nameKey));
    }

    private EventType(NameKey nameKey) {
        this.nameKey = nameKey;
    }

    public static EventType register(String nameKey) {
        return register(new NameKey(nameKey));
    }

    public static EventType register(NameKey nameKey) {
        if (byName.containsKey(nameKey)) {
            throw new IllegalStateException("event type already exist!");
        }
        return byName.put(nameKey, new EventType(nameKey));
    }

    public static EventType getByName(String nameKey) {
        return getByName(new NameKey(nameKey));
    }

    public static EventType getByName(NameKey nameKey) {
        return byName.get(nameKey);
    }

    public NameKey getNameKey() {
        return nameKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return Objects.equals(nameKey, eventType.nameKey);
    }

    @Override
    public int hashCode() {
        return nameKey.hashCode();
    }
}
