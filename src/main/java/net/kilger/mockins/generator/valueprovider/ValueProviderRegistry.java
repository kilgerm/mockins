package net.kilger.mockins.generator.valueprovider;

import java.util.ArrayList;
import java.util.List;

import net.kilger.mockins.generator.valueprovider.factory.ArrayValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.EnumValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.FinalClassValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.ListValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.MapValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.MockValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.PrimitiveValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.SetValueProviderFactory;
import net.kilger.mockins.generator.valueprovider.factory.StringValueProviderFactory;

/**
 * Provides search for a suitable ValueProvider for a given class
 */
public class ValueProviderRegistry {
    
    private static final List<ValueProviderFactory> registeredValueProviderFactories = new ArrayList<ValueProviderFactory>();

    static {
        // <-- more specific
        registerValueProviderFactory(new StringValueProviderFactory());
        registerValueProviderFactory(new PrimitiveValueProviderFactory());
        registerValueProviderFactory(new EnumValueProviderFactory());
        registerValueProviderFactory(new ArrayValueProviderFactory());
        registerValueProviderFactory(new ListValueProviderFactory());
        registerValueProviderFactory(new MapValueProviderFactory());
        registerValueProviderFactory(new SetValueProviderFactory());
        registerValueProviderFactory(new FinalClassValueProviderFactory());
        registerValueProviderFactory(new MockValueProviderFactory());
        // --> more generic
    }

    private static void registerValueProviderFactory(ValueProviderFactory valueProvider) {
        registeredValueProviderFactories.add(valueProvider);
    }

    public static <T> ValueProvider<T> providerFor(Class<T> clazz) {
        // specific match?
        for (ValueProviderFactory factory : registeredValueProviderFactories) {
            if (factory.specificFor(clazz)) {
                return factory.valueProvider(clazz);
            }
        }
        // general match?
        for (ValueProviderFactory factory : registeredValueProviderFactories) {
            if (factory.canHandle(clazz)) {
                return factory.valueProvider(clazz);
            }
        }
        
        throw new IllegalArgumentException("no ValueProviderFactory for class " + clazz.getCanonicalName());
    }

}
