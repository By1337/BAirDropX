package org.by1337.bairx.util;

import org.by1337.bairx.BAirDropX;

import java.io.File;

public class ConfigUtil {
    public static void trySave(String path){
        path = path.replace('\\', '/');
        if (path.startsWith("/")){
            path = path.substring(1);
        }
        if (!new File(BAirDropX.getInstance().getDataFolder(), path).exists()){
            BAirDropX.getInstance().saveResource(path, false);
        }
    }
}
