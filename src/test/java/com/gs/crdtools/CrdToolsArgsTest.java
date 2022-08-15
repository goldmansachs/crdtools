package com.gs.crdtools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrdToolsArgsTest {

    @Test
    void testParseArgs() {
        var args = new String[] {"-p", "com.gs.crdtools.generated", "-o", "generated.srcjar", "-i", "example_crd_1.yaml"};
        var result = CrdToolsArgs.parseArgs(args);
        assertEquals("com.gs.crdtools.generated", result.packageName());
        assertEquals("generated.srcjar", result.outputPath());
        assertEquals(1, result.crdPaths().size());
        assertEquals("example_crd_1.yaml", result.crdPaths().get(0));
    }

    @Test
    void testParseMultipleInputs() {
        var args = new String[] {"-i", "example_crd_1.yaml", "example_crd_2.yaml"};
        var result = CrdToolsArgs.parseArgs(args);
        assertEquals(2, result.crdPaths().size());
        assertEquals("example_crd_1.yaml", result.crdPaths().get(0));
        assertEquals("example_crd_2.yaml", result.crdPaths().get(1));
    }

    @Test
    void testParseArgsWithDefault() {
        var args = new String[] {"-i", "example_crd_1.yaml"};
        var result = CrdToolsArgs.parseArgs(args);
        assertEquals("com.gs.crdtools.generated", result.packageName());
        assertEquals("generated.srcjar", result.outputPath());
        assertEquals(1, result.crdPaths().size());
        assertEquals("example_crd_1.yaml", result.crdPaths().get(0));
    }

    @Test
    void testHasNextInputFirst() {
        var args = new String[] {"-i", "example_crd_1.yaml", "example_crd_2.yaml", "-p", "this.is.a.package"};
        assertEquals(3, CrdToolsArgs.hasNext(args, 0));
    }

    @Test
    void testHasNextInputMiddle() {
        var args = new String[] {"-p", "this.is.a.package", "-i", "example_crd_1.yaml", "example_crd_2.yaml", "-o", "generated.srcjar"};
        assertEquals(5, CrdToolsArgs.hasNext(args, 2));
    }

    @Test
    void testHasNextInputLast() {
        var args = new String[] {"-p", "this.is.a.package", "-i", "example_crd_1.yaml", "example_crd_2.yaml"};
        assertEquals(5, CrdToolsArgs.hasNext(args, 2));
    }
}