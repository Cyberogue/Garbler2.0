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

/**
 * Class used for merging two or more HeatMaps. The process method must be
 * overwritten with the core functionality. For additional functionality, you
 * may override the preprocess and postprocess methods.
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <K> Comparable key type
 */
public abstract class HeatMapFilter<K extends Comparable> {

    protected HeatMap<K> destination;
    protected int sources;

    /**
     * Applies the filter to a single destination from three sources
     *
     * @param destination the HeatMap to write to, or null to write to a new
     * @param source
     * @return the destination HeatMap
     */
    public final HeatMap applyFilter(HeatMap<K> destination, HeatMap<K> source) {
        initialize(destination, 1);
        preprocess();
        process(source);
        postprocess();
        return destination;
    }

    /**
     * Applies the filter to a single destination from a single source
     *
     * @param destination the HeatMap to write to, or null to write to a new
     * @param source1
     * @param source2
     * @return the destination HeatMap
     */
    public final HeatMap applyFilter(HeatMap<K> destination, HeatMap<K> source1, HeatMap<K> source2) {
        initialize(destination, 2);
        preprocess();
        process(source1);
        process(source2);
        postprocess();
        return destination;
    }

    /**
     * Applies the filter to a single destination from two sources
     *
     * @param destination the HeatMap to write to, or null to write to a new
     * @param source1
     * @param source2
     * @param source3
     * @return the destination HeatMap
     */
    public final HeatMap applyFilter(HeatMap<K> destination, HeatMap<K> source1, HeatMap<K> source2, HeatMap<K> source3) {
        initialize(destination, 3);
        preprocess();
        process(source1);
        process(source2);
        process(source3);
        postprocess();
        return destination;
    }

    /**
     * Applies the filter to a single destination from multiple sources
     *
     * @param destination the HeatMap to write to, or null to write to a new
     * @param sources
     * @return the destination HeatMap
     */
    public final HeatMap applyFilter(HeatMap<K> destination, HeatMap<K>[] sources) {
        initialize(destination, sources.length);
        preprocess();
        for (HeatMap<K> source : sources) {
            process(source);
        }
        postprocess();
        return destination;
    }

    /**
     * Applies the filter to a single destination from multiple sources
     *
     * @param destination the HeatMap to write to, or null to write to a new
     * HeatMap
     * @param sources
     * @return the destination HeatMap
     */
    public final HeatMap applyFilter(HeatMap<K> destination, Collection<HeatMap<K>> sources) {
        initialize(destination, sources.size());
        preprocess();
        for (HeatMap<K> source : sources) {
            process(source);
        }
        postprocess();
        return destination;
    }

    private void initialize(HeatMap<K> destination, int sources) {
        this.destination = destination;
        this.sources = sources;
    }

    /**
     * Hook method called before any processing is done
     */
    public void preprocess() {
        if (destination == null) {
            destination = new HeatMap();
        }
    }

    /**
     * Hook method called after all processing is done
     */
    public void postprocess() {

    }

    /**
     * Override this method to provide custom filter functionality. This method
     * will be called for every source when merged or applied.
     *
     * HeatMap includes protected methods for data verification. Study the
     * JavaDoc for any Protected methods inside HeatMap. In addition, the filter
     * should have two initialized protected-access fields - destination and
     * sources.
     *
     * @param source the current source to get data from
     */
    public abstract void process(HeatMap<K> source);

    /* DEFAULT FILTERS */
    /**
     * Filter to find the average of multiple heatmaps
     *
     * @return a filter instance
     */
    public static final HeatMapFilter getDefaultAveragingFilter() {
        return new HeatMapFilter() {

            private boolean normalize = true;
            private float m;

            @Override
            public void preprocess() {
                // Calculate the influence of each source
                m = 1f / sources;
            }

            @Override
            public void process(HeatMap source) {
                // Modify normalization
                normalize &= source.normalized();

                // Add each map's value times its influence
                for (int i = 0; i < source.size(); i++) {
                    Comparable key = (Comparable) source.indexToKey(i);

                    int index = destination.keyToIndex(key);

                    destination.touch(key, index);
                    destination.getHeatList().overwriteValue(index, destination.getHeatList().getValue(i) + source.getHeatList().getValue(i) * m);
                }
            }

            @Override
            public void postprocess() {
                // Restore the normalization of the map
                if (normalize) {
                    destination.getHeatList().verifyNormalization();
                }
            }
        };
    }

    /**
     * Filter to find the total sums of multiple heatmaps
     *
     * @return a filter instance
     */
    public static final HeatMapFilter getDefaultSummingFilter() {
        return new HeatMapFilter() {

            @Override
            public void process(HeatMap source) {
                // Add each map's value times its influence
                for (int i = 0; i < source.size(); i++) {
                    Comparable key = (Comparable) source.indexToKey(i);

                    int index = destination.keyToIndex(key);

                    destination.touch(key, index);
                    destination.getHeatList().overwriteValue(index, destination.getHeatList().getValue(i) + source.getHeatList().getValue(i));
                }
            }
        };
    }

    /**
     * Filter which cascades a value across all affected HeatMaps.
     *
     * The influence of subsequent maps can be calculated through i(n) = f *
     * i(n-1) + (1 - f) * i(n-2)
     *
     * and follow the pattern i(0) = f, i(1) = f^2 - f, i(2) = f^3 - 2f^2 + f,
     * i(3) = f^4 - 3f^3 + 3f^2 - f, ...
     *
     * @param influence the cascading influence factor (f). This should be
     * between 0 and 1.
     * @return a filter instance
     */
    public static final HeatMapFilter getDefaultCascadingFilter(final float influence) {
        return new HeatMapFilter() {

            private final float f = influence;
            private final float finv = 1 - influence;

            @Override
            public void process(HeatMap source) {
                // Add each map's value times its influence
                for (int i = 0; i < source.size(); i++) {
                    Comparable key = (Comparable) source.indexToKey(i);

                    int index = destination.keyToIndex(key);

                    destination.touch(key, index);
                    destination.getHeatList().overwriteValue(index, (destination.getHeatList().getValue(i) * f) + (source.getHeatList().getValue(i) * finv));
                }
            }
        };
    }

    /**
     * Simple filter which creates a copy of the source
     *
     * @return a filter instance
     */
    public static final HeatMapFilter getDefaultCopyFilter() {
        return new HeatMapFilter<Integer>() {
            @Override
            public void process(HeatMap<Integer> source) {
                for (int i = 0; i < source.size(); i++) {
                    int key = source.indexToKey(i);
                    int index = destination.keyToIndex(key);

                    destination.touch(key, index);
                    destination.getHeatList().overwriteValue(index, source.getHeatList().getValue(i));
                }
                if (source.normalized()) {
                    destination.verifyNormalization();
                }
            }
        };
    }
}
