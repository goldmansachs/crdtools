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
import static org.junit.jupiter.api.Assertions.assertThrows;

class YAMLSequenceTest {
    @Test
    void testWrongCast() {
        assertThrows(
                ClassCastException.class,
                () -> YAMLUtil.fromBare(List.of()).asSequence().asInteger()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLUtil.fromBare(List.of()).asSequence().asBoolean()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLUtil.fromBare(List.of()).asSequence().asString()
        );
        assertThrows(
                ClassCastException.class,
                () -> YAMLUtil.fromBare(List.of()).asSequence().asMapping()
        );
    }

    @Test
    void testAsSequence() {
        var sequence = YAMLUtil.fromBare(List.of()).asSequence();
        assertEquals(sequence, sequence.asSequence());
    }
}