package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;


public class CreateValueInstruction implements Instruction {

    private final String targetName;
    private final String valueCreationCode;
    private Class<?> type;
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    public CreateValueInstruction(String targetName, String valueCreationCode, Class<?> type) {
        this.targetName = targetName;
        this.valueCreationCode = valueCreationCode;
        this.type = type;
    }

    protected String targetPrefix() {
        return classNamer.className(type) + " ";
    }
    
    public String message() {
        return targetPrefix() + targetName + " = " + valueCreationCode + ";\n";
    }

    @Override
    public String toString() {
        return message();
    }
}
