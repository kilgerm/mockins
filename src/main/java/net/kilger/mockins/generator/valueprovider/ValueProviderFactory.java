package net.kilger.mockins.generator.valueprovider;

public interface ValueProviderFactory {

    <T> ValueProvider<T> valueProvider(Class<T> clazz);
    
    /**
     * true iff this provider factory is specific for clazz.
     * general purpose factories should return false here. 
     */
    boolean specificFor(Class<?> clazz);
    
    boolean canHandle(Class<?> clazz);
}
