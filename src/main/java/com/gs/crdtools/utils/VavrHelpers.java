package com.gs.crdtools.utils;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.TreeMap;

public class VavrHelpers {
    private static <T> T checkType(T t, Object debugData, Set<Class<?>> expectedLeafTypes) {
        if (t != null && (t.getClass().getPackage().equals(java.util.List.class.getPackage())
                        || expectedLeafTypes.find(c -> c.isAssignableFrom(t.getClass())).isDefined())) {
            return t;
        }

        throw new IllegalArgumentException(
                "Probably erroneous value - " + t + "(" + (t == null ? "null" : t.getClass()) + "; at " + debugData + ")");
    }

    public static <V> java.util.List<Object> deepToJava(List<V> l, List<Object> debugData,
                                                        Set<Class<?>> expectedLeafTypes) {
        var recursed = l.zipWithIndex().map(t -> {
            var i = t._1;
            var nextStepDebug = debugData.append(t._2);
            if (i instanceof Map<?, ?> m) {
                return deepToJava(m, nextStepDebug, expectedLeafTypes);
            }
            if (i instanceof List<?> subList) {
                return deepToJava(subList, nextStepDebug, expectedLeafTypes);
            }
            return checkType(i, nextStepDebug, expectedLeafTypes);
        });

        return recursed.toJavaList();
    }

    public static <K, V> java.util.Map<K, Object> deepToJava(Map<K, V> m, List<Object> debugData,
                                                             Set<Class<?>> expectedLeafTypes) {
        var recursed = m.map((k, v) -> {
            var nextStepDebug = debugData.append(k);
            if (v instanceof Map<?, ?> subMap) {
                return Tuple.of(k, deepToJava(subMap, nextStepDebug, expectedLeafTypes));
            }
            if (v instanceof List<?> l) {
                return Tuple.of(k, deepToJava(l, nextStepDebug, expectedLeafTypes));
            }
            return Tuple.of(k, checkType(v, nextStepDebug, expectedLeafTypes));
        });

        return new TreeMap<>(recursed.toJavaMap());
    }

    public static <T> T extractByPath(Object k8sResourceRef, String... path) {
        var asList = List.of(path);
        Object ret = k8sResourceRef;
        while (!asList.isEmpty()) {
            //noinspection unchecked
            ret = ((java.util.Map<String, Object>) ret).get(asList.head());
            asList = asList.tail();
        }

        //noinspection unchecked
        return (T) ret;
    }
}
