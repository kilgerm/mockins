package net.kilger.mockins.handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.kilger.mockins.analysis.ExceptionAnalyzer;
import net.kilger.mockins.analysis.model.FieldInfo;
import net.kilger.mockins.analysis.model.ParamInfo;
import net.kilger.mockins.analysis.model.SubstitutableObjectInfo;
import net.kilger.mockins.common.LOG;
import net.kilger.mockins.generator.MockStubInstructionBuilder;
import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderRegistry;
import net.kilger.mockins.generator.valueprovider.impl.MockValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.NullValueProvider;
import net.kilger.mockins.util.MockinsContext;


public class InvocationHandler implements RetryCallback {

    private MockStubInstructionBuilder mockInstructionBuilder = new MockStubInstructionBuilder(); 

    private ExceptionAnalyzer exceptionAnalyzer = MockinsContext.INSTANCE.getExceptionAnalyzer();

    /** the maximum number of levels where to stub - use with caution */
    int maxStubLevel = 2;
    
    InvocationContext invocationContext = new InvocationContext();
    private NullPointerHandler nullPointerHandler = new NullPointerHandler(invocationContext);
    
    public InvocationHandler(Object classUnderTest, Method method, Object[] initialArgs) {
        this.invocationContext.setClassUnderTest(classUnderTest);
        this.invocationContext.setMethod(method);
        this.invocationContext.setInitialArgs(initialArgs);
    }

    public Instruction handle() {
        LOG.debug("start handle NPE");
        init();

        retry();
        if (!isNpe()) {
            LOG.info("no npe - nothing to do");
            return null;
        }

        nullPointerHandler.init();
        while (true) {
        boolean triedMore = nullPointerHandler.tryMore(this);
            if (!triedMore) {
                LOG.error("nothing more we can do");
                // FIXME: nicer exit
                // if still exception: throw latest exception
                return throwLatestExceptionOrReturnNull();
            }
            retry();
            if (!isNpe()) {
                LOG.info("no npe after subst params and fields");
    
                LOG.info("removing unneccessary params and fields");
                removeNullableParams();
                removeNullableFields();
                
                LOG.info("removing unneccessary stubbings for params and fields with mocks");
                removeUnneccesaryStubbingsForParams();
                removeUnneccesaryStubbingsForFields();
                
                return resultInstruction();
            }
        }
        
    }

    private Instruction throwLatestExceptionOrReturnNull() {
        if (invocationContext.getLatestException() != null) {
            LOG.error("did everything we could but still exception...");
            
            if (invocationContext.getLatestException() instanceof RuntimeException) {
                throw (RuntimeException) invocationContext.getLatestException();
            }
            else {
                // if no RuntimeException, wrap it first
                // TODO: this is questionable. but do we want "throws Throwable" as signature?
                throw new RuntimeException(invocationContext.getLatestException());
            }
        }
        else {
            // otherwise: log problem and return null
            
            if (invocationContext.isTimeoutHappened()) {
                LOG.error("last invokation of test method did not return before timeout");
            }
            else {
                LOG.error("mocking/stubbing failed due to other reasons");
            }
            return null;
        }
    }

    private Instruction resultInstruction() {
        return mockInstructionBuilder.buildInstruction(invocationContext.getFieldInfos(), invocationContext.getParamInfos(), invocationContext.getMethod());
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
                mvp.removeUneccesaryStubbings(this);
            }
            
        }
    }

    void createAllStubbingsForParams(int maxStubLevel) {
        createAllStubbingsFor(invocationContext.getParamInfos(), maxStubLevel);
    }
    
    void createAllStubbingsForFields(int maxStubLevel) {
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
            
            retry();
            
            if (isNpe()) {
                LOG.debug(displayName + ": may not be null");
                substitutable.setValueProvider(originalVp);
            }
            else {
                LOG.debug(displayName + ": may be null");
            }
        }
    }
    
    public Result retry() {
        buildCurrentValues();
        insertFields();
        retryInvocation();
        return new Result(isNpe());
    }

    private void buildCurrentValues() {
        for (SubstitutableObjectInfo paramInfo : invocationContext.getParamInfos()) {
            paramInfo.createValue();
        }
        for (SubstitutableObjectInfo fieldInfo : invocationContext.getFieldInfos()) {
            fieldInfo.createValue();
        }
    }

    private void insertFields() {
        for (FieldInfo fieldInfo : invocationContext.getFieldInfos()) {
            Field field = fieldInfo.getField();
            field.setAccessible(true); // ignore accessibility
            LOG.debug("setting field " + field + " to " + fieldInfo.getValueProvider().code());
            try {
                field.set(invocationContext.getClassUnderTest(), fieldInfo.getCurrentValue());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    void fillAllParams() {
        fillAllFor(invocationContext.getParamInfos());
    }

    void fillAllFields() {
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
    
    private void init() {
        /* 
         * bypassing accessibility is in fact needed in case 
         * the method belongs to a superclass of the instance class.
         */
        invocationContext.getMethod().setAccessible(true);

        invocationContext.setArgLength(invocationContext.getInitialArgs().length);
        
        collectParamInfo();
        
        collectFieldInfo();
    }

    private void collectFieldInfo() {
        invocationContext.setFieldInfos(new ArrayList<FieldInfo>());
        Class<? extends Object> clazz = invocationContext.getClassUnderTest().getClass();
        
        for (Field candidateField : clazz.getDeclaredFields()) { // TODO: inherited fields! getFields() won't do either, fields might not be accessible in legacy code
            if (isInteresting(candidateField)) {
                candidateField.setAccessible(true);
                Object initialValue = null;
                try {
                    initialValue = candidateField.get(invocationContext.getClassUnderTest());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } 
                FieldInfo fieldInfo = new FieldInfo(candidateField, initialValue); 
                invocationContext.getFieldInfos().add(fieldInfo);
            }
        }
    }

    private boolean isInteresting(Field candidateField) {
        boolean isPrimitive = candidateField.getType().isPrimitive();
        boolean isStatic = Modifier.isStatic(candidateField.getModifiers());
        boolean isFinal = Modifier.isFinal(candidateField.getModifiers());
        return !isPrimitive && !isStatic && !isFinal;
    }

    private void collectParamInfo() {
        Class<?>[] paramTypes = invocationContext.getMethod().getParameterTypes();

        invocationContext.setParamInfos(new ArrayList<ParamInfo>());
        for (int i = 0; i < invocationContext.getArgLength(); i++) {
            ParamInfo paramInfo = new ParamInfo(i, paramTypes[i], invocationContext.getInitialArgs()[i]);
            invocationContext.getParamInfos().add(paramInfo);
        }
    }

    private void retryInvocation() {
        final Object[] currentArgs = determineCurrentArgs();
        
        resetStatusFlags();

        retryInvocationWithTimeout(currentArgs);
    }

    private void retryInvocationWithTimeout(final Object[] currentArgs) throws Error {
        ExecutorService xs = Executors.newSingleThreadExecutor();
        xs.submit(methodInvokator(currentArgs));
        
        xs.shutdown();
        try {
            invocationContext.setTimeoutHappened(!xs.awaitTermination(invocationContext.getInvocationTimeoutMillis(), TimeUnit.MILLISECONDS));

            if (invocationContext.isTimeoutHappened()) {
                LOG.debug("timeout while method invocation - assuming non-termination");
            }
            
            if (invocationContext.isInvocationCompletedWithoutError()) {
                LOG.debug("invocation completed without error");
                invocationContext.setLatestException(null);                
            }
            
        } catch (InterruptedException e) {
            // this should not happen normally
            throw new Error("interrupted while invoking method", e);
        }
    }

    private void resetStatusFlags() {
        invocationContext.setTimeoutHappened(false);
        invocationContext.setLatestException(null);
        invocationContext.setInvocationCompletedWithoutError(false);
    }

    private Runnable methodInvokator(final Object[] currentArgs) {
        return new Runnable() {

            public void run() {
                try {
                    invocationContext.getMethod().invoke(invocationContext.getClassUnderTest(), currentArgs);
                    invocationContext.setInvocationCompletedWithoutError(true);
                }
                catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    invocationContext.setLatestException(cause);
                    if (cause instanceof NullPointerException) {
                    }

                } catch (IllegalArgumentException e) {
                    throw new Error("internal error: method parameter mismatch", e);
                } catch (IllegalAccessException e) {
                    throw new Error("internal error: method not accessible", e);
                }
            }
        };
    }

    private Object[] determineCurrentArgs() {
        Object[] currentArgs = new Object[invocationContext.getArgLength()];
        for (ParamInfo paramInfo : invocationContext.getParamInfos()) {
            currentArgs[paramInfo.getIndex()] = paramInfo.getCurrentValue();
        }
        return currentArgs;
    }

    public boolean isNpe() {
        // timeout needs to be treated as "potentially npe"
        return exceptionAnalyzer.isNpe(invocationContext.getLatestException()) || invocationContext.isTimeoutHappened();
    }

}
