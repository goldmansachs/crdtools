package com.gs.crdtools;

import io.vavr.collection.HashMap;
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
}
