package net.kilger.mockins.generator.result.model;


public class CreateValueInstruction implements Instruction {

    private final String targetName;
    private final String valueCreationCode;

    public CreateValueInstruction(String targetName, String valueCreationCode) {
        this.targetName = targetName;
        this.valueCreationCode = valueCreationCode;
    }

    public String message() {
        return targetName + " = " + valueCreationCode + ";\n";
    }

    @Override
    public String toString() {
        return message();
    }
}
