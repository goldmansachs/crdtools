package com.gs.crdtools;

import com.gs.crdtools.generated.CronTab;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlifierTest {

    @Test
    void testYamlify() {
        // N.B. If changing any of the code above the "expected" variable,
        // please change YamlifierTest.java/29 to the new line where cronTabObj is defined.
        var expected =
                """
                apiVersion: stable.example.com/v1
                kind: CronTab
                metadata:
                  annotations:
                    com.gs.crdtools/yamlgeneratedby: YamlifierTest.java/28
                  labels:
                    buildscmrevision: __FILL_IN_BUILD_SCM_REVISION__
                    buildscmstatus: __FILL_IN_BUILD_SCM_STATUS__
                  name: toyaml
                  """;

        var cronTabObj = Yamlifier.base(CronTab.class, "toyaml");
        var cronTabBytes = Yamlifier.yamlify(cronTabObj);
        var actual = new String(cronTabBytes);

        assertEquals(expected.strip(), actual.strip());
    }

    @Test
    void testRecursiveToJavaMap() {
        var vavrNestedMaps = HashMap.of(
                "a", HashMap.of(
                        "b", HashMap.of(
                                "c", HashMap.of(
                                        "d", "e"
                                )
                        )
                ),
                "g", HashMap.of("h", "i")
        );

        // Creating the same map as above, but using the Java API.
        java.util.HashMap<String, Object> actual = new java.util.HashMap<>();
        java.util.HashMap<String, Object> level1aNested = new java.util.HashMap<>();
        java.util.HashMap<String, Object> level2aNested = new java.util.HashMap<>();
        java.util.HashMap<String, Object> level3aNested = new java.util.HashMap<>();
        level3aNested.put("d", "e");
        level2aNested.put("c", level3aNested);
        level1aNested.put("b", level2aNested);
        actual.put("a", level1aNested);

        java.util.HashMap<String, Object> level1bNested = new java.util.HashMap<>();
        level1bNested.put("h", "i");
        actual.put("g", level1bNested);

        // Testing for equality.
        assertEquals(Yamlifier.toJavaMapRecursive((vavrNestedMaps.toJavaMap())), actual);
    }
}
