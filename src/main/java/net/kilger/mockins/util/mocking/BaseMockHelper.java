package net.kilger.mockins.util.mocking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.generator.model.Stubbing;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.ClassNamerHolder;

public abstract class BaseMockHelper implements MockHelper {

    protected abstract Object argMatcherAny(Class<?> clazz);
    private ClassNamer classNamer = ClassNamerHolder.getClassNamer();

    public abstract void stubMethod(Object mock, Method method, Object value, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

    protected Object callMethod(Object mock, Method method, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return method.invoke(mock, args);
    }

    @Override
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
    
}
