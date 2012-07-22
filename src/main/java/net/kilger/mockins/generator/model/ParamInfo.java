package net.kilger.mockins.generator.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


public class ParamInfo extends BaseSubstitutableObjectInfo {

    final int index;
    public ParamInfo(int index, Class<?> paramType, Object initialValue) {
        super(initialValue, paramType);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getDisplayName() {
        return "param" + index;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
}
