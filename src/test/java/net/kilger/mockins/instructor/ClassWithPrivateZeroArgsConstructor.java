package net.kilger.mockins.instructor;

import static org.junit.Assert.*;

public class ClassWithPrivateZeroArgsConstructor {
    
    @SuppressWarnings("unused")
    private ClassWithPrivateZeroArgsConstructor() {
        fail("should not call this constructor");
    }
    
    public ClassWithPrivateZeroArgsConstructor(String arg0) {
    }

}