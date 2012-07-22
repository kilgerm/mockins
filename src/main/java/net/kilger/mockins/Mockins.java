package net.kilger.mockins;


import net.kilger.mockins.generator.TestCallInterceptor;
import net.sf.cglib.proxy.Enhancer;

/**
 * Mockins - mock instructor
 * 
 * Given problem:
 *   - test of yet untested (e.g. legacy) code
 *   - invocation of method to test yields NPE
 *   - reason: some parameter or field introduces a null value
 *   - it's not clear which one, and where stubbing might be necessary
 * 
 * This class aims at doing this job for you!
 * 
 * up-to-date TODOs:
 * 
 * - log4j logging
 * - better stubbable method detections
 * 
 * - ? intelligent value provider for lists
 */
public final class Mockins {

    /**
     * Create a proxy for handling NPEs in naive method invocation
     */
    @SuppressWarnings("unchecked")
    public static <T> T interceptor(T classUnderTest) {
        Class<? extends Object> clazz = classUnderTest.getClass();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new TestCallInterceptor<T>(classUnderTest));
        return (T) enhancer.create();
    }
    
    /**
     * For conveniently setting global Mockins options
     */
    public static OngoingOptions option() {
        return new OngoingOptions();
    }

    private Mockins() {
        // not instantiable
    }
    
}
