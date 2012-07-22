package net.kilger.mockins.generator.valueprovider.impl;

public class GivenValueProvider extends FixedValueProvider {

    public GivenValueProvider(Object givenValue) {
        super(givenValue, "<given>");
    }

// FIXME: not sure actually!
//    @Override
//    public boolean isMocking() {
//        return false;
//    }

}
