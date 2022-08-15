package com.gs.crdtools;

import io.vavr.collection.List;

import java.util.Arrays;

/**
 * A class listing all the possible arguments for the CRDTools tool.
 */
record CrdToolsArgs(List<String> crdPaths, String packageName, String outputPath) {

    static final String INPUT_ARG = "-i";
    static final String OUTPUT_ARG = "-o";
    static final String PACKAGE_ARG = "-p";
    public static final String DEFAULT_TARGET_PACKAGE = "com.gs.crdtools.generated";
    public static final String DEFAULT_OUTPUT = "generated.srcjar";

    /**
     * Parse the given arguments.
     * @param args The arguments to parse.
     */
    public static CrdToolsArgs parseArgs(String[] args) {
        int parseIndex = 0;
        List<String> crdPaths = List.empty();
        String packageName = DEFAULT_TARGET_PACKAGE;
        String outputPath = DEFAULT_OUTPUT;

        while (hasNext(args, parseIndex) != -1) {
            String currentArg = args[parseIndex];

            switch (currentArg) {
                case INPUT_ARG -> crdPaths = getCrdsList(args, parseIndex);
                case PACKAGE_ARG -> packageName = args[parseIndex + 1];
                case OUTPUT_ARG -> outputPath = args[parseIndex + 1];
            }
            parseIndex++;
        }
        return new CrdToolsArgs(crdPaths, packageName, outputPath);
    }

    /**
     * Find the next argument in the list and return its index.
     * Return -1 if there are no more arguments.
     * @param args The list of arguments to search.
     * @return The index of the next argument in the list, or -1 if none.
     */
    static int hasNext(String[] args, int index) {
        int i = index + 1;
        while (i < args.length) {
            if (args[i].startsWith("-")) {
                return i;
            } else if (i == args.length - 1) {
                return i + 1;
            }
            i++;
        }
        return -1;
    }

    /**
     * Get the values for the input argument.
     * @param args The list of arguments to search.
     * @return The values for the input argument.
     */
    static List<String> getCrdsList(String[] args, int index) {
        return List.of(Arrays.copyOfRange(args, index + 1, hasNext(args, index)));
    }

}