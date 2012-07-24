package net.kilger.mockins.util.mocking;

public final class MockHelperHolder {

    private MockHelperHolder() {
        // not instantiable
    }

    private static MockHelper mockHelper = defaultMockHelper();

    private static MockHelper defaultMockHelper() {
        return autodetectedMockHelper();
    }

    private static MockHelper autodetectedMockHelper() {
        boolean hasMockito = isClassPresent("org.mockito.Mockito");
        boolean hasEasyMock = isClassPresent("org.easymock.EasyMock");

        if (hasMockito && hasEasyMock) {
            // logger.warn("multiple mocking frameworks detected - this is generally not desirable");
        }
        
        if (hasEasyMock) {
            return new EasyMockHelper();
        }
        if (hasMockito) {
            return new MockitoHelper();
        }
        
        // this is fatal
        throw new RuntimeException("No mocking framework has been detected. " +
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

    public static MockHelper getMockHelper() {
        return mockHelper;
    }

    public static void setMockHelper(MockHelper mockHelper) {
        MockHelperHolder.mockHelper = mockHelper;
    }

}
