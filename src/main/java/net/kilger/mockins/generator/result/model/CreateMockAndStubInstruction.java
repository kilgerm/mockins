package net.kilger.mockins.generator.result.model;

import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockHelper;

import org.apache.commons.lang.StringUtils;

/**
 * Instruction for creating a mock and adding stubs.
 */
public class CreateMockAndStubInstruction extends CompositeInstruction {

    private final String targetName;
    private final String valueCreationCode;
    private final Class<?> type;
    private final boolean useLocalVariable;
    
    private MockHelper mockHelper = MockinsContext.INSTANCE.getMockHelper();
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();
    
    public CreateMockAndStubInstruction(String targetName, Class<?> type, String valueCreationCode, boolean useLocalVariable) {
        this.targetName = targetName;
        this.type = type;
        this.valueCreationCode = valueCreationCode;
        this.useLocalVariable = useLocalVariable;
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
        if (!StringUtils.isEmpty(prepareMockCode)) {
            message.append(prepareMockCode).append(";\n");
        }
        
        return message.toString();
    }

    protected String targetPrefix() {
        String targetPrefix;
        if (useLocalVariable) {
            targetPrefix = classNamer.className(type) + " ";
        }
        else {
            targetPrefix = "";
        }
        return targetPrefix;
    }

    @Override
    public String toString() {
        return message();
    }
}
