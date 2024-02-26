package org.by1337.bairx.timer.strategy;

import org.by1337.bairx.timer.Ticker;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TimerRegistry {
    private static final Map<NameKey, TimerRegistry> types = new HashMap<>();
    public static final TimerRegistry TICKER = register(new NameKey("ticker"), Ticker::new);
    private final NameKey id;
    private final TimerCreator creator;

    private TimerRegistry(NameKey id, TimerCreator creator) {
        this.id = id;
        this.creator = creator;
    }

    public NameKey getId() {
        return id;
    }

    public TimerCreator getCreator() {
        return creator;
    }

    public static TimerRegistry byId(NameKey id){
        return types.get(id);
    }

    public static TimerRegistry register(NameKey id, TimerCreator creator) {
        if (types.containsKey(id)) {
            throw new IllegalStateException(String.format("There is already a timer associated with the %s identifier.", id));
        }
        TimerRegistry timerRegistry = new TimerRegistry(id, creator);
        types.put(id, timerRegistry);
        return timerRegistry;
    }

    @Nullable
    public static TimerRegistry unregister(NameKey id) {
        return types.remove(id);
    }
}
