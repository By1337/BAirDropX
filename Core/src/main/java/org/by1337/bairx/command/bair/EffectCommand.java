package org.by1337.bairx.command.bair;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.effect.EffectCreator;
import org.by1337.bairx.effect.EffectLoader;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.argument.ArgumentSetList;
import org.by1337.blib.command.requires.RequiresPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EffectCommand extends Command<CommandSender> {
    private final Map<String, Effect> createdEffects = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    public EffectCommand() {
        super("effect");
        requires(new RequiresPermission<>("bair.effect"));
        addSubCommand(new Start(this));
        addSubCommand(new Stop(this));
    }

    private void clean(){
        for (String str : createdEffects.keySet().toArray(new String[0])) {
            if (createdEffects.get(str).isStopped()){
                createdEffects.remove(str);
            }
        }
    }
    private static class Start extends Command<CommandSender> {
        public Start(EffectCommand owner) {
            super("start");
            requires(new RequiresPermission<>("bair.effect.start"));
            requires((sender -> sender instanceof Player));
            argument(new ArgumentSetList<>("effect", () -> EffectLoader.keys().stream().toList()));
            executor(((sender, args) -> {
                owner.clean();
                String effectS = (String) args.getOrThrow("effect", "Укажите эффект");
                EffectCreator creator = EffectLoader.getByName(effectS);
                Effect effect = creator.create();
                effect.start(((Player) sender).getLocation());
                String id = "effect-" + owner.counter.getAndIncrement();
                BAirDropX.getMessage().sendMsg(sender, "&aЭффект %s запущен!", id);
                owner.createdEffects.put(id, effect);
            }));
        }
    }
    private static class Stop extends Command<CommandSender> {
        public Stop(EffectCommand owner) {
            super("stop");
            requires(new RequiresPermission<>("bair.effect.stop"));
            requires((sender -> sender instanceof Player));
            argument(new ArgumentSetList<>("effect", () -> owner.createdEffects.keySet().stream().toList()));
            executor(((sender, args) -> {
                owner.clean();
                String effectS = (String) args.getOrThrow("effect", "Укажите эффект");
                var ef = owner.createdEffects.remove(effectS);
                ef.stop();
            }));
        }
    }
}
