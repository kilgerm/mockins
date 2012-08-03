package net.kilger.mockins.generator.valueprovider.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;

/**
 * Provides an empty set.
 */
public class EmptySetValueProvider implements ValueProvider<Object> {

    public EmptySetValueProvider(Class<?> clazz) {
    }
    
    public Object createValue() {
        return new HashSet<Object>();
    }

    public String code() {
        return "new HashSet()"; // we intentionally omit the type parameter here
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
