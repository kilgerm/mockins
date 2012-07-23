package net.kilger.mockins;


import net.kilger.mockins.generator.TestCallInterceptor;
import net.sf.cglib.proxy.Enhancer;

/**
 * This is the base API to use Mockins.
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
