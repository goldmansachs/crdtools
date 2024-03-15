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

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class YAMLMapping extends WrappingMap<String, YAMLValue> implements YAMLValue {
    public static YAMLMapping of() {
        return new YAMLMapping(Map.of());
    }

    public static YAMLMapping of(String k1, YAMLValue v1) {
        return new YAMLMapping(Map.of(k1, v1));
    }

    public static YAMLMapping of(String k1, YAMLValue v1, String k2, YAMLValue v2) {
        return new YAMLMapping(Map.of(k1, v1, k2, v2));
    }

    public static YAMLMapping of(String k1, YAMLValue v1, String k2, YAMLValue v2, String k3, YAMLValue v3) {
        return new YAMLMapping(Map.of(k1, v1, k2, v2, k3, v3));
    }

    public static YAMLMapping of(String k1, YAMLValue v1, String k2, YAMLValue v2, String k3, YAMLValue v3, String k4, YAMLValue v4) {
        return new YAMLMapping(Map.of(k1, v1, k2, v2, k3, v3, k4, v4));
    }

    public YAMLMapping(Map<String, YAMLValue> input) {
        super(input);
    }

    @Override
    public YAMLMapping asMapping() {
        return this;
    }

    @Override
    public YAMLSequence asSequence() {
        throw new ClassCastException("Refusing to treat YAMLMapping as sequence");
    }

    @Override
    public String asString() {
        throw new ClassCastException("Refusing to treat YAMLMapping as String");
    }

    @Override
    public boolean asBoolean() {
        throw new ClassCastException("Refusing to treat YAMLMapping as boolean");
    }

    @Override
    public int asInteger() {
        throw new ClassCastException("Refusing to treat YAMLMapping as int");
    }

    @Override
    public Object toBareObject() {
        return this.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().toBareObject()));
    }


    public YAMLMapping combine(YAMLMapping other) {
        var result = new HashMap<>(this);
        result.putAll(other);
        return new YAMLMapping(result);
    }

    public YAMLMapping withReplaced(String key, YAMLValue value) {
        var result = new HashMap<>(this);
        result.put(key, value);
        return new YAMLMapping(result);
    }
}
