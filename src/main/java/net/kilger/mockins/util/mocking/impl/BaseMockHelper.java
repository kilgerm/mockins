package net.kilger.mockins.util.mocking.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockHelper;

public abstract class BaseMockHelper implements MockHelper {

    protected abstract Object argMatcherAny(Class<?> clazz);
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    /**
     * List of methods that cannot be mocked.
     */
    protected static final List<Method> DISALLOWED_METHODS = new ArrayList<Method>();
    static {
        try {
            DISALLOWED_METHODS.add(Object.class.getMethod("equals", Object.class));
            DISALLOWED_METHODS.add(Object.class.getMethod("hashCode"));
            DISALLOWED_METHODS.add(Object.class.getMethod("toString"));
            DISALLOWED_METHODS.add(Object.class.getMethod("getClass"));
            
        } catch (Exception e) {
            throw new Error("cannot happen", e);
        }
    }

    public abstract void stubMethod(Object mock, Method method, Object value, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

    protected Object callMethod(Object mock, Method method, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return method.invoke(mock, args);
    }

    public void addStub(Object mock, Stubbing stubbing) throws IllegalAccessException, InvocationTargetException {
        if (!stubbing.isEmpty()) {
            Object[] methodArgMatchers = argMatcherAnyForAll(stubbing.getMethodParamTypes());
    
            /* 
             * Note: We force accessibility of the method to stub here.
             * This is because the method to stub might not be visible,
             * but still be required for stubbing.
             * The stubbing instructions then might additionally require
             * to change visibility - but then you would need that
             * for a working test with mocks anyway.
             */
            stubbing.getMethod().setAccessible(true);

            stubMethod(mock, stubbing.getMethod(), stubbing.createValue(), methodArgMatchers);
        }
    }

    protected Object[] argMatcherAnyForAll(Class<?>[] methodParamTypes) {
        int methodParamCount = methodParamTypes.length;
        Object[] methodArgMatchers = new Object[methodParamCount];
        for (int j = 0; j < methodParamCount; j++) {
            methodArgMatchers[j] = argMatcherAny(methodParamTypes[j]);
        }
        return methodArgMatchers;
    }

    protected String classLiteral(Class<?> clazz) {
        return classNamer.classLiteral(clazz);
    }
    
    public boolean needsToBeStubbed(Method method) {
        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Void.TYPE)) {
            return false; // no need to stub void methods (nice mocking)
        }

        if (returnType.isPrimitive()) {
            return false; // no need to stub methods with primitive return value (nice mocking)
        }
        
        return true;
    }

    public boolean canBeStubbed(Method method) {
        if (Modifier.isFinal(method.getModifiers())) {
            return false;
        }

        if (DISALLOWED_METHODS.contains(method)) {
            return false;
        }
        
        return true;
    }

}
