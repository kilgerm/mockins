package net.kilger.mockins;

import net.kilger.mockins.util.mocking.EasyMockHelper;
import net.kilger.mockins.util.mocking.MockHelperHolder;
import net.kilger.mockins.util.mocking.MockitoHelper;

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
