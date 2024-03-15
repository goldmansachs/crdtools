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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Map that wraps another Map
 */
class WrappingMap<K, V> implements Map<K, V> {

    private final Map<K, V> inner;

    public WrappingMap(Map<K, V> input) {
        this.inner = input;
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return inner.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return inner.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return inner.get(key);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        return inner.keySet();
    }

    @Override
    public Collection<V> values() {
        return inner.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return inner.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappingMap<?, ?> that = (WrappingMap<?, ?>) o;
        return inner.equals(that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner);
    }

}