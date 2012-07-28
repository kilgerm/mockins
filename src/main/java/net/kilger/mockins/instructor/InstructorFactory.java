package net.kilger.mockins.instructor;

import net.sf.cglib.proxy.Enhancer;

public class InstructorFactory {

    @SuppressWarnings("unchecked")
    public <T> T create(T classToEnhanceWithInstructor) {
        Class<? extends Object> clazz = classToEnhanceWithInstructor.getClass();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(new Class<?>[] { Instructor.class });
        enhancer.setCallback(new InstructingInterceptor<T>(classToEnhanceWithInstructor));
        return (T) enhancer.create();
    }
}
