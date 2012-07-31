package net.kilger.mockins.handler;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kilger.mockins.generator.result.InstructionAssert;
import net.kilger.mockins.generator.result.InstructionTreeWalker;
import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.generator.result.model.StubInstruction;
import net.kilger.mockins.util.LocalVarNamer;
import net.kilger.mockins.util.MethodByNameComparator;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.impl.SimpleLocalVarNamer;
import net.kilger.mockins.util.mocking.DummyMockHelper;

import org.junit.BeforeClass;
import org.junit.Test;


public class NpeHandlerTest {

    public static class B {
        public C getData() {
            return new C();
        }
    }
    
    public static class B2 {
        public C getData() {
            return new C();
        }

        public D getMoreData() {
            return new D();
        }
    }
    
    public static class C {
    }
    
    public static class D {
    }
    
    public static class A1 {
        public void something(B arg0) {
            System.out.println(arg0.toString());
        }
    }
    
    public static class A2 {
        public void something(B arg0, B arg1) {
            // arg0 may even be null
            System.out.println(arg1.toString());
        }
    }
    
    public static class A3 {
        public void something(B arg0) {
            System.out.println(arg0.getData().toString());
        }
    }

    public static class A4 {
        public void something(B2 arg0) {
            System.out.println(arg0.getData().toString());
        }
    }

    public static class F1 {
        C field0;
        
        public void something(B arg0) {
            System.out.println(field0.toString());
        }
    }

    public static class F2 {
        C field0;

        public void something(B arg0) {
            System.out.println(arg0.toString());
        }
    }
    
    // field0 and field2 may not be null
    public static class F2b {
        C field0;
        C field1; // not needed
        B field2;
        
        public void something(B arg0) {
            System.out.println(field0.toString() + field2.toString());
        }
    }

    public static class F3 {
        B field0;
        
        public void something() {
            System.out.println(field0.getData().toString());
        }
    }

    @BeforeClass
    public static void useDummyMockHelper() {
        MockinsContext.INSTANCE.setMockHelper(new DummyMockHelper());
    }
    
    @Test
    public void testMockNullParam() throws SecurityException, NoSuchMethodException {
        A1 a = new A1();
        
        Method method = A1.class.getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(a, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B.class);"));
    }

    @Test
    public void testDontTouchGivenParam() throws SecurityException, NoSuchMethodException {
        A2 a = new A2();
        
        Method method = A2.class.getMethod("something", new Class<?>[] {B.class, B.class} );
        Object[] initialArgs = { new B(), null };
        NpeHandler classUnderTest = new NpeHandler(a, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B.class);"));
        assertTrue("arg0 should not be dealt with, since given (even if it could be null)", 
                result.contains("param0 = <given>"));
    }

    @Test
    public void testDontMockUnneededParam() throws SecurityException, NoSuchMethodException {
        A2 a = new A2();
        
        Method method = A2.class.getMethod("something", new Class<?>[] {B.class, B.class} );
        Object[] initialArgs = { null, null };
        NpeHandler classUnderTest = new NpeHandler(a, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B.class);"));
        assertTrue("arg0 should not be mocked as it can be null", 
                result.contains("param0 = null"));
    }

    @Test
    public void testStubNullParam() throws SecurityException, NoSuchMethodException {
        A3 a3 = new A3();
        
        Method method = A3.class.getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(a3, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B.class);"));
        assertTrue("result must have stub for getData()", 
                result.contains(".getData()"));
    }
    
    @Test
    public void testDontStubUnneededMethod() throws SecurityException, NoSuchMethodException {
        A4 a4 = new A4();
        
        Method method = A4.class.getMethod("something", new Class<?>[] {B2.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(a4, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B2.class);"));
        assertTrue("result must have stub for getData()", 
                result.contains(".getData()"));
        assertFalse("result should not have stub for getMoreData()", 
                result.contains(".getMoreData()"));
    }

    public static class D1 {
        public C getDataD1() {
            return null;
        }
    }
    
    public static class D2 extends D1 {
        public C getDataD2() {
            return null;
        }
    }
    
    public static class A5 {
        public void something(D2 arg0) {
            System.out.println(arg0.getDataD1().toString() + arg0.getDataD2().toString());
        }
    }

    @Test
    public void testStubInherited() throws SecurityException, NoSuchMethodException {
        A5 obj = new A5();
        Method method = obj.getClass().getMethod("something", new Class<?>[] {D2.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();

        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(result);
        List<Method> stubbedMethods = collectStubbedMethodsSortedByName(instruction);
        assertEquals(2, stubbedMethods.size());
        assertEquals("getDataD1", stubbedMethods.get(0).getName());
        assertEquals("getDataD2", stubbedMethods.get(1).getName());
    }

    public static class D1b {
        protected C getDataD1bProtected() {
            return null;
        }
    }
    public static class D2b extends D1b {
        public C getDataD2b() {
            return null;
        }
    }
    
    public static class A6 {
        public void something(D2b arg0) {
            System.out.println(arg0.getDataD2b().toString() + arg0.getDataD1bProtected().toString());
        }
    }
    
    @Test
    public void testStubInheritedProtected() throws SecurityException, NoSuchMethodException {
        A6 obj = new A6();
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {D2b.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();
        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(result);

        List<Method> stubbedMethods = collectStubbedMethodsSortedByName(instruction);
        
        assertEquals(2, stubbedMethods.size());
        assertEquals("getDataD1bProtected", stubbedMethods.get(0).getName());
        assertEquals("getDataD2b", stubbedMethods.get(1).getName());
    }

    private List<Method> collectStubbedMethodsSortedByName(Instruction instruction) {
        List<Method> stubbedMethods = collectStubbedMethods(instruction);
        Collections.sort(stubbedMethods, new MethodByNameComparator());
        return stubbedMethods;
    }

    private List<Method> collectStubbedMethods(Instruction instruction) {
        final List<Method> stubbedMethods = new ArrayList<Method>();
        new InstructionTreeWalker() {
            @Override
            public void callback(Instruction instruction) {
                if (instruction instanceof StubInstruction) {
                    StubInstruction stubInstruction = (StubInstruction) instruction;
                    stubbedMethods.add(stubInstruction.getStubbing().getMethod());
                }
            }
        }.walkThrough(instruction);
        return stubbedMethods;
    }
    
    // -- fields ----------------------------------------------------------------------------
    
    @Test
    public void testMockNullField() throws SecurityException, NoSuchMethodException {
        F1 obj = new F1();
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("classUnderTest.field0 = $MOCK.CREATE(C.class);"));
    }

    @Test
    public void testDontTouchGivenField() throws SecurityException, NoSuchMethodException {
        F2 obj = new F2();
        obj.field0 = new C();
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("$MOCK.CREATE(B.class);"));
        assertFalse("field0 should not be dealt with, since given", 
                result.contains("field0 = "));
    }

    @Test
    public void testDontTouchGivenField2() throws SecurityException, NoSuchMethodException {
        F2b a = new F2b();
        a.field0 = null;
        a.field1 = new C();
        a.field2 = null;
        
        Method method = a.getClass().getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(a, method, initialArgs);
        
        Instruction handle = classUnderTest.handle();
        String result = handle.toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have mock for field0", 
                result.contains("field0 = $MOCK.CREATE(C.class);"));
        assertTrue("result must have mock for field2", 
                result.contains("field2 = $MOCK.CREATE(B.class);"));
        assertFalse("result must have not mock for field1", 
                result.contains("field1 ="));
    }

    @Test
    public void testDontMockUnneededField() throws SecurityException, NoSuchMethodException {
        F2b a = new F2b();
        a.field0 = null;
        a.field1 = null;
        a.field2 = null;
        
        Method method = a.getClass().getMethod("something", new Class<?>[] {B.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(a, method, initialArgs);
        
        Instruction handle = classUnderTest.handle();
        String result = handle.toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have mock for field0", 
                result.contains("field0 = $MOCK.CREATE(C.class);"));
        assertTrue("result must have mock for field2", 
                result.contains("field2 = $MOCK.CREATE(B.class);"));
        assertFalse("result must have no mock for field1", 
                result.contains("field1 ="));
    }

    @Test
    public void testStubNullField() throws SecurityException, NoSuchMethodException {
        F3 obj = new F3();
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {} );
        Object[] initialArgs = { };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        String result = classUnderTest.handle().toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("field0 = $MOCK.CREATE(B.class);"));
        assertTrue("result must have stub for getData()", 
                result.contains(".getData()"));
    }
    
    public static class F4 {
        B field0;
        B field1; // stubbing not needed
        
        public void something() {
            System.out.println(field0.toString() + field0.getData().toString() + field1.toString());
        }
    }

    @Test
    public void testDontStubUnneededMethodOnField() throws SecurityException, NoSuchMethodException {
        F4 obj = new F4();
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {} );
        Object[] initialArgs = { };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();
        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(result);
        assertTrue("result must have statement for mock", 
                result.contains("field0 = $MOCK.CREATE(B.class);"));
        assertTrue("result must have statement for mock", 
                result.contains("field1 = $MOCK.CREATE(B.class);"));

        new InstructionTreeWalker() {
            @Override
            public void callback(Instruction instruction) {
                if (instruction instanceof StubInstruction) {
                    StubInstruction stubInstruction = (StubInstruction) instruction;
                    
                    // only field0 may have a stub - field1 doesn't need stubbing
                    assertEquals("classUnderTest.field0", stubInstruction.getMockName());
                }
            }
        }.walkThrough(instruction);
    }

    
    public static class C2 {
        public String getData() {
            return null;
        }
    }
    
    public static class B4 {
        public C2 getC2() {
            return null;
        }
    }
    
    public static class A7 {
        public void something(B4 arg0) {
            System.out.println(arg0.getC2().getData().toString());
        }
    }
    
    @Test
    public void testStubLevel2() throws SecurityException, NoSuchMethodException {
        A7 obj = new A7();
        
        LocalVarNamer localVarNamer = new SimpleLocalVarNamer("x", 0);
        MockinsContext.INSTANCE.setLocalVarNamer(localVarNamer);
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {B4.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();
        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(instruction);
        
        assertHasMockAndStub(instruction, "param0", "getC2");
        assertHasMockAndStub(instruction, "x0", "getData");
    }

    public static class A8 {
        public void something(B4 arg0) {
            System.out.println(arg0.getC2().toString());
        }
    }
    
    @Test
    public void testStubLevel2onlyIfRequired() throws SecurityException, NoSuchMethodException {
        A8 obj = new A8();
        
        LocalVarNamer localVarNamer = new SimpleLocalVarNamer("x", 0);
        MockinsContext.INSTANCE.setLocalVarNamer(localVarNamer);
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {B4.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();
        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(instruction);
        
        assertHasMockAndStub(instruction, "param0", "getC2");
        assertNotHasStubForMethod(instruction, "getData");
    }

    public static class C3 {
        public String getMoreData() {
            return null;
        }
        public String getData() {
            return null;
        }
    }
    
    public static class B5 {
        public C3 getC3() {
            return null;
        }
    }
    
    public static class A9 {
        public void something(B5 arg0) {
            System.out.println(arg0.getC3().getData().toString());
        }
    }
    
    @Test
    public void testStubLevel2onlyRequiredMethods() throws SecurityException, NoSuchMethodException {
        A9 obj = new A9();
        
        LocalVarNamer localVarNamer = new SimpleLocalVarNamer("x", 0);
        MockinsContext.INSTANCE.setLocalVarNamer(localVarNamer);
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {B5.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        Instruction instruction = classUnderTest.handle();
        String result = instruction.toString();
        System.err.println(result);
        assertNotNull(instruction);
        
        assertHasMockAndStub(instruction, "param0", "getC3");
        assertHasMockAndStub(instruction, "x0", "getData");
        assertNotHasStubForMethod(instruction, "getMoreData");
    }
    
    
    public static class A10d {
    }
    
    public static class A10c {
        public A10d getA10d() {
            return null;
        }
    }
    
    public static class A10b {
        public A10c getA10c() {
            return null;
        }
    }
    
    public static class A10a {
        public A10b getA10b() {
            return null;
        }
    }
    
    public static class A10 {
        public void something(A10a arg0) {
            System.out.println(arg0.getA10b().getA10c().getA10d().toString());
        }
    }
    
    @Test
    public void testStubLevelNotSufficient() throws SecurityException, NoSuchMethodException {
        A10 obj = new A10();
        
        LocalVarNamer localVarNamer = new SimpleLocalVarNamer("x", 0);
        MockinsContext.INSTANCE.setLocalVarNamer(localVarNamer);
        
        Method method = obj.getClass().getMethod("something", new Class<?>[] {A10a.class} );
        Object[] initialArgs = { null };
        NpeHandler classUnderTest = new NpeHandler(obj, method, initialArgs);
        
        try {
            classUnderTest.maxStubLevel = 1;
            classUnderTest.handle();
        } catch(NullPointerException e) {
            // that's fine - stubbing needs level >= 3            
        }
        try {
            classUnderTest.maxStubLevel = 2;
            classUnderTest.handle();
        } catch(NullPointerException e) {
            // that's fine - stubbing needs level >= 3            
        }
        
        // with level 3, stubbing should work
        classUnderTest.maxStubLevel = 3;
        Instruction instruction = classUnderTest.handle();
        assertNotNull(instruction);
    }
    
    private void assertNotHasStubForMethod(Instruction instruction, final String methodName) {
        InstructionAssert.assertHasNoInstruction(instruction, new InstructionAssert.InstructionMatcher() {

            public boolean matches(Instruction instruction) {
                if (!(instruction instanceof StubInstruction)) {
                    return false;
                }
                StubInstruction stubInstruction = (StubInstruction) instruction;
                return stubInstruction.getStubbing().getMethod().getName().equals(methodName);
            }
            
        });        
    }

    private void assertHasMockAndStub(Instruction instruction, final String mockName, final String stubMethodName) {
        InstructionAssert.assertHasInstruction(instruction, new InstructionAssert.InstructionMatcher() {
            public boolean matches(Instruction instruction) {
                if (!(instruction instanceof StubInstruction)) {
                    return false;
                }
                StubInstruction stubInstruction = (StubInstruction) instruction;
                return mockName.equals(stubInstruction.getMockName()) && 
                       stubMethodName.equals(stubInstruction.getStubbing().getMethod().getName());
            }
        });
    }
    
    
}
