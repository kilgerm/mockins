package net.kilger.mockins.instructor;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstructorFactoryTest {

    private static final String VALUE_ORIGINAL = "original";

    private static final String ACTUAL_RESULT = "RESULT";
    
    private InstructorFactory classUnderTest = new InstructorFactory();
    
    static class A {
        String x;
        public A(String x) {
            this.x = x;
        }
        
        public String method() {
            return x;
        }
    }
    
    @Test
    public void testInterceptorDelegatesToRealObject() {
        A obj = new A(VALUE_ORIGINAL);
        assertEquals(VALUE_ORIGINAL, obj.x);
        assertEquals(VALUE_ORIGINAL, obj.method());
        
        A instructor = classUnderTest.create(obj);
        
        // instructor is a proxy, field values are dummy
        assertEquals(null, instructor.x);
        // but methods delegate to the real object
        assertEquals(VALUE_ORIGINAL, instructor.method());
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
    
    @Test
    public void testInterceptorClassWithDefaultConstructor() { 
        ClassWithDefaultConstructor obj = new ClassWithDefaultConstructor();
        classUnderTest.create(obj);
    }
    
}
