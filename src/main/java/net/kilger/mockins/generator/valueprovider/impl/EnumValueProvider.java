package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.ClassNamerHolder;

public class EnumValueProvider implements ValueProvider<Object> {

    private final Class<?> clazz;
    private Object defaultValue;
    
    private ClassNamer classNamer = ClassNamerHolder.getClassNamer();

    public EnumValueProvider(Class<?> clazz) {
        this.clazz = clazz;
        this.defaultValue = clazz.getEnumConstants()[0];
    }

    @Override
    public Object createValue() {
        return defaultValue;
    }

    @Override
    public String code() {
        return classNamer.className(clazz) + "." + defaultValue;
    }

    @Override
    public boolean isMocking() {
        return false;
    }

}
