package net.kilger.mockins.generator.valueprovider.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.common.LOG;
import net.kilger.mockins.generator.result.model.CompositeInstruction;
import net.kilger.mockins.generator.result.model.StubInstruction;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderRegistry;
import net.kilger.mockins.handler.RetryCallback;
import net.kilger.mockins.handler.RetryCallback.Result;
import net.kilger.mockins.util.ReflectionUtil;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.impl.MockHelperHolder;

public class MockValueProvider implements ValueProvider<Object> {
    
    private MockHelper mockHelper = MockHelperHolder.getMockHelper();

    private final Class<?> clazz;

    private final List<Stubbing> stubbings = new ArrayList<Stubbing>();

    public MockValueProvider(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object createValue() {
        Object mock = mockHelper.createMock(clazz);
        applyStubbings(mock);
        return mock;
    }

    private void applyStubbings(Object mock) {
        for (Stubbing stubbing : stubbings) {
            try {
                mockHelper.addStub(mock, stubbing);
            } catch (IllegalAccessException e) {
                LOG.error("stubbing failed:" + e);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                LOG.error("stubbing failed:" + e);
                e.printStackTrace();
            }
        }
        mockHelper.prepareMock(mock);
    }

    public String code() {
        return mockHelper.createMockCode(clazz);
    }

    public boolean isMocking() {
        return true;
    }
    
    public List<Stubbing> getStubbings() {
        return stubbings;
    }

    public void addAllStubs() {
        LOG.debug("all add stubs");

        /* 
         * Use reflection util to access even protected/default methods.
         * Note that these *might* not be accessible from the client,
         * but we don't know at this point...
         */
        Method[] methods = new ReflectionUtil(clazz).getAllMethods().toArray(new Method[] {});
        
        for (Method method : methods) {

            if (!shouldBeStubbed(method)) {
                // l("not stubbing: " + method);
                continue;
            }
            
            Class<?> returnType = method.getReturnType();
            Class<?>[] methodParamTypes = method.getParameterTypes();
            int methodParamCount = methodParamTypes.length;
            LOG.debug("  stub " + method + " with " + methodParamCount + " params and returntype " + returnType);

            ValueProvider<?> vp = ValueProviderRegistry.providerFor(returnType);

            Stubbing stubbing = new Stubbing(method, vp, methodParamTypes);
            stubbings.add(stubbing);
        }
        
    }

    private boolean shouldBeStubbed(Method method) {
        return needsToBeStubbed(method) && canBeStubbed(method);
    }

    private boolean needsToBeStubbed(Method method) {
        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Void.TYPE)) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }

        if (returnType.isPrimitive()) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }
        
        return true;
    }

    private boolean canBeStubbed(Method method) {
        if (Modifier.isFinal(method.getModifiers())) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }

        // FIXME: rather getDeclaringClass() not Object?
        List<String> disallowed = Arrays.asList("equals", "hashCode", "getClass", "toString");
        if (disallowed.contains(method.getName())) {
            return false;
        }
        
        return true;
    }

    public void removeUneccesaryStubbings(RetryCallback retryCallback) {
        for (int j = 0; j < stubbings.size(); j++) {
            Stubbing stubbingToTest = stubbings.get(j);
            stubbings.set(j, Stubbing.EMPTY);
            Result result = retryCallback.retry();
            if (!result.hasNpe()) {
                LOG.debug("not required: " + stubbingToTest);
            }
            else {
                LOG.debug("required: " + stubbingToTest);
                stubbings.set(j, stubbingToTest);
            }
        }
    }

    public void addStubbingInstructions(CompositeInstruction createMockInstruction, String targetName) {
        for (Stubbing stubbing : stubbings) {
            if (stubbing.isEmpty()) {
                continue;
            }
            StubInstruction stubInstruction = new StubInstruction(targetName, stubbing);
            createMockInstruction.addComponent(stubInstruction);
        }
        
    }

}