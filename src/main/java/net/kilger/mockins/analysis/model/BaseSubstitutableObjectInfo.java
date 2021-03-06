package net.kilger.mockins.analysis.model;


import java.util.ArrayList;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.GivenValueProvider;
import net.kilger.mockins.util.MockinsContext;
import net.kilger.mockins.util.mocking.MockHelper;


public abstract class BaseSubstitutableObjectInfo implements SubstitutableObjectInfo {

    protected final Object initialValue;
    protected ValueProvider<?> valueProvider;
    protected Object currentValue;

    protected final List<Stubbing> stubbings = new ArrayList<Stubbing>();

    protected MockHelper mockHelper = MockinsContext.INSTANCE.getMockHelper();
    protected final Class<?> type;

    public BaseSubstitutableObjectInfo(Object initialValue, Class<?> type) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
        this.valueProvider = new GivenValueProvider(initialValue);
        this.type = type;
    }

    public abstract String getDisplayName();

    public Class<?> getType() {
        return type;
    }

    public boolean wasInitiallyNull() {
        return initialValue == null;
    }

    public boolean wasInitiallyGiven() {
        return !wasInitiallyNull();
    }

    public boolean wasGivenAsMock() {
        return mockHelper.isMock(initialValue);        
    }

    public Object createValue() {
        currentValue = valueProvider.createValue();
        return currentValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public ValueProvider<?> getValueProvider() {
        return valueProvider;
    }

    public void setValueProvider(ValueProvider<?> valueProvider) {
        this.valueProvider = valueProvider;
        this.currentValue = valueProvider.createValue();
    }

    public List<Stubbing> getStubbings() {
        return stubbings;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public boolean isValueChanged() {
        // TODO: just a heuristic for now...
        boolean usingGivenValue = (valueProvider instanceof GivenValueProvider) 
                || (currentValue == null && initialValue == null);
        return !usingGivenValue;
    }

}
