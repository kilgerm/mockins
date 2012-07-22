package net.kilger.mockins.util.mocking;

import java.lang.reflect.InvocationTargetException;

import net.kilger.mockins.generator.model.Stubbing;

public interface MockHelper {

    boolean isMock(Object object);
    
    void reset(Object mock);

    void addStub(Object mock, Stubbing stubbing) throws IllegalAccessException, InvocationTargetException;
    String addStubCode(String mockName, Stubbing stubbing);
    
    void prepareMock(Object mock);
    Object prepareMockCode(String targetName);

    String argMatcherAnyCode(Class<?> typeToMock);

    <T> T createMock(Class<T> clazz);
    String createMockCode(Class<?> clazz);


}
