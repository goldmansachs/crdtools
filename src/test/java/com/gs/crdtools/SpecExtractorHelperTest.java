package com.gs.crdtools;

import com.google.devtools.build.runfiles.Runfiles;
import com.gs.crdtools.SpecExtractorHelper.Metadata;
import com.nryaml.YAMLUtil;
import com.nryaml.YAMLValue;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecExtractorHelperTest {

    @Test
    void testCreateSpec() throws IOException {
        // given
        var runFiles = Runfiles.create();
        var input = List.of("crd0.yaml", "crd1.yaml")
                .map("__main__/src/test/resources/%s"::formatted)
                .map(runFiles::rlocation)
                .map(Path::of)
                .flatMap(YAMLUtil::allFromPath)
                .map(YAMLValue::asMapping);

        // when
        var result = SpecExtractorHelper.createSpec(input);

        // then
        var expected = new SpecExtractorHelper.Spec("""
                components:
                  schemas:
                    Phone:
                      type: object
                      properties:
                        colour:
                          type: string
                        metadata:
                          type: V1ObjectMeta
                        apiVersion:
                          type: string
                        kind:
                          type: string
                    Tablet:
                      type: object
                      properties:
                        metadata:
                          type: V1ObjectMeta
                        apiVersion:
                          type: string
                        kind:
                          type: string
                        height:
                          type: integer
                openapi: 3.0.0
                paths:
                  /dummy: {}
                info:
                  license:
                    name: MIT
                  title: kcc resources
                  version: 1.0.0
                """,
                HashMap.of(
                    "Phone", new Metadata("example.com", "v1"),
                    "Tablet", new Metadata("example.org", "v2")
                )
        );
        assertEquals(expected, result);
    }

    @Test
    void testGetLatestVersion() {
        // dummy versions list, schema is missing, storage is set to true
        // if two instances of storage=true exist, the first one is returned
        var versions = YAMLUtil.fromBare(List.of(
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
        ).toJavaList()).asSequence();

        assertEquals(versions.get(0).asMapping(), SpecExtractorHelper.getLatestVersion(versions));
    }

    @Test
    void testGetLatestVersionThrowsError() {
        // dummy versions list where storage is never true
        var versions = YAMLUtil.fromBare(List.of(
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
        ).toJavaList()).asSequence();

        Exception exception = assertThrows(IllegalStateException.class,
                () -> SpecExtractorHelper.getLatestVersion(versions));

        assertTrue(exception.getMessage().contains("No version found with storage=true"));
    }

}
