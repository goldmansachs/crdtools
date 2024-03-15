/*
 * Copyright 2021 Noa Resare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nryaml;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YAMLValueTest {
    @Test
    void testSimpleTraversal() {
        var map = Map.of(
                "left", List.of(1, 2, 3),
                "middle", "foobar",
                "right", true
        );
        var value = YAMLUtil.fromBare(map);

        var left = value.asMapping().get("left");
        var aNumber = left.asSequence().get(1);
        assertEquals(2, aNumber.asInteger());

        var middle = value.asMapping().get("middle");
        assertEquals("foobar", middle.asString());

        var right = value.asMapping().get("right");
        assertTrue(right.asBoolean());

        assertEquals(map, value.toBareObject());
    }

    @Test
    void testNonStringMapKey() {
        assertThrows(IllegalArgumentException.class, () -> YAMLUtil.fromBare(Map.of(true, 42)));
    }

    @Test
    void checkUnhandledClass() {
        assertThrows(IllegalArgumentException.class, () -> YAMLUtil.fromBare(String.class));
    }

    @Test
    void testNullValue() {
        var value = YAMLUtil.fromString("""
                key: null""");
        assertNull(value.asMapping().get("key").asString());
    }
}
