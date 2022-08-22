package com.gs.crdtools;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.util.Map;

public record GenerationInformation(String className, String template) {
    public static final String CLASS_AND_LINE_NUMBER_KEY = "com.gs.crdtools/yamlgeneratedby";
    public static final String TEMPLATE_KEY = "com.gs.crdtools/yamltempatedby";

    public GenerationInformation(String className) {
        this(className, "");
    }

    public HashMap<String, String> insertAnnotations(Map<String, String> annotations) {
        var newAnnotations = HashMap.ofAll(Option.of(annotations)
                .getOrElse(java.util.Map.of()));
        var withClassAndLineNumber = newAnnotations.put(
                CLASS_AND_LINE_NUMBER_KEY, classAndLineNumber()
        );

        if (!template().isEmpty()) {
            return withClassAndLineNumber.put(TEMPLATE_KEY, template);
        }

        return withClassAndLineNumber;
    }

    private String classAndLineNumber() {
        return List.of(new Exception().getStackTrace())
                .dropWhile(stackElement -> shouldSkipClassName(stackElement.getClassName()))
                .headOption()
                .map(stackTraceElement -> stackTraceElement.getFileName() + "/" + stackTraceElement.getLineNumber())
                .getOrElse("Unknown");
    }

    private boolean shouldSkipClassName(String className) {
        if (className.equals(this.className()) || className.startsWith("com.gs.crdtools.GenerationInformation")) {
            return true;
        }
        var parts = List.of(className.split("\\."));
        return parts.contains("vavr") || parts.contains("java");
    }
}
