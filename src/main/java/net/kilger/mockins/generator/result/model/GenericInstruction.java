package net.kilger.mockins.generator.result.model;

/**
 * Generic instruction type with message and children.
 */
public class GenericInstruction extends CompositeInstruction {

    private final String message;

    public GenericInstruction(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message + super.message();
    }
    
}
