package net.kilger.mockins.instructor;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstructorFactoryTest {

    private static final String ACTUAL_RESULT = "RESULT";
    
    private InstructorFactory classUnderTest = new InstructorFactory();
    
    static class A {
        public String method(String arg0) {
            return ACTUAL_RESULT;
        }
    }
    
    @Test
    public void testInterceptorDelegatesToRealObject() {
        A a = new A();
        
        A instructor = classUnderTest.create(a);

        String result = instructor.method(null);
        assertEquals(ACTUAL_RESULT, result);
    }
    
    /***
     * test that it is possible to create an interceptor
     * for classes without zero-args constructor
     */
    // TODO: the following tests actuall test that getDesiredConstructor returns a reasonable constructor
    @Test
    public void testInterceptorClassWithoutZeroArgsConstructor() {
        ClassWithoutZeroArgsConstructor obj = new ClassWithoutZeroArgsConstructor(null);
        classUnderTest.create(obj);
    }
    
    @Test
    public void testInterceptorClassWithPrivateZeroArgsConstructor() { 
        ClassWithPrivateZeroArgsConstructor obj = new ClassWithPrivateZeroArgsConstructor(null);
        classUnderTest.create(obj);
    }
    
    @Test
    public void testInterceptorClassWithProtectedConstructor() { 
        ClassWithProtectedConstructor obj = new ClassWithProtectedConstructor(null);
        classUnderTest.create(obj);
    }
    
    @Test
    public void testInterceptorClassWithMultipleConstructors() { 
        ClassWithMultipleConstructors obj = new ClassWithMultipleConstructors(null, null, null);
        classUnderTest.create(obj);
    }
    
    @Test
    public void testInterceptorClassWithPrimitiveArgConstructor() { 
        ClassWithPrimitiveArgConstructor obj = new ClassWithPrimitiveArgConstructor(null, false, ' ', 0, 0.0);
        classUnderTest.create(obj);
    }
    
}
