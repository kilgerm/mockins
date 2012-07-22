package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

public abstract class PrimitiveValueProvider implements ValueProvider<Object> {

    private final Object value;
    private final String code;

    public PrimitiveValueProvider(Class<?> boxedType, Class<?> primitiveType, Object value, String code) {
        this.value = value;
        this.code = code;
    }
    
    @Override
    public Object createValue() {
        return value;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public boolean isMocking() {
        return false;
    }

}
