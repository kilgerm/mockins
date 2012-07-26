package net.kilger.mockins.analysis.model;


import java.util.List;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

public interface SubstitutableObjectInfo {
    
    String getDisplayName();

    Class<?> getType();

    boolean wasInitiallyGiven();
    boolean isValueChanged();
    boolean isMock();
    
    List<Stubbing> getStubbings();

    Object createValue();
    Object getCurrentValue();

    void setValueProvider(ValueProvider<?> valueProvider);
    ValueProvider<?> getValueProvider();

}
