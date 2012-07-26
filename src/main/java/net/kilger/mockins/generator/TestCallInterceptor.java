package net.kilger.mockins.generator;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.kilger.mockins.generator.result.model.Instruction;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TestCallInterceptor<T> implements MethodInterceptor {
    
    private static final String MOCKINS_RESULT_HEADER = "==============================================================================\n" +
            "* START Mockins result *\n" +
            "==============================================================================\n" + "";
    
    private final T originalObject;

    public TestCallInterceptor(T originalObject) {
        this.originalObject = originalObject;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;
        try {
            // not using the proxy now:
            //            result = proxy.invokeSuper(obj, args);
            method.invoke(originalObject, args);
        } catch (InvocationTargetException ite) {
//          } catch (NullPointerException originalNpe) {
            Throwable cause = ite.getCause();
            if (cause instanceof NullPointerException) {
                NullPointerException originalNpe = (NullPointerException) cause;
                NpeHandler npeHandler = new NpeHandler(originalObject, method, args);
                Instruction instructions = npeHandler.handle();

                System.err.println(MOCKINS_RESULT_HEADER);
                if (instructions != null) {
                    System.err.println(instructions);
                }
                else {
                    System.err.println("no suitable mocking/stubbing found");
                }
                throw originalNpe;
            }
            else {
                throw cause;
            }
        }
        return result;
    }
}
