package net.kilger.mockins.generator.valueprovider.impl;

import java.util.Collections;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
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

    public Object createValue() {
        return defaultValue;
    }

    public String code() {
        return classNamer.className(clazz) + "." + defaultValue;
    }

    public boolean isMocking() {
        return false;
    }
    
    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
