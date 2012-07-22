package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

public class FixedValueProvider implements ValueProvider<Object> {

    private final Object value;
    private final String code;

    public FixedValueProvider(Object value, String code) {
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
