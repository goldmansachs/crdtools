package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These tests check that the bazel commands generate the expected files.
 * Only run these after having run the specific bazel rules:
 * - bazel build //:kcc_java_genned
 * - bazel build //:spec
 */
public class SourceGeneratorTest {

    private static String finalPath;

    /**
     * Set the correct path for the following tests.
     * It may be the case that the paths have to be modified to reflect the machine (and OS) in use.
     */
    @BeforeAll
    static void setUp() {
        // Navigating to the bazel-out folder
        String userDirNoSandbox = new File(System.getProperty("user.dir")).getAbsolutePath();
        String userDirExecRoot = userDirNoSandbox;

        // Trying to get to the bazel configuration folder
        while (!userDirNoSandbox.endsWith("/sandbox")) {
            userDirNoSandbox = userDirNoSandbox.substring(0, userDirNoSandbox.length() - 1);
        }
        if (userDirNoSandbox.endsWith("/sandbox")) {
            userDirNoSandbox = userDirNoSandbox.substring(0, userDirNoSandbox.length() - "sandbox".length());
        }

        // Trying to get to the bazel src folder
        while (!userDirExecRoot.startsWith("execroot/")) {
            userDirExecRoot = userDirExecRoot.substring(userDirExecRoot.length() - (userDirExecRoot.length() - 1));
        }
        while (!userDirExecRoot.endsWith("/src/")) {
            userDirExecRoot = userDirExecRoot.substring(0, userDirExecRoot.length() - 1);
        }

        // Combining both paths and checking for existence of the desired file
        finalPath = userDirNoSandbox + userDirExecRoot;
    }

    @Nested
    @DisplayName("Nested class for testing files generation")
    class FileGenerationTest {

        /**
         * Test that the all-specs.yaml file has been generated successfully.
         * N.B.: Run this test ONLY after having run bazel build //:kcc_java_genned.
         */
        @Test
        @DisplayName("Test that the all-specs.yaml file has been generated successfully.")
        void testSpecFileGeneration() {
            File filepath = new File(SourceGeneratorTest.finalPath + "main/java/com/gs/crdtools/all-specs.yaml");
            assertTrue(filepath.exists());
        }

        /**
         * Test that the genned.srcjar file has been generated successfully.
         * N.B.: Run this test ONLY after having run bazel build //:kcc_java_genned.
         */
        @Test
        @DisplayName("Test that the genned.srcjar file has been generated successfully.")
        void testGennedFileGeneration() {
            File filepath = new File(SourceGeneratorTest.finalPath + "main/java/com/gs/crdtools/genned.srcjar");
            assertTrue(filepath.exists());
        }

        /**
         * Test that the genned.srcjar file has been generated successfully.
         * N.B.: Run this test ONLY after having run bazel build //:spec
         */
        @Test
        @DisplayName("Test that the all-specs-only.yaml file has been generated successfully.")
        void testAllSpecsOnlyFileGeneration() {
            File filepath = new File(SourceGeneratorTest.finalPath + "main/java/com/gs/crdtools/all-specs-only.yaml");
            assertTrue(filepath.exists());
        }
    }


    /**
     * In order to make this work it is necessary to hardcode the two paths.
     * This is because bazel test creates a sandbox environment around each test,
     * this does not happen using bazel run instead.
     * @throws IOException If any of the files cannot be read.
     */
    @Test
    @DisplayName("Test that the generated code is valid")
    void testGeneratedCode() throws IOException {
        var runFiles = Runfiles.create();

        Path generatedCode = Path.of(runFiles.rlocation("__main__/src/test/resources/AccessContextManagerAccessLevelCorrect.txt"));
        Path correctCode = Path.of(runFiles.rlocation("__main__/src/test/resources/AccessContextManagerAccessLevelGenerated.txt"));

        assertEquals(-1, Files.mismatch(generatedCode, correctCode));
    }

}
