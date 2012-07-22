package net.kilger.mockins.generator.result.model;

public class GenericInstruction extends CompositeInstruction {

    private final String message;

    public GenericInstruction(String message) {
        this.message = message;
    }

    public String message() {
        return message + super.message();
    }
    
}
