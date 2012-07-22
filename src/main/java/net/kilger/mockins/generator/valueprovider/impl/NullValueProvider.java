package net.kilger.mockins.generator.valueprovider.impl;

/**
 * just creates nulls - can handle every (non-primitive) class
 */
public class NullValueProvider extends FixedValueProvider {

    public NullValueProvider() {
        super(null, "null");
    }

}