package top.totoro.swing.widget.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Log {
    protected static final DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public static void d(Object target, Object msg) {
        if (target instanceof String) {
            printD(target, msg);
        } else if (target instanceof Class) {
            printD(((Class<?>) target).getSimpleName(), msg);
        } else {
            printD(target.getClass().getSimpleName(), msg);
        }
    }

    public static void d(Object target, String format, Object... msg) {
        d(target, String.format(format, msg));
    }

    public static void e(Object target, Object error) {
        if (target instanceof String) {
            printE(target, error);
        } else if (target instanceof Class) {
            printE(((Class<?>) target).getSimpleName(), error);
        } else {
            printE(target.getClass().getSimpleName(), error);
        }
    }

    public static void e(Object target, String format, Object... msg) {
        e(target, String.format(format, msg));
    }

    protected static void printD(Object target, Object msg) {
        System.out.printf("%s  D/%s : %s\n", dataFormat.format(System.currentTimeMillis()), getTag(target), msg);
    }

    protected static void printE(Object target, Object msg) {
        System.err.printf("%s  E/%s : %s\n", dataFormat.format(System.currentTimeMillis()), getTag(target), msg);
    }

    protected static String getTag(Object target) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        if (traces.length <= 4) {
            return String.valueOf(target);
        }
        return String.format("(%s:%d) %s", traces[4].getFileName(), traces[4].getLineNumber(), target);
    }
}
