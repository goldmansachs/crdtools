package com.gs.crdtools;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class SpecExtractor {

    /**
     * Generate an all-spec.yaml file from a directory of CRD files.
     * @param args The path to the final spec file location.
     * @throws IllegalArgumentException If the path is not valid.
     * @throws IOException If the k8s-config-connector has not been downloaded.
     */
    public static void main(String[] args) throws IOException, IllegalArgumentException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Check if the parameters are correct. Is the location correct?");
        }
        var specFile = Path.of(args[args.length - 1]);
        System.out.println(specFile);
        List<Object> allTheYamls = getCrdsYaml();

        var metadataSpec = HashMap.of("type", V1ObjectMeta.class.getSimpleName());

        // Now just pull out the openapi specs
        HashMap<Object, HashMap<String, Object>> onlySpecs = pullOpenapiSpecs(allTheYamls, metadataSpec);

        var full = HashMap.of(
                "openapi", "3.0.0",
                "info", HashMap.of("version", "1.0.0",
                        "title", "kcc resources",
                        "license", HashMap.of("name", "MIT")),
                "paths", HashMap.of("/dummy", HashMap.empty()),
                "components", HashMap.of("schemas", onlySpecs)
        );

        var dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var yaml = new Yaml(dumperOptions);

        Files.writeString(specFile, yaml.dump(VavrHelpers.deepToJava(full, List.empty(), HashSet.of(String.class))));
    }

    /**
     * Pull out the openapi specs from the yaml files.
     * @param allTheYamls A list containing the previously extracted crds.
     * @param metadataSpec The metadata spec to use for the openapi spec.
     * @return The extracted specs.
     */
    private static HashMap<Object, HashMap<String, Object>> pullOpenapiSpecs(List<Object> allTheYamls,
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
    private static List<Object> getCrdsYaml() throws IllegalArgumentException {
        List<Object> allTheYamls;

        try {
            allTheYamls = List.ofAll(new Yaml().loadAll(SpecExtractor.class.getResourceAsStream(
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
