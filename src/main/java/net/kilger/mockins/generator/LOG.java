package net.kilger.mockins.generator;

public final class LOG {

    private static final Level DEFAULT_LEVEL = Level.DEBUG;

    private enum Level {
        DEBUG(4), INFO(3), WARN(2), ERROR(1);
        
        private int verbosity;
        
        Level(int verbosity) {
            this.verbosity = verbosity;
        }
        
        boolean shouldDisplayMessageOf(Level messageLevel) {
            return isMoreVerboseOrEqualThan(messageLevel);
        }

        private boolean isMoreVerboseOrEqualThan(Level messageLevel) {
            return this.verbosity >= messageLevel.verbosity;
        }
    }
    
    private static Level logLevel = DEFAULT_LEVEL;
    
    public static void l(Level messageLevel, Object... logitems) {
        if (logLevel.shouldDisplayMessageOf(messageLevel)) {
            System.out.print("[" + messageLevel + "] ");
            l(logitems);
        }
    }

    public static void debug(Object... logitems) {
        l(Level.DEBUG, logitems);
    }

    public static void info(Object... logitems) {
        l(Level.INFO, logitems);
    }
    
    public static void warn(Object... logitems) {
        l(Level.WARN, logitems);
    }
    
    public static void error(Object... logitems) {
        l(Level.ERROR, logitems);
    }
    
    private static void l(Object... logitems) {
        for (Object l : logitems) {
            System.out.print(l);
            System.out.print(" ");
        }
        System.out.println();
    }

    private LOG() {
        // not instantiable
    }
    
}
