package net.kilger.mockins.util.mocking;

public final class MockHelperHolder {

    private MockHelperHolder() {
        // not instantiable
    }

    private static MockHelper mockHelper = null;

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
    public static MockHelper getMockHelper() {
        if (mockHelper == null) {
            mockHelper = autodetectedMockHelper();
        }
        return mockHelper;
    }

    public static void setMockHelper(MockHelper mockHelper) {
        MockHelperHolder.mockHelper = mockHelper;
    }

}
