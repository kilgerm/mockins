package net.kilger.mockins.util.mocking;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.generator.model.Stubbing;

import org.easymock.EasyMock;

public class EasyMockHelper extends BaseMockHelper {

    @Override
    public void stubMethod(Object mock, Method method, Object value, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        callMethod(mock, method, args);
        EasyMock.expectLastCall().andReturn(value).anyTimes();
    } 

    @Override
    public <T> T createMock(Class<T> clazz) {
        return EasyMock.createNiceMock(clazz);
    }

    @Override
    public boolean isMock(Object o) {
        // heuristically check toString()...
        return o.toString().startsWith("EasyMock for");
    }

    @Override
    public Object argMatcherAny(Class<?> clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return EasyMock.anyBoolean();
        }
        if (clazz.equals(Byte.TYPE)) {
            return EasyMock.anyByte();
        }
        if (clazz.equals(Short.TYPE)) {
            return EasyMock.anyShort();
        }
        if (clazz.equals(Integer.TYPE)) {
            return EasyMock.anyInt();
        }
        if (clazz.equals(Long.TYPE)) {
            return EasyMock.anyLong();
        }
        if (clazz.equals(Float.TYPE)) {
            return EasyMock.anyFloat();
        }
        if (clazz.equals(Double.TYPE)) {
            return EasyMock.anyDouble();
        }
        if (clazz.equals(Character.TYPE)) {
            return EasyMock.anyChar();
        }
        if (clazz.isPrimitive()) {
            throw new Error("programming error: unknown primitive " + clazz);
        }
        return EasyMock.anyObject(clazz);
    }

    @Override
    public void prepareMock(Object mock) {
        EasyMock.replay(mock);
    }

    @Override
    public Object prepareMockCode(String targetName) {
        return "EasyMock.replay("+ targetName + ")";
    }

    @Override
    public void reset(Object mock) {
        EasyMock.reset(mock);
    }

    @Override
    public String argMatcherAnyCode(Class<?> clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return "EasyMock.anyBoolean()";
        }
        if (clazz.equals(Byte.TYPE)) {
            return "EasyMock.anyByte()";
        }
        if (clazz.equals(Short.TYPE)) {
            return "EasyMock.anyShort()";
        }
        if (clazz.equals(Integer.TYPE)) {
            return "EasyMock.anyInt()";
        }
        if (clazz.equals(Long.TYPE)) {
            return "EasyMock.anyLong()";
        }
        if (clazz.equals(Float.TYPE)) {
            return "EasyMock.anyFloat()";
        }
        if (clazz.equals(Double.TYPE)) {
            return "EasyMock.anyDouble()";
        }
        if (clazz.equals(Character.TYPE)) {
            return "EasyMock.anyChar()";
        }
        if (clazz.isPrimitive()) {
            throw new Error("programming error: unknown primitive " + clazz);
        }
        return "EasyMock.anyObject(" + classLiteral(clazz) + ")";
    }

    @Override
    public String createMockCode(Class<?> clazz) {
        return "EasyMock.createMock(" + classLiteral(clazz) + ")";
    }

    // FIXME: Test

    @Override
    public String addStubCode(String mockName, Stubbing stubbing) {
        StringBuilder stubStatement = new StringBuilder();

        stubStatement
                .append("EasyMock.expect(")
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
                .append(").andReturn(")
                .append(stubbing.getValueProvider().code())
                .append(")");
        return stubStatement.toString();
    }

}
