package net.kilger.mockins;

import net.kilger.mockins.util.mocking.EasyMockHelper;
import net.kilger.mockins.util.mocking.MockHelperHolder;
import net.kilger.mockins.util.mocking.MockitoHelper;

public class OngoingOptions {

    public OngoingOptions useEasyMock() {
        MockHelperHolder.setMockHelper(new EasyMockHelper());
        return this;
    }
    
    public OngoingOptions useMockito() {
        MockHelperHolder.setMockHelper(new MockitoHelper());
        return this;
    }

}
