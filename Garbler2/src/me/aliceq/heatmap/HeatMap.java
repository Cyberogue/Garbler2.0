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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An extension of a Heatlist which provides key-based access using a sorted
 * list. Accesses are performed in O(logn);
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <K> the type of object used as a key
 */
public class HeatMap<K extends Comparable> {

    final protected HeatList values = new HeatList();
    final protected ArrayList<K> keys = new ArrayList();

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
     * Returns the total sum of all values in the HeatMap
     *
     * @return the total sum of all values in the HeatMap
     */
    public float getTotal() {
        return values.size();
    }

    /**
     * Adds a key to the list with initial value 0
     *
     * @param key the key to add
     * @return true if successful or false if the key already exists
     */
    public boolean addKey(K key) {
        int index = keyToIndex(key);

        if (keys.size() == index || keys.get(index) != key) {
            values.addNewIndex(index);
            keys.add(index, key);
            return true;
        }
        return false;
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
     * Returns a Collection of all the entries in the HeatMap as key-value pairs
     *
     * @return a Collection of key-value pairs
     */
    public Map.Entry<K, Float>[] entries() {
        Map.Entry<K, Float>[] set = new Map.Entry[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            set[i] = new AbstractMap.SimpleImmutableEntry(keys.get(i), values.getValue(i));
        }
        return set;
    }

    /**
     * Returns a map of all the key-value pairs in the HeatMap
     *
     * @return a new Map instance
     */
    public Map<K, Float> toMap() {
        Map<K, Float> map = new HashMap();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.getValue(i));
        }
        return map;
    }

    /**
     * Retrieves the index that a key should be at
     *
     * @param key the key to check
     * @return the index to place the key into
     */
    protected int keyToIndex(K key) {
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

    @Override
    public String toString() {
        if (keys.size() <= 0) {
            return values.normalized() ? "{}" : "<>";
        }

        String s = (values.normalized() ? "{" : "<") + keys.get(0) + "=" + values.getValue(0);
        for (int i = 1; i < keys.size(); i++) {
            s += ", " + keys.get(i) + "=" + values.getValue(0);
        }
        s += (values.normalized() ? "}" : ">");
        return s;
    }

    public static void main(String[] args) {
        java.util.Random r = new java.util.Random();

        HeatMap<Character> map = new HeatMap();
        System.out.println(map);

        map.addKey('c');
        map.addKey('a');
        map.addKey('t');
        map.addKey('f');
        map.addKey('h');
        map.addKey('j');
        map.addKey('w');

        map.addKey('a');
        map.addKey('p');

        System.out.println(map.toMap());
        System.out.println(map);

        map.removeKey('h');
        System.out.println(map);
    }
}
