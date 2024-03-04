package org.by1337.bairx.hook.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.location.generator.GeneratorSetting;

import java.util.Map;

public class RegionManager {
    public static void removeRegion(AirDrop airDrop) {
        World world;
        if (airDrop.getLocation() != null)
            world = airDrop.getLocation().getWorld();
        else world = airDrop.getWorld();
        if (world == null) {
            BAirDropX.getMessage().error("Не удалось установить регион из-за того что мир не указан");
            return;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions.hasRegion(airDrop.getId().getName() + "_region"))
            regions.removeRegion(airDrop.getId().getName() + "_region", RemovalStrategy.REMOVE_CHILDREN);
    }

    public static <T> void setRegion(AirDrop airDrop) {
        ProtectedCuboidRegion rg = createProtectedCuboidRegion(airDrop.getGeneratorSetting(), airDrop.getLocation(), airDrop.getId().getName() + "_region");
        World world = airDrop.getLocation().getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(BukkitAdapter.adapt(world));


        try {
            for (Map.Entry<Flag<?>, String> flagStringEntry : airDrop.getGeneratorSetting().flags.entrySet()) {
                Flag<T> f = (Flag<T>) flagStringEntry.getKey();
                String state = BAirDropX.getMessage().messageBuilder(airDrop.replace(flagStringEntry.getValue()));
                rg.setFlag(f, f.parseInput(FlagContext.create().setInput(state).build()));
            }
        } catch (Exception e) {
            BAirDropX.getMessage().error("Не удалось установить флаг!", e);
        }
        regions.addRegion(rg);
        System.out.println(rg);
    }

    public static ProtectedCuboidRegion createProtectedCuboidRegion(GeneratorSetting setting, Location location, String name) {
        int xMax = location.getBlockX() + setting.regionRadius.getX();
        int yMax = location.getBlockY() + setting.regionRadius.getY();
        int zMax = location.getBlockZ() + setting.regionRadius.getZ();

        int xMin = location.getBlockX() - setting.regionRadius.getX();
        int yMin = location.getBlockY() - setting.regionRadius.getY();
        int zMin = location.getBlockZ() - setting.regionRadius.getZ();

        if (yMax > location.getWorld().getMaxHeight()) {
            yMax = location.getWorld().getMaxHeight();
        }
        if (yMin < location.getWorld().getMinHeight()) {
            yMin = location.getWorld().getMinHeight();
        }
        return new ProtectedCuboidRegion(name,
                BlockVector3.at(xMax, yMax, zMax),
                BlockVector3.at(xMin, yMin, zMin));
    }
}
