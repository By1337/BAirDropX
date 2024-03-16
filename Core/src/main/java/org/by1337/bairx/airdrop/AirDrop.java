package org.by1337.bairx.airdrop;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.by1337.bairx.effect.Effect;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventType;
import org.by1337.bairx.inventory.InventoryManager;
import org.by1337.bairx.location.generator.GeneratorSetting;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.blib.util.NameKey;
import org.by1337.blib.util.SpacedNameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Main interface for the airdrop.
 */
public interface AirDrop extends Placeholderable {
    /**
     * Should return true if the airdrop can spawn.
     * Used in timers to skip airdrops that cannot spawn.
     *
     * @return true if the airdrop can spawn, false otherwise.
     */
    boolean canSpawn();

    /**
     * Should return the world where the airdrop spawns.
     *
     * @return the world where the airdrop spawns.
     */
    World getWorld();

    /**
     * Contains the logic for the airdrop tick.
     * This method is automatically called by timers.
     */
    void tick();

    /**
     * Returns the set of listeners subscribed to this airdrop.
     *
     * @return the set of listeners subscribed to this airdrop.
     */
    Set<SpacedNameKey> getSignedListeners();

    /**
     * Checks if the airdrop is currently started.
     *
     * @return true if the airdrop is started, false otherwise.
     */
    boolean isStarted();

    /**
     * Should return the ID of the airdrop.
     *
     * @return the ID of the airdrop.
     */
    NameKey getId();

    /**
     * Specifies whether the default timer should call the tick for this airdrop.
     *
     * @return true if the default timer should call the tick, false otherwise.
     */
    boolean isUseDefaultTimer();

    /**
     * Returns the location where the airdrop will spawn.
     *
     * @return the location where the airdrop will spawn.
     */
    Location getLocation();

    /**
     * Returns the inventory manager.
     *
     * @return the inventory manager.
     */
    InventoryManager getInventoryManager();

    /**
     * Passes this event to all listeners subscribed to this airdrop.
     * Also passes the event to {@link org.by1337.bairx.event.EventListenerManager}.
     *
     * @param event the event to be passed.
     */
    void callEvent(@NotNull Event event);

    /**
     * Creates an event and passes it to {@link AirDrop#callEvent(Event)}.
     *
     * @param player    the player associated with the event.
     * @param eventType the type of event.
     */
    void callEvent(@Nullable Player player, EventType eventType);

    /**
     * Called when something changes and changes need to be saved.
     *
     */
    void save() throws Exception;

    /**
     * Calls {@link AirDrop#save()}.
     */
    void trySave();

    /**
     * This method is called when someone tries to force start the airdrop with a command.
     *
     * @param sender   the command sender.
     * @param location the location where the airdrop will start.
     */
    void forceStart(CommandSender sender, @Nullable Location location);

    /**
     * This method is called when someone tries to force stop the airdrop with a command.
     */
    void forceStop();

    /**
     * Should return the location generator settings.
     *
     * @return the location generator settings.
     */
    GeneratorSetting getGeneratorSetting();

    /**
     * Should start and save the effect.
     *
     * @param id     the unique ID of the effect.
     * @param effect the effect to be started.
     */
    void addEffectAndStart(String id, Effect effect);

    /**
     * Should stop the effect.
     *
     * @param id the unique ID specified during creation.
     */
    void stopEffect(String id);

    /**
     * Should stop all effects.
     */
    void stopAllEffects();

    /**
     * Should create a temporary mirror of the current airdrop with the specified ID.
     * The mirrored airdrop should not affect the original configs.
     * The mirror MUST self-destruct after the event is completed.
     *
     * @param id the ID of the temporary mirror.
     * @return the temporary mirrored airdrop.
     */
    AirDrop createTempMirror(NameKey id);

    /**
     * Executes custom commands supported by this airdrop.
     *
     * @param event the event associated with the command.
     * @param cmd   the command to be executed.
     */
    void executeCustomCommand(Event event, String cmd);

    /**
     * Should return a list of auto completions for the command.
     * Used in the /bair execute command.
     *
     * @param event the event associated with the command.
     * @param cmd   the command.
     * @return a list of auto completions for the command.
     */
    List<String> tabCompleter(Event event, String cmd);

    /**
     * To ensure that the custom command lands in {@link AirDrop#executeCustomCommand(Event, String)}.
     *
     * @param command the command.
     * @return true if the command exists, false otherwise.
     */
    boolean hasCommand(String command);

    /**
     * Fully stops all processes of the object.
     */
    void close();

    /**
     * Should return true if {@link AirDrop#close()} has been called.
     *
     * @return true if {@link AirDrop#close()} has been called, false otherwise.
     */
    boolean isUnloaded();
}
