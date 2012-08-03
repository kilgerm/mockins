package net.kilger.mockins.generator.valueprovider.factory;

import java.util.Collection;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.EmptyListValueProvider;

/**
 * ValueProviderFactory for List. Also handles Collection by default.
 * 
 * TODO: There's the follwong problem with all collection classes:
 * We'd like to provide non-empty implementations as value as well,
 * but how could we tell the element type? 
 * Since that info is not availabe at runtime.
 */
public class ListValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new EmptyListValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return clazz.equals(List.class);
    }

    // we also use List as the default Collection implementation
    public boolean canHandle(Class<?> clazz) {
        return specificFor(clazz) || clazz.isAssignableFrom(Collection.class);
    }

}
