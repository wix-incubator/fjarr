package com.wixpress.fjarr.util;

import java.util.*;

/**
 * @author AlexeyR
 * @since 11/29/12 11:10 AM
 */

/**
 * Represents a associative array in which each key is assigned with a set of items.
 * This class is intended for a use in a single thread and not thread safe
 *
 * @param <KType> Key type
 * @param <VType> Value type
 */
public class MultiMap<KType, VType> implements Map<KType, VType>, ReadOnlyMultiMap<KType, VType> {
    Map<KType, Set<VType>> map = new HashMap<KType, Set<VType>>();


    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (KType k : map.keySet()) {
            if (map.get(k).contains(value))
                return true;
        }
        return false;
    }

    /**
     * Returns the first item from a collection
     *
     * @param key
     * @return
     */
    public VType get(Object key) {

        final Set<VType> vs = map.get(key);
        if (vs == null)
            return null;
        return vs.iterator().next();
    }

    /**
     * All items from a collection
     *
     * @param key
     * @return
     */
    public Set<VType> getAll(Object key) {
        return map.get(key);
    }

    public VType put(KType key, VType value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<VType>());
        }
        Set<VType> bucket = map.get(key);
        if (bucket.contains(value))
            return null;
        bucket.add(value);
        return value;
    }

    public VType remove(Object key) {
        if (map.containsKey(key))
            map.get(key).clear();

        return null;
    }

    public void putAll(Map<? extends KType, ? extends VType> m) {
        for (Entry<? extends KType, ? extends VType> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }



    public void clear() {
        map.clear();
    }

    public Set<KType> keySet() {
        return map.keySet();
    }

    public Collection<VType> values() {
        Set<VType> values = new HashSet<VType>();
        for (Set<VType> vc : map.values())
            values.addAll(vc);

        return values;
    }

    public Set<Entry<KType, VType>> entrySet() {
        Set<Entry<KType, VType>> entries = new HashSet<Entry<KType, VType>>();
        for (Entry<KType, Set<VType>> entry : map.entrySet()) {
            for (VType value : entry.getValue()) {
                entries.add(new MMapEntry(entry.getKey(), value));
            }
        }
        return entries;
    }

    public MultiMap<KType, VType> with(KType key, VType value) {
        this.put(key, value);
        return this;
    }

    public MultiMap<KType, VType> with(KType key, VType... values) {
        for (VType value : values)
            this.put(key, value);
        return this;
    }


    private static class MMapEntry<KType, VType> implements Map.Entry<KType, VType> {
        private final KType key;
        private VType value;


        private MMapEntry(KType key, VType value) {
            this.key = key;
            this.value = value;
        }

        public KType getKey() {
            return key;
        }

        public VType getValue() {
            return value;
        }

        public VType setValue(VType value) {
            this.value = value;
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiMap<KType, VType> multiMap = (MultiMap<KType, VType>) o;

        if (multiMap.map.size() != this.map.size()) return false;
        for (KType k : map.keySet()) {
            if (!multiMap.containsKey(k) || map.get(k).size() != multiMap.map.get(k).size()) return false;
            Set<VType> vs = multiMap.getAll(k);
            for (VType v : map.get(k)) {
                if (!vs.contains(v)) return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }
}
