package net.kilger.mockins.common;

import net.kilger.mockins.generator.result.model.Instruction;

public class MockinsResultPrinter {

    private static final String MOCKINS_RESULT_HEADER = 
                "//============================================================================\n" +
                "// * Mockins result *\n" +
                "//============================================================================\n" + "";

    protected void printResults(Instruction instructions) {
        System.err.flush();
        System.err.println(MOCKINS_RESULT_HEADER);
        if (instructions != null) {
            System.err.println(instructions);
        }
        else {
            System.err.println("no result");
        }
    }

}
