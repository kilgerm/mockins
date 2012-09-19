package net.kilger.mockins.util;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class UniqueClassGenerator {

    private static final String SEPARATOR = "$$";
    private static final String UNIQUE_CLASS_PREFIX = UniqueClassGenerator.class.getName() + SEPARATOR;

    private static class CounterNamingPolicy implements NamingPolicy {

        private AtomicLong counter = new AtomicLong(0);
        private final String baseName;
        
        public CounterNamingPolicy(String baseName) {
            this.baseName = baseName;
        }

        public String getClassName(String prefix, String source, Object key, Predicate names) {
            String counterHex = Long.toHexString(counter.getAndIncrement());
            return baseName + counterHex;
        }
    }
    
    private static NamingPolicy namingPolicy = new CounterNamingPolicy(UNIQUE_CLASS_PREFIX);

    public Class<?> create() {
        return create(null);
    }

    public Class<?> create(Class<?> superClass) {
        Object obj = createObj(superClass);
        return obj.getClass();
    }

    private Object createObj(Class<?> superClass) {
        Enhancer e = new Enhancer();
        if (superClass != null) {
            e.setSuperclass(superClass);
        }
        e.setNamingPolicy(namingPolicy);

        e.setUseCache(false); // don't use caching, we want different classes each!
        e.setCallback(NoOp.INSTANCE);
        return e.create();
    }

    // actually implemented statically
    public boolean isGenerated(Class<?> clazz) {
        return clazz.getName().startsWith(UNIQUE_CLASS_PREFIX);
    }
}
