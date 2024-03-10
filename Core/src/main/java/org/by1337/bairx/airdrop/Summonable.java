package org.by1337.bairx.airdrop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.by1337.bairx.summon.Summoner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Summonable {
    Summoner.Result canBeSummoned(@NotNull Player player, @NotNull Summoner summoner);
    void summon(@NotNull Player player,@Nullable Location location, @NotNull Summoner summoner);
    boolean isSummoned();
    @Nullable
    String getSummonerName();

}
