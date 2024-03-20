package org.by1337.bairx.timer;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventListener;
import org.by1337.bairx.event.EventListenerManager;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.exception.PluginInitializationException;
import org.by1337.bairx.random.WeightedAirDrop;
import org.by1337.bairx.random.WeightedItem;
import org.by1337.bairx.random.WeightedRandomItemSelector;
import org.by1337.bairx.timer.strategy.TimerRegistry;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Ticker implements Timer, EventListener {
    private static final Random random = new Random();
    private final List<WeightedAirDrop> airdrops = new ArrayList<>();
    @Nullable
    private AirDrop current;
    private final HashSet<AirDrop> bypassed = new HashSet<>();
    private final HashSet<NameKey> lickedAirDrops = new HashSet<>();
    private final NameKey name;
    private int tickSpeed;
    private TickType tickType;
    private final WeightedRandomItemSelector<WeightedAirDrop> randomAirdropSelector;

    public Ticker(YamlContext context) {
        name = Validate.notNull(context.getAsNameKey("name"), "Параметр `name` не указан!");
        tickSpeed = Validate.notNull(context.getAsInteger("tick-speed"), "Параметр `tick-speed` не указан!");
        String tickTypeString = Validate.notNull(context.getAsString("tick-type"), "Параметр `tick-type` не указан!");
        Validate.test(tickTypeString, str -> !str.equals("all") && !str.equals("by_chance"), () -> new PluginInitializationException("Параметр `tick-type` может быть только 'all' или 'by_chance'"));
        tickType = tickTypeString.equals("all") ? TickType.ALL : TickType.BY_CHANCE;


        Map<?, ?> airdropsRawMap = context.getAs("linked-airdrops", MemorySection.class, new YamlConfiguration()).getValues(false);

        for (Map.Entry<?, ?> entry : airdropsRawMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            NameKey id = new NameKey(String.valueOf(key));
            if (tickType == TickType.ALL) {
                airdrops.add(new WeightedAirDrop(id, 100));
            } else {
                airdrops.add(new WeightedAirDrop(id,
                        Validate.tryMap(value, (obj) -> Integer.parseInt(String.valueOf(obj)), "%s должен быть числом!", value)));
            }
            lickedAirDrops.add(id);
        }
        if (tickType == TickType.BY_CHANCE) {
            EventListenerManager.register(BAirDropX.getInstance(), this);
        }
        randomAirdropSelector = new WeightedRandomItemSelector<>(airdrops);
    }

    @Override
    public void tick(final long currentTick) {
        if (currentTick % tickSpeed != 0) return;
        if (tickType == TickType.BY_CHANCE) {
            if (current == null) {
                current = nextAirDrop();
            }
            if (current != null) current.tick();
            bypassed.forEach(AirDrop::tick);
        } else {
            lickedAirDrops.stream().map(BAirDropX::getAirdropById).filter(Objects::nonNull).forEach(AirDrop::tick);
        }
    }

    private AirDrop nextAirDrop() {
        var wair = randomAirdropSelector.getRandomItem();
        if (wair != null) {
            var air = BAirDropX.getAirdropById(wair.getId());
            if (air == null) {
                BAirDropX.getMessage().warning("Таймер %s не найден аирдроп %s", name, wair.getChance());
            } else if (!air.isStarted() && air.canSpawn()) {
                return air;
            }
        }
        return null;
    }

    @Override
    public void onEvent(@NotNull Event event, @NotNull AirDrop airDrop) {
        if (!lickedAirDrops.contains(airDrop.getId()))
            return;
        if (tickType == TickType.ALL) return;
        if (event.getEventType() == EventType.END) {
            if (current == airDrop) {
                current = null;
            }
            bypassed.remove(airDrop);
        }
        if (event.getEventType() == EventType.START) {
            if (current != airDrop) {
                bypassed.add(airDrop);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker = (Ticker) o;
        return tickSpeed == ticker.tickSpeed && Objects.equals(airdrops, ticker.airdrops) && Objects.equals(current, ticker.current) && Objects.equals(bypassed, ticker.bypassed) && Objects.equals(lickedAirDrops, ticker.lickedAirDrops) && Objects.equals(name, ticker.name) && tickType == ticker.tickType;
    }


    @Override
    public NameKey name() {
        return name;
    }

    @Override
    public TimerRegistry getType() {
        return TimerRegistry.TICKER;
    }


    private enum TickType {
        ALL,
        BY_CHANCE
    }

}
