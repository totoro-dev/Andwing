package top.totoro.swing.widget.util;

public class SLog extends Log {
    private static final boolean release = false;

    public static void d(Object target, Object msg) {
        if (release) {
            return;
        }
        if (target instanceof String) {
            printD(target, msg);
        } else if (target instanceof Class) {
            printD(((Class<?>) target).getSimpleName(), msg);
        } else {
            printD(target.getClass().getSimpleName(), msg);
        }
    }

    public static void d(Object target, String format, Object... msg) {
        if (release) {
            return;
        }
        d(target, String.format(format, msg));
    }

}
