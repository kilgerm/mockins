package net.kilger.mockins.util.impl;

import net.kilger.mockins.util.LocalVarNamer;

public class SimpleLocalVarNamer implements LocalVarNamer {

    public SimpleLocalVarNamer(String prefix, int counter) {
        this.counter = counter;
        this.prefix = prefix;
    }

    public SimpleLocalVarNamer() {
    }

    private int counter = 0;
    private String prefix = "x";
    
    public String localVarName(Class<?> type) {
        return prefix + (counter++);
    }

}
