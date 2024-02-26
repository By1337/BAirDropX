package org.by1337.bairx.event;

import org.by1337.bairx.airdrop.AirDrop;
import org.jetbrains.annotations.NotNull;

public interface EventListener {
    void onEvent(@NotNull Event event, @NotNull AirDrop airDrop);
}
