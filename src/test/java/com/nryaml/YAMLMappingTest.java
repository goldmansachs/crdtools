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

import static org.junit.jupiter.api.Assertions.*;

class YAMLMappingTest {
    @Test
    void testWrongCast() {
        assertThrows(
              ClassCastException.class,
              () -> YAMLMapping.of().asInteger()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLMapping.of().asString()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLMapping.of().asBoolean()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLMapping.of().asSequence()
        );
    }

    @Test
    void testAsMapping() {
        var mapping = YAMLMapping.of().asMapping();
        assertEquals(mapping, mapping.asMapping());
    }

    @Test
    void testCombine() {
        var mapping = YAMLMapping.of();
        var other = YAMLMapping.of("key", YAMLMapping.of("inner_key", YAMLValue.of("inner_value")));
        mapping = mapping.combine(other);
        assertEquals(
                Map.of("key", Map.of("inner_key", "inner_value")),
                mapping.toBareObject()
        );
    }

    @Test
    void testWithReplaced() {
        var mapping = YAMLMapping.of("key", YAMLValue.of("first"));
        var another = mapping.withReplaced("key", YAMLValue.of("second"));
        assertEquals(YAMLMapping.of("key", YAMLValue.of("first")), mapping);
        assertEquals(YAMLMapping.of("key", YAMLValue.of("second")), another);
    }
}