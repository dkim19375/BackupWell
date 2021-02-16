package me.dkim19375.backupwell.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static long getDuration(final Instant one, final Instant two, final TimeUnit timeUnit) {
        final Duration duration = Duration.between(one, two).abs();
        switch (timeUnit) {
            case DAYS:
                return duration.toDays();
            case HOURS:
                return duration.toHours();
            case MINUTES:
                return duration.toMinutes();
            case SECONDS:
                return duration.toSeconds();
            case MILLISECONDS:
                return duration.toMillis();
            case MICROSECONDS:
                return duration.toNanos() / 1000;
            case NANOSECONDS:
                return duration.toNanos();
            default:
                return 0;
        }
    }

    public static String formatNumbers(final long number) {
        long newNumber = number;
        int hours;
        int minutes;
        int seconds;
        hours = (int) newNumber / 3600;
        newNumber = newNumber % 3600;
        minutes = (int) newNumber / 60;
        seconds = (int) newNumber % 60;
        if (hours < 1) {
            if (minutes < 1) {
                return seconds + " second" + (seconds == 1 ? "" : "s");
            }
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " and " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        return hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", and " + seconds + " second" + (seconds == 1 ? "" : "s");
    }
}
