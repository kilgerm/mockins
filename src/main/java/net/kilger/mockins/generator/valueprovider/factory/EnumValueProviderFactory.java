package net.kilger.mockins.generator.valueprovider.factory;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.EnumValueProvider;

public class EnumValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new EnumValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return false;
    }

    public boolean canHandle(Class<?> clazz) {
        return clazz.isEnum();
    }

}
