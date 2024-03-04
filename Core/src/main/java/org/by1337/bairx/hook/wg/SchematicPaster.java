package org.by1337.bairx.hook.wg;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.schematics.SchematicsLoader;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.util.NameKey;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class SchematicPaster {
    private static final Map<NameKey, EditSession> editSessions = new HashMap<>();

    public static void paste(String schematic, AirDrop paster) {
        SchematicsLoader.SchematicsConfig cfg = SchematicsLoader.getSchematicsConfig(schematic);
        Validate.notNull(cfg, "Настройки вставки схематики с именем %s не найдены!", schematic);
        File schematicFile = SchematicsLoader.getSchematic(cfg.getSchemFileName());
        Validate.notNull(schematicFile, "Файл схематики %s не найден!", cfg.getClass());

        if (editSessions.containsKey(paster.getId())) {
            throw new IllegalStateException("Вы не можете установить более одной схематики за раз!");
        }
        Validate.notNull(paster.getLocation(), "Аирдроп ещё не нашёл локацию для спавна! Некуда вставлять схематику!");

        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            Validate.notNull(format, "WE/FAWE не может прочитать схематику такого формата!");
            ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));

            Clipboard clipboard = reader.read();

            Location location = paster.getLocation();
            Validate.notNull(location.getWorld());
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
            EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);

            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(
                                    location.getBlockX() + cfg.getOffsets().getZ(),
                                    location.getBlockY() + cfg.getOffsets().getY(),
                                    location.getBlockZ() + cfg.getOffsets().getZ()
                            )
                    ).ignoreAirBlocks(cfg.isIgnoreAirBlocks()).build();

            Operations.complete(operation);
            editSession.close();

            editSessions.put(paster.getId(), editSession);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void undo(AirDrop paster) {
        var session = editSessions.remove(paster.getId());
        if (session != null) {
            EditSession newEditSession = WorldEdit.getInstance().newEditSession(session.getWorld());
            session.undo(newEditSession);
            session.close();
        }
    }
}
