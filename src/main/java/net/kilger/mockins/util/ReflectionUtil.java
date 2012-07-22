package net.kilger.mockins.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectionUtil {

    private Class<?> clazz;
    private boolean caseSensitive = false;
    
    public ReflectionUtil(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<Method> getDeclaredMethods(String name) {
        List<Method> methods = new ArrayList<Method>();
        
        for (Method candidate : clazz.getDeclaredMethods()) {
            if (nameMatches(candidate, name)) {
                methods.add(candidate);
            }
        }
        
        return methods;
    }
    
    public Method getDeclaredMethod(String name) {
        List<Method> methods = getDeclaredMethods(name);
        
        if (methods.size() == 0) {
            throw new IllegalArgumentException("no method named " + name + " in " + clazz.getCanonicalName());
        }
        if (methods.size() > 1) {
            throw new IllegalArgumentException("found multiple methods named " + name + " in " + clazz.getCanonicalName());
        }
        return methods.get(0);
    }

    public List<Method> getAllMethods() {
        return getAllMethods(false);
    }
    
    public List<Method> getAllMethods(boolean includeObjectMethods) {
        List<Method> methodsWithDuplicates = getAllMethodsInternal(includeObjectMethods);
        // remove duplicates from overridden methods
        ArrayList<Method> methods = new ArrayList<Method>(methodsWithDuplicates.size());

        // TODO: probably ugly complexity - might have to optimize for classes with many methods
        for (Method candidate : methodsWithDuplicates) {
            boolean alreadyPresent = hasMethodWithSameSignature(candidate, methods);
            if (!alreadyPresent) {
                methods.add(candidate);
            }
        }
        
        methods.trimToSize();
        return methods;
    }

    boolean hasMethodWithSameSignature(Method signatureToSearchFor, Collection<Method> methods) {
        boolean alreadyPresent = false;
        for (Method methodToCheck : methods) {
            if (hasSameSignature(methodToCheck, signatureToSearchFor)) {
                alreadyPresent = true;
                break;
            }
        }
        return alreadyPresent;
    }

    private boolean hasSameSignature(Method a, Method b) {
        return a.getName().equals(b.getName()) 
                && Arrays.equals(a.getParameterTypes(), b.getParameterTypes());
    }

    private List<Method> getAllMethodsInternal(boolean includeObjectMethods) {
        List<Method> methods = new ArrayList<Method>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        
        Class<?> superclass = clazz.getSuperclass();
        // for primitives or Object, superclass will be null
        if (superclass != null) {
            if (includeObjectMethods || !Object.class.equals(superclass)) {
                // recursively add methods
                // TODO: might want to save recursive instances to avoid duplicate instantiation
                methods.addAll(new ReflectionUtil(superclass).getAllMethodsInternal(includeObjectMethods));
            }
        }
        return methods;
    }

    private boolean nameMatches(Method candidate, String name) {
        return candidate.getName().equals(name) 
                || (caseSensitive && candidate.getName().equalsIgnoreCase(name));
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public Object callMethod(Object mock, String methodName, Object... args) throws IllegalAccessException, InvocationTargetException {
        Method method = getDeclaredMethod(methodName);
        return callMethod(mock, method, args);
    }
    
    private Object callMethod(Object obj, Method method, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return method.invoke(obj, args);
    }


}
