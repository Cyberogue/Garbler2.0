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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An extension of a Heatlist which provides key-based access using a sorted
 * list. Accesses are performed in O(logn).
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
     * Returns true when the map is empty
     *
     * @return true when the map is empty
     */
    public boolean isEmpty() {
        return keys.isEmpty();
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
     * Returns a set of all the keys in the HeatMap
     *
     * @return a new Set of keys
     */
    public K[] keys() {
        return (K[]) keys.toArray(new Comparable[keys.size()]);
    }

    /**
     * Verifies that a key exists and if not, it creates it
     *
     * @param key the key to add
     * @return true if a new key was made or false if the key already exists
     */
    public boolean touch(K key) {
        return touch(key, positionOf(key));
    }

    /**
     * Delete a key and its corresponding value from the map, normalizing the
     * map as needed
     *
     * @param key the key to remove
     * @return true if successful or false if the key doesn't exist
     */
    public synchronized boolean removeKey(K key) {
        int index = positionOf(key);
        if (keys.get(index) == key) {
            keys.remove(index);
            values.deleteIndex(index);
            return true;
        }
        return false;
    }

    /**
     * Delete a key and its corresponding value from the map. Unlike removeKey,
     * this method simply removes the data at the risk of denormalizing a
     * normalized HeatMap.
     *
     * @param key the key to remove
     * @return true if successful or false if the key doesn't exist
     */
    public synchronized boolean discardKey(K key) {
        int index = positionOf(key);
        if (keys.get(index) == key) {
            keys.remove(index);
            values.discardIndex(index);
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
        int index = positionOf(key);
        return keys.get(index) == key;
    }

    /**
     * Retrieves the value at a certain key. If the key doesn't exist a value of
     * 0 is returned.
     *
     * @param key the key to retrieve
     * @return the value at the given key or 0
     */
    public float getValue(K key) {
        int index = positionOf(key);
        if (index >= keys.size()) {
            return 0;
        } else if (keys.get(index) == key) {
            return values.get(index);
        } else {
            return 0;
        }
    }

    /**
     * Clears the HeatMap
     */
    public synchronized void Clear() {
        keys.clear();
        values.clear();
    }

    /**
     * Overwrites the value at a given key. If the key doesn't exist, a new key
     * is made. Note that this flags a HeatMap as denormalized.
     *
     * @param key the key to set
     * @param value the value to set
     */
    public synchronized void set(K key, float value) {
        int index = positionOf(key);

        if (index < keys.size() && keys.get(index) == key) {
            values.set(index, value);
        } else {
            // Add a new key and increment it
            keys.add(index, key);
            values.addNewIndex(index);
            values.set(index, value);
        }
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
    public synchronized float increment(K key, int amount) {
        int index = positionOf(key);

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
    public synchronized float incrementIfExists(K key, int amount) {
        int index = positionOf(key);

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
     * Returns a read-only map of all the key-value pairs in the HeatMap
     *
     * @return a new Map instance
     */
    public Map<K, Float> toMap() {
        Map<K, Float> map = new HashMap();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Verifies that a key exists and if not, it creates it. This method should
     * only be used with an external call to positionOf in order to minimize
     * positionOf calls. Using any other index may cause errors or put the map
     * in an illegal state.
     *
     * @param key the key to add
     * @param index the index to touch. This should be obtained using
     * KeyToIndex(key).
     * @return true if a new key was made or false if the key already exists
     */
    protected boolean touch(K key, int index) {
        if (checkValidity(index, key)) {
            values.addNewIndex(index);
            keys.add(index, key);

            return true;
        }
        return false;
    }

    /**
     * Retrieves the value at the given key as a Map.Entry
     *
     * @param key the key to retrieve
     * @return a new Map.Entry<K, Float>
     */
    public Map.Entry<K, Float> getEntry(K key) {
        int position = positionOf(key);
        if (keys.get(position).equals(key)) {
            return getEntry(position);
        } else {
            return null;
        }
    }

    /**
     * Retrieves the value at the given index as a Map.Entry
     *
     * @param index the index to retrieve
     * @return a new Map.Entry<K, Float>
     */
    protected Map.Entry<K, Float> getEntry(int index) {
        rangeCheck(index);
        return new AbstractMap.SimpleImmutableEntry(keys.get(index), values.get(index));
    }

    /**
     * Retrieves the index that a key should be at
     *
     * @param key the key to check
     * @return the index to place the key into
     */
    protected synchronized int positionOf(K key) {
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
     * Verifies that a key exists at the given index
     *
     * @param index the index to check
     * @param key the key to check
     * @return true if the key exists at the given index
     */
    protected boolean verifyKey(int index, K key) {
        rangeCheck(index);

        return keys.get(index) == key;
    }

    /**
     * Throws an IndexOutOfBoundsException if index is outside the array bounds
     *
     * @param index index to check
     */
    protected void rangeCheck(int index) {
        if (index < 0 || index > keys.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Returns the number of samples in the map
     *
     * @return the number of samples in the map
     */
    public int getSampleCount() {
        return values.getSampleCount();
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
        rangeCheck(index);

        return (keys.size() == index || keys.get(index) != key);
    }

    /**
     * Creates a copy of the HeatMap with the same values and parameters
     *
     * @return a new HeatMap instance
     */
    public synchronized HeatMap<K> copy() {
        HeatMap<K> map = new HeatMap();
        map.keys = new ArrayList(keys);
        map.values = values.copy();
        return map;
    }

    /**
     * Returns a DataSet view into the HeatMap's contents
     *
     * @return a new HeatMapDataSet instance
     */
    public HeatMapDataSet<K> getData() {
        return new HeatMapDataSet(this);
    }

    public String toString(DecimalFormat format) {
        if (keys.size() <= 0) {
            return values.normalized() ? "{}" : "<>";
        }

        String s = (values.normalized() ? "{" : "<") + keys.get(0) + "=" + values.get(0);
        for (int i = 1; i < keys.size(); i++) {
            s += ", " + format.format(keys.get(i)) + "=" + format.format(values.get(i));
        }
        s += (values.normalized() ? "}" : ">");
        return s;
    }

    @Override
    public String toString() {
        if (keys.size() <= 0) {
            return values.normalized() ? "{}" : "<>";
        }

        String s = (values.normalized() ? "{" : "<") + keys.get(0) + "=" + values.get(0);
        for (int i = 1; i < keys.size(); i++) {
            s += ", " + keys.get(i) + "=" + values.get(i);
        }
        s += (values.normalized() ? "}" : ">");
        return s;
    }
}
