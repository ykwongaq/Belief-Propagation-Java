package wyk.bp.utils;

public class Log {

    public static String genLogMsg(final String className, final String message) {
        return "[" + className + "] " + message;
    }

    public static String genLogMsg(final Class<?> clazz, final String message)  {
        return Log.genLogMsg(clazz.getSimpleName(), message);
    }
}
