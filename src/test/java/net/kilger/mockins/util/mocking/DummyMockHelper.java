package net.kilger.mockins.util.mocking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.kilger.mockins.analysis.model.Stubbing;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.factory.PrimitiveValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.impl.FixedValueProvider;
import net.kilger.mockins.util.ClassNamer;
import net.kilger.mockins.util.SimpleClassNamer;
import net.kilger.mockins.util.mocking.impl.BaseMockHelper;
import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * NOT quite a mocking helper - but uses cglib enhancer (which is used anyway)
 * to provide very, very basic mocking functionality.
 * 
 * Used for tests of Mockins functionality isolated from mocking frameworks.
 */
public class DummyMockHelper extends BaseMockHelper {

    private static final String MOCK_CLASS_PREFIX = "DummyMockHelper.";

    private static Map<Object, Map<Method, Stubbing>> stubbingsMap = new HashMap<Object, Map<Method, Stubbing>>();
    
    private static DefaultNamingPolicy NAMING_POLICY = new DefaultNamingPolicy() {
        @Override
        public String getClassName(String prefix, String source, Object key, Predicate names) {
            return MOCK_CLASS_PREFIX + super.getClassName(prefix, source, key, names);
        }};

    private static class DummyMockHelperInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            // don't ever stub equals or hashCode (TODO: check signature)
            if (method.getName().equals("equals") || method.getName().equals("hashCode") ) {
                return proxy.invokeSuper(obj, args);
            }
            if (method.getName().equals("toString")) {
                return "DUMMY";
            }
            
            Map<Method, Stubbing> stubbingsForMock = stubbingsMap.get(obj);
            if (stubbingsForMock != null) {
                if (stubbingsForMock.containsKey(method)) {
                    // return the stubbed value - no check on args
                    Stubbing stubbing = stubbingsForMock.get(method);
                    Object value = stubbing.getValueProvider().createValue();
                    return value;
                } 
            }
            
            // no stub: default to null or primitive
            if (method.getReturnType().isPrimitive()) {
                ValueProvider<?> vp = new PrimitiveValueProviderFactory().valueProvider(method.getReturnType());
                Object value = vp.createValue();
                return value;
            }
            return null;
        }
    }
    
    // ---
    
    private ClassNamer classNamer = new SimpleClassNamer();
    
    public boolean isMock(Object object) {
        return object.toString().startsWith(MOCK_CLASS_PREFIX);
    }

    public void reset(Object mock) {
        if (stubbingsMap.containsKey(mock)) {
            stubbingsMap.remove(mock);
        }
    }

    public String addStubCode(String mockName, Stubbing stubbing) {
        return "$MOCK.STUB(" + mockName + "." + stubbing.getMethod().getName() + ", " + stubbing + ")";
    }

    public void prepareMock(Object mock) {
        // nothing to do here
    }

    public String prepareMockCode(String mockName) {
        return "$MOCK.PREPARE(" + mockName + ")";
    }

    public String argMatcherAnyCode(Class<?> typeToMock) {
        return "$MOCK.ANY(" + classNamer.classLiteral(typeToMock) + ")";
    }

    @SuppressWarnings("unchecked")
    public <T> T createMock(Class<T> clazz) {
        Enhancer e = new Enhancer();
        if (clazz.isInterface()) {
            e.setInterfaces(new Class[] { clazz });
        }
        else {
            e.setSuperclass(clazz);
        }
        
        e.setNamingPolicy(NAMING_POLICY);
        e.setCallback(new DummyMockHelperInterceptor());
        
        Object enhanced = e.create();
        return (T) enhanced;
    }

    public String createMockCode(Class<?> clazz) {
        return "$MOCK.CREATE(" + classNamer.classLiteral(clazz) + ")";
    }

    @Override
    protected Object argMatcherAny(Class<?> clazz) {
        // we are not using matchers actually
        return null;
    }

    @Override
    public void stubMethod(Object mock, Method method, Object value, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Map<Method, Stubbing> stubbingsForMock = stubbingsMap.get(mock);
        if (stubbingsForMock == null) {
            stubbingsForMock = new HashMap<Method, Stubbing>();
            stubbingsMap.put(mock, stubbingsForMock);
        }
        ValueProvider<?> valueProvider = new FixedValueProvider(value, "$GIVEN");
        Stubbing stubbing = new Stubbing(method, valueProvider, method.getParameterTypes());
        stubbingsForMock.put(method, stubbing);
    }

}
