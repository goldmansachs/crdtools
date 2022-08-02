package com.gs.crdtools;

import com.resare.nryaml.YAMLValue;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

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

        return HashMap.ofEntries(allTheYamls.map(YAMLValue::toBareObject).map(y -> {
            var kind = VavrHelpers.extractByPath(y, "spec", "names", "kind");

            // Get the latest version of the current CDR and read its openAPIV3Schema
            var latestVersion = getLatestVersion(VavrHelpers.extractByPath(y, "spec", "versions"));
            Map<String, Object> schema = HashMap.ofAll(
                    (java.util.Map<String, Object>) ((java.util.Map<?, ?>) latestVersion
                    .get("schema"))
                    .get("openAPIV3Schema"));

            //noinspection unchecked
            var properties = (java.util.Map<String, Object>)schema.get("properties").get();
            properties.put("metadata", makeSpec("V1ObjectMeta"));
            if (!properties.containsKey("kind")) {
             properties.put("kind", makeSpec("string"));
            }
            if (!properties.containsKey("apiVersion")) {
             properties.put("apiVersion", makeSpec("string"));
            }

            return Tuple.of(kind, HashMap.of("type", (Object) "object").merge(schema));
        }));
    }

    /**
     * Get the latest version of the current CDR.
     * @param versions A list of all the versions (in the format of maps) of the current CDR.
     * @return The latest version of the current CDR.
     */
    static java.util.Map<String, Object> getLatestVersion(java.util.List<Object> versions) {
        for (var v : versions) {
            //noinspection unchecked
            boolean storage = (boolean) ((java.util.Map<String, Object>) v).get("storage");
            if (storage) {
                return (java.util.Map<String, Object>) v;
            }
        }

        throw new IllegalStateException("No version found with storage=true");
    }

    private static java.util.Map<String, String> makeSpec(String qualifiedType) {
         return java.util.Map.of("type", qualifiedType);
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
