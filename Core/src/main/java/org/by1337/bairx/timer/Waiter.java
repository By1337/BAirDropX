package org.by1337.bairx.timer;

import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.timer.strategy.TimerRegistry;
import org.by1337.bairx.util.Validate;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.util.NameKey;

import java.util.*;

public class Waiter implements Timer {
    private final NameKey name;
    private int tickSpeed;
    private final String timeZone;

    public Waiter(YamlContext context) {
        name = Validate.notNull(context.getAsNameKey("name"), "Параметр `name` не указан!");
        tickSpeed = Validate.notNull(context.getAsInteger("tick-speed"), "Параметр `tick-speed` не указан!");
        timeZone = Validate.notNull(context.getAsString("time-zone"), "Параметр `time-zone` не указан!");

        Map<String, List<String>> map = context.getMapList("licked-airdrops", String.class);


    }

//    private static class TimeWait {
//        private final List<String> times;
//        private final String timeZone;
//        private final NameKey airdrop;
//
//    }

    @Override
    public NameKey name() {
        return null;
    }

    @Override
    public void tick(long currentTick) {

    }

    @Override
    public TimerRegistry getType() {
        return null;
    }

    private long getTimeMils() {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZone);
        calendar.setTimeZone(timeZone);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return (hour * 3600 + minute * 60 + second) * 1000L;
    }


    private long getMilsTo(String time) {
        long timeTo = 0;
        String[] arr = time.split(":");
        System.out.println(Arrays.toString(arr));
        if (arr.length >= 1) {
            timeTo += Long.parseLong(arr[0]) * 3600;
        }
        if (arr.length >= 2) {
            timeTo += Long.parseLong(arr[1]) * 60;
        }
        if (arr.length >= 3) {
            timeTo += Long.parseLong(arr[2]);
        }
        timeTo = timeTo * 1000L;
        return timeTo - getTimeMils();
    }
}
