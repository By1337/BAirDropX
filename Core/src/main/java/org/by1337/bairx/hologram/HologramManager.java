package org.by1337.bairx.hologram;

import org.bukkit.Location;
import org.by1337.bairx.command.CommandRegistry;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.hook.wg.SchematicPaster;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.argument.ArgumentString;

import java.util.HashMap;
import java.util.Map;

public class HologramManager {
    public static final HologramManager INSTANCE = new HologramManager();
    private final Map<String, Hologram> hologramMap = new HashMap<>();

    private HologramManager() {

    }
    public void createHologram(String name, String id, Location location, Placeholderable placeholderable){
        if (hologramMap.containsKey(id)){
            throw new IllegalStateException("Голограмма с именем " + id + " уже создана!");
        }
        var holo = HologramLoader.create(name);
        holo.spawn(location, placeholderable);
        hologramMap.put(id, holo);
    }
    public void updateHologram(String id, Placeholderable placeholderable){
        var holo = hologramMap.get(id);
        if (holo == null){
            throw new IllegalStateException("Голограмма с именем " + id + " не найдена!");
        }
        holo.update(placeholderable);
    }
    public void remove(String id){
        var holo = hologramMap.remove(id);
        if (holo == null){
            throw new IllegalStateException("Голограмма с именем " + id + " не найдена!");
        }
        holo.remove();
    }
    public void registerCommands(){
        CommandRegistry. registerCommand(new Command<Event>("[HOLOGRAM]")
                .aliases("[HOLO]")
                .addSubCommand(new Command<Event>("[CREATE]")
                        .argument(new ArgumentString<>("name"))
                        .argument(new ArgumentString<>("id"))
                        .executor(((event, args) -> {
                            String name = (String) args.getOrThrow("name", "[HOLOGRAM] [CREATE] <name> <id>");
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [CREATE] <name> <id>");
                            Validate.notNull(event.getAirDrop().getLocation(), "Аирдроп ещё не нашёл локацию для спавна!");
                            createHologram(name, id, event.getAirDrop().getLocation(), event.getAirDrop());
                        }))
                )
                .addSubCommand(new Command<Event>("[UPDATE]")
                        .argument(new ArgumentString<>("id"))
                        .executor(((event, args) -> {
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [UPDATE] <id>");
                            updateHologram(id, event.getAirDrop());
                        }))
                )
                .addSubCommand(new Command<Event>("[REMOVE]")
                        .argument(new ArgumentString<>("id"))
                        .executor(((event, args) -> {
                            String id = (String) args.getOrThrow("id", "[HOLOGRAM] [REMOVE] <id>");
                            remove(id);
                        }))
                )
        );
    }
}
