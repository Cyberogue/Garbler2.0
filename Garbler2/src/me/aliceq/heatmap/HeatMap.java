/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, average, publish, distribute, sublicense, and/or sell
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

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An extension of a Heatlist which provides key-based access using a sorted
 * list. Accesses are performed in O(logn);
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <K> Comparable key type
 */
public class HeatMap<K extends Comparable> {

    private HeatList values;
    private List<K> keys;

    /**
     * Constructs an empty HeatMap
     */
    public HeatMap() {
        this(1);
    }

    /**
     * Constructs an empty HeatMap with specified initial capacity
     *
     * @param initialCapacity the initial capacity of the map
     */
    public HeatMap(int initialCapacity) {
        values = new HeatList(initialCapacity);
        keys = new ArrayList(initialCapacity);
    }

    /**
     * Returns the number of elements in the HeatMap
     *
     * @return the number of elements in the HeatMap
     */
    public int size() {
        return keys.size();
    }

    /**
     * Checks whether or not the HeatMap is normalized
     *
     * @return true if the HeatMap is normalized
     */
    public boolean normalized() {
        return values.normalized();
    }

    /**
     * Forces normalization of the HeatMap by scaling each value to make their
     * sum equal 1
     */
    public void normalizeAll() {
        values.normalize();
    }

    /**
     * Returns the total sum of all values in the HeatMap
     *
     * @return the total sum of all values in the HeatMap
     */
    public float getTotal() {
        return values.getTotal();
    }

    /**
     * Verifies that a key exists and if not, it creates it
     *
     * @param key the key to add
     * @return true if a new key was made or false if the key already exists
     */
    public boolean touch(K key) {
        return touch(key, keyToIndex(key));
    }

    /**
     * Delete a key and its corresponding value from the map, normalizing the
     * map as needed
     *
     * @param key the key to remove
     * @return true if successful or false if the key doesn't exist
     */
    public boolean removeKey(K key) {
        int index = keyToIndex(key);
        if (keys.get(index) == key) {
            keys.remove(index);
            values.deleteIndex(index);
            return true;
        }
        return false;
    }

    /**
     * Delete a key and its corresponding value from the map. Note that this
     * method is unsafe and risks de-normalizing the HeatMap.
     *
     * @param key the key to remove
     * @return true if successful or false if the key doesn't exist
     */
    public boolean removeKeyUnsafe(K key) {
        int index = keyToIndex(key);
        if (keys.get(index) == key) {
            keys.remove(index);
            values.deleteIndexUnsafe(index);
            return true;
        }
        return false;
    }

    /**
     * Checks if a key exists
     *
     * @param key the key to check
     * @return true if the key exists
     */
    public boolean contains(K key) {
        int index = keyToIndex(key);
        return keys.get(index) == key;
    }

    /**
     * Returns the set of keys held
     *
     * @return the set of keys held
     */
    public Collection<K> keySet() {
        return keys;
    }

    /**
     * Retrieves the value at a certain key. If the key doesn't exist a value of
     * 0 is returned;
     *
     * @param key the key to retrieve
     * @return the value at the given key or 0
     */
    public float getValue(K key) {
        int index = keyToIndex(key);
        if (keys.get(index) == key) {
            return values.getValue(index);
        } else {
            return 0;
        }
    }

    /**
     * Retrieves a key at a specified index
     *
     * @param index the index
     * @return the key at the specified index
     */
    public K getKey(int index) {
        return keys.get(index);
    }

    /**
     * Clears the HeatMap
     */
    public void Clear() {
        keys.clear();
        values.clear();
    }

    /**
     * Increments the value of a key by a sample. If the key doesn't exist, a
     * new key is made.
     *
     * @param key the key to increment
     * @return the new value at the given key
     */
    public float increment(K key) {
        return increment(key, 1);
    }

    /**
     * Increments the value of a key by an amount of samples. If the key doesn't
     * exist, a new key is made.
     *
     * @param key the key to increment
     * @param amount the number of samples to increase
     * @return the new value at the given key
     */
    public float increment(K key, int amount) {
        int index = keyToIndex(key);
        if (index < keys.size() && keys.get(index) == key) {
            return values.increment(index, amount);
        } else {
            // Add a new key and increment it
            keys.add(index, key);
            values.addNewIndex(index);
            return values.increment(index, amount);
        }
    }

    /**
     * Increments the value of a key by an amount of samples. If the key doesn't
     * exist this method returns 0.
     *
     * @param key the key to increment
     * @return the new value at the given key or 0
     */
    public float incrementIfExists(K key) {
        return HeatMap.this.incrementIfExists(key, 0);
    }

    /**
     * Increments the value of a key by an amount of samples. If the key doesn't
     * exist this method returns 0.
     *
     * @param key the key to increment
     * @param amount the number of samples to increase
     * @return the new value at the given key or 0
     */
    public float incrementIfExists(K key, int amount) {
        int index = keyToIndex(key);
        if (index < keys.size() && keys.get(index) == key) {
            return values.increment(index, amount);
        }
        return 0f;
    }

    /**
     * Flags the HeatMap as normalized if the sum of its values equals 1.0f or
     * 0f (+/-.00001f)
     *
     * @return true if the List is normalized, false otherwise
     */
    public boolean verifyNormalization() {
        return values.verifyNormalization();
    }

    /**
     * Returns a read-only Collection of all the entries in the HeatMap as
     * key-value pairs
     *
     * @return a Collection of key-value pairs
     */
    public Collection<Map.Entry<K, Float>> entries() {
        ArrayList< Map.Entry<K, Float>> set = new ArrayList(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            set.add(new AbstractMap.SimpleImmutableEntry(keys.get(i), values.getValue(i)));
        }
        return Collections.unmodifiableList(set);
    }

    /**
     * Returns a read-only map of all the key-value pairs in the HeatMap
     *
     * @return a new Map instance
     */
    public Map<K, Float> toMap() {
        Map<K, Float> map = new HashMap();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.getValue(i));
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates an ordered list of keys and cumulative-sum values
     *
     * @return a read-only ordered list of key-value pairs
     */
    public Collection<Map.Entry<K, Float>> getCumulative() {
        ArrayList< Map.Entry<K, Float>> set = new ArrayList(keys.size());
        float sum = 0;
        for (int i = 0; i < keys.size(); i++) {
            sum += values.getValue(i);
            set.add(new AbstractMap.SimpleImmutableEntry(keys.get(i), sum));
        }
        return Collections.unmodifiableList(set);
    }

    /**
     * Creates a read-only copy of the HeatList instance with the same
     * parameters
     *
     * @return a read-only copy of the HeatList instance
     */
    public HeatMap<K> asReadonly() {
        HeatMap<K> map = new ReadOnlyHeatMap();
        map.keys = Collections.unmodifiableList(keys);
        map.values = values;
        return map;
    }

    /**
     * Verifies that a key exists and if not, it creates it. This method should
     * only be used with an external call to keyToIndex in order to minimize
     * keyToIndex calls. Using any other index may cause errors or put the map
     * in an illegal state.
     *
     * @param key the key to add
     * @param index the index to touch. This should be obtained using
     * KeyToIndex(key).
     * @return true if a new key was made or false if the key already exists
     */
    public boolean touch(K key, int index) {
        if (checkValidity(index, key)) {
            values.addNewIndex(index);
            keys.add(index, key);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the index that a key should be at
     *
     * @param key the key to check
     * @return the index to place the key into
     */
    public int keyToIndex(K key) {
        int low = 0, high = keys.size();
        while (high != low) {
            int mid = (low + high) / 2;
            // Extract the middle element of the two
            K element = keys.get(mid);
            // Compare to key
            if (element.compareTo(key) < 0) {
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
     * Retrieves the key stored at a given index
     *
     * @param index the index to retrieve
     * @return a key object
     */
    public K indexToKey(int index) {
        return keys.get(index);
    }

    /**
     * Returns a HeatList of values by indeces rather than by key
     *
     * @return a HeatList of values
     */
    public HeatList getHeatList() {
        return values;
    }

    /**
     * Verifies that a key exists at the given index
     *
     * @param index the index to check
     * @param key the key to check
     * @return true if the key exists at the given index
     */
    public boolean verifyKey(int index, K key) {
        if (index < 0 || index >= keys.size()) {
            throw new IndexOutOfBoundsException();
        }

        return keys.get(index) == key;
    }

    /**
     * Pushes a key into the given index. Note that this does not check if the
     * key exists so use checkValidity before calling this method.
     *
     * @param key the key to add
     * @param index the index to push the key into
     */
    public void pushKey(K key, int index) {
        values.addNewIndex(index);
        keys.add(index, key);
    }

    /**
     * Verifies that the given key can go into the provided index. That is, it
     * is either being added to the end or the key at the given location is
     * different. Note that this method does not check if the key already exists
     * in another index.
     *
     * @param index the index to touch
     * @param key the key to verify
     * @return true if the key is valid at the given index
     */
    public boolean checkValidity(int index, K key) {
        if (index < 0 || index > keys.size()) {
            throw new IndexOutOfBoundsException();
        }

        return (keys.size() == index || keys.get(index) != key);
    }

    public String toString(DecimalFormat format) {
        if (keys.size() <= 0) {
            return values.normalized() ? "{}" : "<>";
        }

        String s = (values.normalized() ? "{" : "<") + keys.get(0) + "=" + values.getValue(0);
        for (int i = 1; i < keys.size(); i++) {
            s += ", " + format.format(keys.get(i)) + "=" + format.format(values.getValue(i));
        }
        s += (values.normalized() ? "}" : ">");
        return s;
    }

    @Override
    public String toString() {
        if (keys.size() <= 0) {
            return values.normalized() ? "{}" : "<>";
        }

        String s = (values.normalized() ? "{" : "<") + keys.get(0) + "=" + values.getValue(0);
        for (int i = 1; i < keys.size(); i++) {
            s += ", " + keys.get(i) + "=" + values.getValue(i);
        }
        s += (values.normalized() ? "}" : ">");
        return s;
    }

    /**
     * Successively applies a filter to a source
     *
     * @param source
     * @param filter
     * @return a new HeatMap with the result
     */
    public static final HeatMap applyFilter(HeatMap source, HeatMapFilter filter) {
        return filter.applyFilter(new HeatMap(), source);
    }

    /**
     * Successively applies a filter to two sources
     *
     * @param source1
     * @param source2
     * @param filter
     * @return a new HeatMap with the result
     */
    public static final HeatMap applyFilter(HeatMap source1, HeatMap source2, HeatMapFilter filter) {
        return filter.applyFilter(new HeatMap(), source1, source2);
    }

    /**
     * Successively applies a filter to three sources
     *
     * @param source1
     * @param source2
     * @param source3
     * @param filter
     * @return a new HeatMap with the result
     */
    public static final HeatMap applyFilter(HeatMap source1, HeatMap source2, HeatMap source3, HeatMapFilter filter) {
        return filter.applyFilter(new HeatMap(), source1, source2, source3);
    }

    /**
     * Successively applies a filter to multiple sources
     *
     * @param sources
     * @param filter
     * @return a new HeatMap with the result
     */
    public static final HeatMap applyFilter(HeatMap[] sources, HeatMapFilter filter) {
        return filter.applyFilter(new HeatMap(), sources);
    }

    /**
     * Successively applies a filter to multiple sources
     *
     * @param sources
     * @param filter
     * @return a new HeatMap with the result
     */
    public static final HeatMap applyFilter(Collection<HeatMap> sources, HeatMapFilter filter) {
        return filter.applyFilter(new HeatMap(), sources);
    }
}
