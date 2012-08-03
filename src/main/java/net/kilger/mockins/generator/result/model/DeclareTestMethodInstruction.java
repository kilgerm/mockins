package net.kilger.mockins.generator.result.model;


public class DeclareTestMethodInstruction extends CompositeInstruction {

    private static final String NEWLINE = "\n";
    
    private final String nameOfTestedMethod;
    
    public DeclareTestMethodInstruction(String nameOfTestedMethod) {
        super();
        this.nameOfTestedMethod = nameOfTestedMethod;
    }

    @Override
    public String message() {
        StringBuilder sb = new StringBuilder();
        
        // create simple a JUnit/TestNG style test
        sb.append("@Test").append(NEWLINE);
        sb.append("public void ")
            .append(testMethodName())
            .append("() {")
            .append(NEWLINE);
        
        sb.append(super.message());
        
        sb.append("}");
        return sb.toString();
    }

    private String testMethodName() {
        return "test" + nameOfTestedMethod.substring(0, 1).toUpperCase() + nameOfTestedMethod.substring(1);
    }

    public String getNameOfTestedMethod() {
        return nameOfTestedMethod;
    }
    
}
