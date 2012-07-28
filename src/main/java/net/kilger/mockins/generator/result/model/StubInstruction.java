package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.impl.MockHelperHolder;

public class StubInstruction implements Instruction {

    private final String mockName;
    private final Stubbing stubbing;
    private MockHelper mockHelper = MockHelperHolder.getMockHelper();

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
