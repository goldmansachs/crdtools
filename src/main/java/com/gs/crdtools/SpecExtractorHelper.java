package com.gs.crdtools;

import com.resare.nryaml.YAMLMapping;
import com.resare.nryaml.YAMLSequence;
import com.resare.nryaml.YAMLUtil;
import com.resare.nryaml.YAMLValue;
import io.vavr.Tuple2;
import io.vavr.Tuple4;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * A helper class to extract the openAPI specs from a given yaml file in the format of a string.
 */
class SpecExtractorHelper {

    record Metadata(String group, String version) {}
    // A single openapi spec, yaml serialized into a string and map between class names
    // and a tuple holding version and group strings
    record Spec(String openapiSpec, Map<String, Metadata> metadata) {}

    static Spec createSpec(List<YAMLMapping> crds) {
        Map<String, Metadata> metadata = HashMap.empty();
        Map<String, YAMLMapping> schemas = HashMap.empty();
        for (var tuple : crds.map(SpecExtractorHelper::handleCRD)) {
            schemas = schemas.put(tuple._1, fixSchema(tuple._4));
            metadata = metadata.put(tuple._1, new Metadata(tuple._3, tuple._2));
        }
        return new Spec(innerCreateSpec(schemas), metadata);
    }

    // extract the relevant parts of a crd into a tuple
    private static Tuple4<String, String, String, YAMLMapping> handleCRD(YAMLMapping y) {
        var kind = y.get("spec")
                .asMapping().get("names")
                .asMapping().get("kind")
                .asString();

        var group = y.get("spec")
                .asMapping().get("group").asString();

        var withStorage = getLatestVersion(y.get("spec").asMapping().get("versions").asSequence());

        var version = withStorage.asMapping().get("name").asString();

        var schema = withStorage
                .asMapping().get("schema")
                .asMapping().get("openAPIV3Schema").asMapping();

        return new Tuple4<>(kind, version, group, schema);
    }

    private static YAMLMapping fixSchema(YAMLMapping schema) {
        var extraProperties = YAMLMapping.of(
                "metadata", YAMLMapping.of("type", YAMLValue.of("V1ObjectMeta")),
                "kind", YAMLMapping.of("type", YAMLValue.of("string")),
                "apiVersion", YAMLMapping.of("type", YAMLValue.of("string"))
        );
        schema = schema.withReplaced("properties", schema.get("properties").asMapping().combine(extraProperties));
        schema = schema.withReplaced("type", YAMLValue.of("object"));
        return schema;
    }

    private static String innerCreateSpec(Map<String, YAMLMapping> schemas) {
        var typeFix = new YAMLMapping(schemas.map((k, v) -> new Tuple2<>(k, (YAMLValue)v)).toJavaMap());
        var openapi = YAMLMapping.of(
                "openapi", YAMLValue.of("3.0.0"),
                "info", YAMLMapping.of(
                        "version", YAMLValue.of("1.0.0"),
                        "title", YAMLValue.of("kcc resources"),
                        "license", YAMLMapping.of("name", YAMLValue.of("MIT"))
                ),
                "paths", YAMLMapping.of("/dummy", YAMLMapping.of()),
                "components", YAMLMapping.of("schemas", typeFix)
        );

        return YAMLUtil.toString(openapi);
    }

    /**
     * Get the latest version of the current CDR.
     * @param versions A list of all the versions (in the format of maps) of the current CDR.
     * @return The latest version of the current CDR.
     */
    static YAMLMapping getLatestVersion(YAMLSequence versions) {
        for (var v : versions.stream().map(YAMLValue::asMapping).toList()) {
            if (v.asMapping().get("storage").asBoolean()) {
                return v;
            }
        }

        throw new IllegalStateException("No version found with storage=true");
    }

    private static java.util.Map<String, String> makeSpec(String qualifiedType) {
         return HashMap.of("type", qualifiedType).toJavaMap();
    }

}
