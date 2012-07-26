package net.kilger.mockins.generator.model;


import java.lang.reflect.Method;

import net.kilger.mockins.generator.valueprovider.ValueProvider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class Stubbing {

    public static final Stubbing EMPTY = new Stubbing();
    
    private final Method method;
    private final ValueProvider<?> valueProvider;
    private final Class<?>[] methodParamTypes;

    private Stubbing() {
        this.method = null;
        this.valueProvider = null;
        this.methodParamTypes = null;
    }
    
    public Stubbing(Method method, ValueProvider<?> valueProvider, Class<?>[] methodParamTypes) {
        this.method = method;
        this.valueProvider = valueProvider;
        this.methodParamTypes = methodParamTypes;
    }

    public Method getMethod() {
        return method;
    }

    public ValueProvider<?> getValueProvider() {
        return valueProvider;
    }

    public Class<?>[] getMethodParamTypes() {
        return methodParamTypes;
    }

    public boolean isEmpty() {
        return method == null;
    }
    
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public Object createValue() {
        return valueProvider.createValue();
    }
    
    public boolean equals(Object o) {
        if (o instanceof Stubbing) {
            Stubbing otherStubbing = (Stubbing) o;
            return new EqualsBuilder()
                .append(method, otherStubbing.method)
                .append(valueProvider, otherStubbing.valueProvider)
                .append(methodParamTypes, otherStubbing.methodParamTypes)
                .isEquals();
        }
        else {
            return false;
        }
    }
    
}
