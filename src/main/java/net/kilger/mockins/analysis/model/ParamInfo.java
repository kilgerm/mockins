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
    
    // FIXME: temporary to allow common treatment of paramters/fields
    @Override
    public boolean isValueChanged() {
        return true;
    }

    public boolean isField() {
        return false;
    }
    
}
