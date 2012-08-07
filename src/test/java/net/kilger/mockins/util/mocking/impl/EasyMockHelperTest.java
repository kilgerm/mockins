package net.kilger.mockins.util.mocking.impl;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.FixedValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.GivenValueProvider;

import org.easymock.EasyMock;
import org.junit.Test;

public class EasyMockHelperTest {
    
    private static final String PARAM = "P";

    private static final String VALUE = "X";

    private static final Integer INT_VALUE = 124;
    
    EasyMockHelper classUnderTest = new EasyMockHelper();
    
    @Test
    public void testCallMethod() throws Exception {
        A mock = EasyMock.createMock(A.class);
        
        Method m = A.class.getMethod("voidMethod");
        classUnderTest.callMethod(mock, m);

        EasyMock.replay(mock);
        mock.voidMethod();
        EasyMock.verify(mock);
    }
    
    @Test
    public void testCallMethodOneArg() throws Exception {
        A mock = EasyMock.createMock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithOneArg", String.class);
        classUnderTest.callMethod(mock, m, EasyMock.eq(PARAM));
        
        EasyMock.replay(mock);
        mock.voidMethodWithOneArg(PARAM);
        EasyMock.verify(mock);
    }
    
    @Test
    public void testCallMethodTwoArgs() throws Exception {
        A mock = EasyMock.createMock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithTwoArgs", String.class, Integer.TYPE);
        classUnderTest.callMethod(mock, m, EasyMock.anyObject(String.class), EasyMock.anyInt());
        
        EasyMock.replay(mock);
        mock.voidMethodWithTwoArgs("", -1);
        EasyMock.verify(mock);
    }
    
    @Test(expected=AssertionError.class)
    public void testCallMethodOneArgNotMatchingParam() throws Exception {
        A mock = EasyMock.createMock(A.class);
        
        Method m = A.class.getMethod("voidMethodWithOneArg", String.class);
        classUnderTest.callMethod(mock, m, EasyMock.eq(PARAM));
        
        EasyMock.replay(mock);
        mock.voidMethodWithOneArg("NOT_" + PARAM); // must throw error, since different param
    }

    @Test
    public void testStubMethodDirect() throws Exception {
        A mock = EasyMock.createMock(A.class);
        Method method = A.class.getMethod("stringMethodWithOneArg", new Class<?>[] { Object.class });
        
        classUnderTest.stubMethod(mock, method, VALUE, new Object[] { EasyMock.anyObject() });
        
        EasyMock.replay(mock);
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));
    }
    
    @Test
    public void testStubMethodPrimitive() throws Exception {
        A mock = EasyMock.createMock(A.class);
        Method method = A.class.getMethod("intMethodWithOneArg", new Class<?>[] { Integer.TYPE });
        
        classUnderTest.stubMethod(mock, method, INT_VALUE, new Object[] { EasyMock.anyInt() });
        
        EasyMock.replay(mock);
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
        EasyMock.replay(mock);
        EasyMock.verify(mock);
    }
    
    @Test
    public void testIsMock() throws Exception {
        A mock = EasyMock.createMock(A.class);
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
        A mock = EasyMock.createMock(A.class);
        
        Method method = A.class.getMethod("stringMethodWithOneArg", Object.class);
        
        ValueProvider<?> valueProvider = new GivenValueProvider(VALUE);
        Class<?>[] methodParamTypes = method.getParameterTypes();
        
        Stubbing stubbing = new Stubbing(method, valueProvider, methodParamTypes);
        
        classUnderTest.addStub(mock, stubbing);
        
        EasyMock.replay(mock);
        
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));

        // again
        assertEquals(VALUE, mock.stringMethodWithOneArg(new Object()));
    }
    
    @Test
    public void testAddStubCode() throws SecurityException, NoSuchMethodException {
        String methodName = "voidMethodWithTwoArgs";
        Method method = A.class.getMethod(methodName, String.class, Integer.TYPE);
        
        ValueProvider<?> valueProvider = new FixedValueProvider(null /*unused*/, "$CREATEVALUE");
        Class<?>[] methodParamTypes = method.getParameterTypes();
        Stubbing stubbing = new Stubbing(method, valueProvider, methodParamTypes);
        
        String code = classUnderTest.addStubCode("$MOCK", stubbing);
        
        String expected = "EasyMock.expect(" +
            		"$MOCK."+methodName+"(" +
            			"EasyMock.anyObject(String.class), EasyMock.anyInt()" +
            			")" +
            		").andReturn(" +
            		    "$CREATEVALUE" +
        		")";
        
        assertEquals(expected, code);
        
    }
}
