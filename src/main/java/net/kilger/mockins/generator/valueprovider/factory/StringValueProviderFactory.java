package net.kilger.mockins.generator.valueprovider.factory;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.FixedValueProvider;


public class StringValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new FixedValueProvider("", "\"\"");
    }

    public boolean specificFor(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    public boolean canHandle(Class<?> clazz) {
        return specificFor(clazz);
    }
    
}