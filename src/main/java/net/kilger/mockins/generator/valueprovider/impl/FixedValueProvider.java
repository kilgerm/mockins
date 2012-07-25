package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

public class FixedValueProvider implements ValueProvider<Object> {

    private final Object value;
    private final String code;

    public FixedValueProvider(Object value, String code) {
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
