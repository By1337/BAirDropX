/*
package org.by1337.bairx.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class PacketArmorStand {
    private static final Random RANDOM = new Random();
    private Vector3d position;
    private WrapperPlayServerSpawnLivingEntity spawn;
    private final int id;

    public PacketArmorStand(Vector3d position) {
        this.position = position;
        id = RANDOM.nextInt();
        buildSpawnPacket();
    }

    private void buildSpawnPacket() {
        spawn = new WrapperPlayServerSpawnLivingEntity(
                1337,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                position,
                0,
                0,
                0,
                new Vector3d(0, 0, 0),
                new ArrayList<>() // meta data
        );
    }

    public void sendSpawn(Player player) {
        var channel = PacketEvents.getAPI().getPlayerManager().getChannel(player);
        PacketEvents.getAPI().getProtocolManager().sendPacket(channel, spawn);
    }

}
*/
