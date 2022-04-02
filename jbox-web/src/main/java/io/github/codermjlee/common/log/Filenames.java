package io.github.codermjlee.common.log;

import ch.qos.logback.core.Context;
import io.github.codermjlee.common.util.Strings;

/**
 * @author MJ
 */
public class Filenames {
    public static String filepath(Object obj, Context context) {
        return context.getProperty("LOG_PATH") + filename(obj);
    }

    public static String filename(Object obj) {
        String v = "log";
        if (obj == null) return v;
        String name = Strings.camel2underline(obj.getClass().getSimpleName());
        String[] names = {"info", "warn", "error"};
        for (String s : names) {
            if (name.contains(s)) return s;
        }
        return v;
    }
}
