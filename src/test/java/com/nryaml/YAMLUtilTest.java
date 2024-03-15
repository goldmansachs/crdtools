/*
 * Copyright 2022 Noa Resare
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
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.nryaml.YAMLUtil.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class YAMLUtilTest {
    @Test
    void testAllFromPath(@TempDir Path tempDir) {
        // given
        var path = tempDir.resolve("test.yaml");
        try {
            Files.writeString(path, """
                    foo: bar
                    ---
                    foo: baz
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // when
        var result = stream(YAMLUtil.allFromPath(path)).toList();

        // then
        assertEquals(2, result.size());
        assertEquals(YAMLUtil.fromBare(Map.of("foo", "bar")), result.get(0));
        assertEquals(YAMLUtil.fromBare(Map.of("foo", "baz")), result.get(1));
    }
}
