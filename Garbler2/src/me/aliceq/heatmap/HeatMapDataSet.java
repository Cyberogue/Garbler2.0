/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.aliceq.heatmap;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A read-only window until a HeatMap's data set. This class lists the
 * value-pair associations in a sorted, easily iterable order.
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <E> Comparable key type
 */
public class HeatMapDataSet<E extends Comparable> implements Set<Map.Entry<E, Float>> {

    private final int modcount;
    private final HeatMap<E> parent;

    /**
     * Creates a data set based on a parnet heatmap
     *
     * @param parent
     */
    public HeatMapDataSet(HeatMap<E> parent) {
        modcount = parent.modcount;
        this.parent = parent;
    }

    /**
     * Gets a Map.Entry representing the data at the given index
     *
     * @param index index to retrieve
     * @return a new Map.Entry<E, Float> instance
     */
    public Map.Entry get(int index) {
        if (modcount != parent.modcount) {
            throw new ConcurrentModificationException("Parent map was modified");
        }
        return parent.getEntry(index);
    }

    /**
     * Gets the key at the given index
     *
     * @param index index to retrieve
     * @return the key at the given index
     */
    public E getKey(int index) {
        if (modcount != parent.modcount) {
            throw new ConcurrentModificationException("Parent map was modified");
        }
        return parent.getKey(index);
    }

    /**
     * Gets the value at the given index
     *
     * @param index index to retrieve
     * @return the value at the given index
     */
    public float getValue(int index) {
        if (modcount != parent.modcount) {
            throw new ConcurrentModificationException("Parent map was modified");
        }
        return parent.getValue(index);
    }

    /**
     * Returns the index of a given key or -1 if it doesn't exist
     *
     * @param key the key to check
     * @return the index of the key or -1
     */
    public int indexOf(E key) {
        int index = parent.keyToIndex(key);
        if (key.equals(parent.getKey(index))) {
            return index;
        } else {
            return -1;
        }
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Comparable) {
            int index = parent.keyToIndex((E) o);
            return parent.verifyKey(index, (E) o);
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) { // oh shut up netbeans, blame the JDK
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Map.Entry<E, Float>> iterator() {
        return new HeatMapDataSetIterator();
    }

    @Override
    public Object[] toArray() {
        Map.Entry[] entries = new Map.Entry[parent.size()];

        for (int i = 0; i < entries.length; i++) {
            entries[i] = parent.getEntry(i);
        }
        return entries;
    }

    @Override
    public String toString() {
        return parent.toString() + '*';
    }

    public class HeatMapDataSetIterator implements Iterator<Map.Entry<E, Float>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < parent.size();
        }

        @Override
        public Map.Entry<E, Float> next() {
            return parent.getEntry(index++);
        }

        @Override
        @Deprecated
        public void remove() {
            throw new UnsupportedOperationException("Object is read-only");
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Use Object[] toArray() method instead");
    }

    @Override
    public boolean add(Map.Entry<E, Float> e) {
        throw new UnsupportedOperationException("Object is read-only");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Object is read-only");
    }

    @Override
    @Deprecated
    public boolean addAll(Collection<? extends Map.Entry<E, Float>> c) {
        throw new UnsupportedOperationException("Object is read-only");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Object is read-only");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Object is read-only");
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException("Object is read-only");
    }
}
