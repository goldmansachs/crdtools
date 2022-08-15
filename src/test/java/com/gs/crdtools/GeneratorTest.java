package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorTest {

    public static final String OUTPUT_PACKAGE = "com.gs.crdtools.generated";
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
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE));

        var cronTabSpec = OUTPUT_DIR.resolve("CronTabSpec.java");
        var cronTab = OUTPUT_DIR.resolve("CronTab.java");
        assertEquals(HashSet.of(cronTab, cronTabSpec), sourceCodeFromSpecs.inner().keySet());

        sourceCodeFromSpecs.assertIn("CronTabSpec.java", "class CronTabSpec");
        sourceCodeFromSpecs.assertIn("CronTab.java", "class CronTab");
    }

    @Test
    void testGenerateCodeContainsAdditionalProperties() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE));

        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"metadata\")");
        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"kind\")");
        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"apiVersion\")");

    }

    @Test
    void testGeneratedCodeContainsGroupAndVersion() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE));

        sourceCodeFromSpecs.assertIn("CronTab.java", "group = \"stable.example.com\"");
        sourceCodeFromSpecs.assertIn("CronTab.java", "version = \"v1\"");
    }
}
