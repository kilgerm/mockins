package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;

/**
 * Instruction for calling the default constructor
 */
public class InstantiateClassInstruction implements Instruction {

    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    private final String variableName;
    private final Class<?> clazz;

    public InstantiateClassInstruction(String variableName, Class<?> clazz) {
        this.variableName = variableName;
        this.clazz = clazz;
    }

    public String message() {
        return classNamer.className(clazz) + " " + variableName + " = "
                + "new " + classNamer.className(clazz) + "()" + ";" + "\n";
    }

}
