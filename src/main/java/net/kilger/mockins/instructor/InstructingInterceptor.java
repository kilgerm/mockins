package net.kilger.mockins.instructor;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.handler.NpeHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class InstructingInterceptor<T> implements MethodInterceptor {
    
    private static final String MOCKINS_RESULT_HEADER = 
            "//============================================================================\n" +
            "// * Mockins result *\n" +
            "//============================================================================\n" + "";
    
    
    private final T originalObject;

    public InstructingInterceptor(T originalObject) {
        this.originalObject = originalObject;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;
        try {
            /* 
             * bypassing accessibility is in fact needed in case 
             * the method belongs to a superclass of the instance class.
             */
            method.setAccessible(true);
            result = method.invoke(originalObject, args);
            
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof NullPointerException) {
                NullPointerException originalNpe = (NullPointerException) cause;
                handleNpe(method, args, originalNpe);
            }
            else if (cause != null) {
                throw cause;
            }
            else {
                throw ite;
            }
        }
        return result;
    }

    private void handleNpe(Method method, Object[] args, NullPointerException originalNpe) {
        NpeHandler npeHandler = new NpeHandler(originalObject, method, args);
        Instruction instructions = npeHandler.handle();
        printResults(instructions);
        
        // give back the actual NPE anyway
        throw originalNpe;
    }

    private void printResults(Instruction instructions) {
        System.err.flush();
        System.err.println(MOCKINS_RESULT_HEADER);
        if (instructions != null) {
            System.err.println(instructions);
        }
        else {
            System.err.println("no suitable mocking/stubbing found");
        }
    }
}
