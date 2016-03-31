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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A read-only window into a HeatMap's data set. This class lists the value-pair
 * associations in a sorted, easily iterable order. This instance contains a
 * copy of the HeatMap data so if the original is altered, this instance is not
 * changed.
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <E> Comparable key type
 */
public class HeatMapDataSet<E extends Comparable> implements Set<Map.Entry<E, Float>> {

    private final Map.Entry<E, Float>[] entries;

    /**
     * Creates a data set based on a parnet heatmap
     *
     * @param from the HeatMap to create a data view from
     */
    protected HeatMapDataSet(HeatMap<E> from) {
        this.entries = new Map.Entry[from.size()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = from.getEntry(i);
        }
    }

    /**
     * Gets a Map.Entry representing the data at the given index
     *
     * @param index index to retrieve
     * @return a new Map.Entry<E, Float> instance
     */
    public Map.Entry get(int index) {
        return entries[index];
    }

    /**
     * Gets the key at the given index
     *
     * @param index index to retrieve
     * @return the key at the given index
     */
    public E getKey(int index) {
        return entries[index].getKey();
    }

    /**
     * Gets the value at the given index
     *
     * @param index index to retrieve
     * @return the value at the given index
     */
    public float getValue(int index) {
        return entries[index].getValue();
    }

    /**
     * Gets the key-value pairing at the given index
     *
     * @param index index to retrieve
     * @return the key-value pairing at the given index
     */
    public Map.Entry<E, Float> getEntry(int index) {
        return entries[index];
    }

    /**
     * Returns the index of a given key or -1 if it doesn't exist
     *
     * @param key the key to check
     * @return the index of the key or -1
     */
    public int indexOf(E key) {
        int position = positionOf(key);
        if (entries[position].getKey().equals(key)) {
            return position;
        } else {
            return -1;
        }
    }

    /**
     * Returns the position of a key, meaning where in the list it should exist.
     * If the key exists this is the index of the key. If not, this is where the
     * key would be inserted.
     *
     * @param key the key to check
     * @return the insertion point of the key
     */
    public int positionOf(E key) {
        int low = 0, high = entries.length;
        while (high != low) {
            int mid = (low + high) / 2;
            // Extract the middle element of the two
            Map.Entry<E, Float> element = entries[mid];
            // Compare to key
            if (element.getKey().compareTo(key) < 0) {
                // Increase low pointer
                low = mid + 1;
            } else {
                // Decrease high pointer
                high = mid;
            }
        }
        return low;
    }

    /**
     * Returns an array of all the keys in the HeatMap. Each key will only exist
     * once in the array.
     *
     * @return
     */
    public Set<E> keySet() {
        E[] keys = (E[]) new Comparable[entries.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = entries[i].getKey();
        }
        return new HashSet(Arrays.asList(keys));
    }

    /**
     * Gets the total sum of all elements in the set
     *
     * @return the total cumulative sum
     */
    public float getTotal() {
        float sum = 0f;
        for (Map.Entry<E, Float> entry : entries) {
            sum += entry.getValue();
        }
        return sum;
    }

    @Override
    public int size() {
        return entries.length;
    }

    @Override
    public boolean isEmpty() {
        return entries.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Comparable) {
            return indexOf((E) o) >= 0;
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
        return new HeatMapDataViewIterator();
    }

    @Override
    public Object[] toArray() {
        Map.Entry[] copy = new Map.Entry[entries.length];
        System.arraycopy(entries, 0, copy, 0, copy.length);

        return copy;
    }

    @Override
    public String toString() {
        if (entries.length == 0) {
            return "[]";
        }
        String s = "[" + entries[0];
        for (int i = 1; i < entries.length; i++) {
            s += ", " + entries[i].getKey() + "=" + (float) entries[i].getValue();
        }
        return s + "]";
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Object[] array;
        if (a.length > entries.length) {
            array = a;
            for (int i = entries.length; i < a.length; i++) {
                a[i] = null;
            }
        } else {
            array = new Object[entries.length];
        }

        System.arraycopy(entries, 0, array, 0, entries.length);

        return (T[]) array;
    }

    private class HeatMapDataViewIterator implements Iterator<Map.Entry<E, Float>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < entries.length;
        }

        @Override
        public Map.Entry<E, Float> next() {
            return entries[index++];
        }

        @Override
        @Deprecated
        public void remove() {
            throw new UnsupportedOperationException("Object is read-only");
        }
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
