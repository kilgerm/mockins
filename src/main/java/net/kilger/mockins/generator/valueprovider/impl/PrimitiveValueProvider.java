package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

public abstract class PrimitiveValueProvider implements ValueProvider<Object> {

    private final Object value;
    private final String code;

    public PrimitiveValueProvider(Class<?> boxedType, Class<?> primitiveType, Object value, String code) {
        this.value = value;
        this.code = code;
    }
    
    public Object createValue() {
        return value;
    }

    public String code() {
        return code;
    }

    public boolean isMocking() {
        return false;
    }

}
