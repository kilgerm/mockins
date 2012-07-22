package net.kilger.mockins.util;


public final class ClassNamerHolder {

    private ClassNamerHolder() {
        // not instantiable
    }

    private static ClassNamer classNamer = defaultClassNamer();

    private static ClassNamer defaultClassNamer() {
        return new SimpleClassNamer();
    }
    
    public static ClassNamer getClassNamer() {
        return classNamer;
    }

    public static void setClassNamer(ClassNamer classNamer) {
        ClassNamerHolder.classNamer = classNamer;
    }

}
