package me.dkim19375.backupwell.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
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

    @NotNull
    public static Duration getDurationFromMidnight(final Instant time) {
        return getDuration(time, getMidnightTime(time));
    }

    @NotNull
    public static Duration getDurationUntilMidnight(final Instant time) {
        if (getDurationFromMidnight(time).toDays() > 0) {
            return Duration.ofSeconds((86400 * getDurationFromMidnight(time).toDays()) - getDurationFromMidnight(time).toSeconds());
        }
        return Duration.ofSeconds((86400 * (getDurationFromMidnight(time).toDays() + 1)) - getDurationFromMidnight(time).toSeconds());
    }

    @NotNull
    public static Duration getDuration(final Instant one, final Instant two) {
        return Duration.between(one, two).abs();
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

    @NotNull
    public static Instant getMidnightTime(Instant time) {
        return time.truncatedTo(ChronoUnit.DAYS);
    }

    @Nullable
    public static Duration getTimeUntilExpires(Instant time) {
        if (!getMidnightTime(time).equals(getMidnightTime(Instant.now()))) {
            return null;
        }
        return getDuration(Instant.now(), getMidnightTime(time).plus(Period.ofDays(1)));
    }
}
