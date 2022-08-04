package com.gs.crdtools.codegen;

import com.google.devtools.build.runfiles.Runfiles;
import com.gs.crdtools.SourceGenFromSpec;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomGenerationTest {

    public static final Path OUTPUT_FILE = Path.of("src/main/java/com/gs/crdtools/Thing.java");

    @Test
    void testGenerateMinimalJava() throws IOException {
        var runFiles = Runfiles.create();

        var p = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-openapi.yaml"));
        var result = SourceGenFromSpec.generateSourceCodeFromSpecs(Files.readString(p));

        assertEquals(HashSet.of(OUTPUT_FILE), result.keySet());
        assertTrue(result.get(OUTPUT_FILE).get().contains("GS annotation goes here"));

    }

}
