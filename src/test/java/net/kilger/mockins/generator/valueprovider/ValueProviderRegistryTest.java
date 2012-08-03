package net.kilger.mockins.generator.valueprovider;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.kilger.mockins.generator.valueprovider.impl.EmptyListValueProvider;

import org.junit.Test;

public class ValueProviderRegistryTest {

    @Test
    public void testString() {
        ValueProvider<String> vp = ValueProviderRegistry.providerFor(String.class);
        assertTrue(vp.createValue() instanceof String);
        assertEquals("", vp.createValue());
    }

    @Test
    public void testBoolean() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Boolean.TYPE);
        assertTrue(vp.createValue() instanceof Boolean);
    }

    @Test
    public void testBooleanBox() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Boolean.class);
        assertTrue(vp.createValue() instanceof Boolean);
    }

    @Test
    public void testInt() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Integer.TYPE);
        assertTrue(vp.createValue() instanceof Integer);
    }

    @Test
    public void testInteger() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Integer.class);
        assertTrue(vp.createValue() instanceof Integer);
    }

    @Test
    public void testChar() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Character.TYPE);
        assertTrue(vp.createValue() instanceof Character);
    }

    @Test
    public void testCharacter() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Character.class);
        assertTrue(vp.createValue() instanceof Character);
    }

    @Test
    public void testLong() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Long.TYPE);
        assertTrue(vp.createValue() instanceof Long);
    }

    @Test
    public void testLongBox() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Long.class);
        assertTrue(vp.createValue() instanceof Long);
    }

    @Test
    public void testStringBuffer() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(StringBuffer.class);
        assertTrue(vp.createValue() instanceof StringBuffer);
        assertEquals("new StringBuffer()", vp.code());
    }

    public static class SIMPLE_CLASS {
    }

    @Test
    public void testSimple() {
        ValueProvider<SIMPLE_CLASS> vp = ValueProviderRegistry.providerFor(SIMPLE_CLASS.class);
        assertTrue(vp.createValue() instanceof SIMPLE_CLASS);
    }

    public static class INHERITED_CLASS extends SIMPLE_CLASS {
    }

    @Test
    public void testInherited() {
        ValueProvider<INHERITED_CLASS> vp = ValueProviderRegistry.providerFor(INHERITED_CLASS.class);
        assertTrue(vp.createValue() instanceof INHERITED_CLASS);
    }

    public static interface INTERFACE_CLASS {
    }

    @Test
    public void testInterface() {
        ValueProvider<INTERFACE_CLASS> vp = ValueProviderRegistry.providerFor(INTERFACE_CLASS.class);
        assertTrue(vp.createValue() instanceof INTERFACE_CLASS);
    }

    private static enum ENUM_CLASS {
        E1, E2;
    }

    @Test
    public void testEnum() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(ENUM_CLASS.class);
        assertTrue(vp.createValue() instanceof ENUM_CLASS);
        assertEquals(ENUM_CLASS.E1, vp.createValue());
        assertEquals("ENUM_CLASS.E1", vp.code());
    }

    // public access to allow instantiation from other classes
    public static final class FINAL_CLASS {
    }

    @Test
    public void testFinalClass() {
        ValueProvider<FINAL_CLASS> vp = ValueProviderRegistry.providerFor(FINAL_CLASS.class);
        assertTrue(vp.createValue() instanceof FINAL_CLASS);
        assertEquals("new FINAL_CLASS()", vp.code());
    }

    static class A {
    }
    
    @Test
    public void testArray() throws Exception {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(A[].class);
        Object value = vp.createValue();
        assertTrue(value.getClass().isArray());
        assertEquals(0, Array.getLength(value));
    }
    
    @Test
    public void testArrayPrimitive() throws Exception {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(int[].class);
        Object value = vp.createValue();
        assertTrue(value.getClass().isArray());
        assertEquals(0, Array.getLength(value));
    }
    
    @Test
    public void testList() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(List.class);
        Object value = vp.createValue();
        assertTrue(value instanceof List);
        @SuppressWarnings("unchecked")
        List<A> list = (List<A>) value;
        assertEquals(0, list.size());
        assertNotNull(list.iterator());

        // test that we can use it as a modifiable list
        // with the element type we have in mind
        A a = new A();
        list.add(a);
        assertTrue(list.contains(a));
    }
    
    @Test
    public void testMap() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Map.class);
        Object value = vp.createValue();
        assertTrue(value instanceof Map);
        @SuppressWarnings("unchecked")
        Map<A, A> map = (Map<A, A>) value;
        assertEquals(0, map.size());
        assertNotNull(map.keySet());

        // test that we can use it as a modifiable map
        // with the element type we have in mind
        A a = new A();
        map.put(a, a);
        assertTrue(map.containsKey(a));
        assertTrue(map.containsValue(a));
    }

    @Test
    public void testSet() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Set.class);
        Object value = vp.createValue();
        assertTrue(value instanceof Set);
        @SuppressWarnings("unchecked")
        Set<A> set = (Set<A>) value;
        assertEquals(0, set.size());
        assertNotNull(set.iterator());

        // test that we can use it as a modifiable set
        // with the element type we have in mind
        A a = new A();
        set.add(a);
        assertTrue(set.contains(a));

        set.add(a);
        assertEquals(1, set.size());
    }
    
    @Test
    public void testCollection() {
        ValueProvider<?> vp = ValueProviderRegistry.providerFor(Collection.class);
        Object value = vp.createValue();
        assertTrue(vp instanceof EmptyListValueProvider);
        assertTrue(value instanceof Collection);
        @SuppressWarnings("unchecked")
        Collection<A> collection = (Collection<A>) value;
        assertEquals(0, collection.size());
        assertNotNull(collection.iterator());

        // test that we can use it as a modifiable list
        // with the element type we have in mind
        A a = new A();
        collection.add(a);
        assertTrue(collection.contains(a));
    }
}
