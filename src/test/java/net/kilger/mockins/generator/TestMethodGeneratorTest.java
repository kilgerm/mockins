package net.kilger.mockins.generator;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.kilger.mockins.generator.result.model.DeclareTestMethodInstruction;
import net.kilger.mockins.generator.result.model.Instruction;

import org.junit.Test;

public class TestMethodGeneratorTest {
    
    public interface A {
        String getData();
    }
    
    public static class ToTest {
        public void methodToTest(String arg0, A arg1) {
            @SuppressWarnings("unused")
            String s = arg0.toLowerCase() + arg1.getData();
        }
    }
    
    @Test
    public void testGenerate() throws SecurityException, NoSuchMethodException {
        Method method = ToTest.class.getMethod("methodToTest", String.class, A.class);
        TestMethodGenerator classUnderTest = new TestMethodGenerator();

        Instruction result = classUnderTest.generateTestSkeleton(ToTest.class, method);

        assertNotNull(result);
        
        System.out.println(result);
        
        assertTrue(result instanceof DeclareTestMethodInstruction);
        DeclareTestMethodInstruction declareInstruction = (DeclareTestMethodInstruction) result;
        assertEquals("methodToTest", declareInstruction.getNameOfTestedMethod());
        
    }
    
}
