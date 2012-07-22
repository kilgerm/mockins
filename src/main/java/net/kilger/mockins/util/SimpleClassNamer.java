package net.kilger.mockins.util;

public class SimpleClassNamer implements ClassNamer {

    private static final String CLASS_SUFFIX = ".class";

    @Override
    public String className(Class<?> clazz) {
        return clazz.getSimpleName();
    }
    
    @Override
    public String classLiteral(Class<?> clazz) {
        return className(clazz) + CLASS_SUFFIX;
    }

}
