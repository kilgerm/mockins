package net.kilger.mockins;

import net.kilger.mockins.api.OngoingOptions;
import net.kilger.mockins.instructor.InstructorFactory;

/**
 * <p>This is the main API for Mockins.</p>
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
     * Adds an "instructor" to an object to test. 
     * Calls of methods on this object will be watched and 
     * in case of an NPE, Mockins will search automatically 
     * for a working set of mocks/stubs that will resolve the NPE.
     */
    public static <T> T instructor(T classUnderTest) {
        return new InstructorFactory().create(classUnderTest);
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
