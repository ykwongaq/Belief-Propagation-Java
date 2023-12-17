package BeliefPropagation.utils;

/**
 *  Generate log message that follow the certain format. <br/>
 *
 *  The default format is as follows: <br/>
 *  <pre>
 *      [ClassName]: [Message]
 *  </pre>
 *
 * @author WYK
 */
public class Log {

    /**
     * Generate message based on give class name and message
     * @param className Class name as string
     * @param message Message to show
     * @return Formatted message
     */
    public static String genLogMsg(final String className, final String message) {
        return "[" + className + "] " + message;
    }

    /**
     * Generate message based on give class and message
     * @param clazz Target class
     * @param message Message to show
     * @return Formatted message
     */
    public static String genLogMsg(final Class<?> clazz, final String message)  {
        return Log.genLogMsg(clazz.getSimpleName(), message);
    }
}
