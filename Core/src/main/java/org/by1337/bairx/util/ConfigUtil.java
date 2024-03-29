package org.by1337.bairx.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.by1337.bairx.BAirDropX;

import java.io.File;

public class ConfigUtil {
    @CanIgnoreReturnValue
    public static File trySave(String path){
        path = path.replace('\\', '/');
        if (path.startsWith("/")){
            path = path.substring(1);
        }
        var f = new File(BAirDropX.getInstance().getDataFolder(), path);
        if (!f.exists()){
            BAirDropX.getInstance().saveResource(path, false);
        }
        return f;
    }
}
