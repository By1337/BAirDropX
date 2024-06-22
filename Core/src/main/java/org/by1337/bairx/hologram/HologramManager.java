package org.by1337.bairx.hologram;

import org.bukkit.Location;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.command.CommandRegistry;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.hook.wg.SchematicPaster;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.argument.ArgumentBoolean;
import org.by1337.blib.command.argument.ArgumentSetList;
import org.by1337.blib.command.argument.ArgumentString;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager {
    public static final HologramManager INSTANCE = new HologramManager();
    private final Map<SpacedNameKey, Hologram> hologramMap = new HashMap<>();

    private HologramManager() {

    }

    public void createHologram(String name, String id, AirDrop airDrop) {
        SpacedNameKey spacedNameKey = new SpacedNameKey(airDrop.getId(), new NameKey(id));
        if (hologramMap.containsKey(spacedNameKey)) {
            throw new IllegalStateException("Голограмма с именем " + id + " уже создана!");
        }
        var holo = HologramLoader.create(name);
        holo.spawn(airDrop.getLocation(), airDrop, spacedNameKey);
        hologramMap.put(spacedNameKey, holo);
    }

    public void updateHologram(String id, AirDrop airDrop) {
        SpacedNameKey spacedNameKey = new SpacedNameKey(airDrop.getId(), new NameKey(id));
        var holo = hologramMap.get(spacedNameKey);
        if (holo == null) {
            BAirDropX.getMessage().warning("Голограмма с именем " + id + " не найдена!");
            return;
        }
        holo.update(airDrop);
    }

    public void removeIfExist(String id, AirDrop airDrop) {
        SpacedNameKey spacedNameKey = new SpacedNameKey(airDrop.getId(), new NameKey(id));
        var holo = hologramMap.remove(spacedNameKey);
        if (holo != null) {
            holo.remove();
        }
    }

    public void remove(String id, AirDrop airDrop) {
        SpacedNameKey spacedNameKey = new SpacedNameKey(airDrop.getId(), new NameKey(id));
        var holo = hologramMap.remove(spacedNameKey);
        if (holo == null) {
            BAirDropX.getMessage().warning("Голограмма с именем " + id + " не найдена!");
            return;
        }
        holo.remove();
    }

    public void registerCommands() {
        CommandRegistry.registerCommand(new Command<Event>("[HOLOGRAM]")
                .aliases("[HOLO]")
                .addSubCommand(new Command<Event>("[CREATE]")
                        .argument(new ArgumentString<>("name"))
                        .argument(new ArgumentString<>("id"))
                        .executor(((event, args) -> {
                            String name = (String) args.getOrThrow("name", "[HOLOGRAM] [CREATE] <name> <id>");
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [CREATE] <name> <id>");
                            Validate.notNull(event.getAirDrop().getLocation(), "Аирдроп ещё не нашёл локацию для спавна!");
                            createHologram(name, id, event.getAirDrop());
                        }))
                )
                .addSubCommand(new Command<Event>("[UPDATE]")
                        .argument(new ArgumentString<>("id"))
                        .executor(((event, args) -> {
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [UPDATE] <id>");
                            updateHologram(id, event.getAirDrop());
                        }))
                )
                .addSubCommand(new Command<Event>("[UPDATE_ALL]")
                        .executor(((event, args) -> {
                            for (SpacedNameKey spacedNameKey : hologramMap.keySet()) {
                                if (spacedNameKey.getSpace().equals(event.getAirDrop().getId())){
                                    updateHologram(spacedNameKey.getName().getName(), event.getAirDrop());
                                }
                            }
                        }))
                )
                .addSubCommand(new Command<Event>("[REMOVE]")
                        .argument(new ArgumentString<>("id"))
                        .argument(new ArgumentSetList<>("ifexist", List.of("-ifexist")))
                        .executor(((event, args) -> {
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [REMOVE] <id>");
                            boolean ifexist = (args.get("ifexist") != null);
                            if (ifexist) {
                                removeIfExist(id, event.getAirDrop());
                            } else {
                                remove(id, event.getAirDrop());
                            }
                        }))
                )
        );
    }
}
