package net.kilger.mockins.generator.valueprovider;

/**
 * Factory for creating {@link ValueProvider}s.   
 */
public interface ValueProviderFactory {

    /**
     * Returns a {@link ValueProvider} for the given <i>clazz</i>.
     * Whether this factory can deal with <i>clazz</i> must 
     * be checked using {@link #canHandle(Class)} before calling
     * this method.
     */
    <T> ValueProvider<T> valueProvider(Class<T> clazz);
    
    /**
     * True iff this provider factory is specific for clazz.
     * general purpose factories should return false here.
     * This is because some types should have special
     * ValueProviders (e.g. array types) rather than
     * generic implementations.
     * Note that returning true here already implies
     * {@link #canHandle(Class)}.
     */
    boolean specificFor(Class<?> clazz);

    /**
     * Returns true if this factory can create a {@link ValueProvider}
     * with {@link #valueProvider(Class)} for <i>clazz</i>
     */
    boolean canHandle(Class<?> clazz);
}
