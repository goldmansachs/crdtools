package com.gs.crdtools;

import com.resare.nryaml.YAMLMapping;
import com.resare.nryaml.YAMLSequence;
import com.resare.nryaml.YAMLValue;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.yaml.snakeyaml.Yaml;

/**
 * A helper class to extract the openAPI specs from a given yaml file in the format of a string.
 */
public class SpecExtractorHelper {

    /**
     * Pull out the openapi specs according to the "kind" attribute in the inputted yaml file.
     * @param allTheYamls A list containing the previously extracted crds.
     * @return The extracted specs.
     */
     static HashMap<Object, HashMap<String, Object>> pullOpenapiSpecs(List<YAMLValue> allTheYamls) {

        return HashMap.ofEntries(allTheYamls.map(y -> {
            var kind = y.asMapping().get("spec")
                    .asMapping().get("names")
                    .asMapping().get("kind")
                    .asString();

            // Get the latest version of the current CDR and read its openAPIV3Schema
            var latestVersion = getLatestVersion(y.asMapping().get("spec").asMapping().get("versions").asSequence());
            var schema = latestVersion.get("schema").asMapping().get("openAPIV3Schema").asMapping();

            //noinspection unchecked
            var properties = (java.util.Map)schema.get("properties").asMapping().toBareObject();

            properties.put("metadata", makeSpec("V1ObjectMeta"));
            if (!properties.containsKey("kind")) {
             properties.put("kind", makeSpec("string"));
            }
            if (!properties.containsKey("apiVersion")) {
             properties.put("apiVersion", makeSpec("string"));
            }

            var bareSchema = io.vavr.collection.HashMap.ofAll((java.util.Map<String,Object>)schema.toBareObject());
            bareSchema = bareSchema.put("properties", properties);
            return Tuple.of(kind, HashMap.of("type", (Object) "object").merge(bareSchema));
        }));
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

    /**
     * Get the crds yaml file from the previously downloaded repository.
     * This only works if the k8s-config-connector has been successfully downloaded by bazel.
     * @return A list reflecting the content of the crds.yaml file.
     * @throws IllegalArgumentException If the file cannot be read or the list is empty.
     */
    static List<Object> getCrdsYaml() throws IllegalArgumentException {
        List<Object> allTheYamls;

        try {
            allTheYamls = List.ofAll(new Yaml().loadAll(SpecExtractorHelper.class.getResourceAsStream(
                    "/external/k8s-config-connector/install-bundles/install-bundle-workload-identity/crds.yaml")));
        } catch (Exception ex) {
            // Correct exception would be IOException, but it is thrown at runtime;
            // hence not recognised by the try block
            throw new IllegalArgumentException("Error occurred while accessing the yaml file. " +
                    "Has the repo been download correctly?", ex);
        }

        if (allTheYamls.isEmpty()) {
            throw new IllegalArgumentException("Didn't find any crds?");
        }

        return allTheYamls;
    }
}
