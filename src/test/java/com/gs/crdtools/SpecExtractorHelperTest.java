package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SpecExtractorHelperTest {

    @Test
    void testPullOpenapiSpecsAddsInfo() throws IOException {
        var runFiles = Runfiles.create();
        var input = Path.of(runFiles.rlocation("__main__/src/test/resources/minimal-crd.yaml"));

        var parsedCrd = Generator.parseCrds(List.of(input));
        java.util.Map<String, Object> specsProperties = (java.util.Map<String, Object>) SpecExtractorHelper.pullOpenapiSpecs(parsedCrd)
                .get()
                ._2
                .get("properties")
                .get();

        assertTrue(specsProperties.containsKey("metadata"));
        assertTrue(specsProperties.containsKey("kind"));
        assertTrue(specsProperties.containsKey("apiVersion"));
    }

    @Test
    void testGetLatestVersion() {
        // dummy versions list, schema is missing, storage is set to true
        // if two instances of storage=true exist, the first one is returned
        java.util.List<?> versions = List.of(
                HashMap.of(
                        "name", "v2",
                        "served", true,
                        "storage", true,
                        "schema", (Object) "object").toJavaMap(),
                HashMap.of(
                        "name", "v1",
                        "served", true,
                        "storage", false,
                        "schema", (Object) "object").toJavaMap(),
                HashMap.of(
                        "name", "v1beta1",
                        "served", true,
                        "storage", false,
                        "schema", (Object) "object").toJavaMap()
        ).toJavaList();

        assertEquals(versions.get(0), SpecExtractorHelper.getLatestVersion((java.util.List<Object>) versions));
    }

    @Test
    void testGetLatestVersionThrowsError() {
        // dummy versions list where storage is never true
        java.util.List<?> versions = List.of(
                HashMap.of(
                        "name", "v1beta1",
                        "served", true,
                        "storage", false,
                        "schema", (Object) "object").toJavaMap(),
                HashMap.of(
                        "name", "v1",
                        "served", true,
                        "storage", false,
                        "schema", (Object) "object").toJavaMap()
        ).toJavaList();

        Exception exception = assertThrows(IllegalStateException.class,
                () -> SpecExtractorHelper.getLatestVersion((java.util.List<Object>) versions));

        assertTrue(exception.getMessage().contains("No version found with storage=true"));
    }

}
