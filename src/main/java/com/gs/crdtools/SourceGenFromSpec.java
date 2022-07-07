package com.gs.crdtools;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.codegen.v3.cli.MyCodegen;
import io.swagger.codegen.v3.cli.SwaggerCodegen;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to generate the source code from the OpenAPI specs.
 * It can be run using the following bazel rules:
 * - bazel build //:spec (for the all-specs-only.yaml file)
 * - bazel build //:kcc_java_genned for the source code generation.
 */
public class SourceGenFromSpec {

    /**
     * Generate the all-specs.yaml file or the complete source code, according
     * to the number of parameters given:
     * 1 - generate the all-specs-only.yaml file
     * 2 - generate the complete source code from the spec file.
     * @param argsIn The all-specs.yaml and genned.srcjar locations.
     * @throws IOException If any error occurs while loading the given paths.
     * @throws IllegalArgumentException If the number of arguments is not 1 or 2.
     */
    public static void main(String[] argsIn) throws IllegalArgumentException, IOException {
        if (argsIn.length == 1) {
            // Generate  yaml only -- rule //:spec
            var specFile = Paths.get(argsIn[0]);

            extractSpecs(specFile);
        } else if (argsIn.length == 2) {
            // Generate yaml and java -- rule //:kcc_java_genned
            var specFile = Paths.get(argsIn[0]);
            var out = Paths.get(argsIn[1]);

            extractSpecs(specFile);

            var outputDir = Files.createTempDirectory("openAPIGen");
            generateSourceInDir(specFile, out, outputDir);
        } else {
            throw new IllegalArgumentException("Invalid number of arguments. " +
                    "Expected 1 or 2, got " + argsIn.length);
        }
    }

    /**
     * Generate source code from the given spec file to a temporary directory (outputDir),
     * then write the contents of the temporary directory to the given out Path.
     * @param specFile The spec file to generate source code from.
     * @param out The output path.
     * @param outputDir The temporary directory to write the generated source code to.
     * @throws IOException If any error occurs while loading the given paths.
     * @throws RuntimeException If any error arises during the writing process.
     */
    private static void generateSourceInDir(Path specFile, Path out, Path outputDir) throws IOException, RuntimeException {
        var args = List.of(
                "generate",
                "-i", specFile.toAbsolutePath().toString(),
                "-l", MyCodegen.class.getCanonicalName(),
                "-o", outputDir.toAbsolutePath().toString(),
                "--model-package", "source-gen",
                "--additional-properties", "java8=true", "hideGenerationTimestamp=true",
                "notNullJacksonAnnotation=true",
                "--type-mappings",
                V1ObjectMeta.class.getSimpleName() + "=" + V1ObjectMeta.class.getCanonicalName()
        );

        SwaggerCodegen.main(args.toJavaArray(String[]::new));

        SourceGeneratorHelper.writeJarToOutput(out, outputDir);
    }

    /**
     * Extract all CRD definitions and pull out the openapi specs according to these.
     * Finally, write the specs to the specFile (yaml file).
     * @param specFile The path to the final spec file location.
     * @throws IOException If the k8s-config-connector has not been downloaded.
     */
    private static void extractSpecs(Path specFile) throws IOException {
        List<Object> allTheYamls = SpecExtractorHelper.getCrdsYaml();

        var metadataSpec = HashMap.of("type", V1ObjectMeta.class.getSimpleName());

        // Now just pull out the openapi specs
        HashMap<Object, HashMap<String, Object>> onlySpecs = SpecExtractorHelper.pullOpenapiSpecs(allTheYamls, metadataSpec);

        var full = HashMap.of(
                "openapi", "3.0.0",
                "info", HashMap.of("version", "1.0.0",
                        "title", "kcc resources",
                        "license", HashMap.of("name", "MIT")),
                "paths", HashMap.of("/dummy", HashMap.empty()),
                "components", HashMap.of("schemas", onlySpecs)
        );

        writeSpecsToFile(specFile, full);
    }

    /**
     * Write the openapi specs to the given file (specFile).
     * @param specFile The resulting yaml file containing the openapi specs.
     * @param openapiSpecs A map containing the openapi specs.
     * @throws IOException If any error occurs while writing the file.
     */
    private static void writeSpecsToFile(Path specFile, HashMap<String, Serializable> openapiSpecs) throws IOException {
        var dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var yaml = new Yaml(dumperOptions);

        Files.writeString(specFile, yaml.dump(VavrHelpers.deepToJava(openapiSpecs, List.empty(), HashSet.of(String.class))));
    }
}
