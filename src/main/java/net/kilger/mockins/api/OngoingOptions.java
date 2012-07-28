package net.kilger.mockins.api;

import net.kilger.mockins.util.mocking.MockitoHelper;
import net.kilger.mockins.util.mocking.impl.EasyMockHelper;
import net.kilger.mockins.util.mocking.impl.MockHelperHolder;

public class OngoingOptions {

    /**
     * Tells Mockins to use EasyMock mocking framework.
     */
    public OngoingOptions useEasyMock() {
        MockHelperHolder.setMockHelper(new EasyMockHelper());
        return this;
    }
    
    /**
     * Tells Mockins to use the Mockito mocking framework.
     */
    public OngoingOptions useMockito() {
        MockHelperHolder.setMockHelper(new MockitoHelper());
        return this;
    }

}
