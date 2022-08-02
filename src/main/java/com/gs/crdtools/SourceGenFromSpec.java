package com.gs.crdtools;

import com.gs.crdtools.codegen.CrdtoolsCodegen;
import com.resare.nryaml.YAMLUtil;
import com.resare.nryaml.YAMLValue;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
            var openApiSpecs = extractSpecs(allTheYamls.toStream().map(YAMLUtil::fromBare).toList());

            toZip(generateSourceCodeFromSpecs(openApiSpecs), outputPath);

        } else {
            throw new IllegalArgumentException("Invalid number of arguments. Expected 1, got " + args.length);
        }
    }

    static void toZip(Map<Path, String> content, Path output) throws IOException {
        try (var zipOutputStream = new ZipOutputStream(Files.newOutputStream(output))) {
            for (var pathData : content) {
                var entry = new ZipEntry(pathData._1.toString());
                entry.setTime(0);
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(pathData._2.getBytes(StandardCharsets.UTF_8));
            }
        }
    }


    /**
     * Generate POJOs from a given OpenAPIV3 specification string and place them at the given path.
     * The result is a collection of jar files zipped within a .srcjar file at the path provided.
     * @param specs The OpenAPIV3 specification yaml file in the form of a string.
     * @throws IOException If any error occurs while loading the given paths.
     */
    public static Map<Path, String> generateSourceCodeFromSpecs(String specs) throws IOException {
        // setting this system property has the interesting effect of preventing the
        // generation of a whole set of unrelated files that we don't care about.
        System.setProperty("generateModels", "true");

        var tmpOutputDir = Files.createTempDirectory("openAPIGen");

        var cc = new CodegenConfigurator()
                .setInputSpec(specs)
                .setLang(CrdtoolsCodegen.class.getCanonicalName())
                .setOutputDir(tmpOutputDir.toAbsolutePath().toString())
                .setModelPackage("com.gs.crdtools")
                // CodegenConfigurator modifies its Map arguments, so we need to wrap it in something mutable
                .setAdditionalProperties(
                    HashMap.of(
                        "java8", (Object)true,
                        "hideGenerationTimestamp", true,
                        "notNullJacksonAnnotation", true
                    ).toJavaMap()
                )
                .setTypeMappings(
                    HashMap.of(
                        V1ObjectMeta.class.getSimpleName(),
                        V1ObjectMeta.class.getCanonicalName()
                    ).toJavaMap()
                );
        try {
            new DefaultGenerator().opts(cc.toClientOptInput()).generate();
            return readDir(tmpOutputDir, ".java");
        } finally {
            delete(tmpOutputDir);
        }
    }

    private static void delete(Path dir) throws IOException {
            try (var p = Files.walk(dir)) {
                p.sorted(Comparator.reverseOrder())
                .forEach(f -> {
                    try {
                        Files.delete(f);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
    }

    /**
     * Traverse all files in dir, read the contents of any file that ends with suffix and return
     * the filenames and contents.
     *
     * @param dir the directory to look for files in
     * @param suffix the suffix to match
     */
     static Map<Path, String> readDir(Path dir, String suffix) throws IOException {
         return Stream.ofAll(Files.walk(dir))
                 .filter(path -> path.toString().endsWith(suffix))
                 .map(p -> {
                     try {
                         return new Tuple2<>(p, Files.readString(p));
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 })
                 .map(kv -> new Tuple2<>(dir.relativize(kv._1), kv._2))
                 .collect(HashMap.collector());
     }


    /**
     * Extract the OpenAPIV3 specs from a list of CRDs objects extracted from a previous yaml file,
     * then returns the specs as a string.
     * @param crdsYaml The list of CRDs objects.
     */
    static String extractSpecs(List<YAMLValue> crdsYaml) {

        // Now just pull out the openapi specs
        HashMap<Object, HashMap<String, Object>> onlySpecs = SpecExtractorHelper.pullOpenapiSpecs(crdsYaml);

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
