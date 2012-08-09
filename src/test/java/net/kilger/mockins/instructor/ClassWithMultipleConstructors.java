package net.kilger.mockins.instructor;

public class ClassWithMultipleConstructors {

    @SuppressWarnings("unused")
    private ClassWithMultipleConstructors(String arg0) {
    }
    
    public ClassWithMultipleConstructors(String arg0, Integer arg1, Integer arg2) {
    }
    
    protected ClassWithMultipleConstructors(String arg0, Integer arg1) {
    }
    
    ClassWithMultipleConstructors(Integer arg0, String arg1) {
    }
    
}
