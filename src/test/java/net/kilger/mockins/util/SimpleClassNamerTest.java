package net.kilger.mockins.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleClassNamerTest {

    SimpleClassNamer classUnderTest = new SimpleClassNamer();
    
    private static class INNER_CLASS {
    }
    
    @Test
    public void testClassLiteral() {
        assertEquals("String.class", classUnderTest.classLiteral(String.class));
        assertEquals("Integer.class", classUnderTest.classLiteral(Integer.class));
    }
    
    @Test
    public void testClassLiteralInnerClass() {
        assertEquals("INNER_CLASS.class", classUnderTest.classLiteral(INNER_CLASS.class));
    }
}
