package net.kilger.mockins.util.mocking;


public final class MockHelperHolder {

    private MockHelperHolder() {
        // not instantiable
    }
    
    private static MockHelper mockHelper = defaultMockHelper();

    private static MockHelper defaultMockHelper() {
        return new EasyMockHelper();
    }

    public static MockHelper getMockHelper() {
        return mockHelper;
    }

    public static void setMockHelper(MockHelper mockHelper) {
        MockHelperHolder.mockHelper = mockHelper;
    }
    
}
