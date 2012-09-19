package net.kilger.mockins.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class UniqueClassGeneratorTest {

    private UniqueClassGenerator classUnderTest = new UniqueClassGenerator();
    
    @Test
    public void testCreate() {
        Class<?> c = classUnderTest.create();
        assertNotNull(c);
        // c must be a proper subclass of Object
        assertFalse(c.isAssignableFrom(Object.class));
    }
    
    public static class SuperClass {
    };
    
    @Test
    public void testCreateWithSuperClass() {
        Class<?> c = classUnderTest.create(SuperClass.class);
        // c must be a proper subclass of BaseClass
        assertTrue(SuperClass.class.isAssignableFrom(c));
        assertFalse(c.isAssignableFrom(SuperClass.class));
    }
    
    @Test
    public void testCreateUniqueClass() {
        Class<?> c1 = classUnderTest.create();
        Class<?> c2 = classUnderTest.create();
        assertNotNull(c1);
        assertNotNull(c2);
        // mutually non-assignable
        assertFalse(c1.isAssignableFrom(c2));
        assertFalse(c2.isAssignableFrom(c1));
    }
    
    @Test
    public void testCreateUniqueName() {
        Class<?> c1 = classUnderTest.create();
        Class<?> c2 = classUnderTest.create();
        assertNotNull(c1);
        assertNotNull(c2);
        // different names each
        assertNotSame(c1.getName(), c2.getName());
    }

    @Test
    public void testIsGenerated() {
        Class<?> c1 = classUnderTest.create();
        Class<?> c2 = classUnderTest.create(SuperClass.class);
        Class<?> notGenerated1 = String.class;
        Class<?> notGenerated2 = this.getClass();
        
        assertTrue(classUnderTest.isGenerated(c1));
        assertTrue(classUnderTest.isGenerated(c2));
        assertFalse(classUnderTest.isGenerated(notGenerated1));        
        assertFalse(classUnderTest.isGenerated(notGenerated2));
    }
}
