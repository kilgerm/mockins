package net.kilger.mockins.generator.valueprovider.factory;


import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.MockValueProvider;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockHelper;


public class MockValueProviderFactory implements ValueProviderFactory {

    private MockHelper mockHelper = MockinsContext.INSTANCE.getMockHelper();
    
    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        return (ValueProvider<T>) new MockValueProvider(clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return false;
    }

    public boolean canHandle(Class<?> clazz) {
        return mockHelper.canBeMocked(clazz);
    }
    
}