package io.github.codermjlee.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * @author MJ
 */
public class Times {
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            // 忽略
        }
    }

    public static Timer delay(Runnable task, long ms) {
        if (task == null) return null;
        if (ms <= 0) {
            task.run();
            return null;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
                timer.cancel();
            }
        }, ms);
        return timer;
    }

    public static boolean waitIn100(Supplier<Boolean> condition, long timeout) {
        return waitIn(100, condition, timeout);
    }

    public static boolean waitIn1000(Supplier<Boolean> condition, long timeout) {
        return waitIn(1000, condition, timeout);
    }

    /**
     *  等待某个条件
     * @param timeout 等待多久会超时
     * @return true等到了，false超时未等到
     */
    private static boolean waitIn(int unit, Supplier<Boolean> condition, long timeout) {
        if (condition == null || timeout < 1) return false;
        while (!condition.get()) {
            if (timeout <= 0) return false;
            sleep(unit);
            timeout -= unit;
        }
        return true;
    }

    public static Long time(String string) {
        return time("yyyy-MM-dd HH:mm:ss", string);
    }

    public static Long time(String fmt, String string) {
        Date date = date(fmt, string);
        return date == null ? null : date.getTime();
    }

    public static Date date(String string) {
        return date("yyyy-MM-dd HH:mm:ss", string);
    }

    public static Date date(String fmt, String string) {
        try {
            return new SimpleDateFormat(fmt).parse(string);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String string(String fmt) {
        return string(fmt, new Date());
    }

    public static String string(String fmt, long time) {
        return string(fmt, new Date(time));
    }

    public static String string(String fmt, Date date) {
        if (fmt == null || date == null) return null;
        return new SimpleDateFormat(fmt).format(date);
    }

    public static String timeString() {
        return timeString(new Date());
    }

    public static String dateTimeString() {
        return dateTimeString(new Date());
    }

    public static String dateString() {
        return dateString(new Date());
    }

    public static String timeString(long time) {
        return timeString(new Date(time));
    }

    public static String dateTimeString(long time) {
        return dateTimeString(new Date(time));
    }

    public static String dateString(long time) {
        return dateString(new Date(time));
    }

    public static String timeString(Date date) {
        return string("HH:mm:ss", date);
    }

    public static String dateTimeString(Date date) {
        return string("yyyy-MM-dd HH:mm:ss", date);
    }

    public static String dateString(Date date) {
        return string("yyyy-MM-dd", date);
    }
}
