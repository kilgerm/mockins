package net.kilger.mockins.generator.valueprovider;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import net.kilger.mockins.generator.valueprovider.impl.FixedValueProvider;
import net.kilger.mockins.generator.valueprovider.impl.InitializedArrayValueProvider;

import org.junit.Test;

public class InitializedArrayValueProviderTest {

    @Test
    public void testEmptyArray() {
        InitializedArrayValueProvider classUnderTest = new InitializedArrayValueProvider(Object.class);
        
        Object array = classUnderTest.createValue();
        assertNotNull(array);
        assertEquals(0, Array.getLength(array));
    }
    
    @Test
    public void testArrayWithTwoElements() {
        FixedValueProvider vp1 = new FixedValueProvider("1", "$CODE1");
        FixedValueProvider vp2 = new FixedValueProvider("2", "$CODE2");
        InitializedArrayValueProvider classUnderTest = new InitializedArrayValueProvider(Object.class, vp1, vp2);
        
        Object array = classUnderTest.createValue();
        assertNotNull(array);
        assertEquals(2, Array.getLength(array));
        assertEquals("new Object[] {$CODE1, $CODE2}", classUnderTest.code());
    }
    
}
