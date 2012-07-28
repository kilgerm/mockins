package net.kilger.mockins.generator.valueprovider.factory;


import java.lang.reflect.Modifier;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.MockValueProvider;


public class MockValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new MockValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return false;
    }

    public boolean canHandle(Class<?> clazz) {
        // every class that is not final...
        return (clazz.getModifiers() & Modifier.FINAL) == 0;
    }
    
}