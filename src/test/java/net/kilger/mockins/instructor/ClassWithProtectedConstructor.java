package net.kilger.mockins.instructor;

import static org.junit.Assert.*;

public class ClassWithProtectedConstructor {
    @SuppressWarnings("unused")
    private ClassWithProtectedConstructor() {
        fail("should not call this constructor");
    }
    
    protected ClassWithProtectedConstructor(String carg0) {
    }

}