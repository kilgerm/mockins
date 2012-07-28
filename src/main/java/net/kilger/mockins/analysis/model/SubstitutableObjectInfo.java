package net.kilger.mockins.analysis.model;


import net.kilger.mockins.generator.valueprovider.ValueProvider;

public interface SubstitutableObjectInfo {
    
    String getDisplayName();

    Class<?> getType();

    boolean wasInitiallyGiven();
    boolean isValueChanged();
    
    Object createValue();
    Object getCurrentValue();

    void setValueProvider(ValueProvider<?> valueProvider);
    ValueProvider<?> getValueProvider();

}
