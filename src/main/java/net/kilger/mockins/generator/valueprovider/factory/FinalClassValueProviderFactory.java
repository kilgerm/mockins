package net.kilger.mockins.generator.valueprovider.factory;

import java.lang.reflect.Modifier;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.NewInstanceValueProvider;

public class FinalClassValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        try {
            return (ValueProvider<T>) new NewInstanceValueProvider(clazz);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean specificFor(Class<?> clazz) {
        return false;
    }

    public boolean canHandle(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers()) && (!clazz.isArray());
    }

}
