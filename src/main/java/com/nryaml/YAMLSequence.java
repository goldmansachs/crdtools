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

import static java.util.stream.Collectors.toList;

public class YAMLSequence extends WrappingList<YAMLValue> implements YAMLValue {
    public YAMLSequence(List<YAMLValue> inner) {
        super(inner);
    }

    @Override
    public YAMLMapping asMapping() {
        throw new ClassCastException("Refusing to treat YAMLSequence as mapping");
    }

    @Override
    public YAMLSequence asSequence() {
        return this;
    }

    @Override
    public String asString() {
        throw new ClassCastException("Refusing to treat YAMLSequence as String");
    }

    @Override
    public boolean asBoolean() {
        throw new ClassCastException("Refusing to treat YAMLSequence as boolean");
    }

    @Override
    public int asInteger() {
        throw new ClassCastException("Refusing to treat YAMLSequence as int");
    }

    @Override
    public Object toBareObject() {
        return this.stream().map(YAMLValue::toBareObject).collect(toList());
    }
}
