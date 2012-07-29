package net.kilger.mockins.generator.valueprovider.impl;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;

/**
 * Provides empty arrays (of primitives or arbitrary class objects).
 * Only 1-dimensional arrays are supported so far.
 */
public class EmptyArrayValueProvider implements ValueProvider<Object> {

    private final Class<?> clazz;
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    public EmptyArrayValueProvider(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Object createValue() {
        return Array.newInstance(clazz, 0);
    }

    public String code() {
        return "new " + classNamer.classLiteral(clazz) + "[0]";
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
