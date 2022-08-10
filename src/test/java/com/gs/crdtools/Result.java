package com.gs.crdtools;

import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public record Result(Map<Path, String> inner) {
    public void assertIn(String file, String in) {

        Path key = Stream.ofAll(inner.keySet()).find(candidate -> candidate.endsWith(file))
                .getOrElseThrow(IllegalArgumentException::new);
        assertTrue(
                inner.get(key).get().contains(in),
                "Output file does not contain '%s'".formatted(in));

    }
}
