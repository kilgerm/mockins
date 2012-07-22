package net.kilger.mockins.generator.valueprovider.factory;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.EmptyArrayValueProvider;

public class ArrayValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new EmptyArrayValueProvider(clazz);
    }

    @Override
    public boolean specificFor(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return clazz.isArray();
    }

}
