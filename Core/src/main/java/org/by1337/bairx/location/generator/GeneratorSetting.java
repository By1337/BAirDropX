package org.by1337.bairx.location.generator;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.by1337.bairx.AirDrop;
import org.by1337.bairx.BAirDropX;
import org.by1337.blib.world.BlockPosition;
import org.by1337.blib.world.Vector2D;

import java.util.*;

public class GeneratorSetting {
   public List<BlockPosition> hasBlock = new ArrayList<>();
   public List<BlockPosition> hasNoBlock = new ArrayList<>();
   public List<Material> ignoreBlocks = new ArrayList<>();
   public List<Material> whiteListBlocks = new ArrayList<>(List.of(
           Material.GRASS_BLOCK,
           Material.END_STONE,
           Material.NETHERRACK,
           Material.SOUL_SAND,
           Material.SOUL_SOIL,
           Material.CRIMSON_NYLIUM,
           Material.NETHER_WART_BLOCK,
           Material.WARPED_NYLIUM,
           Material.WARPED_WART_BLOCK,
           Material.SAND
   ));
   public List<Biome> whiteListBiomes = new ArrayList<>(List.of(Biome.values()));
   public BlockPosition offsets = new BlockPosition(0, 1, 0);
   public Vector2D center = new Vector2D(0, 0);
   public int radius = 2500;
   public int maxY = 100;
   public int minY = 30;
   public BlockPosition regionRadius = new BlockPosition(15, 15, 15);
   public Map<Flag<?>, String> flags;


   public void applyDefaultFlags(){
      if (flags == null) flags = new HashMap<>();
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("pvp"), "deny");
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("tnt"), "deny");
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("creeper-explosion"), "deny");
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("wither-damage"), "deny");
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("ghast-fireball"), "deny");
      flags.put(WorldGuard.getInstance().getFlagRegistry().get("greeting"), "&lEntering &r{air-name}&r&l area");
   }
   @SuppressWarnings("unchecked")
   public <T> void applyFlags(ProtectedCuboidRegion rg, AirDrop airDrop){
      try {
         for (Map.Entry<Flag<?>, String> entry : flags.entrySet()) {
            Flag<T> flag = (Flag<T>) entry.getKey();
            String value = entry.getValue();

            rg.setFlag(flag, flag.parseInput(
                    FlagContext.create().setInput(airDrop.replace(value)).build()
            ));
         }
      } catch (Exception e) {
         BAirDropX.getMessage().error("failed to set region flags", e);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof GeneratorSetting setting)) return false;
      return radius == setting.radius && maxY == setting.maxY && minY == setting.minY && Objects.equals(hasBlock, setting.hasBlock) && Objects.equals(hasNoBlock, setting.hasNoBlock) && Objects.equals(ignoreBlocks, setting.ignoreBlocks) && Objects.equals(whiteListBlocks, setting.whiteListBlocks) && Objects.equals(whiteListBiomes, setting.whiteListBiomes) && Objects.equals(offsets, setting.offsets) && Objects.equals(center, setting.center) && Objects.equals(regionRadius, setting.regionRadius);
   }

   @Override
   public int hashCode() {
      return Objects.hash(hasBlock, hasNoBlock, ignoreBlocks, whiteListBlocks, whiteListBiomes, offsets, center, radius, maxY, minY, regionRadius);
   }


   @Override
   public String toString() {
      return "GeneratorSetting{" +
              "hasBlock=" + hasBlock +
              ", hasNoBlock=" + hasNoBlock +
              ", ignoreBlocks=" + ignoreBlocks +
              ", whiteListBlocks=" + whiteListBlocks +
              ", whiteListBiomes=" + whiteListBiomes +
              ", offsets=" + offsets +
              ", center=" + center +
              ", radius=" + radius +
              ", maxY=" + maxY +
              ", minY=" + minY +
              ", regionRadius=" + regionRadius +
              '}';
   }
}
