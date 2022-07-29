package com.gs.crdtools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Utils {

    /**
     * Delete the specified directory and all its contents.
     * @param path The path to the directory to delete.
     * @throws IOException If the directory is not found at the path.
     */
    public static void deleteDirectory(Path path) throws IOException {
        try (var folder = Files.walk(path)) {
            // we use comparator reverse order to delete the deepest files first
            folder.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
