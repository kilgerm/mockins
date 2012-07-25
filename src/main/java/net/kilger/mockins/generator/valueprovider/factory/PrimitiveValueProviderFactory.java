package net.kilger.mockins.generator.valueprovider.factory;

import java.util.Arrays;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.FixedValueProvider;

public class PrimitiveValueProviderFactory implements ValueProviderFactory {

    @SuppressWarnings("unchecked")
    private static final List<Class<?>> PRIMITIVE_TYPES_AND_BOXED =  Arrays.asList(
            (Class<?>) 
            Boolean.class, Boolean.TYPE,
            Byte.class, Byte.TYPE,
            Short.class, Short.TYPE,
            Integer.class, Integer.TYPE,
            Long.class, Long.TYPE,
            Float.class, Float.TYPE,
            Double.class, Double.TYPE,
            Character.class, Character.TYPE
            );
    
    @SuppressWarnings("unchecked")
    public <T> ValueProvider<T> valueProvider(Class<T> clazz) {
        if (Boolean.class.equals(clazz) || Boolean.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Boolean.FALSE, "false");
        }
        if (Byte.class.equals(clazz) || Byte.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Byte.valueOf((byte) 0), "0");
        }
        if (Short.class.equals(clazz) || Short.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Short.valueOf((short) 0), "0");
        }
        if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Integer.valueOf(0), "0");
        }
        if (Long.class.equals(clazz) || Long.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Long.valueOf(0L), "0L");
        }
        if (Float.class.equals(clazz) || Float.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Float.valueOf(0.0f), "0.0f");
        }
        if (Double.class.equals(clazz) || Double.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider(Double.valueOf(0.0), "0.0");
        }
        if (Character.class.equals(clazz) || Character.TYPE.equals(clazz)) {
            return (ValueProvider<T>) new FixedValueProvider('x', "'x'");
        }
        throw new IllegalArgumentException("cannot handle class " + clazz);
    }

    public boolean specificFor(Class<?> clazz) {
        return PRIMITIVE_TYPES_AND_BOXED.contains(clazz);
    }

    public boolean canHandle(Class<?> clazz) {
        return specificFor(clazz);
    }


}
