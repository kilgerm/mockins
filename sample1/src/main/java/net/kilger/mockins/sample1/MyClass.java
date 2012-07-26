package net.kilger.mockins.sample1;

public class MyClass {

    SomeOtherClass source;
    
    public int myMethod(SomeOtherClass input, Object unused) {
        int sum = 0;
        
        sum += input.hashCode(); // input may not be null
        sum += input.getData().length(); // input.getData() may not be null
        
        return sum;
    }
    
}
