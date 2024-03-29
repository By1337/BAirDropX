package org.by1337.bairx.summon;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.util.PlayerUtil;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.argument.ArgumentIntegerAllowedMath;
import org.by1337.blib.command.argument.ArgumentPlayer;
import org.by1337.blib.command.argument.ArgumentSetList;
import org.by1337.blib.command.requires.RequiresPermission;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SummonerManager {
    public static final NamespacedKey NBT_KEY = new NamespacedKey(BAirDropX.getInstance(), "summoner");
    private static final Map<NameKey, Summoner> summonersMap = new HashMap<>();

    public static void load() {
        summonersMap.clear();
        BAirDropX.getCfg().getMap("summoners", YamlContext.class).values().forEach(cfg -> {
            Summoner summoner = new Summoner(cfg);
            summonersMap.put(summoner.getId(), summoner);
        });
    }

    public static void registerBAirCommands() {
        BAirDropX.registerBAirCommand(
                new Command<CommandSender>("summon").addSubCommand(new Command<CommandSender>("give")
                        .requires(new RequiresPermission<>("bair.summon"))
                        .argument(new ArgumentPlayer<>("player"))
                        .argument(new ArgumentSetList<>("item", () -> summonersMap.keySet().stream().map(NameKey::getName).toList()))
                        .argument(new ArgumentIntegerAllowedMath<>("count", List.of("1", "16", "32", "64"), 1, 64))
                        .executor(((sender, args) -> {
                            Player player = (Player) args.getOrThrow("player", "/bairx summon <player> <item> <?count>");
                            String item = (String) args.getOrThrow("item", "/bairx summon <player> <item> <?count>");
                            int count = (int) args.getOrDefault("count", 1);
                            var summoner = summonersMap.get(new NameKey(item));
                            var itemStack = summoner.getItem();
                            itemStack.setAmount(count);
                            PlayerUtil.giveItems(player, itemStack);
                            if (sender instanceof Player) {
                                BAirDropX.getMessage().sendMsg(sender, Component.translatable("command.summon.successfully"), summoner.getName(), player.getName());
                            }
                        }))
                )
        );
    }

    public static Set<NameKey> summonersKeys() {
        return summonersMap.keySet();
    }

    @Nullable
    public static Summoner getByItem(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) return null;
        var meta = itemStack.getItemMeta();
        if (meta == null) return null;
        var pdc = meta.getPersistentDataContainer();
        if (!pdc.has(NBT_KEY, PersistentDataType.STRING)) return null;
        for (Summoner value : summonersMap.values()) {
            if (value.isIt(itemStack)) return value;
        }
        return null;
    }
}

