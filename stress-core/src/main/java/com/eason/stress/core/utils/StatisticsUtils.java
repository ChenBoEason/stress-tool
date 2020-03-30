package com.eason.stress.core.utils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Eason
 * @date 2019/02/18
 **/
public class StatisticsUtils {

    public static long getTotal(List<Long> times) {
        long total = 0L;

        Long time;
        for(Iterator index = times.iterator(); index.hasNext(); total += time) {
            time = (Long)index.next();
        }

        return total;
    }

    public static float getAverage(List<Long> allTimes) {
        long total = getTotal(allTimes);
        return getAverage(total, allTimes.size());
    }

    public static float getAverage(long total, int size) {
        return (float)total / (float)size;
    }

    public static float getTps(float ms, int concurrencyLevel) {
        return (float)concurrencyLevel / ms * 1000.0F;
    }

    public static float toMs(long nm) {
        return (float)nm / 1000000.0F;
    }

    public static float toMs(float nm) {
        return nm / 1000000.0F;
    }
}
