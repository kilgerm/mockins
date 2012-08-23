package net.kilger.mockins.generator.valueprovider.impl;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;

/**
 * Provides arrays that are initialized to given value providers for elements.
 * Only 1-dimensional arrays are supported so far.
 */
public class InitializedArrayValueProvider implements ValueProvider<Object> {

    private final Class<?> clazz;
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();
    private final ValueProvider<?>[] elementValueProviders;

    public InitializedArrayValueProvider(Class<?> elementType, ValueProvider<?>... elementValueProviders) {
        this.clazz = elementType;
        this.elementValueProviders = elementValueProviders;
    }
    
    public Object createValue() {
        Object array = Array.newInstance(clazz, elementValueProviders.length);
        for (int i = 0; i < elementValueProviders.length; i++) {
            Object value = elementValueProviders[i].createValue();
            Array.set(array, i, value);
        }
        return array;
    }

    public String code() {
        StringBuilder sb = new StringBuilder();
        sb.append("new ")
            .append(classNamer.className(clazz))
            .append("[")
            .append("]")
            .append(" {");
        
        for (int i = 0; i < elementValueProviders.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(elementValueProviders[i].code());
        }
        sb.append("}");
        
        return sb.toString();
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
