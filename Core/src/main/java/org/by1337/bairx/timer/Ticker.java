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
    private final HashSet<NameKey> linkedAirDrops = new HashSet<>();
    private final NameKey name;
    private int tickSpeed;
    private TickType tickType;
    private final WeightedRandomItemSelector<WeightedAirDrop> randomAirdropSelector;

    public Ticker(YamlContext context) {
        name = Validate.notNull(context.getAsNameKey("name"), BAirDropX.translate("timer.ticker.missing.name"));
        tickSpeed = Validate.notNull(context.getAsInteger("tick-speed"), BAirDropX.translate("timer.ticker.missing.tick-speed"));
        String tickTypeString = Validate.notNull(context.getAsString("tick-type"), BAirDropX.translate("timer.ticker.missing.tick-type"));
        Validate.test(tickTypeString, str -> !str.equals("all") && !str.equals("by_chance"), () -> new PluginInitializationException(BAirDropX.translate("timer.ticker.incorrectly.tick-type")));
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
                        Validate.tryMap(value, (obj) -> Integer.parseInt(String.valueOf(obj)), BAirDropX.translate("number.must.be.number"), value)));
            }
            linkedAirDrops.add(id);
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
            linkedAirDrops.stream().map(BAirDropX::getAirdropById).filter(Objects::nonNull).forEach(AirDrop::tick);
        }
    }

    @Nullable
    public Pair<AirDrop, Long> getNearest() {
        List<AirDrop> airDrops = new ArrayList<>();
        if (tickType == TickType.BY_CHANCE) {
            if (current != null) {
                airDrops.add(current);
            }
            airDrops.addAll(bypassed);
        } else {
            airDrops.addAll(linkedAirDrops.stream().map(BAirDropX::getAirdropById).filter(Objects::nonNull).toList());
        }
        airDrops.sort(Comparator.comparingInt(AirDrop::getToSpawn));
        if (airDrops.isEmpty()) return null;
        AirDrop airDrop = airDrops.get(0);
        int toSpawnTicks = airDrop.getToSpawn();
        return Pair.of(airDrop, ((tickSpeed * 50L) * toSpawnTicks));
    }

    private AirDrop nextAirDrop() {
        var randomAir = randomAirdropSelector.getRandomItem();
        if (randomAir != null) {
            var air = BAirDropX.getAirdropById(randomAir.getId());
            if (air == null) {
                BAirDropX.getMessage().warning(BAirDropX.translate("timer.ticker.unknown.airdrop"), name, randomAir.getChance());
            } else if (!air.isStarted()) {
                return air;
            }
        }
        return null;
    }

    @Override
    public void onEvent(@NotNull Event event, @NotNull AirDrop airDrop) {
        if (!linkedAirDrops.contains(airDrop.getId()))
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
        return tickSpeed == ticker.tickSpeed && Objects.equals(airdrops, ticker.airdrops) && Objects.equals(current, ticker.current) && Objects.equals(bypassed, ticker.bypassed) && Objects.equals(linkedAirDrops, ticker.linkedAirDrops) && Objects.equals(name, ticker.name) && tickType == ticker.tickType;
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
