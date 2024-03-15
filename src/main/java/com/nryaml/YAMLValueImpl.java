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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

class YAMLValueImpl implements YAMLValue {
    private record Entry(String key, YAMLValue value) {}
    Object inner;

    public YAMLValueImpl(Object input) {
        if (input == null) {
            inner = null;
        } else if (input instanceof Map<?, ?> m) {
            inner = buildMapping(m);
        } else if (input instanceof List<?> l) {
            inner = new YAMLSequence(l.stream()
                    .map(YAMLValueImpl::new)
                    .collect(toList())
            );
        } else if (input instanceof Integer || input instanceof String || input instanceof Boolean) {
            inner = input;
        } else {
            throw new IllegalArgumentException("Don't know how to handle value of type %s".formatted(input.getClass()));
        }
    }

    private YAMLMapping buildMapping(Map<?, ?> map) {
        return new YAMLMapping(map.keySet().stream()
                .map(key -> {
                    if (key instanceof String s) return s;
                    var m = "Key '%s' was not a String but a %s".formatted(key, key.getClass());
                    throw new IllegalArgumentException(m);
                })
                .sorted()
                .map(key -> new Entry(key, new YAMLValueImpl(map.get(key))))
                .collect(toMap(Entry::key, Entry::value))
        );
    }

    @Override
    public YAMLMapping asMapping() {
        return (YAMLMapping) inner;
    }

    @Override
    public YAMLSequence asSequence() {
        return (YAMLSequence) inner;
    }

    @Override
    public String asString() {
        return (String) inner;
    }

    @Override
    public boolean asBoolean() {
        return (Boolean) inner;
    }

    @Override
    public int asInteger() {
        return (Integer) inner;
    }

    @Override
    public Object toBareObject() {
        if (inner instanceof YAMLMapping mapping) {
            return mapping.toBareObject();
        }
        if (inner instanceof YAMLSequence sequence) {
            return sequence.toBareObject();
        }
        return inner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YAMLValueImpl yamlValue = (YAMLValueImpl) o;
        return inner.equals(yamlValue.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner);
    }
}
