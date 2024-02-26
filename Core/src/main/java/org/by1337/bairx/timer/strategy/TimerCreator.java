package org.by1337.bairx.timer.strategy;

import org.by1337.bairx.timer.Timer;
import org.by1337.blib.configuration.YamlContext;

@FunctionalInterface
public interface TimerCreator {
    Timer create(YamlContext context);
}
