package net.kilger.mockins.generator.result;

import net.kilger.mockins.generator.result.model.Instruction;

/**
 * Auxiliary methods for tests and asserts with Instructions 
 */
public final class InstructionAssert {
    
    public interface InstructionMatcher {
        boolean matches(Instruction instruction);
    }

    public static void assertHasInstruction(Instruction rootInstruction, final InstructionAssert.InstructionMatcher matcher) {
        if (!hasInstruction(rootInstruction, matcher)) {
            throw new AssertionError("instruction tree must contain an instruction matching " + matcher);
        }
    }

    public static void assertHasNoInstruction(Instruction rootInstruction, InstructionAssert.InstructionMatcher matcher) {
        if (hasInstruction(rootInstruction, matcher)) {
            throw new AssertionError("instruction tree may not have an instruction matching " + matcher);
        }
    }
    
    public static boolean hasInstruction(Instruction rootInstruction, final InstructionAssert.InstructionMatcher matcher) {
        final boolean[] found = {false};
        
        new InstructionTreeWalker() {
            @Override
            public void callback(Instruction instruction) {
                if (matcher.matches(instruction)) {
                    found[0] = true;
                }
            }
        }.walkThrough(rootInstruction);
        return found[0];
    }
    
    private InstructionAssert() {
        // not instantiable
    }

}
