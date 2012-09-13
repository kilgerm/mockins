package net.kilger.mockins.analysis;

/**
 * Gathers information about an exception (or throwable).
 * If your code somehow masks or wraps exceptions, you should
 * use a custom implementation here.
 */
public interface ExceptionAnalyzer {

    /**
     * Must Return true iff <i>exception</i> is a null pointer exception.
     */
    boolean isNpe(Throwable exception);

}
