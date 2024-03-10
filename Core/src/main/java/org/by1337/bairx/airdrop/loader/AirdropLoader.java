package org.by1337.bairx.airdrop.loader;

import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.airdrop.AirDropMetaData;
import org.by1337.bairx.nbt.io.ByteBuffer;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AirdropLoader {

    public static void load() {
        File file = new File(BAirDropX.getInstance().getDataFolder(), "airdrops");
        if (!file.exists()) {
            file.mkdir();
//            ConfigUtil.trySave("airdrops/default/config.yml");
//            ConfigUtil.trySave("airdrops/default/desc.metadata");
//            ConfigUtil.trySave("airdrops/default/generator_setting.yml");
            return;
        }
        var list = file.listFiles();
        if (list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                try {
                    File meta = new File(f, "desc.metadata");
                    if (!meta.exists()) continue;
                    BAirDropX.debug(() -> "load airdrop from: " + f.getPath());
                    AirDropMetaData metaData = AirDropMetaData.read(new ByteBuffer(Files.readAllBytes(meta.toPath())));

                    AirDrop airDrop = AirdropRegistry.byId(new NameKey(metaData.getType())).getCreator().load(f, metaData);
                    BAirDropX.registerAirDrop(airDrop);

                } catch (IOException e) {
                    BAirDropX.getMessage().error("failed to load airdrop from %s", e, f.getPath());
                }
            }
        }
    }

}
