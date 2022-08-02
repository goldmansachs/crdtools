package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {
    @Test
    void testParseCrds() throws IOException {
        var runFiles = Runfiles.create();
        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));

        var result = Generator.parseCrds(List.of(input));
        assertEquals(1, result.length());
        assertEquals("stable.example.com", VavrHelpers.extractByPath(result.get(0), "spec", "group"));
    }

    @Test
    void testGenerate() throws IOException  {
        // given
        var runFiles = Runfiles.create();
        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));

        // when
        var result = Generator.generate(Generator.parseCrds(List.of(input)));

        // then
        var base = Path.of("src/main/java/com/gs/crdtools");
        var cronTabSpec = base.resolve("CronTabSpec.java");
        var cronTab = base.resolve("CronTab.java");
        assertEquals(HashSet.of(cronTab, cronTabSpec), result.keySet());

        assertTrue(result.get(cronTabSpec).get().contains("class CronTabSpec"));
        assertTrue(result.get(cronTab).get().contains("class CronTab"));
    }
}