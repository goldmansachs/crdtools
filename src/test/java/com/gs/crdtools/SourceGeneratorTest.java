package com.gs.crdtools;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;


import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
