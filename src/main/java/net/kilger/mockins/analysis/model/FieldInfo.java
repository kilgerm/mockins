package net.kilger.mockins.analysis.model;

import java.lang.reflect.Field;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class FieldInfo extends BaseSubstitutableObjectInfo {

    private final Field field;
    
    public FieldInfo(Field field, Object initialValue) {
        super(initialValue, field.getType());
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getDisplayName() {
        return field.getName();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public boolean isField() {
        return true;
    }
    
}
