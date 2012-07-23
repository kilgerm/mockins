package net.kilger.mockins;


import net.kilger.mockins.generator.TestCallInterceptor;
import net.sf.cglib.proxy.Enhancer;

/**
 * <p>This is the base API class to use Mockins.</p>
 * <p>
 * Example:
 * <pre>
 * // This is the class you would like to test
 * MyClass classUnderTest = new MyClass();
 * 
 * // first, create an "instructed" wrapper
 * instructed = Mockins.instruct(classUnderTest);
 * 
 * // then call whatever method you would like to test...
 * instructed.testMethod(null);
 * 
 * // ...this will run testMethod and try to add mocks/stubs for parameters and fields so that the method does not throw a NPE!
 * </pre>
 * <p>
 */
public final class Mockins {

    /**
     * Creates an "instructed" instance. Test calls of methods on this object 
     * will be watched and in case of an NPE, Mockins will search
     * automatically for a working set of mocks/stubs so that the 
     * NPE resolved.
     */
    @SuppressWarnings("unchecked")
    public static <T> T instruct(T classUnderTest) {
        Class<? extends Object> clazz = classUnderTest.getClass();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new TestCallInterceptor<T>(classUnderTest));
        return (T) enhancer.create();
    }
    
    /**
     * Use this to set global Mockins options.
     */
    public static OngoingOptions option() {
        return new OngoingOptions();
    }

    private Mockins() {
        // not instantiable
    }
    
}
