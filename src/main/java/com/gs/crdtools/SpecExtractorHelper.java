package com.gs.crdtools;

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
     * @param metadataSpec The metadata spec to use for the openapi spec.
     * @return The extracted specs.
     */
     static HashMap<Object, HashMap<String, Object>> pullOpenapiSpecs(List<Object> allTheYamls,
                                                                             HashMap<String, String> metadataSpec) {
        return HashMap.ofEntries(allTheYamls.map(y -> {
            var kind = VavrHelpers.extractByPath(y, "spec", "names", "kind");
            //noinspection unchecked
            Map<String, Object> schema = HashMap.ofAll(
                    ((java.util.Map<String, java.util.Map<String, java.util.List<java.util.Map<String,
                            java.util.Map<String, java.util.Map<String, Object>>>>>>) y)
                            .get("spec")
                            .get("versions")
                            .get(0)
                            .get("schema")
                            .get("openAPIV3Schema"));

            // Splat on the metadata spec
            //noinspection unchecked
            ((java.util.Map<String, Object>) schema.get("properties").get()).put("metadata", metadataSpec.toJavaMap());

            return Tuple.of(kind, HashMap.of("type", (Object) "object").merge(schema));
        }));
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
