package net.kilger.mockins.generator.valueprovider.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;

/**
 * Provides an empty map.
 */
public class EmptyMapValueProvider implements ValueProvider<Object> {

    public EmptyMapValueProvider(Class<?> clazz) {
    }
    
    public Object createValue() {
        return new HashMap<Object, Object>();
    }

    public String code() {
        return "new HashMap()"; // we intentionally omit the type parameters here
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
