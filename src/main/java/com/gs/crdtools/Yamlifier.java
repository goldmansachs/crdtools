package com.gs.crdtools;

import com.resare.nryaml.YAMLUtil;
import com.resare.nryaml.YAMLValue;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.common.KubernetesType;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.stream.Collectors;

public class Yamlifier {

    private static final String FILL_IN_HASH = "__FILL_IN_HASH";
    private static final String REV_KEY = "BUILD_SCM_REVISION";
    private static final String STATUS_KEY = "BUILD_SCM_STATUS";

    record ApiGroupVersion(String group, String version) {

        String getGroupAndVersion() {
            return Option.of(group).filter(g -> !g.isEmpty()).getOrElse("") +
                    "/" + Option.of(version).filter(v -> !v.isEmpty()).getOrElse("");
        }
    }

    record KindR(Class<? extends KubernetesObject> clazz, ApiGroupVersion apiGroupVersion) {

        String getSingularName() {
            return clazz.getSimpleName();
        }

    }

    static <T extends KubernetesObject> T base(Class<T> clazz, String name) {
        ApiInformation apiInfo;
        try {
            apiInfo = clazz.getAnnotation(ApiInformation.class);
        } catch (Exception e) {
            throw new RuntimeException("Annotation ApiInformation not found on class " + clazz.getName());
        }

        var group = apiInfo.group();
        var version = apiInfo.version();

        var k8sKind = new KindR(clazz, new ApiGroupVersion(group, version));
        return instantiateK8sClass(clazz,
                k8sKind.apiGroupVersion.getGroupAndVersion(),
                k8sKind.getSingularName(),
                name,
                YAMLUtil.fromBare(java.util.Map.of()),
                YAMLUtil.fromBare(java.util.Map.of()));
    }

    private static String sanitizeName(String name) {
        if (name.equals(FILL_IN_HASH)) {
            return name;
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Nothing to work with - name was \"" + name + "\"");
        }

        name = name.toLowerCase().replaceAll("[^0-9a-z_.\\-]", "-");

        if (name.length() >= 254) {
            name = name.substring(0, 254);
        }

        return name;
    }

    private static Map<String, String> fillableLabels() {
        return List.of(REV_KEY, STATUS_KEY)
                .toMap(key -> Tuple.of(labelOf(key), placeholderValue(key)));
    }

    private static String labelOf(String key) {
        return key.toLowerCase().replaceAll("_", "");
    }

    private static String placeholderValue(String key) {
        return "__FILL_IN_" + key + "__";
    }

    static Map<String, Object> base(String apiVersion,
                                            String kind,
                                            String name,
                                            YAMLValue labels,
                                            YAMLValue annotations) {
        if (!name.equals(sanitizeName(name))) {
            throw new IllegalArgumentException(
                    "Name must be shorter than 254 chars, and all alphanumeric, '_', '-' or '.' - it was \""
                            + name + "\"");
        }

        var generatingLocation = new GenerationInformation(Yamlifier.class.getName());
        var newAnnotations = generatingLocation
                .insertAnnotations((java.util.Map<String, String>) annotations.toBareObject());
        var newLabels = HashMap.ofAll((java.util.Map<String, String>) labels.toBareObject())
                .merge(fillableLabels());

        var metadata = HashMap.of(
                "name", name,
                "annotations", newAnnotations,
                "labels", newLabels
        );

        return HashMap.of(
                "apiVersion", apiVersion,
                "kind", kind,
                "metadata", metadata
        );
    }

    static java.util.Map<?, ?> toJavaMapRecursive(java.util.Map<?, ?> map) {
        return map.entrySet().stream().map(entry -> {
            var key = entry.getKey();
            var value = entry.getValue();
            if (value instanceof Map) {
                return Tuple.of(key, toJavaMapRecursive(((Map<?, ?>) value).toJavaMap()));
            } else {
                return Tuple.of(key, value);
            }
        }).collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
    }

    static <V extends KubernetesObject> V instantiateK8sClass(Class<V> clazz,
                                                                     String apiGroupVersion,
                                                                     String kind,
                                                                     String name,
                                                                     YAMLValue labels,
                                                                     YAMLValue annotations) {
        try {
            V instance = clazz.getDeclaredConstructor().newInstance();
            var base = base(apiGroupVersion, kind, name, labels, annotations);
            var baseParsed = YAMLUtil.fromBare(toJavaMapRecursive(base.toJavaMap()));

            var mergedAnnotations = baseParsed.asMapping()
                    .get("metadata").asMapping()
                    .get("annotations");

            var mergedLabels = baseParsed.asMapping()
                    .get("metadata").asMapping()
                    .get("labels");

            clazz.getMethod("apiVersion", String.class).invoke(instance, apiGroupVersion);
            clazz.getMethod("kind", String.class).invoke(instance, kind);
            clazz.getMethod("metadata", V1ObjectMeta.class).invoke(instance, new V1ObjectMeta()
                    .name(name)
                    .labels((java.util.Map<String, String>) mergedLabels.toBareObject())
                    .annotations((java.util.Map<String, String>) mergedAnnotations.toBareObject()));

            return instance;
        } catch (Exception e) {
            final var classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
            throw new RuntimeException("Could not instantiate class " + clazz.getName() + "." +
                    " Class location at: " + classPath);
        }
    }

    public static byte[] yamlify(Object obj) {
        if (obj instanceof KubernetesType k8s0) {
            return io.kubernetes.client.util.Yaml.dumpAll(List.of(k8s0).iterator()).getBytes();
        }

        return yamlifyFinal(obj);
    }

    private static byte[] yamlifyFinal(Object o) {
        var dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(dumperOptions).dump(o).getBytes();
    }
}
