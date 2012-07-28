package net.kilger.mockins.handler;


public interface RetryCallback {
    
    public static class Result {
        private boolean hasNpe;
        
        public Result(boolean wasNpe) {
            this.hasNpe = wasNpe;
        }
        
        public boolean hasNpe() {
            return hasNpe;
        }
    }
    
    RetryCallback.Result retry();

}