package net.kilger.mockins.util;

public class SimpleClassNamer implements ClassNamer {

    private static final String CLASS_SUFFIX = ".class";

    public String className(Class<?> clazz) {
        return clazz.getSimpleName();
    }
    
    public String classLiteral(Class<?> clazz) {
        return className(clazz) + CLASS_SUFFIX;
    }

}
