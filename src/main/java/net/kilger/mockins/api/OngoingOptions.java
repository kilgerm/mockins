package net.kilger.mockins.api;

import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockitoHelper;
import net.kilger.mockins.util.mocking.impl.EasyMockHelper;

public class OngoingOptions {

    /**
     * Tells Mockins to use EasyMock mocking framework.
     */
    public OngoingOptions useEasyMock() {
        MockinsContext.INSTANCE.setMockHelper(new EasyMockHelper());
        return this;
    }
    
    /**
     * Tells Mockins to use the Mockito mocking framework.
     */
    public OngoingOptions useMockito() {
        MockinsContext.INSTANCE.setMockHelper(new MockitoHelper());
        return this;
    }

}
