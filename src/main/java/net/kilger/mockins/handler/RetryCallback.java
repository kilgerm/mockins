package net.kilger.mockins.handler;

/**
 * Callback interface which runs the method to test again
 * and provides the result.
 */
public interface RetryCallback {
    
    public static class Result {
        private final boolean hasNpe;
        
        public Result(boolean hasNpe) {
            this.hasNpe = hasNpe;
        }
        
        public boolean hasNpe() {
            return hasNpe;
        }
    }
    
    RetryCallback.Result retry();

}