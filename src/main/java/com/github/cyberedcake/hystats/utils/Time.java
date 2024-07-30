package com.github.cyberedcake.hystats.utils;

public class Time {

    /**
     * Represents one second in milliseconds
     */
    public final static long ONE_SECOND = 1000;
    /**
     * Represents one minute in milliseconds
     */
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    /**
     * Represents one hour in milliseconds
     */
    public final static long ONE_HOUR = ONE_SECOND * 3600;
    /**
     * Represents on day in milliseconds
     */
    public final static long ONE_DAY = ONE_SECOND * 86400;
    /**
     * Represents one week in milliseconds
     */
    public final static long ONE_WEEK = ONE_SECOND * 604800;
    /**
     * Represents one month in milliseconds
     */
    public final static long ONE_MONTH = ONE_SECOND * 2628000;
    /**
     * Represents one year in milliseconds
     */
    public final static long ONE_YEAR = ONE_SECOND * 31560000;

    public static String getDuration(long bigger, long smaller, boolean showAll) {
        StringBuilder durationBuilder = new StringBuilder();
        long duration = (bigger - smaller)*1000L;
        long temp;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_YEAR;
            if (temp > 0) {
                duration -= temp * ONE_YEAR;
                if (!showAll) return temp + "y";
                durationBuilder.append(temp).append("y")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MONTH;
            if (temp > 0) {
                duration -= temp * ONE_MONTH;
                if (!showAll) return temp + "mo";
                durationBuilder.append(temp).append("mo")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_WEEK;
            if (temp > 0) {
                duration -= temp * ONE_WEEK;
                if (!showAll) return temp + "w";
                durationBuilder.append(temp).append("w")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                if (!showAll) return temp + "d";
                durationBuilder.append(temp).append("d")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                if (!showAll) return temp + "h";
                durationBuilder.append(temp).append("h")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                if (!showAll) return temp + "m";
                durationBuilder.append(temp).append("m")
                        .append(duration >= ONE_SECOND ? ", " : "")
                ;
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                durationBuilder.append(temp).append("s");
                if (!showAll) return temp + "s";
            }
            return durationBuilder.toString();
        } else {
            return "0s";
        }
    }

}
