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
import com.gs.utils.VavrHelpers;

public class SpecExtractor {

    public static void main(String[] args) throws IOException {
        System.out.println("debug");
        var specFile = Path.of(args[args.length - 1]);
        var allTheYamls = List.ofAll(
                new Yaml().loadAll(SpecExtractor.class.getResourceAsStream(
                        "/external/k8s-config-connector/install-bundles/install-bundle-workload-identity/crds.yaml"))
        );

        if (allTheYamls.isEmpty()) {
            throw new IllegalArgumentException("Didn't find any crds?");
        }

        var metadataSpec = HashMap.of("type", V1ObjectMeta.class.getSimpleName());

        // Now just pull out the openapi specs
        var onlySpecs = HashMap.ofEntries(allTheYamls.map(y -> {
            var kind = VavrHelpers.extractByPath(y, "spec", "names", "kind");
            //noinspection unchecked
            Map<String, Object> schema = HashMap.ofAll(
                    ((java.util.Map<String, java.util.Map<String, java.util.List<java.util.Map<String, java.util.Map<String, java.util.Map<String, Object>>>>>>) y)
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

        Files.writeString(specFile,
                yaml.dump(VavrHelpers.deepToJava(full, List.empty(), HashSet.of(String.class))));

    }

}
