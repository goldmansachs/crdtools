package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static com.gs.crdtools.SourceGenFromSpec.OUTPUT_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {

    public static final Path OUTPUT_DIR = Path.of(
            "src/main/java",
            OUTPUT_PACKAGE.replaceAll("\\.", "/")
    );

    @Test
    void testParseCrds() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));

        assertEquals(1, parsedCrd.length());
        assertEquals("stable.example.com", parsedCrd.get(0).asMapping().get("spec").asMapping().get("group").asString());
    }

    @Test
    void testGenerate() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = Generator.generate(parsedCrd);

        var cronTabSpec = OUTPUT_DIR.resolve("CronTabSpec.java");
        var cronTab = OUTPUT_DIR.resolve("CronTab.java");
        assertEquals(HashSet.of(cronTab, cronTabSpec), sourceCodeFromSpecs.keySet());

        assertTrue(sourceCodeFromSpecs.get(cronTabSpec).get().contains("class CronTabSpec"));
        assertTrue(sourceCodeFromSpecs.get(cronTab).get().contains("class CronTab"));
    }

    @Test
    void testGenerateCodeContainsAdditionalProperties() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = Generator.generate(parsedCrd);

        // Additionally to SpecExtractorHelperTest.testPullOpenapiSpecsAddsInfo()
        // this test checks if the additional properties are added to the generated code
        var cronTab = OUTPUT_DIR.resolve("CronTab.java");

        assertTrue(sourceCodeFromSpecs.get(cronTab).get().contains("@JsonProperty(\"metadata\")"));
        assertTrue(sourceCodeFromSpecs.get(cronTab).get().contains("@JsonProperty(\"kind\")"));
        assertTrue(sourceCodeFromSpecs.get(cronTab).get().contains("@JsonProperty(\"apiVersion\")"));
    }

    @Test
    void testGenerateCodeContainsGroupAndVersion() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = Generator.generate(parsedCrd);

        var cronTabSpec = OUTPUT_DIR.resolve("CronTabSpec.java");
        var cronTab = OUTPUT_DIR.resolve("CronTab.java");
        assertEquals(HashSet.of(cronTab, cronTabSpec), sourceCodeFromSpecs.keySet());

        assertTrue(sourceCodeFromSpecs.get(cronTabSpec).get().contains("group = \"stable.example.com\""));
        assertTrue(sourceCodeFromSpecs.get(cronTab).get().contains("version = \"v1\""));
    }
}
