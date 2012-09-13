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


public class NpeHandler implements RetryCallback {

    private final Object classUnderTest;
    private final Method method;
    private final Object[] initialArgs;
    private int argLength;
    
    private MockStubInstructionBuilder mockInstructionBuilder = new MockStubInstructionBuilder(); 

    private List<ParamInfo> paramInfos;
    private List<FieldInfo> fieldInfos;

    private Throwable latestException;

    private ExceptionAnalyzer exceptionAnalyzer = MockinsContext.INSTANCE.getExceptionAnalyzer();

    private volatile boolean invocationCompletedWithoutError;
    boolean timeoutHappened;
    

    /** the maximum number of levels where to stub - use with caution */
    int maxStubLevel = 2;
    
    /** timeout for invocation of the method to test in milliseconds */
    int invocationTimeoutMillis = MockinsContext.INSTANCE.getInvocationTimeoutMillis();

    public NpeHandler(Object classUnderTest, Method method, Object[] initialArgs) {
        this.classUnderTest = classUnderTest;
        this.method = method;
        this.initialArgs = initialArgs;
    }

    public Instruction handle() {
        LOG.debug("start handle NPE");
        init();

        retry();
        if (!isNpe()) {
            LOG.info("no npe - nothing to do");
            return null;
        }

        LOG.info("fill all params and fields");
        fillAllParams();
        fillAllFields();

        retry();
        if (!isNpe()) {
            LOG.info("no npe after subst params and fields");

            LOG.info("removing unneccessary params and fields");
            removeNullableParams();
            removeNullableFields();
            
            return resultInstruction();
        }

        LOG.info("still npe after subst params and fields");

        int stubLevel = 1;

        do {
            LOG.info("stub all level " + stubLevel);
            createAllStubbingsForParams(stubLevel);
            createAllStubbingsForFields(stubLevel);

            retry();
            if (!isNpe()) {
                LOG.info("ok now after stubbing all");
                verifyAllStubsReproducable();

                LOG.debug("removing unneccessary params and fields");
                removeNullableParams();
                removeNullableFields();

                LOG.debug("removing unneccessary stubs for params and fields");
                removeUnneccesaryStubbingsForParams();
                removeUnneccesaryStubbingsForFields();

                return resultInstruction();
            }
            
            LOG.info("still NPE after stub of level " + stubLevel);
            stubLevel++;
        } while (stubLevel <= maxStubLevel);

        
        // if still exception: throw latest exception
        if (latestException != null) {
            LOG.error("stubbed all up to level " + stubLevel + " but still exception - nothing we can do...");
            
            if (latestException instanceof RuntimeException) {
                throw (RuntimeException) latestException;
            }
            else {
                // if no RuntimeException, wrap it first
                // TODO: this is questionable. but do we want "throws Throwable" as signature?
                throw new RuntimeException(latestException);
            }
        }
        else {
            // otherwise: log problem and return null
            
            if (timeoutHappened) {
                LOG.error("last invokation of test method did not return before timeout");
            }
            else {
                LOG.error("mocking/stubbing failed due to other reasons");
            }
            return null;
        }
    }

    private Instruction resultInstruction() {
        return mockInstructionBuilder.buildInstruction(fieldInfos, paramInfos, method);
    }

    private void removeUnneccesaryStubbingsForParams() {
        removeUnnecessaryStubbingsFor(paramInfos);
    }
    
    private void removeUnneccesaryStubbingsForFields() {
        removeUnnecessaryStubbingsFor(fieldInfos);
    }

    private void removeUnnecessaryStubbingsFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo substitutableObjectInfo : substitutableObjectInfos) {
            if (substitutableObjectInfo.getValueProvider() instanceof MockValueProvider) {
                MockValueProvider mvp = (MockValueProvider) substitutableObjectInfo.getValueProvider();
                mvp.removeUneccesaryStubbings(this);
            }
            
        }
    }

    private void verifyAllStubsReproducable() throws AssertionError {
        retry();
        if (isNpe()) {
            throw new AssertionError("something went wrong");
        }
    }

    private void createAllStubbingsForParams(int maxStubLevel) {
        createAllStubbingsFor(paramInfos, maxStubLevel);
    }
    
    private void createAllStubbingsForFields(int maxStubLevel) {
        createAllStubbingsFor(fieldInfos, maxStubLevel);
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
        trySubstituteNullValueProvider(paramInfos);
    }

    private void removeNullableFields() {
        // try out, which fields are actually mandatory
        trySubstituteNullValueProvider(fieldInfos);
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
        for (SubstitutableObjectInfo paramInfo : paramInfos) {
            paramInfo.createValue();
        }
        for (SubstitutableObjectInfo fieldInfo : fieldInfos) {
            fieldInfo.createValue();
        }
    }

    private void insertFields() {
        for (FieldInfo fieldInfo : fieldInfos) {
            Field field = fieldInfo.getField();
            field.setAccessible(true); // ignore accessibility
            LOG.debug("setting field " + field + " to " + fieldInfo.getValueProvider().code());
            try {
                field.set(classUnderTest, fieldInfo.getCurrentValue());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillAllParams() {
        fillAllFor(paramInfos);
    }

    private void fillAllFields() {
        fillAllFor(fieldInfos);
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
        method.setAccessible(true);

        argLength = initialArgs.length;
        
        collectParamInfo();
        
        collectFieldInfo();
    }

    private void collectFieldInfo() {
        fieldInfos = new ArrayList<FieldInfo>();
        Class<? extends Object> clazz = classUnderTest.getClass();
        
        for (Field candidateField : clazz.getDeclaredFields()) { // TODO: inherited fields! getFields() won't do either, fields might not be accessible in legacy code
            if (isInteresting(candidateField)) {
                candidateField.setAccessible(true);
                Object initialValue = null;
                try {
                    initialValue = candidateField.get(classUnderTest);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } 
                FieldInfo fieldInfo = new FieldInfo(candidateField, initialValue); 
                fieldInfos.add(fieldInfo);
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
        Class<?>[] paramTypes = method.getParameterTypes();

        paramInfos = new ArrayList<ParamInfo>();
        for (int i = 0; i < argLength; i++) {
            ParamInfo paramInfo = new ParamInfo(i, paramTypes[i], initialArgs[i]);
            paramInfos.add(paramInfo);
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
            timeoutHappened = !xs.awaitTermination(invocationTimeoutMillis, TimeUnit.MILLISECONDS);

            if (timeoutHappened) {
                LOG.debug("timeout while method invocation - assuming non-termination");
            }
            
            if (invocationCompletedWithoutError) {
                LOG.debug("invocation completed without error");
                latestException = null;                
            }
            
        } catch (InterruptedException e) {
            // this should not happen normally
            throw new Error("interrupted while invoking method", e);
        }
    }

    private void resetStatusFlags() {
        timeoutHappened = false;
        latestException = null;
        invocationCompletedWithoutError = false;
    }

    private Runnable methodInvokator(final Object[] currentArgs) {
        return new Runnable() {

            public void run() {
                try {
                    method.invoke(classUnderTest, currentArgs);
                    invocationCompletedWithoutError = true;
                }
                catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    latestException = cause;
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
        Object[] currentArgs = new Object[argLength];
        for (ParamInfo paramInfo : paramInfos) {
            currentArgs[paramInfo.getIndex()] = paramInfo.getCurrentValue();
        }
        return currentArgs;
    }

    public boolean isNpe() {
        // timeout needs to be treated as "potentially npe"
        return exceptionAnalyzer.isNpe(latestException) || timeoutHappened;
    }

}
