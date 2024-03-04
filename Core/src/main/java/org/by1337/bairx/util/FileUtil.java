package org.by1337.bairx.util;

import org.by1337.bairx.BAirDropX;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileUtil {
    public static List<File> findFiles(File folder, Predicate<File> filter) {
        List<File> list = new ArrayList<>();
        try {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        list.addAll(findFiles(file, filter));
                    }
                }
            } else if (filter.test(folder)) {
                list.add(folder);
            }
        } catch (Exception e) {
            BAirDropX.getMessage().error(e);
        }
        return list;
    }
}
