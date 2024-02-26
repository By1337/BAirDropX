package org.by1337.bairx.timer;

import org.by1337.bairx.timer.strategy.TimerRegistry;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TimerManager {
    private final Map<NameKey, Timer> timerMap = new HashMap<>();
    public void load(YamlContext yamlContext){
        Collection<YamlContext> list = yamlContext.getMap("timers", YamlContext.class, NameKey.class).values();

        for (YamlContext context : list) {
            TimerRegistry registry = TimerRegistry.byId(context.getAsNameKey("type"));
            Timer timer = registry.getCreator().create(context);
            timerMap.put(timer.name(), timer);
        }
    }

    public void tick(final long currentTick){
        timerMap.values().forEach(timer -> timer.tick(currentTick));
    }

    public void register(Timer timer) throws TimerRegistrationException {
        if (timerMap.containsKey(timer.name())) {
            throw new TimerRegistrationException("Timer with name " + timer.name() + " is already registered.");
        }
        timerMap.put(timer.name(), timer);
    }


    public void unregister(NameKey timerName) throws TimerRegistrationException {
        if (!timerMap.containsKey(timerName)) {
            throw new TimerRegistrationException("Timer with name " + timerName + " is not registered.");
        }
        timerMap.remove(timerName);
    }

    @Nullable
    public Timer getTimer(NameKey timerName) {
        return timerMap.get(timerName);
    }

    public static class TimerRegistrationException extends RuntimeException {
        public TimerRegistrationException(String message) {
            super(message);
        }
    }

}
