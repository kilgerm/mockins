package net.kilger.mockins.analysis.model;

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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
    @Override
    public boolean isValueChanged() {
        // params are always considered "changed", so that they are displayed
        return true;
    }

    public boolean isField() {
        return false;
    }
    
}
