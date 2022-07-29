package com.gs.crdtools;

import com.gs.crdtools.codegen.CrdtoolsCodegen;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
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
import java.util.Map;

/**
 * This class is used to generate POJO(s) from the given OpenAPIV3 specifications.
 * It can be run using the following bazel rule:
 * - bazel build //:kcc_java_genned.
 */
public class SourceGenFromSpec {

    /**
     * Generate POJOs from the given OpenAPIV3 specifications and write them to the given path.
     * @param args The genned.srcjar location, that is, the path where the generated POJOs will be written.
     * @throws IOException If any error occurs while loading the given paths.
     * @throws IllegalArgumentException If the number of arguments is not 1.
     */
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        if (args.length == 1) {
            var outputPath = Paths.get(args[0]);

            List<Object> allTheYamls = SpecExtractorHelper.getCrdsYaml();
            var openApiSpecs = extractSpecs(allTheYamls);

            generateSourceCodeFromSpecs(openApiSpecs, outputPath);
        } else {
            throw new IllegalArgumentException("Invalid number of arguments. Expected 1, got " + args.length);
        }
    }

    /**
     * Generate POJOs from a given OpenAPIV3 specification string and place them at the given path.
     * The result is a collection of jar files zipped within a .srcjar file at the path provided.
     * @param specs The OpenAPIV3 specification yaml file in the form of a string.
     * @param out The output path.
     * @throws IOException If any error occurs while loading the given paths.
     */
    public static void generateSourceCodeFromSpecs(String specs, Path out) throws IOException {
        var tmpOutputDir = Files.createTempDirectory("openAPIGen");

        var cc = new CodegenConfigurator()
                .setInputSpec(specs)
                .setLang(CrdtoolsCodegen.class.getCanonicalName())
                .setOutputDir(tmpOutputDir.toAbsolutePath().toString())
                .setModelPackage("kccapi")
                // CodegenConfigurator modifies its Map arguments, so we need to wrap it in something mutable
                .setAdditionalProperties(mutable(Map.of(
                        "java8", true,
                        "hideGenerationTimestamp", true,
                        "notNullJacksonAnnotation", true
                )))
                .setTypeMappings(mutable(Map.of(
                        V1ObjectMeta.class.getSimpleName(), V1ObjectMeta.class.getCanonicalName()))
                );

        new DefaultGenerator().opts(cc.toClientOptInput()).generate();

        SourceGeneratorHelper.writeJarToOutput(out, tmpOutputDir);
    }

    /**
     * Copy the keys and values from inner into a mutable Map and return it.
     * @param inner The map to copy.
     */
    private static <K, V> Map<K, V> mutable(Map<K, V> inner) {
        return new java.util.HashMap<>(inner);
    }

    /**
     * Extract the OpenAPIV3 specs from a list of CRDs objects extracted from a previous yaml file,
     * then returns the specs as a string.
     * @param crdsYaml The list of CRDs objects.
     */
    private static String extractSpecs(List<Object> crdsYaml) {
        var metadataSpec = HashMap.of("type", V1ObjectMeta.class.getSimpleName());

        // Now just pull out the openapi specs
        HashMap<Object, HashMap<String, Object>> onlySpecs = SpecExtractorHelper.pullOpenapiSpecs(crdsYaml, metadataSpec);

        var full = HashMap.of(
                "openapi", "3.0.0",
                "info", HashMap.of("version", "1.0.0",
                        "title", "kcc resources",
                        "license", HashMap.of("name", "MIT")),
                "paths", HashMap.of("/dummy", HashMap.empty()),
                "components", HashMap.of("schemas", onlySpecs)
        );

        return writeSpecsToString(full);
    }

    /**
     * Write the OpenApiV3 specs to a string and return it.
     * @param openapiSpecs A map containing the openapi specs.
     */
    private static String writeSpecsToString(HashMap<String, Serializable> openapiSpecs) {
        var dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var yaml = new Yaml(dumperOptions);

        return yaml.dump(VavrHelpers.deepToJava(openapiSpecs, List.empty(), HashSet.of(String.class)));
    }

}
