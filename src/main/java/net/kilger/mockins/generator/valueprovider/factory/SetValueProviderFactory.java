package net.kilger.mockins.generator.valueprovider.factory;

import java.util.Set;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.EmptySetValueProvider;

/**
 * TODO: There's the follwong problem with all collection classes:
 * We'd like to provide non-empty implementations as value as well,
 * but how could we tell the element type? 
 * Since that info is not availabe at runtime.
 */
public class SetValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new EmptySetValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return clazz.equals(Set.class);
    }

    public boolean canHandle(Class<?> clazz) {
        return specificFor(clazz);
    }

}
