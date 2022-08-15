package com.gs.crdtools;

import com.gs.crdtools.codegen.CrdtoolsCodegen;
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
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * This class is used to generate POJO(s) from the given OpenAPIV3 specifications.
 * It can be run using the following bazel rule:
 * - bazel build //:kcc_java_genned.
 */
public class SourceGenFromSpec {

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
    public static Map<Path, String> generateSourceCodeFromSpecs(List<Spec> specs, String modelPackage) throws IOException {
        // setting this system property has the interesting effect of preventing the
        // generation of a whole set of unrelated files that we don't care about.
        System.setProperty("generateModels", "true");

        HashMap <Path, String> result = HashMap.empty();
        var tmpOutputDir = Files.createTempDirectory("openAPIGen");

        try {
            for (var spec : specs) {
                var cc = new CodegenConfigurator()
                        .setInputSpec(spec.openApiSpec())
                        .setLang(CrdtoolsCodegen.class.getCanonicalName())
                        .setOutputDir(tmpOutputDir.toAbsolutePath().toString())
                        .setModelPackage(modelPackage)
                        // CodegenConfigurator modifies its Map arguments, so we need to wrap it in something mutable
                        .setAdditionalProperties(
                                HashMap.of(
                                        "java8", (Object) true,
                                        "hideGenerationTimestamp", true,
                                        "notNullJacksonAnnotation", true,
                                        "crdGroup", spec.group(),
                                        "crdVersion", spec.version()
                                ).toJavaMap()
                        )
                        .setTypeMappings(
                                HashMap.of(
                                        V1ObjectMeta.class.getSimpleName(),
                                        V1ObjectMeta.class.getCanonicalName()
                                ).toJavaMap()
                        );

                new DefaultGenerator().opts(cc.toClientOptInput()).generate();
                result = result.merge(readDir(tmpOutputDir, ".java"));
            }
        } finally {
            delete(tmpOutputDir);
        }

        return result;
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

     public record Spec(String group, String version, String openApiSpec) {
     }

    /**
     * Extract the OpenAPIV3 specs from a list of CRDs objects extracted from a previous yaml file,
     * then returns the specs as a string.
     * @param crdsYaml The list of CRDs objects.
     */
    static List<Spec> extractSpecs(List<YAMLValue> crdsYaml) {
        // Now just pull out the openapi specs
        var onlySpecs = SpecExtractorHelper.pullOpenapiSpecs(crdsYaml);

        return onlySpecs.map(spec -> {
            List<String> crdAdditionalInfo = (List<String>) spec._1;
            String group = crdAdditionalInfo.get(0);
            String version = crdAdditionalInfo.get(1);
            String kind = crdAdditionalInfo.get(2);

            var openapi = HashMap.of(
                    "openapi", "3.0.0",
                    "info", HashMap.of("version", "1.0.0",
                            "title", "kcc resources",
                            "license", HashMap.of("name", "MIT")),
                    "paths", HashMap.of("/dummy", HashMap.empty()),
                    "components", HashMap.of("schemas", HashMap.of(kind, spec._2))
            );

            return new Spec(group, version, writeSpecsToString(openapi));
        }).toList();
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
