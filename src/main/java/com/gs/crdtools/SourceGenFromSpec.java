package com.gs.crdtools;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import java.io.IOException;
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
class SourceGenFromSpec {

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
     * @throws IOException If any error occurs while loading the given paths.
     */
    static Map<Path, String> generateSource(SpecExtractorHelper.Spec spec, String modelPackage) throws IOException {
        // setting this system property has the interesting effect of preventing the
        // generation of a whole set of unrelated files that we don't care about.
        System.setProperty("generateModels", "true");
        System.setProperty("maxYamlCodePoints", "1073741824"); // 1 gb (1024 * 1024 * 1024)

        HashMap <Path, String> result = HashMap.empty();
        var tmpOutputDir = Files.createTempDirectory("openAPIGen");

        try {
            var cc = new CodegenConfigurator()
                    .setInputSpec(spec.openapiSpec())
                    .setLang(CrdtoolsCodegen.class.getCanonicalName())
                    .setOutputDir(tmpOutputDir.toAbsolutePath().toString())
                    .setModelPackage(modelPackage)
                    .setSkipInlineModelMatches(true)
                    // CodegenConfigurator modifies its Map arguments, so we need to wrap it in something mutable
                    .setAdditionalProperties(
                            HashMap.of(
                                    "java8", (Object) true,
                                    "hideGenerationTimestamp", true,
                                    "notNullJacksonAnnotation", true,
                                    // since we now combined input from all the crds into a single
                                    // spec, we need to send a map with all the Metadata for the
                                    // different classes here. The right values are unpacked and
                                    // used in CrdtoolsCodegen
                                    "crdtoolsMetadataMap", spec.metadata()
                            ).toJavaMap()
                    )
                    .setTypeMappings(
                            HashMap.of(
                                    V1ObjectMeta.class.getSimpleName(), V1ObjectMeta.class.getCanonicalName(),
                                    "integer", "Long"
                            ).toJavaMap()
                    );
            new DefaultGenerator().opts(cc.toClientOptInput()).generate();
            result = result.merge(readDir(tmpOutputDir, ".java"));
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


}
