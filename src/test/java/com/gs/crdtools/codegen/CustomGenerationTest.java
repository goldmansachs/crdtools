package com.gs.crdtools.codegen;

import com.google.devtools.build.runfiles.Runfiles;
import com.gs.crdtools.SourceGenFromSpec;
import io.vavr.collection.List;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.gs.crdtools.SourceGenFromSpec.OUTPUT_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomGenerationTest {

    public static final Path OUTPUT_FILE = Path.of(
            "src/main/java",
            OUTPUT_PACKAGE.replaceAll("\\.", "/")
    ).resolve("Thing.java");

    @Test
    void testGenerateMinimalJava() throws IOException {
        var runFiles = Runfiles.create();

        var p = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-openapi.yaml"));
        var crd = List.of(new SourceGenFromSpec.Spec("", "", Files.readString(p)));
        var result = new Result(SourceGenFromSpec.generateSourceCodeFromSpecs(crd));

        assertEquals(HashSet.of(OUTPUT_FILE), result.inner().keySet());

        result.assertIn("Thing.java","GS annotation goes here");
        result.assertIn("Thing.java", "import com.gs.crdtools.BaseObject");
    }

    private void assertIn(Map<Path, String> result, String in) {
        assertTrue(
                result.get(OUTPUT_FILE).get().contains(in),
                "Output file does not contain '%s'".formatted(in));
    }

    public record Result(Map<Path, String> inner) {
        public void assertIn(String file, String in) {

            Path key = Stream.ofAll(inner.keySet()).find(candidate -> candidate.endsWith(file)).getOrElseThrow(IllegalArgumentException::new);
            assertTrue(
                    inner.get(key).get().contains(in),
                    "Output file does not contain '%s'".formatted(in));

        }
    }
}
