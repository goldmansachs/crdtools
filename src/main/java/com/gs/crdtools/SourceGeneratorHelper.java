package com.gs.crdtools;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * A helper class to help with the generation of source code given an all-specs.yaml file
 * and an output file.
 */
public class SourceGeneratorHelper {

    /**
     * Write the contents of the output directory into jar files as specified by the JarOutputStream.
     * @param out The output jar file.
     * @param outputDir The directory to copy the contents from.
     * @throws IOException If any of the specified path does not exist.
     * @throws RuntimeException If any issues occur while copying the content.
     */
    static void writeJarToOutput(Path out, Path outputDir) throws IOException, RuntimeException {
        var jarOut = new JarOutputStream(Files.newOutputStream(out));
        var root = outputDir.resolve("src/main/java/kccapi");

        // try with resources is used to close the jarOut stream when the block is exited
        try (var stream = Files.walk(root).sorted()) { // NB: sorted for stable output
            stream.forEach(p -> {
                if (Files.isRegularFile(p) && p.toString().endsWith(".java")) {
                    var path = root.relativize(p).toString();
                    var entry = new ZipEntry(path);
                    entry.setTime(0); // NB: fixed for stable output
                    try {
                        jarOut.putNextEntry(entry);
                        Files.copy(p, jarOut);
                        jarOut.flush();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to copy " + p, e);
                    }
                }
            });
        } finally {
            jarOut.close();
        }
    }

}
