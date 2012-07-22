package net.kilger.mockins.generator.result.model;

public class CreateFieldMockInstruction extends CreateParameterMockInstruction {

    public CreateFieldMockInstruction(String targetName, Class<?> type, String valueCreationCode) {
        super(targetName, type, valueCreationCode);
    }

    @Override
    public String targetPrefix() {
        return "";
    }

}
