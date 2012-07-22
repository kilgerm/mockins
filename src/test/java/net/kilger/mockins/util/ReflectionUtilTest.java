package net.kilger.mockins.util;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class ReflectionUtilTest {

    private final Comparator<Method> sortByMethodNameComparator = new MethodByNameComparator();

    @Test
    public void testNoMethods() {
        ReflectionUtil classUnderTest = new ReflectionUtil(NoMethods.class);

        List<Method> results = classUnderTest.getDeclaredMethods("foo");

        assertEquals(0, results.size());
    }

    @Test
    public void testNoMatchingMethods() {
        ReflectionUtil classUnderTest = new ReflectionUtil(NoMatchingMethods.class);

        List<Method> results = classUnderTest.getDeclaredMethods("foo");

        assertEquals(0, results.size());
    }

    @Test
    public void testSingleMatch() {
        ReflectionUtil classUnderTest = new ReflectionUtil(SingleMethod.class);

        List<Method> results = classUnderTest.getDeclaredMethods("foo");

        assertEquals(1, results.size());
    }

    @Test
    public void testMultiMatch() {
        ReflectionUtil classUnderTest = new ReflectionUtil(MultiMethod.class);

        List<Method> results = classUnderTest.getDeclaredMethods("foo");

        assertEquals(3, results.size());
    }

    @Test
    public void testCaseSensitive() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Case.class);
        classUnderTest.setCaseSensitive(false);
        
        List<Method> results = classUnderTest.getDeclaredMethods("foo");
        
        assertEquals(1, results.size());
    }
    
    @Test
    public void testCaseInsensitive() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Case.class);
        classUnderTest.setCaseSensitive(true);
        
        List<Method> results = classUnderTest.getDeclaredMethods("foo");
        
        assertEquals(3, results.size());
    }
    
    @Test
    public void testMixed() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Mixed.class);

        List<Method> results = classUnderTest.getDeclaredMethods("foo");

        assertEquals(2, results.size());
        assertEquals(0, results.get(0).getParameterTypes().length);
        assertEquals(2, results.get(1).getParameterTypes().length);
        assertArrayEquals(new Class<?>[] {String.class, Integer.TYPE}, results.get(1).getParameterTypes());
    }
    
    @Test
    public void testCallMethodByName() throws Exception {
        ReflectionUtil classUnderTest = new ReflectionUtil(SingleMethod.class);
        SingleMethod mock = Mockito.mock(SingleMethod.class);
        
        classUnderTest.callMethod(mock, "foo");
        
        Mockito.verify(mock).foo();
    }

    @Test
    public void testGetDeclaredMethod() throws Exception {
        ReflectionUtil classUnderTest = new ReflectionUtil(SingleMethod.class);
        Method method = classUnderTest.getDeclaredMethod("foo");
        assertEquals("foo", method.getName());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetDeclaredMethodError() throws Exception {
        ReflectionUtil classUnderTest = new ReflectionUtil(NoMatchingMethods.class);
        
        @SuppressWarnings("unused")
        Method method = classUnderTest.getDeclaredMethod("foo");
        fail("exception must be thrown");
    }
    
    @Test
    public void testGetDeclaredMethods() throws Exception {
        ReflectionUtil classUnderTest = new ReflectionUtil(NoMatchingMethods.class);
        assertEquals(0, classUnderTest.getDeclaredMethods("nosuchmethod").size());
    }
    
    private static class NoMethods {
    }

    @SuppressWarnings("unused")
    private static class NoMatchingMethods {
        public void bar() {
        };

        public int bar(int x) {
            return x;
        };
    }

    private static class SingleMethod {
        public void foo() {
        }
    }
    
    @SuppressWarnings("unused")
    private static class Case {
        public void foo() {
        }
        public void FOO() {
        }
        public void Foo() {
        }
    }

    @SuppressWarnings("unused")
    private static class MultiMethod {
        public void foo() {
        }

        public void foo(String a) {
        }

        public int foo(String a, int b) {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    private static class Mixed {
        public void bar() {
        };

        public int bar(int x) {
            return x;
        };

        public void foo() {
        }

        public int foo(String a, int b) {
            return 0;
        }
    }

    @Test
    public void testGetAllMethods_types() {
        ReflectionUtil classUnderTest = new ReflectionUtil(MethodTypes.class);
        List<Method> methods = classUnderTest.getAllMethods();
        assertEquals(6, methods.size());
        Collections.sort(methods, sortByMethodNameComparator);
        int n = 0;
        assertEquals("defaultMethod", methods.get(n++).getName());
        assertEquals("finalMethod", methods.get(n++).getName());
        assertEquals("intMethod", methods.get(n++).getName());
        assertEquals("privateMethod", methods.get(n++).getName());
        assertEquals("protectedMethod", methods.get(n++).getName());
        assertEquals("voidMethod", methods.get(n++).getName());
    }
    
    @SuppressWarnings("unused")
    private static class MethodTypes {
        public void defaultMethod() {
        }
        
        public void voidMethod() {
        }
        
        protected void protectedMethod() {
        }
        
        public int intMethod() {
            return 0;
        }
        
        public final void finalMethod() {
        }
        
        private void privateMethod() {
        }
    }
    
    @Test
    public void testGetAllMethods_recursive() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Level1.class);
        List<Method> methods = classUnderTest.getAllMethods();
        assertEquals(2, methods.size());
        Collections.sort(methods, sortByMethodNameComparator);
        int n = 0;
        assertEquals("baseMethod", methods.get(n++).getName());
        assertEquals("level1Method", methods.get(n++).getName());
    }
    
    @Test
    public void testGetAllMethods_recursive2() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Level2.class);
        List<Method> methods = classUnderTest.getAllMethods();
        
        // Level2 adds one method and overrides another - naive declared methods would add up to 4!
        assertEquals(3, methods.size());
        Collections.sort(methods, sortByMethodNameComparator);
        int n = 0;
        assertEquals("baseMethod", methods.get(n++).getName());
        assertEquals("level1Method", methods.get(n++).getName());
        assertEquals("level2Method", methods.get(n++).getName());
    }
    
    @Test
    public void testGetAllMethods_recursive3() {
        ReflectionUtil classUnderTest = new ReflectionUtil(Level3.class);
        List<Method> methods = classUnderTest.getAllMethods();

        // still only three methods, since Level3 only overrides
        assertEquals(3, methods.size());
        Collections.sort(methods, sortByMethodNameComparator);
        int n = 0;
        assertEquals("baseMethod", methods.get(n++).getName());
        assertEquals("level1Method", methods.get(n++).getName());
        assertEquals("level2Method", methods.get(n++).getName());
    }
    
    @SuppressWarnings("unused")
    private static class Base {
        public void baseMethod() {
        }
    }
    
    @SuppressWarnings("unused")
    private static class Level1 extends Base {
        public void level1Method() {
        }
    }
    
    @SuppressWarnings("unused")
    private static class Level2 extends Level1 {
        public void level2Method() {
        }
        
        @Override
        public void level1Method() {
        }
    }
    
    private static class Level3 extends Level2 {
        @Override
        public void baseMethod() {
        }
        
        @Override
        public void level2Method() {
        }
    }
 
    @SuppressWarnings("unused")
    private static class Signature0 {
        public void signature(String arg0, int arg1) {
        }
    }
    
    @SuppressWarnings("unused")
    private static class Signature1 {
        public int signature(String arg0, int arg1) { // matching
            return 0;
        }
        public int notMatching(String arg0, int arg1) { // different name
            return 0;
        }
        public int signature(Object arg0, int arg1) { // different parameter
            return 0;
        }
        public int signature(String arg0, String arg1) { // different parameter
            return 0;
        }
    }
    
    @SuppressWarnings("unused")
    private static class Signature2 {
        public int notMatching(String arg0, int arg1) { // different name
            return 0;
        }
        public int signature(Object arg0, int arg1) { // different parameter
            return 0;
        }
        public int signature(String arg0, String arg1) { // different parameter
            return 0;
        }
    }
    
    @Test
    public void testFindMethodWithSameSignature() throws Exception {
        ReflectionUtil classUnderTest = new ReflectionUtil(Signature1.class);

        Method signatureToSearchFor = Signature0.class.getDeclaredMethod("signature", String.class, Integer.TYPE);

        List<Method> methods = Arrays.asList(Signature1.class.getMethods());
        assertTrue(classUnderTest.hasMethodWithSameSignature(signatureToSearchFor, methods));

        methods = Arrays.asList(Signature2.class.getMethods());
        assertFalse(classUnderTest.hasMethodWithSameSignature(signatureToSearchFor, methods));
        
    }
    
}
