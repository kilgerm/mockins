package net.kilger.mockins.generator;

import java.lang.reflect.Method;

import net.kilger.mockins.generator.result.model.DeclareTestMethodInstruction;
import net.kilger.mockins.generator.result.model.InstantiateClassInstruction;
import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.generator.result.model.LineCommentInstruction;
import net.kilger.mockins.handler.NpeHandler;

/**
 * Class for generating the code skeleton for a test call to a given method.
 */
public class TestMethodGenerator {
    
    public Instruction generateTestSkeleton(Class<?> clazz, Method method) {

        try {
            DeclareTestMethodInstruction testMethodInstruction = new DeclareTestMethodInstruction(method.getName());

            Object classUnderTest = clazz.newInstance();
            
            Object[] initialArgs = getInitialArgs(method);

            NpeHandler npeHandler = new NpeHandler(classUnderTest, method, initialArgs);
            Instruction stubMockAndCall = npeHandler.handle();
            if (stubMockAndCall == null) {
                throw new RuntimeException("FAIL");
            }

            testMethodInstruction.addComponent(new InstantiateClassInstruction("classUnderTest", clazz));
            testMethodInstruction.addComponent(stubMockAndCall);
            testMethodInstruction.addComponent(new LineCommentInstruction("now perform verifications as needed"));
            return testMethodInstruction;

        } catch (InstantiationException e) {
            throw new RuntimeException("error while generating test for " + clazz.getCanonicalName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("error while generating test for " + clazz.getCanonicalName(), e);
        }
    }

    private Object[] getInitialArgs(Method method) {
        int paramCount = method.getParameterTypes().length;
        Object[] initialArgs = new Object[paramCount]; // implicitly initialized to null
        return initialArgs;
    }

}
