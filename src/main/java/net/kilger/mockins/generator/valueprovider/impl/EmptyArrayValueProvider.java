package net.kilger.mockins.generator.valueprovider.impl;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;

/**
 * Provides empty arrays (of primitives or arbitrary class objects).
 * Only 1-dimensional arrays are supported so far.
 */
public class EmptyArrayValueProvider extends InitializedArrayValueProvider implements ValueProvider<Object> {

    private final Class<?> elementType;
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    public EmptyArrayValueProvider(Class<?> elementType) {
        super(elementType);
        this.elementType = elementType;
    }
    
    @Override
    public String code() {
        // don't use static initializer code from superclass
        return "new " + classNamer.classLiteral(elementType) + "[0]";
    }

}
