package com.gs.crdtools.codegen;

import com.google.devtools.build.runfiles.Runfiles;
import com.gs.crdtools.ApiInformation;
import com.gs.crdtools.Result;
import com.gs.crdtools.generated.CronTab;
import com.gs.crdtools.SourceGenFromSpec;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CustomGenerationTest {

    public static final String OUTPUT_PACKAGE = "com.gs.crdtools.generated";
    public static final Path OUTPUT_FILE = Path.of(
            "src/main/java",
            OUTPUT_PACKAGE.replaceAll("\\.", "/")
    ).resolve("Thing.java");

    @Test
    void testGenerateMinimalJava() throws IOException {
        var runFiles = Runfiles.create();

        var p = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-openapi.yaml"));
        var crd = List.of(new SourceGenFromSpec.Spec("", "", Files.readString(p)));
        var result = new Result(SourceGenFromSpec.generateSourceCodeFromSpecs(crd, OUTPUT_PACKAGE));

        assertEquals(HashSet.of(OUTPUT_FILE), result.inner().keySet());
        result.assertIn("Thing.java", "@ApiInformation");
        result.assertIn("Thing.java", "import com.gs.crdtools.BaseObject");
    }

    @Test
    void testReadAnnotation() {
        var cronTab = new CronTab();
        var annotation = cronTab.getClass().getAnnotation(ApiInformation.class);
        assertEquals("stable.example.com", annotation.group());
        assertEquals("v1", annotation.version());
    }
}
