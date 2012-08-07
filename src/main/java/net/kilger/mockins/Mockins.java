package net.kilger.mockins;

import java.lang.reflect.Method;

import net.kilger.mockins.api.OngoingOptions;
import net.kilger.mockins.generator.TestMethodGeneratorPrinter;
import net.kilger.mockins.instructor.InstructorFactory;

/**
 * <p>Mockins is a code generator for mock and stub code.</p>
 * <p>Please visit https://github.com/kilgerm/mockins/wiki 
 * for full documentation.</p>
 * <p>
 * Example:
 * <pre>
 * // This is the class you would like to test
 * MyClass classUnderTest = new MyClass();
 * 
 * // first, create an "instructor" wrapper
 * MyClass instructor = Mockins.instructor(classUnderTest);
 * 
 * // then call whatever method you would like to test...
 * instructor.myMethod(null);
 * 
 * // ...this will run myMethod and try to add mocks/stubs 
 * for parameters and fields so that the method does not throw a NPE!
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
    
    /**
     * Generates a test method skeleton for the given method
     * to be tested in the given class.
     */
    public static void generateTestSkeleton(Class<?> testedClass, Method testedMethod) {
        new TestMethodGeneratorPrinter(testedClass, testedMethod).generateTestSkeletonAndPrint();
    }

    private Mockins() {
        // not instantiable
    }
    
}
