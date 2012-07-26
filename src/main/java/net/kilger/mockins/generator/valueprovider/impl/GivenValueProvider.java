package net.kilger.mockins.generator.valueprovider.impl;

public class GivenValueProvider extends FixedValueProvider {

    public GivenValueProvider(Object givenValue) {
        super(givenValue, "<given>");
    }

}
