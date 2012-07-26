package net.kilger.mockins.util.mocking;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.GivenValueProvider;

import org.junit.Test;
import org.mockito.Mockito;

public class MockitoHelperTest {
    
    private static final String PARAM = "P";

    private static final String VALUE = "X";

    private static final Integer INT_VALUE = 124;

    MockitoHelper classUnderTest = new MockitoHelper();
    
    @Test
    public void testCallMethod() throws Exception {
        A mock = Mockito.mock(A.class);
        
        Method m = A.class.getMethod("voidMethod");
        classUnderTest.callMethod(mock, m);
        
        Mockito.verify(mock).voidMethod();
    }
    
    @Test
    public void testCallMethodOneArg() throws Exception {
        A mock = Mockito.mock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithOneArg", String.class);
        classUnderTest.callMethod(mock, m, PARAM);
        
        Mockito.verify(mock).voidMethodWithOneArg(PARAM);
    }
    
    @Test
    public void testCallMethodTwoArgs() throws Exception {
        A mock = Mockito.mock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithTwoArgs", String.class, Integer.TYPE);
        classUnderTest.callMethod(mock, m, "", 0);
        
        Mockito.verify(mock).voidMethodWithTwoArgs(Mockito.any(String.class), Mockito.anyInt());
    }
    
    @Test(expected=AssertionError.class)
    public void testCallMethodOneArgNotMatchingParam() throws Exception {
        A mock = Mockito.mock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithOneArg", String.class);
        classUnderTest.callMethod(mock, m, Mockito.eq(PARAM));
        
        Mockito.verify(mock).voidMethodWithOneArg(Mockito.eq("NOT_" + PARAM)); // must throw error, since different param
    }

    @Test
    public void testStubMethodDirect() throws Exception {
        A mock = Mockito.mock(A.class);
        Method method = A.class.getMethod("stringMethodWithOneArg", new Class<?>[] { Object.class });
        
        classUnderTest.stubMethod(mock, method, VALUE, new Object[] { Mockito.anyObject() });
        
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));
    }
    
    @Test
    public void testStubMethodPrimitive() throws Exception {
        A mock = Mockito.mock(A.class);
        Method method = A.class.getMethod("intMethodWithOneArg", new Class<?>[] { Integer.TYPE });
        
        classUnderTest.stubMethod(mock, method, INT_VALUE, new Object[] { Mockito.anyInt() });
        
        assertEquals(INT_VALUE, (Integer) mock.intMethodWithOneArg(1));
    }
    
    private static interface A {
        void voidMethod();
        void voidMethodWithOneArg(String p1);
        void voidMethodWithTwoArgs(String p1, int p2);

        String stringMethod();
        String stringMethodWithOneArg(Object p1);
        int intMethodWithOneArg(int p1);
    }

    @Test
    public void testCreateMock() throws Exception {
        Object mock = classUnderTest.createMock(A.class);

        assertTrue(mock instanceof A);
    }
    
    @Test
    public void testIsMock() throws Exception {
        A mock = Mockito.mock(A.class);
        A real = new A() {

            public void voidMethod() {
            }

            public void voidMethodWithOneArg(String p1) {
            }

            public void voidMethodWithTwoArgs(String p1, int p2) {
            }

            public String stringMethod() {
                return null;
            }
            
            public String stringMethodWithOneArg(Object p1) {
                return null;
            }

            public int intMethodWithOneArg(int p1) {
                return 0;
            }
            
        };
        
        assertTrue(classUnderTest.isMock(mock));
        assertFalse(classUnderTest.isMock(real));
    }

    @Test
    public void testAddStub() throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        A mock = Mockito.mock(A.class);
        
        Method method = A.class.getMethod("stringMethodWithOneArg", Object.class);
        
        ValueProvider<?> valueProvider = new GivenValueProvider(VALUE);
        Class<?>[] methodParamTypes = method.getParameterTypes();
        
        Stubbing stubbing = new Stubbing(method, valueProvider, methodParamTypes);
        
        classUnderTest.addStub(mock, stubbing);
        
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));

        // again
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));
    }
}
