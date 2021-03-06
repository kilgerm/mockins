package net.kilger.mockins.generator.valueprovider.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.common.LOG;
import net.kilger.mockins.generator.result.model.CompositeInstruction;
import net.kilger.mockins.generator.result.model.LocalVariableInstruction;
import net.kilger.mockins.generator.result.model.StubInstruction;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderRegistry;
import net.kilger.mockins.handler.RetryCallback;
import net.kilger.mockins.handler.RetryCallback.Result;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.impl.ReflectionUtil;
import net.kilger.mockins.util.mocking.MockHelper;

public class MockValueProvider implements ValueProvider<Object> {
    
    private MockHelper mockHelper = MockinsContext.INSTANCE.getMockHelper();

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

    public void addAllStubs(int levelsOfStubbing) {
        LOG.debug("all add stubs");

        /* 
         * Use reflection util to access even protected/default methods.
         * Note that these *might* not be accessible from the client,
         * but we don't know at this point...
         */
        List<Method> methods = new ReflectionUtil(clazz).getAllMethods();
        
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
            if ((levelsOfStubbing > 1) && (vp instanceof MockValueProvider)) {
                // we need to go deeper
                MockValueProvider mvp = (MockValueProvider) vp;
                mvp.addAllStubs(levelsOfStubbing - 1);
            }

            Stubbing stubbing = new Stubbing(method, vp, methodParamTypes);
            stubbings.add(stubbing);
        }
        
    }

    private boolean shouldBeStubbed(Method method) {
        return mockHelper.needsToBeStubbed(method) && mockHelper.canBeStubbed(method);
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

                LOG.debug("recursive stubbings");
                if (stubbingToTest.getValueProvider() instanceof MockValueProvider) {
                    MockValueProvider mvp = (MockValueProvider) stubbingToTest.getValueProvider();
                    mvp.removeUneccesaryStubbings(retryCallback);
                }
            }
        }
    }

    public void addStubbingInstructions(CompositeInstruction createMockInstruction, String targetName) {
        for (Stubbing stubbing : stubbings) {
            if (stubbing.isEmpty()) {
                continue;
            }

            boolean needLocalVar = false;
            ValueProvider<?> vp = stubbing.getValueProvider();
            if (vp instanceof MockValueProvider) {
                MockValueProvider mvp = (MockValueProvider) vp;
                if (!mvp.getStubbings().isEmpty()) {
                    // gonna need a local var, since the mock returned also will have stubs
                    needLocalVar = true;                    
                }
            }
            
            Stubbing stubbingToUse;
            if (needLocalVar) {
                LocalVariableInstruction lvi = new LocalVariableInstruction(stubbing.getMethod().getReturnType(), stubbing.getValueProvider());
                createMockInstruction.addComponent(lvi);
                
                if (vp instanceof MockValueProvider) {
                    MockValueProvider mvp = (MockValueProvider) vp;
                    mvp.addStubbingInstructions(createMockInstruction, lvi.getLocalVarName());
                }
                
                ValueProvider<?> vpx = new FixedValueProvider(null /*unused*/, lvi.getLocalVarName());
                stubbingToUse = new Stubbing(stubbing.getMethod(), vpx, stubbing.getMethodParamTypes());
            }
            else {
                stubbingToUse = stubbing;
            }
            
            StubInstruction stubInstruction = new StubInstruction(targetName, stubbingToUse);
            
            createMockInstruction.addComponent(stubInstruction);
        }
        
    }

}