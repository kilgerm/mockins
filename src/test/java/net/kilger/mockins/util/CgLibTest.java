package net.kilger.mockins.util;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.junit.Test;

/** 
 * CGLIB API Test
 */
public class CgLibTest {

    boolean intercepted = false;

    @Test
    public void test() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(A.class);
        Callback callback = new MethodInterceptor() {
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//                System.out.println(">> intercept: " + method + "," + args + "," + proxy);
                Object result = proxy.invokeSuper(obj, args);
//                System.out.println("<< yielded result: " + result);
                intercepted = true;
                return result;
            }
        };
        enhancer.setCallback(callback);
        A o = (A) enhancer.create();
        o.something();
        assertTrue(o.done);
        assertTrue(intercepted);
    }
    
    @SuppressWarnings("unused")
    private static class A {
        public A() {
        }
        
        boolean done = false;
        
        public Object something() {
//            System.out.println("doing something");
            done = true;
            return "X";
        }
    }
    
}
