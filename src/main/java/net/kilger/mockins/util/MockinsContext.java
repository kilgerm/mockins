package net.kilger.mockins.util;

import net.kilger.mockins.analysis.DefaultExceptionAnalyzer;
import net.kilger.mockins.analysis.ExceptionAnalyzer;
import net.kilger.mockins.util.impl.SimpleClassNamer;
import net.kilger.mockins.util.impl.SimpleLocalVarNamer;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.impl.EasyMockHelper;
import net.kilger.mockins.util.mocking.impl.MockitoHelper;

/**
 * Context information for mockins.
 * Exists only as singleton and holds various global elements,
 * e.g. the MockHelper.
 */
public enum MockinsContext {

    /** the singleton instance */ INSTANCE;

    private MockHelper mockHelper;

    private ClassNamer classNamer;
    private LocalVarNamer localVarNamer;
    private ExceptionAnalyzer exceptionAnalyzer;

    /** the timeout for each invocation of the test method in milliseconds*/
    private int invocationTimeoutMillis;

    private static final int DEFAULT_INVOCATION_TIMEOUT_MILLIS = 500;

    /**
     * Reset the Mockins context to default values.
     */
    public void resetToDefault() {
        mockHelper = null;
        classNamer = defaultClassNamer();
        localVarNamer = defaultLocalVarNamer();
        exceptionAnalyzer = defaultExceptionAnalyzer();
        invocationTimeoutMillis = DEFAULT_INVOCATION_TIMEOUT_MILLIS;
    }
    
    private MockinsContext() {
        resetToDefault();
    }
    
    /**
     * Returns the current mockhelper, autodetecting which one to use
     * if none was explicitely configured. 
     */
    /*
     * Note: This method is not synchronized, since even
     * if you ran your tests in parallel,
     * having multiple MockHelper instances should not 
     * create any problems.
     */
    public MockHelper getMockHelper() {
        if (INSTANCE.mockHelper == null) {
            INSTANCE.mockHelper = autodetectedMockHelper();
        }
        return INSTANCE.mockHelper;
    }

    public void setMockHelper(MockHelper mockHelper) {
        INSTANCE.mockHelper = mockHelper;
    }

    private static MockHelper autodetectedMockHelper() {
        boolean hasMockito = isClassPresent("org.mockito.Mockito");
        boolean hasEasyMock = isClassPresent("org.easymock.EasyMock");

        if (hasMockito && hasEasyMock) {
            // logger.warn("multiple mocking frameworks detected - this is generally not desirable");
        }
        
        /* 
         * In case there are both mocking framework available, EasyMock
         * is preferred for the simple reason it was the framework already
         * in use in the project I first used Mockins for.
         * This is not a statement about which mocking framework is better.
         */
        if (hasEasyMock) {
            return new EasyMockHelper();
        }
        if (hasMockito) {
            return new MockitoHelper();
        }
        
        // this is fatal
        throw new Error("No mocking framework has been detected. " +
                "Please add a supported mocking framework to the test classpath. " +
                "Consider the main documentation for supported mocking frameworks and versions.");
    }

    private static ClassNamer defaultClassNamer() {
        return new SimpleClassNamer();
    }

    private static LocalVarNamer defaultLocalVarNamer() {
        return new SimpleLocalVarNamer();
    }

    private static ExceptionAnalyzer defaultExceptionAnalyzer() {
        return new DefaultExceptionAnalyzer();
    }
    
    private static boolean isClassPresent(String qualifiedClassName) {
        boolean found;
        try {
            Class.forName(qualifiedClassName);
            found = true;
        } catch (ClassNotFoundException e) {
            found = false;
        }
        return found;
    }

    public ClassNamer getClassNamer() {
        return classNamer;
    }
    
    public void setClassNamer(ClassNamer classNamer) {
        INSTANCE.classNamer = classNamer;
    }

    public LocalVarNamer getLocalVarNamer() {
        return localVarNamer;
    }

    public void setLocalVarNamer(LocalVarNamer localVarNamer) {
        INSTANCE.localVarNamer = localVarNamer;
    }

    public int getInvocationTimeoutMillis() {
        return invocationTimeoutMillis;
    }

    public void setInvocationTimeoutMillis(int invocationTimeoutMillis) {
        this.invocationTimeoutMillis = invocationTimeoutMillis;
    }

    public ExceptionAnalyzer getExceptionAnalyzer() {
        return exceptionAnalyzer;
    }

    public void setExceptionAnalyzer(ExceptionAnalyzer exceptionAnalyzer) {
        this.exceptionAnalyzer = exceptionAnalyzer;
    }

}
