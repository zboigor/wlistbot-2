package com.zboigor.util;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * @author Igor Zboichik
 * @since 2017-04-27
 */
public class DateTimeUtils {

    public static long dateTimeDifference(Temporal d1, Temporal d2, ChronoUnit unit) {
        if (d1 == null || d2 == null) return 0;
        return unit.between(d1, d2);
    }
}
