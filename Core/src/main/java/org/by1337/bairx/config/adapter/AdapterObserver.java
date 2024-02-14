package org.by1337.bairx.config.adapter;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.observer.Observer;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.observer.requirement.Requirements;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.ClassAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterObserver implements ClassAdapter<Observer> {
    @Override
    public ConfigurationSection serialize(Observer obj, YamlContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observer deserialize(YamlContext context) {
        String icon = context.getAsString("icon", Material.OBSERVER.name());
        String description = context.getAsString("description", "none");
        String event = Objects.requireNonNull(context.getAsString("event"), "missing event!");
        EventType eventType = Objects.requireNonNull(EventType.getByName(
                event
        ), "unknown event type! " + event);
        Requirements requirements = new Requirements(
                context.getMap("requirement", Requirement.class, new HashMap<>()).values()
        );
        List<String> commands = context.getList("commands", String.class, Collections.emptyList());
        List<String> denyCommands = context.getList("deny-commands", String.class, Collections.emptyList());

        return new Observer(
                new ItemStack(Material.valueOf(icon))// todo add support base head
                , description, requirements, commands, denyCommands, eventType);
    }
}
