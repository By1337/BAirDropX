package org.by1337.bairx.event;

import org.bukkit.entity.Player;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.util.Placeholder;
import org.by1337.blib.math.MathParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Event extends Placeholder {
    private final AirDrop airDrop;
    @Nullable
    private final Player player;
    private final EventType eventType;

    public Event(@NotNull AirDrop airDrop, @Nullable Player player, @NotNull EventType eventType) {
        this.airDrop = airDrop;
        this.player = player;
        this.eventType = eventType;
    }

    public AirDrop getAirDrop() {
        return airDrop;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String replace(String string) {
        return BAirDropX.getMessage().messageBuilder(MathParser.mathSave(
                airDrop.replace(super.replace(string))
        ), player);
    }

    public Event getWithPlayer(@Nullable Player player) {
        return getWith(airDrop, player);
    }

    public Event getWithAirDrop(@NotNull AirDrop airDrop) {
        return getWith(airDrop, player);
    }

    public Event getWith(@NotNull AirDrop airDrop, @Nullable Player player) {
        return getAs(airDrop, player, eventType);
    }

    public Event getAs(@NotNull AirDrop airDrop, @Nullable Player player, @NotNull EventType eventType) {
        Event event = new Event(airDrop, player, eventType);
        event.registerPlaceholders(super.placeholders.entrySet());
        return event;
    }

}
