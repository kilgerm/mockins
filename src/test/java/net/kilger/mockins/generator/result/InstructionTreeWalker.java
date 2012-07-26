package net.kilger.mockins.generator.result;

import net.kilger.mockins.generator.result.model.CompositeInstruction;
import net.kilger.mockins.generator.result.model.Instruction;

/**
 * Iterates through the tree of instructions starting at the given root instruction
 * and invokes callback for every node.
 * (search is depth-first)
 */
public abstract class InstructionTreeWalker {

    public final void walkThrough(Instruction instruction) {
        callback(instruction);

        if (instruction instanceof CompositeInstruction) {
            CompositeInstruction compositeInstruction = (CompositeInstruction) instruction;
            for (Instruction component : compositeInstruction.getComponents()) {
                walkThrough(component);
            }
        }
    }

    /** method to be called on every instruction in the tree */
    public abstract void callback(Instruction instruction);
    
}
