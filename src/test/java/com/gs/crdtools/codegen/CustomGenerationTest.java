package com.gs.crdtools.codegen;

import com.google.devtools.build.runfiles.Runfiles;
import com.gs.crdtools.SourceGenFromSpec;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomGenerationTest {
    @Test
    void testGenerateMinimalJava() throws IOException {
        var runFiles = Runfiles.create();

        var p = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-openapi.yaml"));
        var result = SourceGenFromSpec.generateSourceCodeFromSpecs(Files.readString(p));


    }

}
