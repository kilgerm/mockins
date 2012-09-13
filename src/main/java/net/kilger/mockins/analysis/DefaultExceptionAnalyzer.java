package net.kilger.mockins.analysis;

public class DefaultExceptionAnalyzer implements ExceptionAnalyzer {

    public boolean isNpe(Throwable exception) {
        if (exception instanceof NullPointerException) {
            return true;
        }
        return false;
    }

}
