package net.kilger.mockins.handler;

import net.kilger.mockins.common.LOG;

public class NullPointerHandler {

    private static final int LEVEL_STUB  = 2;
    private static final int LEVEL_FILLED_FIELDS_AND_PARAMS = 1;
    private static final int LEVEL_INIT = 0;
    
    private final InvocationContext invocationContext;
    
    int triedLevel = LEVEL_INIT;
    int stubLevel;
    
    public NullPointerHandler(InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
    }
    
    public void init() {
        triedLevel = NullPointerHandler.LEVEL_INIT;
    }

    boolean tryMore(InvocationHandler invocationHandler) {
        if (triedLevel  == NullPointerHandler.LEVEL_INIT) {
            invocationHandler.fillAllParams();
            invocationHandler.fillAllFields();
            triedLevel = NullPointerHandler.LEVEL_FILLED_FIELDS_AND_PARAMS;
            return true;
        }
        
        if (triedLevel == NullPointerHandler.LEVEL_FILLED_FIELDS_AND_PARAMS) {
            stubLevel = 1;
            triedLevel = NullPointerHandler.LEVEL_STUB;
            // run to next step
        }
            
        if (triedLevel == NullPointerHandler.LEVEL_STUB && stubLevel <= invocationHandler.maxStubLevel) {
            LOG.info("stub all level " + stubLevel);
            invocationHandler.createAllStubbingsForParams(stubLevel);
            invocationHandler.createAllStubbingsForFields(stubLevel);
            stubLevel++;
            return true;
        }
        
        // nothing more to do
        return false;
    }

}
