package com.gs.crdtools;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
