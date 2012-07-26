package net.kilger.mockins.generator.result.model;

import java.lang.reflect.Method;
import java.util.List;

import net.kilger.mockins.analysis.model.ParamInfo;


public class InvokeTestMethodInstruction implements Instruction {

    private final String classUnderTestName;
    private final Method method;
    private final List<ParamInfo> paramInfos;

    public InvokeTestMethodInstruction(String classUnderTestName, Method method, List<ParamInfo> paramInfos) {
        this.classUnderTestName = classUnderTestName;
        this.method = method;
        this.paramInfos = paramInfos;
    }

    public String message() {
        StringBuilder sb = new StringBuilder();
        sb.append(classUnderTestName);
        sb.append(method.getName());
        sb.append("(");
        boolean first = true;
        for (ParamInfo paramInfo : paramInfos) {
            if (!first) {
                sb.append(", ");
            }
            else {
                first = false;
            }
            sb.append(paramInfo.getDisplayName());
        }
        sb.append(")");
        sb.append(";\n");
        return sb.toString();
    }

}
