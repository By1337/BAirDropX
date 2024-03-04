package org.by1337.bairx.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.event.EventType;

import java.util.HashMap;
import java.util.UUID;

public class ClickListener implements Listener {
    private final HashMap<UUID, Long> antiDoubleClick = new HashMap<>();

    @EventHandler
    public void PlayerClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            AirDrop air = null;
            for (AirDrop airDrop : BAirDropX.getAirDropMap().values()) {
                if (airDrop.getLocation() != null && airDrop.getLocation().getBlock().equals(block)) {
                    air = airDrop;
                    break;
                }
            }
            if (air == null)
                return;
            e.setCancelled(true);
            if (antiDoubleClick.getOrDefault(e.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis()) {
                return;
            } else {
                antiDoubleClick.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 20L);
            }
            Event event = new Event(air, e.getPlayer(), EventType.CLICK);
            event.registerPlaceholder("{clicked_x}", block::getX);
            event.registerPlaceholder("{clicked_y}", block::getY);
            event.registerPlaceholder("{clicked_z}", block::getZ);
            air.callEvent(event);
        }
    }
}
