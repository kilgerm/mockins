package net.kilger.mockins.api;

import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.impl.EasyMockHelper;
import net.kilger.mockins.util.mocking.impl.MockitoHelper;

/**
 * <p>
 * API class for setting Mockins options.
 * </p>
 */
public class OngoingOptions {

    /**
     * Tells Mockins to use the EasyMock mocking framework.
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
    
    /**
     * Sets the timeout for single invocations of the test method.
     */
    public OngoingOptions withInvocationTimeout(int timeoutMillis) {
        MockinsContext.INSTANCE.setInvocationTimeoutMillis(timeoutMillis);
        return this;
    }

}
