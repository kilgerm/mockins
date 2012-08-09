package net.kilger.mockins.instructor;

import static org.junit.Assert.*;

public class ClassWithoutZeroArgsConstructor {
    
    public ClassWithoutZeroArgsConstructor(String arg0) {
    }
    
    public ClassWithoutZeroArgsConstructor(String arg0, Object arg1) {
        fail("should not use this constructor");
    }

}