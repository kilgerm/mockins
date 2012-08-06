package net.kilger.mockins.generator;

import java.lang.reflect.Method;
import java.util.List;

import net.kilger.mockins.analysis.model.FieldInfo;
import net.kilger.mockins.analysis.model.ParamInfo;
import net.kilger.mockins.analysis.model.SubstitutableObjectInfo;
import net.kilger.mockins.generator.result.model.CompositeInstruction;
import net.kilger.mockins.generator.result.model.CreateMockAndStubInstruction;
import net.kilger.mockins.generator.result.model.CreateValueInstruction;
import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.generator.result.model.InvokeTestMethodInstruction;
import net.kilger.mockins.generator.result.model.LineCommentInstruction;
import net.kilger.mockins.generator.valueprovider.impl.MockValueProvider;

public class MockStubInstructionBuilder {

    private String classUnderTestName = "classUnderTest";

    public Instruction buildInstruction(List<FieldInfo> fieldInfos, List<ParamInfo> paramInfos, Method method) {
        CompositeInstruction instruction = new CompositeInstruction();
        instruction.addComponent(new LineCommentInstruction("method call will succeed with the following mocks and stubbings:"));
    
        addMockStubInstructionsFor(instruction, fieldInfos);
        addMockStubInstructionsFor(instruction, paramInfos);
        
        instruction.addComponent(new InvokeTestMethodInstruction(classUnderTestName, method, paramInfos));
        return instruction;
    }

    private void addMockStubInstructionsFor(CompositeInstruction instruction, List<? extends SubstitutableObjectInfo> soInfos) {
        for (SubstitutableObjectInfo soInfo : soInfos) {
            String targetName = soInfo.getDisplayName();
            if (soInfo.isField()) {
                targetName = classUnderTestName + "." + soInfo.getDisplayName();
            }
            
            if (soInfo.getValueProvider() instanceof MockValueProvider) {
                MockValueProvider mvp = (MockValueProvider) soInfo.getValueProvider();
    
                boolean useLocalVariable = !soInfo.isField();
                CompositeInstruction mockFieldInstruction = new CreateMockAndStubInstruction(targetName, soInfo.getType(), soInfo.getValueProvider().code(), useLocalVariable);
    
                mvp.addStubbingInstructions(mockFieldInstruction, targetName);
    
                instruction.addComponent(mockFieldInstruction);
            }
            else {
                if (soInfo.isValueChanged()) {
                    CreateValueInstruction createValueInstruction = new CreateValueInstruction(targetName, soInfo.getValueProvider().code(), soInfo.getType());
                    instruction.addComponent(createValueInstruction);
                }
            }
        }
    }

}
