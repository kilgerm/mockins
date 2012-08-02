package net.kilger.mockins.generator.result.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic instruction that consists of children instructions.
 */
public class CompositeInstruction implements Instruction {

    protected final List<Instruction> components = new ArrayList<Instruction>();
    
    public String message() {
        StringBuilder message = new StringBuilder();
        
        for (Instruction component : components) {
            message.append(component.message());
        }
        
        return message.toString();
    }
    
    public void addComponent(Instruction component) {
        components.add(component);
    }
    
    @Override
    public String toString() {
        return message();
    }

    // TODO: don't expose components here...
    public List<Instruction> getComponents() {
        return components;
    }

}
