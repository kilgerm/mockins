package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.ClassNamerHolder;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.impl.MockHelperHolder;

public class CreateParameterMockInstruction extends CompositeInstruction {

    private final String targetName;
    private final String valueCreationCode;
    private final Class<?> type;

    private MockHelper mockHelper = MockHelperHolder.getMockHelper();
    private ClassNamer classNamer = ClassNamerHolder.getClassNamer();
    
    public CreateParameterMockInstruction(String targetName, Class<?> type, String valueCreationCode) {
        this.targetName = targetName;
        this.type = type;
        this.valueCreationCode = valueCreationCode;
    }

    @Override
    public String message() {
        StringBuilder message = new StringBuilder();
        message
                .append(targetPrefix())
                .append(targetName)
                .append(" = ").append(valueCreationCode).append(";\n");
        message.append(super.message());
        
        String prepareMockCode = mockHelper.prepareMockCode(targetName);
        if (!prepareMockCode.isEmpty()) {
            message.append(prepareMockCode).append(";\n");
        }
        
        return message.toString();
    }

    protected String targetPrefix() {
        return classNamer.className(type) + " ";
    }

    @Override
    public String toString() {
        return message();
    }
}
