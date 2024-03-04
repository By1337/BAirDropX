package org.by1337.bairx.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.effect.EffectCreator;
import org.by1337.bairx.effect.EffectLoader;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.hook.wg.RegionManager;
import org.by1337.bairx.hook.wg.SchematicPaster;
import org.by1337.bairx.schematics.SchematicsLoader;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.argument.*;
import org.by1337.blib.util.NameKey;

public class CommandRegistry {

    private static final Command<Event> commands;

    public static void run(Event event, String command) {
        try {
            commands.process(event, command.split(" "));
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerCommand(Command<Event> command) {
        if (commands.getCommand().contains(command.getCommand())) {
            throw new IllegalArgumentException("command already exist!");
        }
        commands.addSubCommand(command);
    }

    public static void unregisterCommand(String cmd) {
        if (commands.getSubcommands().remove(cmd) == null) {
            throw new IllegalArgumentException("command not-exist!");
        }
    }

    static {
        commands = new Command<>("cmd");

        registerCommand(new Command<Event>("[MESSAGE_ALL]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    BAirDropX.getMessage().sendAllMsg((String) args.getOrThrow("message", "Отсутствует сообщение!"));
                })
        );
        registerCommand(new Command<Event>("[MESSAGE]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    BAirDropX.getMessage().sendMsg(Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!"), (String) args.getOrThrow("message", "Отсутствует сообщение!"));
                })
        );
        registerCommand(new Command<Event>("[TITLE]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    String msg = (String) args.getOrThrow("message", "Отсутствует сообщение!");
                    String[] params = msg.split("\\\\n");
                    BAirDropX.getMessage().sendTitle(Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!"), params[0], params.length > 1 ? params[1] : "");
                })
        );
        registerCommand(new Command<Event>("[TITLE_ALL]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    String msg = (String) args.getOrThrow("message", "Отсутствует сообщение!");
                    String[] params = msg.split("\\\\n");
                    BAirDropX.getMessage().sendAllTitle(params[0], params.length > 1 ? params[1] : "");
                })
        );

        registerCommand(new Command<Event>("[ACTIONBAR_ALL]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    BAirDropX.getMessage().sendAllActionBar((String) args.getOrThrow("message", "Отсутствует сообщение!"));
                })
        );
        registerCommand(new Command<Event>("[ACTIONBAR]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    BAirDropX.getMessage().sendActionBar(Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!"), (String) args.getOrThrow("message", "Отсутствует сообщение!"));
                })
        );
        registerCommand(new Command<Event>("[PLAYER]")
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!").performCommand((String) args.getOrThrow("cmd", "Отсутствует команда!"));
                })
        );
        registerCommand(new Command<Event>("[CONSOLE]")
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) args.getOrThrow("cmd", "Отсутствует команда!"));
                })
        );
        registerCommand(new Command<Event>("[SOUND]")
                .argument(new ArgumentEnumValue<>("sound", Sound.class))
                .argument(new ArgumentFloat<>("volume"))
                .argument(new ArgumentFloat<>("pitch"))
                .executor((event, args) -> {
                    float volume = (float) args.getOrDefault("volume", 1f);
                    float pitch = (float) args.getOrDefault("pitch", 1f);
                    BAirDropX.getMessage().sendSound(Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!"), (Sound) args.getOrThrow("sound", "Отсутствует звук!"), volume, pitch);
                })
        );
        registerCommand(new Command<Event>("[SOUND_ALL]")
                .argument(new ArgumentEnumValue<>("sound", Sound.class))
                .argument(new ArgumentFloat<>("volume"))
                .argument(new ArgumentFloat<>("pitch"))
                .executor((event, args) -> {
                    float volume = (float) args.getOrDefault("volume", 1f);
                    float pitch = (float) args.getOrDefault("pitch", 1f);
                    BAirDropX.getMessage().sendAllSound((Sound) args.getOrThrow("sound", "Отсутствует звук!"), volume, pitch);
                })
        );
        registerCommand(new Command<Event>("[CALL]")
                .argument(new ArgumentString<>("listener"))
                .executor((event, args) -> BAirDropX.getObserverManager().invoke((String) args.getOrThrow("listener", "В команде не указан слушатель для вызова!"), event))
        );
        registerCommand(new Command<Event>("[NEARBY]")
                .argument(new ArgumentInteger<>("radius"))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    int radius = (int) args.getOrThrow("radius", "В команду не указан радиус!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    var loc = Validate.notNull(event.getAirDrop().getLocation(), "Локация аирдропа ещё не определена!");
                    loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream().filter(e -> e instanceof Player).map(e -> (Player) e).forEach(pl -> run(event.getWithPlayer(pl), cmd));
                })
        );
        registerCommand(new Command<Event>("[ERROR]")
                .aliases("[ERR]")
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> BAirDropX.getMessage().error((String) args.getOrThrow("cmd", "Отсутствует сообщение!")))
        );
        registerCommand(new Command<Event>("[LOGGER]")
                .aliases("[LOG]")
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> BAirDropX.getMessage().logger((String) args.getOrThrow("cmd", "Отсутствует сообщение!")))
        );
        registerCommand(new Command<Event>("[EXECUTE_AT]")
                .argument(new ArgumentSetList<>("airdrop", () -> BAirDropX.getAirDropMap().keySet().stream().map(NameKey::getName).toList()))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    AirDrop airDrop = Validate.notNull(BAirDropX.getAirDropMap().get(new NameKey((String) args.getOrThrow("airdrop"))), "аирдроп не найден!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    run(event.getWithAirDrop(airDrop), cmd);
                })
        );
        registerCommand(new Command<Event>("[REPEAT]")
                .argument(new ArgumentInteger<>("count", 1))
                .argument(new ArgumentInteger<>("period", 0))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    int count = (int) args.getOrThrow("count", "В команде не указано количество повторений!");
                    int period = (int) args.getOrThrow("period", "В команде не указана задержка!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    new BukkitRunnable() {
                        int x = 0;

                        @Override
                        public void run() {
                            CommandRegistry.run(event, cmd);
                            if (x >= count) {
                                cancel();
                            }
                            x++;
                        }
                    }.runTaskTimer(BAirDropX.getInstance(), 0, period);
                })
        );
        registerCommand(new Command<Event>("[REPEAT_ASYNC]")
                .argument(new ArgumentInteger<>("count", 1))
                .argument(new ArgumentInteger<>("period", 0))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    int count = (int) args.getOrThrow("count", "В команде не указано количество повторений!");
                    int period = (int) args.getOrThrow("period", "В команде не указана задержка!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    new BukkitRunnable() {
                        int x = 0;

                        @Override
                        public void run() {
                            CommandRegistry.run(event, cmd);
                            if (x >= count) {
                                cancel();
                            }
                            x++;
                        }
                    }.runTaskTimerAsynchronously(BAirDropX.getInstance(), 0, period);
                })
        );
        registerCommand(new Command<Event>("[DELAY]")
                .argument(new ArgumentInteger<>("delay", 0))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    int delay = (int) args.getOrThrow("delay", "В команде не указана задержка!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    Bukkit.getScheduler().runTaskLater(BAirDropX.getInstance(), () -> run(event, cmd), delay);
                })
        );
        registerCommand(new Command<Event>("[DELAY_ASYNC]")
                .argument(new ArgumentInteger<>("delay", 0))
                .argument(new ArgumentStrings<>("cmd"))
                .executor((event, args) -> {
                    int delay = (int) args.getOrThrow("delay", "В команде не указана задержка!");
                    String cmd = (String) args.getOrThrow("cmd", "В команде не указана команда!");
                    Bukkit.getScheduler().runTaskLaterAsynchronously(BAirDropX.getInstance(), () -> run(event, cmd), delay);
                })
        );
        registerCommand(new Command<Event>("[INV_MANAGER]")
                .addSubCommand(new Command<Event>("[OPEN_INVENTORY]")
                        .executor((event, args) -> Validate.notNull(event.getPlayer(), "В ивенте отсутствует игрок!").openInventory(event.getAirDrop().getInventoryManager().getInventory()))
                )
                .addSubCommand(new Command<Event>("[GENERATE_LOOT]")
                        .executor((event, args) -> event.getAirDrop().getInventoryManager().generateItems())
                )
                .addSubCommand(new Command<Event>("[CLEAR_INVENTORY]")
                        .executor((event, args) -> event.getAirDrop().getInventoryManager().release())
                )
        );
        registerCommand(new Command<Event>("[SET_REGION]")
                .executor((event, args) -> RegionManager.setRegion(event.getAirDrop()))
        );
        registerCommand(new Command<Event>("[REMOVE_REGION]")
                .executor((event, args) -> RegionManager.removeRegion(event.getAirDrop()))
        );
        registerCommand(new Command<Event>("[EFFECT_START]")
                .argument(new ArgumentString<>("name"))
                .argument(new ArgumentString<>("id"))
                .executor((event, args) -> {
                    String name = (String) args.getOrThrow("name", "[EFFECT_START] <name> <id>");
                    String id = (String) args.getOrThrow("id", "[EFFECT_START] <name> <id>");
                    EffectCreator creator = EffectLoader.getByName(name);
                    if (creator == null) {
                        throw new CommandException("Эффект %s не найден! Все эффекты %s", name, EffectLoader.keys());
                    }
                    event.getAirDrop().addEffectAndStart(id, creator.create());
                })
        );
        registerCommand(new Command<Event>("[EFFECT_STOP]")
                .argument(new ArgumentString<>("id"))
                .executor((event, args) -> {
                    String id = (String) args.getOrThrow("id", "[EFFECT_STOP] <id>");
                    event.getAirDrop().stopEffect(id);
                })
        );
        registerCommand(new Command<Event>("[SCHEMATIC_PASTE]")
                .aliases("[SCHEM_PASTE]")
                .argument(new ArgumentString<>("id"))
                .executor((event, args) -> {
                    String id = (String) args.getOrThrow("id", "[SCHEMATIC_PASTE] <id>");
                    SchematicPaster.paste(id, event.getAirDrop());
                })
        );
        registerCommand(new Command<Event>("[SCHEMATIC_UNDO]")
                .aliases("[SCHEM_UNDO]")
                .executor((event, args) -> {
                    SchematicPaster.undo(event.getAirDrop());
                })
        );
    }
}
