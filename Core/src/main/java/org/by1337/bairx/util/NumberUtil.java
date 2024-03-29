package org.by1337.bairx.util;

public class NumberUtil {
    public static String format(int sec) {
        int hour = sec / 3600;//3600
        int min = sec % 3600 / 60;
        int sec0 = sec % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec0);
    }
}
