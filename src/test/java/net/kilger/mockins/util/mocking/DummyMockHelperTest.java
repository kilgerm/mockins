package net.kilger.mockins.util.mocking;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Test for DummyMockHelper (which is only a test construct itself; 
 * nevertheless, we need it working)
 */
public class DummyMockHelperTest {

    DummyMockHelper classUnderTest = new DummyMockHelper();
    
    public static class A {
        public String m(int x) {
            return "qqq";
        }
    }
    
    public static interface I {
        public String m(int x);
    }
    
    @Test
    public void testCreateMockClass() throws Exception {
        A mock = classUnderTest.createMock(A.class);
        assertTrue(mock instanceof A);
    }
    
    @Test
    public void testCreateMockInterface() throws Exception {
        I mock = classUnderTest.createMock(I.class);
        assertTrue(mock instanceof I);
    }

    @Test
    public void testStubMethodClass() throws Exception {
        A mock = classUnderTest.createMock(A.class);
        
        Method method = A.class.getDeclaredMethod("m", Integer.TYPE);
        Object value = "abc";
        Object[] args = { Integer.valueOf(0) };
        
        classUnderTest.stubMethod(mock, method, value, args);
        
        String r = mock.m(0);
        assertEquals(value, r);
    }
    
    @Test
    public void testStubMethodInterface() throws Exception {
        I mock = classUnderTest.createMock(I.class);
        
        Method method = I.class.getDeclaredMethod("m", Integer.TYPE);
        Object value = "abc";
        Object[] args = { Integer.valueOf(0) };
        
        classUnderTest.stubMethod(mock, method, value, args);
        
        String r = mock.m(0);
        assertEquals(value, r); 
    }
    
    
    
}
