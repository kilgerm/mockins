package net.kilger.mockins.util;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

public class MethodByNameComparator implements Comparator<Method> {
    public int compare(Method l, Method r) {
        return new CompareToBuilder().append(l.getName(), r.getName()).toComparison();
    }
}