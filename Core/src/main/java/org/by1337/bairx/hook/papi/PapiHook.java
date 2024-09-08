package org.by1337.bairx.hook.papi;

import com.google.common.base.Joiner;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;

import org.by1337.bairx.timer.Timer;
import org.by1337.blib.hook.papi.Placeholder;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PapiHook extends PlaceholderExpansion {
    private final Placeholder placeholder;
    private boolean locked;

    public PapiHook() {
        placeholder = new Placeholder("bairx");
        placeholder.addSubPlaceholder(new Placeholder("placeholder")
                .executor((pl, args) -> {
                    String str = Joiner.on('_').join(args);
                    String[] param = new String[2];
                    param[0] = str.split("_")[0];
                    param[1] = str.substring(param[0].length() + 1);
                    AirDrop airDrop = BAirDropX.getAirdropById(new NameKey(param[0]));
                    if (airDrop == null) return "unknown airdrop " + param[0];
                    return airDrop.replace(String.format("{%s}", param[1]));
                }));
        placeholder.addSubPlaceholder(new Placeholder("nearest")
                .executor(((pl, args) -> {
                  var next = getNext();
                  if (next == null) return "none";
                  return next.getId().getName();
                }))
        );
        //
        //
        placeholder.addSubPlaceholder(new Placeholder("nearest_placeholder")
                .executor((pl, args) -> {
                    String str = Joiner.on('_').join(args);
                    AirDrop airDrop = getNext();
                    if (airDrop == null) return "---";
                    return airDrop.replace(String.format("{%s}", str));
                }));
     //   placeholder.build();
    }

    @Nullable
    public AirDrop getNext(){
        List<Pair<AirDrop, Long>> airs = new ArrayList<>();
        for (Timer timer : BAirDropX.getInstance().getTimerManager().getTimers()) {
            var pair = timer.getNearest();
            if (pair != null && pair.getRight() > 0) {
                airs.add(pair);
            }
        }

        for (AirDrop airDrop : BAirDropX.getAirDrops()) {
            long timeToSpawn = airDrop.getToSpawn() * 20L * 50L;
            if (airDrop.isUseDefaultTimer() && timeToSpawn > 0) {
                airs.add(Pair.of(airDrop, timeToSpawn));
            }
        }
        airs.sort(Comparator.comparingLong(Pair::getRight));
        if (airs.isEmpty()) return null;
        return airs.get(0).getLeft();
    }
    public void addSubPlaceholder(Placeholder subPlaceholder){
        if (locked){
            throw new IllegalStateException("Not allowed now");
        }
        placeholder.addSubPlaceholder(subPlaceholder);
    }

    public void build(){
        if (locked){
            throw new IllegalStateException("Already built");
        }
        locked = true;
        placeholder.build();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "BAirDropX";
    }

    @Override
    public @NotNull String getAuthor() {
        return "By1337";
    }

    @Override
    public @NotNull String getVersion() {
        return BAirDropX.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
        return placeholder.process(player, params.split("_"));
    }
}
