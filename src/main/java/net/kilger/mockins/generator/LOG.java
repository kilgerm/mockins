package net.kilger.mockins.generator;

public abstract class LOG {

    public static void l(Object... log) {
        for (Object l : log) {
            System.out.print(l);
            System.out.print(" ");
        }
        System.out.println();
    }
    
}
