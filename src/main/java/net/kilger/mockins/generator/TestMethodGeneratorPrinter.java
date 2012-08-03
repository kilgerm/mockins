package net.kilger.mockins.generator;

import java.lang.reflect.Method;

import net.kilger.mockins.common.MockinsResultPrinter;
import net.kilger.mockins.generator.result.model.Instruction;

public class TestMethodGeneratorPrinter extends MockinsResultPrinter {

    private final Class<?> clazz;
    private final Method methodToTest;

    public TestMethodGeneratorPrinter(Class<?> clazz, Method methodToTest) {
        this.clazz = clazz;
        this.methodToTest = methodToTest;
    }

    public void generateTestSkeletonAndPrint() {
        Instruction instruction = new TestMethodGenerator().generateTestSkeleton(clazz, methodToTest);
        printResults(instruction);
    }

}
