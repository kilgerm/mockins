package net.kilger.mockins.generator.valueprovider;


/**
 * A ValueProvider encapsulates the creation of an
 * object instance of a certain type
 * as well as the java code required to produce it.
 */
public interface ValueProvider<T> {

    /**
     * creates an object instance
     */
    T createValue();

    /**
     * returns the java code required to produce 
     * an object instance as returned by {@link #createValue()}
     */
    String code();

    /**
     * @deprecated currently, the best way to detect 
     * whether a value provider produces mock objects
     * is to check for an instance of {@link net.kilger.mockins.generator.valueprovider.impl.MockValueProvider}
     */
    @Deprecated
    boolean isMocking();
    
}
