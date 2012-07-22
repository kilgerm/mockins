package net.kilger.mockins.generator.model;


import java.util.ArrayList;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.GivenValueProvider;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.MockHelperHolder;


public abstract class BaseSubstitutableObjectInfo implements SubstitutableObjectInfo {

    protected final Object initialValue;
    protected ValueProvider<?> valueProvider;
    protected Object currentValue;

    protected final List<Stubbing> stubbings = new ArrayList<Stubbing>();

    protected MockHelper mockHelper = MockHelperHolder.getMockHelper();
    protected final Class<?> type;

    public BaseSubstitutableObjectInfo(Object initialValue, Class<?> type) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
        this.valueProvider = new GivenValueProvider(initialValue);
        this.type = type;
    }

    @Override
    public abstract String getDisplayName();

    @Override
    public Class<?> getType() {
        return type;
    }

    public boolean wasInitiallyNull() {
        return initialValue == null;
    }

    @Override
    public boolean wasInitiallyGiven() {
        return !wasInitiallyNull();
    }

    public boolean wasGivenAsMock() {
        return mockHelper.isMock(initialValue);        
    }

    @Override
    public boolean isMock() {
        return valueProvider.isMocking();        
    }

    public Object createValue() {
        currentValue = valueProvider.createValue();
        return currentValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    @Override
    public ValueProvider<?> getValueProvider() {
        return valueProvider;
    }

    @Override
    public void setValueProvider(ValueProvider<?> valueProvider) {
        this.valueProvider = valueProvider;
        this.currentValue = valueProvider.createValue();
    }

    @Override
    public List<Stubbing> getStubbings() {
        return stubbings;
    }

    @Override
    public Object getCurrentValue() {
        return currentValue;
    }

    @Override
    public boolean isValueChanged() {
        // FIXME: not clear whether to use this "heuristic"
        boolean usingGivenValue = (valueProvider instanceof GivenValueProvider) 
                || (currentValue == null && initialValue == null);
        return !usingGivenValue;
    }

}
