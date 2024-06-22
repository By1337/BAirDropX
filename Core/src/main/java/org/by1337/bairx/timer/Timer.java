package org.by1337.bairx.timer;

import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.timer.strategy.TimerRegistry;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Timer {
    NameKey name();
    void tick(final long currentTick);
    TimerRegistry getType();
    @Nullable
     Pair<AirDrop, Long> getNearest();

}
