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

/**
 * An extension of HeatList which provides read-only functionality
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class ReadOnlyHeatList extends HeatList {

    public ReadOnlyHeatList() {
        super();
    }

    public ReadOnlyHeatList(int size) {
        super(size);
    }

    @Deprecated
    @Override
    public float increment(int index) {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public float increment(int index, int amount) {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public void normalize() {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public float equalize() {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public void reset() {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public void markDirty() {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public void addNewIndex(int index) {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public float deleteIndexUnsafe(int index) {
        throw new UnsupportedOperationException("Instance is read-only");
    }

    @Deprecated
    @Override
    public float deleteIndex(int index) {
        throw new UnsupportedOperationException("Instance is read-only");
    }

}
