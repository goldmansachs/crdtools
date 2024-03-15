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

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WrappingMapTest {
    @Test
    void testSimple() {
        var map = new WrappingMap<>(Map.of("a", "b"));

        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertFalse(map.containsKey("z"));
        assertEquals("b", map.get("a"));
        assertTrue(map.containsValue("b"));
        assertTrue(map.containsKey("a"));
        assertEquals(Set.of("a"), map.keySet());
        var values = map.values();
        assertEquals(1, values.size());
        assertEquals("b", values.iterator().next());
    }

    @Test
    void testUnsupported() {
        var map = new WrappingMap<>(Map.of("a", "b"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("foo", "bar"));
        assertThrows(UnsupportedOperationException.class, () -> map.remove("a"));
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(Map.of("c", "d")));
        assertThrows(UnsupportedOperationException.class, map::clear);
    }
}
