package net.kilger.mockins.generator.valueprovider.impl;

import java.util.Collections;
import java.util.List;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.MockinsContext;


/**
 * Value provider that calls the default constructor
 * 
 * TODO: is it possible to force access to constructor regardless of visibility?
 * clazz.getConstructor() plus setAccessible(true) did not work... 
 */
public class NewInstanceValueProvider implements ValueProvider<Object> {

    private final Class<?> clazz;
    
    private ClassNamer classNamer = MockinsContext.INSTANCE.getClassNamer();

    public NewInstanceValueProvider(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        this.clazz = clazz;
        // test now whether this class can be instantiated directly
        createNewInstance();
    }

    public Object createValue() {
        try {
            return createNewInstance();
            
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createNewInstance() throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    public String code() {
        return "new " + classNamer.className(clazz) + "()";
    }

    public boolean isMocking() {
        return false;
    }

    public List<Stubbing> getStubbings() {
        return Collections.emptyList();
    }

}
    