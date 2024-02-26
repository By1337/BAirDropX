package org.by1337.bairx.observer;

import org.bukkit.inventory.ItemStack;
import org.by1337.bairx.command.CommandRegistry;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.observer.requirement.Requirements;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Observer {
    private final ItemStack icon;
    private final String description;
    private final Requirements requirements;
    private final List<String> commands;
    private final List<String> denyCommands;
    private final EventType eventType;
    @Nullable
    private SpacedNameKey name;

    public Observer(ItemStack icon, String description, Requirements requirements, List<String> commands, List<String> denyCommands, EventType eventType) {
        this.icon = icon;
        this.description = description;
        this.requirements = requirements;
        this.commands = commands;
        this.denyCommands = denyCommands;
        this.eventType = eventType;
    }

    public void update(Event event) {
        if (!requirements.test(event)) {
            for (String command : denyCommands) {
                CommandRegistry.run(event, event.replace(command));
            }
        } else {
            for (String command : commands) {
                CommandRegistry.run(event, event.replace(command));
            }
        }
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public String getDescription() {
        return description;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getDenyCommands() {
        return denyCommands;
    }

    public EventType getEventType() {
        return eventType;
    }

    public @Nullable SpacedNameKey getName() {
        return name;
    }

    public void setName(@Nullable SpacedNameKey name) {
        this.name = name;
    }
}
