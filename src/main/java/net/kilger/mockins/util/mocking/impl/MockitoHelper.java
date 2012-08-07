package net.kilger.mockins.util.mocking.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.analysis.model.Stubbing;

import org.mockito.Mockito;

public class MockitoHelper extends BaseMockHelper {

    @Override
    public void stubMethod(Object mock, Method method, Object value, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Mockito.when(callMethod(mock, method, args)).thenReturn(value);
    }

    public boolean isMock(Object object) {
        // heuristically check toString()...
        return object.toString().startsWith("Mock for ");
    }

    public void reset(Object mock) {
        Mockito.reset(mock);
    }

    public void prepareMock(Object mock) {
        // nothing to do in Mockito
    }

    public String prepareMockCode(String targetName) {
        return ""; // nothing to do in Mockito
    }

    @Override
    protected Object argMatcherAny(Class<?> clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return Mockito.anyBoolean();
        }
        if (clazz.equals(Byte.TYPE)) {
            return Mockito.anyByte();
        }
        if (clazz.equals(Short.TYPE)) {
            return Mockito.anyShort();
        }
        if (clazz.equals(Integer.TYPE)) {
            return Mockito.anyInt();
        }
        if (clazz.equals(Long.TYPE)) {
            return Mockito.anyLong();
        }
        if (clazz.equals(Float.TYPE)) {
            return Mockito.anyFloat();
        }
        if (clazz.equals(Double.TYPE)) {
            return Mockito.anyDouble();
        }
        if (clazz.equals(Character.TYPE)) {
            return Mockito.anyChar();
        }
        if (clazz.isPrimitive()) {
            throw new Error("programming error: unknown primitive " + clazz);
        }
        return Mockito.any(clazz);
    }

    public String argMatcherAnyCode(Class<?> clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return "Mockito.anyBoolean()";
        }
        if (clazz.equals(Byte.TYPE)) {
            return "Mockito.anyByte()";
        }
        if (clazz.equals(Short.TYPE)) {
            return "Mockito.anyShort()";
        }
        if (clazz.equals(Integer.TYPE)) {
            return "Mockito.anyInt()";
        }
        if (clazz.equals(Long.TYPE)) {
            return "Mockito.anyLong()";
        }
        if (clazz.equals(Float.TYPE)) {
            return "Mockito.anyFloat()";
        }
        if (clazz.equals(Double.TYPE)) {
            return "Mockito.anyDouble()";
        }
        if (clazz.equals(Character.TYPE)) {
            return "Mockito.anyChar()";
        }
        if (clazz.isPrimitive()) {
            throw new Error("programming error: unknown primitive " + clazz);
        }
        return "Mockito.any(" + classLiteral(clazz) + ")";
    }

    public <T> T createMock(Class<T> clazz) {
        return Mockito.mock(clazz);
    }

    public String createMockCode(Class<?> clazz) {
        return "Mockito.mock(" + classLiteral(clazz) + ")";
    }

    public String addStubCode(String mockName, Stubbing stubbing) {
        StringBuilder stubStatement = new StringBuilder();

        stubStatement
                .append("Mockito.when(")
                .append(mockName)
                .append(".")
                .append(stubbing.getMethod().getName())
                .append("(");

        boolean first = true;
        for (Class<?> methodParamType : stubbing.getMethodParamTypes()) {
            if (first) {
                first = false;
            } else {
                stubStatement.append(", ");
            }
            stubStatement.append(argMatcherAnyCode(methodParamType));
        }

        stubStatement
                .append(")")
                .append(").thenReturn(")
                .append(stubbing.getValueProvider().code())
                .append(")");
        return stubStatement.toString();
    }

}
