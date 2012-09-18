package net.kilger.mockins.handler;

import java.util.List;

import net.kilger.mockins.analysis.model.SubstitutableObjectInfo;
import net.kilger.mockins.common.LOG;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderRegistry;
import net.kilger.mockins.generator.valueprovider.impl.MockValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.NullValueProvider;
import net.kilger.mockins.handler.RetryCallback.Result;

public class NullPointerHandler implements TestExceptionHandler {

    private static final int LEVEL_STUB  = 2;
    private static final int LEVEL_FILLED_FIELDS_AND_PARAMS = 1;
    private static final int LEVEL_INIT = 0;
    
    private int triedLevel = LEVEL_INIT;
    private int stubLevel;
    
    private InvocationContext invocationContext;
    private RetryCallback retryCallback;
    private int maxStubLevel;
    
    public NullPointerHandler(InvocationContext invocationContext, RetryCallback retryCallback, int maxStubLevel) {
        this.triedLevel = NullPointerHandler.LEVEL_INIT;
        this.maxStubLevel = maxStubLevel;
        this.retryCallback = retryCallback;
        this.invocationContext = invocationContext;
    }

    public boolean tryToHandle() {
        
        if (triedLevel  == NullPointerHandler.LEVEL_INIT) {
            fillAllParams();
            fillAllFields();
            triedLevel = NullPointerHandler.LEVEL_FILLED_FIELDS_AND_PARAMS;
            return true;
        }
        
        if (triedLevel == NullPointerHandler.LEVEL_FILLED_FIELDS_AND_PARAMS) {
            stubLevel = 1;
            triedLevel = NullPointerHandler.LEVEL_STUB;
            // run to next step
        }
            
        if (triedLevel == NullPointerHandler.LEVEL_STUB && stubLevel <= maxStubLevel) {
            LOG.info("stub all level " + stubLevel);
            createAllStubbingsForParams(stubLevel);
            createAllStubbingsForFields(stubLevel);
            stubLevel++;
            return true;
        }
        
        // nothing more to do
        return false;
    }

    public void shrink() {
        LOG.info("removing unneccessary params and fields");
        removeNullableParams();
        removeNullableFields();
        
        LOG.info("removing unneccessary stubbings for params and fields with mocks");
        removeUnneccesaryStubbingsForParams();
        removeUnneccesaryStubbingsForFields();
    }

    private void createAllStubbingsForParams(int maxStubLevel) {
        createAllStubbingsFor(invocationContext.getParamInfos(), maxStubLevel);
    }
    
    private void createAllStubbingsForFields(int maxStubLevel) {
        createAllStubbingsFor(invocationContext.getFieldInfos(), maxStubLevel);
    }

    private void createAllStubbingsFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos, int maxStubLevel) {
        for (SubstitutableObjectInfo substitutable : substitutableObjectInfos) {

            if (substitutable.getValueProvider() instanceof MockValueProvider) {
                MockValueProvider vp = (MockValueProvider) substitutable.getValueProvider();
                vp.addAllStubs(maxStubLevel);
                
            } else {
                LOG.debug(substitutable.getDisplayName() + " skip " + substitutable.getCurrentValue());
            }
        }
    }
    
    private void fillAllParams() {
        fillAllFor(invocationContext.getParamInfos());
    }

    private void fillAllFields() {
        fillAllFor(invocationContext.getFieldInfos());
    }

    private void fillAllFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo soi : substitutableObjectInfos) {
            if (!soi.wasInitiallyGiven()) {
                LOG.debug("substitute null " + soi.getDisplayName());
                ValueProvider<?> vp = ValueProviderRegistry.providerFor(soi.getType());
                soi.setValueProvider(vp);
            }
        }
    }

    private void removeNullableParams() {
        // try out, which params are actually mandatory
        trySubstituteNullValueProvider(invocationContext.getParamInfos());
    }

    private void removeNullableFields() {
        // try out, which fields are actually mandatory
        trySubstituteNullValueProvider(invocationContext.getFieldInfos());
    }

    private void trySubstituteNullValueProvider(List<? extends SubstitutableObjectInfo> substitutables) {
        for (SubstitutableObjectInfo substitutable : substitutables) {
            String displayName = substitutable.getDisplayName();
            if (substitutable.wasInitiallyGiven()) {
                LOG.debug("not touching: " + displayName);
                continue;
            }
            ValueProvider<?> originalVp = substitutable.getValueProvider();
            ValueProvider<Object> nullVp = new NullValueProvider();
            substitutable.setValueProvider(nullVp);
            
            Result result = retryCallback.retry();
            
            if (result.hasNpe()) {
                LOG.debug(displayName + ": may not be null");
                substitutable.setValueProvider(originalVp);
            }
            else {
                LOG.debug(displayName + ": may be null");
            }
        }
    }

    private void removeUnneccesaryStubbingsForParams() {
        removeUnnecessaryStubbingsFor(invocationContext.getParamInfos());
    }
    
    private void removeUnneccesaryStubbingsForFields() {
        removeUnnecessaryStubbingsFor(invocationContext.getFieldInfos());
    }

    private void removeUnnecessaryStubbingsFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo substitutableObjectInfo : substitutableObjectInfos) {
            if (substitutableObjectInfo.getValueProvider() instanceof MockValueProvider) {
                MockValueProvider mvp = (MockValueProvider) substitutableObjectInfo.getValueProvider();
                mvp.removeUneccesaryStubbings(retryCallback);
            }
            
        }
    }
    
}
