package net.kilger.mockins.generator.result.model;


public class LineCommentInstruction implements Instruction {

    private final String comment;
    
    public LineCommentInstruction(String comment) {
        this.comment = comment;
    }

    public String message() {
        return "// " + comment + "\n";
    }

}
