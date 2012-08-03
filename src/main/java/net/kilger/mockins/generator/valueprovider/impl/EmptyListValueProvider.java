package net.kilger.mockins.generator.valueprovider.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;

/**
 * Provides an empty list.
 */
public class EmptyListValueProvider implements ValueProvider<Object> {

    public EmptyListValueProvider(Class<?> clazz) {
    }
    
    public Object createValue() {
        return new ArrayList<Object>();
    }

    public String code() {
        return "new ArrayList<Object>()"; // we intentionally omit the type parameter here
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
