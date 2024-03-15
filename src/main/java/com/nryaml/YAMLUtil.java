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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class YAMLUtil {
    private static final DumperOptions OPTIONS = new DumperOptions();
    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }
    private static final ThreadLocal<Yaml> parser = ThreadLocal.withInitial(() -> new Yaml(OPTIONS));

    public static YAMLValue fromInputStream(InputStream inputStream) {
        return new YAMLValueImpl(parser.get().load(inputStream));
    }

    public static YAMLValue fromBare(Object bare) {
        return new YAMLValueImpl(bare);
    }

    public static String toString(YAMLValue value) {
        return parser.get().dump(value.toBareObject());
    }

    public static YAMLValue fromString(String input) {
        return new YAMLValueImpl(parser.get().load(input));
    }

    /**
     * Parse and return all YAML documents present in the file referenced by path.
     * This class will wrap IOExceptions into RuntimeExceptions to facilitate use
     * with streaming APIs.
     */
    public static Iterable<YAMLValue> allFromPath(Path path) {
        try (var inputStream = Files.newInputStream(path)) {
            return stream(parser.get().loadAll(inputStream))
                    .map(YAMLValueImpl::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
