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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WrappingListTest {
    @Test
    void testSimple() {
        var list = new WrappingList<>(List.of("a", "b", "c"));
        assertEquals("a", list.get(0));
        assertEquals(3, list.size());
        //noinspection ConstantConditions
        assertFalse(list.isEmpty());
        assertTrue(new WrappingList<>(List.of()).isEmpty());
        assertFalse(list.contains("z"));

        var objects = list.toArray();
        assertEquals(3, objects.length);
        assertEquals("a", objects[0]);
    }

    @Test
    void testUnsupported() {
        var map = new WrappingList<>(List.of("a", "b"));
        assertThrows(UnsupportedOperationException.class, () -> map.add("foo"));
    }
}