package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.LocalVarNamer;
import net.kilger.mockins.util.MockinsContext;

public class LocalVariableInstruction extends CompositeInstruction {

    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();
    private LocalVarNamer localVarNamer = MockinsContext.INSTANCE.getLocalVarNamer();

    private final Class<?> type;
    private final ValueProvider<?> vp;
    private String localVarName;

    public LocalVariableInstruction(Class<?> type, ValueProvider<?> vp) {
        this.type = type;
        this.vp = vp;
        this.localVarName = localVarNamer.localVarName(type);
    }

    @Override
    public String message() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(classNamer.className(type));
        sb.append(" ");
        sb.append(localVarName);
        sb.append(" = ");
        sb.append(vp.code());
        sb.append(";\n");
        
        return sb.toString();
    }

    public String getLocalVarName() {
        return localVarName;
    }

}
