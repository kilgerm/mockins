package net.kilger.mockins.generator.valueprovider.impl;

import java.lang.reflect.Array;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.ClassNamerHolder;

/**
 * Provides empty arrays (of primitives or arbitrary class objects).
 * Only 1-dimensional arrays are supported so far.
 */
public class EmptyArrayValueProvider implements ValueProvider<Object> {

    private final Class<?> clazz;
    private ClassNamer classNamer = ClassNamerHolder.getClassNamer();

    public EmptyArrayValueProvider(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Object createValue() {
        return Array.newInstance(clazz, 0);
    }

    @Override
    public String code() {
        return "new " + classNamer.classLiteral(clazz) + "[0]";
    }

    @Override
    public boolean isMocking() {
        return false;
    }

}
