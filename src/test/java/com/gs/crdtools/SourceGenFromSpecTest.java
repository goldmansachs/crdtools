package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SourceGenFromSpecTest {

    @Test
    void testReadDir(@TempDir Path temp) throws IOException {
        // given
        var subdir = temp.resolve("some/dir");
        assertTrue(subdir.toFile().mkdirs());
        Files.writeString(subdir.resolve("a.txt"), "content in a");
        Files.writeString(subdir.resolve("b.txt"), "content in b");
        Files.writeString(subdir.resolve("ignore"), "wrong suffix");

        // when
        var result = SourceGenFromSpec.readDir(temp, ".txt");

        // when
        assertEquals(
            HashMap.of(
                Path.of("some/dir/a.txt"), "content in a",
                Path.of("some/dir/b.txt"), "content in b"
            ),
            result
        );
    }

    @Test
    void testExtractSpecs() throws IOException {
        var runFiles = Runfiles.create();
        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));

        var parsedCrds = Generator.parseCrds(List.of(input));
        var specsList = SourceGenFromSpec.extractSpecs(parsedCrds);

        // there is only one crd, therefore there must be only one Spec record
        assertEquals(1, specsList.size());

        // the spec record must contain the following fields:
        assertEquals("stable.example.com", specsList.get(0).group());
        assertEquals("v1", specsList.get(0).version());
    }
}
