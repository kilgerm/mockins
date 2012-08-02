package net.kilger.mockins.generator.result.model;

/**
 * Instruction for creating a mock and assigning it to a field.
 */
public class CreateFieldMockInstruction extends CreateParameterMockInstruction {

    public CreateFieldMockInstruction(String targetName, Class<?> type, String valueCreationCode) {
        super(targetName, type, valueCreationCode);
    }

    @Override
    public String targetPrefix() {
        return "";
    }

}
