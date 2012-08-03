package net.kilger.mockins.generator.valueprovider.factory;

import java.util.Map;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.EmptyMapValueProvider;

/**
 * TODO: There's the follwong problem with all collection classes:
 * We'd like to provide non-empty implementations as value as well,
 * but how could we tell the element type? 
 * Since that info is not availabe at runtime.
 */
public class MapValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new EmptyMapValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return clazz.equals(Map.class);
    }

    public boolean canHandle(Class<?> clazz) {
        return specificFor(clazz);
    }

}
