package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockHelper;

/**
 * Instruction for applying a {@link Stubbing} to a mock.
 */
public class StubInstruction implements Instruction {

    private final String mockName;
    private final Stubbing stubbing;
    private MockHelper mockHelper = MockinsContext.INSTANCE.getMockHelper();

    public StubInstruction(String mockName, Stubbing stubbing) {
        this.mockName = mockName;
        this.stubbing = stubbing;
    }

    public String message() {
        StringBuilder stubStatement = new StringBuilder();
        stubStatement.append(stubCode());
        stubStatement.append(";\n");
        return stubStatement.toString();
    }

    private String stubCode() {
        String stubStatement = mockHelper.addStubCode(mockName, getStubbing());
        return stubStatement.toString();
    }

    @Override
    public String toString() {
        return message();
    }

    public String getMockName() {
        return mockName;
    }

    public Stubbing getStubbing() {
        return stubbing;
    }
    
}
