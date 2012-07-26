package net.kilger.mockins;

import static org.junit.Assert.*;

import net.kilger.mockins.instructor.Instructor;

import org.junit.Test;

public class MockinsTest {

    public static class MyArg {
        public String data() {
            return "";
        }
    }
    
    public static class MyClass {
        public String myMethod(String p0, MyArg p1) {
            return p0.toLowerCase() + p1.data().toLowerCase();
        }
    }
    
    @Test
        public void testInstructoror() {
            MyClass myClass = new MyClass();
            
            Object instructor = Mockins.instructor(myClass);
            
            assertTrue(instructor instanceof MyClass);
            assertTrue(instructor instanceof Instructor);
        }
    
    @Test
        public void testInstructororCall() {
            MyClass myClass = new MyClass();
            
            MyClass instructor = Mockins.instructor(myClass);
            instructor.myMethod("", new MyArg());
        }
    
   
}
