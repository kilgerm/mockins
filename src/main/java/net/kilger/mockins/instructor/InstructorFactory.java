package net.kilger.mockins.instructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.factory.PrimitiveValueProviderFactory;
import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.lang.builder.CompareToBuilder;

public class InstructorFactory {

    @SuppressWarnings("unchecked")
    public <T> T create(T classToEnhanceWithInstructor) {
        Class<? extends Object> clazz = classToEnhanceWithInstructor.getClass();

        Enhancer enhancer = prepareEnhancer(clazz, classToEnhanceWithInstructor);

        Object instance = createInstance(clazz, enhancer);
        
        return (T) instance;
    }

    private Object createInstance(Class<? extends Object> clazz, Enhancer enhancer) {
        Constructor<?> constructor = getSuitableConstructor(clazz);
        Class<?>[] constructorArgTypes = constructor.getParameterTypes();
        Object[] constructorArgs = buildConstructorArgs(constructorArgTypes);
        Object instance = enhancer.create(constructorArgTypes, constructorArgs);
        return instance;
    }

    private <T> Enhancer prepareEnhancer(Class<? extends Object> clazz, T classToEnhanceWithInstructor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(new Class<?>[] { Instructor.class });
        enhancer.setCallback(new InstructingInterceptor<T>(classToEnhanceWithInstructor));
        return enhancer;
    }

    private Object[] buildConstructorArgs(Class<?>[] constructorArgTypes) {
        Object[] args = new Object[constructorArgTypes.length];
        for (int i = 0; i < constructorArgTypes.length; i++) {
            if (constructorArgTypes[i].isPrimitive()) {
                // for primitive types
                args[i] = new PrimitiveValueProviderFactory().valueProvider(constructorArgTypes[i]).createValue();
            }
            // otherwise, init value of null should do
        }
        return args;
    }

    private Constructor<?> getSuitableConstructor(Class<? extends Object> clazz) {
        List<Constructor<?>> declaredConstructors = Arrays.asList(clazz.getDeclaredConstructors());
        
        Collections.sort(declaredConstructors, CONSTRUCTOR_SORTER);
        if (declaredConstructors.size() == 0) {
            throw new IllegalArgumentException("class " + clazz.getCanonicalName() + " has no constructor");
        }
        Constructor<?> constructor = declaredConstructors.get(0);
        return constructor;
    }

    /**
     * sorts constructors by
     * a) accesibility: public > default > protected > private
     * b) number of args (less args first)
     */
    private final Comparator<Constructor<?>> CONSTRUCTOR_SORTER = new Comparator<Constructor<?>>() {

        public int compare(Constructor<?> arg0, Constructor<?> arg1) {
            return new CompareToBuilder()
                .append(!Modifier.isPublic(arg0.getModifiers()), !Modifier.isPublic(arg1.getModifiers())) // public constructors first
                .append(!Modifier.isProtected(arg0.getModifiers()), !Modifier.isProtected(arg1.getModifiers())) // then protected
                .append(Modifier.isPrivate(arg0.getModifiers()), Modifier.isPrivate(arg1.getModifiers())) // then not private (implicit: default visibility first)
                .append(arg0.getParameterTypes().length, arg1.getParameterTypes().length) // prefer constructor with less args
                .append(arg0, arg1)
                .toComparison();
        }
        
    };

}
