package net.kilger.mockins.generator.valueprovider;

public interface ValueProvider<T> {

    T createValue();
    String code();
    boolean isMocking();
}
