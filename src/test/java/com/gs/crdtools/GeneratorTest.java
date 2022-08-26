package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GeneratorTest {

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
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE, Option.none()));

        var cronTabSpec = OUTPUT_DIR.resolve("CronTabSpec.java");
        var cronTab = OUTPUT_DIR.resolve("CronTab.java");
        assertEquals(HashSet.of(cronTab, cronTabSpec), sourceCodeFromSpecs.inner().keySet());

        sourceCodeFromSpecs.assertIn("CronTabSpec.java", "class CronTabSpec");
        sourceCodeFromSpecs.assertIn("CronTab.java", "class CronTab");
    }

    @Test
    void testGenerateMultipleFiles() throws IOException {
        var runFiles = Runfiles.create();

        var input = List.of(
                Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml")),
                Path.of(runFiles.rlocation("__main__/src/test/resources/managedcertificates-crd.yaml"))
        );
        var parsedCrd = Generator.parseCrds(input);
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE, Option.none()));

        var expectedFiles = List.of(
                "CronTab.java",
                "CronTabSpec.java",
                "ManagedCertificate.java",
                "ManagedCertificateSpec.java",
                "ManagedCertificateStatus.java",
                "ManagedCertificateStatusDomainStatus.java"
        );
        var expectedOutput = HashSet.ofAll(expectedFiles.map(OUTPUT_DIR::resolve));

        assertEquals(expectedOutput, sourceCodeFromSpecs.inner().keySet());
    }

    @Test
    void testGenerationDefaultValues() {
        Generator generator = new Generator();
        assertEquals(generator.getPackage(), "com.gs.crdtools.generated");
        assertEquals(generator.getOutput(), "generated.srcjar");
        assertNull(generator.getInputCdrs());
    }

    @Test
    void testGenerationCustomValues() {
        Generator generator = new Generator();

        CommandLine cmd = new CommandLine(generator);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        cmd.execute("src/test/resources/minimal-crd.yaml", "-p my.test.package", "-o my-output.srcjar");
        assertEquals(generator.getPackage().trim(), "my.test.package");
        assertEquals(generator.getOutput().trim(), "my-output.srcjar");
        assertEquals(generator.getInputCdrs().length, 1);
        assertEquals(generator.getInputCdrs()[0], "src/test/resources/minimal-crd.yaml");
    }

    @Test
    void testGenerateCodeContainsAdditionalProperties() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE, Option.none()));

        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"metadata\")");
        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"kind\")");
        sourceCodeFromSpecs.assertIn("CronTab.java", "@JsonProperty(\"apiVersion\")");

    }

    @Test
    void testGeneratedCodeContainsGroupAndVersion() throws IOException {
        var runFiles = Runfiles.create();

        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));
        var parsedCrd = Generator.parseCrds(List.of(input));
        var sourceCodeFromSpecs = new Result(Generator.generate(parsedCrd, OUTPUT_PACKAGE, Option.none()));

        sourceCodeFromSpecs.assertIn("CronTab.java", "group = \"stable.example.com\"");
        sourceCodeFromSpecs.assertIn("CronTab.java", "version = \"v1\"");
    }
}
