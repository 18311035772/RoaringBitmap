/*
 * (c) Daniel Lemire, Owen Kaser, Samy Chambi, Jon Alvarado, Rory Graves, Björn Sperber
 * Licensed under the Apache License, Version 2.0.
 */

package org.roaringbitmap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Fast algorithms to aggregate many bitmaps.
 *
 * @author Daniel Lemire
 */
@SuppressWarnings("unused")
public final class FastAggregation {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private FastAggregation() {}

    /**
     * Sort the bitmap prior to using the and aggregate.
     *
     * @param bitmaps input bitmaps
     * @return aggregated bitmap
     */
    public static RoaringBitmap and(RoaringBitmap... bitmaps) {
        if (bitmaps.length == 0)
            return new RoaringBitmap();
        else if(bitmaps.length == 1)
        	return bitmaps[0].clone();
        RoaringBitmap[] array = Arrays.copyOf(bitmaps, bitmaps.length);
        Arrays.sort(array, new Comparator<RoaringBitmap>() {
            @Override
            public int compare(RoaringBitmap a, RoaringBitmap b) {
                return a.getSizeInBytes() - b.getSizeInBytes();
            }
        });
        RoaringBitmap answer = RoaringBitmap.and(array[0], array[1]);
        for (int k = 2; k < array.length; ++k)
            answer.and(array[k]);
        return answer;
    }

    /**
     * Uses a priority queue to compute the or aggregate.
     *
     * @param bitmaps input bitmaps
     * @return aggregated bitmap
     */
    public static RoaringBitmap or(RoaringBitmap... bitmaps) {
        if (bitmaps.length == 0)
            return new RoaringBitmap();

        PriorityQueue<RoaringBitmap> pq = new PriorityQueue<RoaringBitmap>(bitmaps.length, new Comparator<RoaringBitmap>() {
            @Override
            public int compare(RoaringBitmap a,
                               RoaringBitmap b) {
                return a.getSizeInBytes() - b.getSizeInBytes();
            }
        });
        Collections.addAll(pq, bitmaps);
        while (pq.size() > 1) {
            RoaringBitmap x1 = pq.poll();
            RoaringBitmap x2 = pq.poll();
            pq.add(RoaringBitmap.or(x1, x2));
        }
        return pq.poll();
    }

    /**
     * Uses a priority queue to compute the xor aggregate.
     *
     * @param bitmaps input bitmaps
     * @return aggregated bitmap
     */
    public static RoaringBitmap xor(RoaringBitmap... bitmaps) {
        if (bitmaps.length == 0)
            return new RoaringBitmap();

        PriorityQueue<RoaringBitmap> pq = new PriorityQueue<RoaringBitmap>(bitmaps.length, new Comparator<RoaringBitmap>() {
            @Override
            public int compare(RoaringBitmap a,
                               RoaringBitmap b) {
                return a.getSizeInBytes() - b.getSizeInBytes();
            }
        });
        Collections.addAll(pq, bitmaps);
        while (pq.size() > 1) {
            RoaringBitmap x1 = pq.poll();
            RoaringBitmap x2 = pq.poll();
            pq.add(RoaringBitmap.xor(x1, x2));
        }
        return pq.poll();
    }
}
