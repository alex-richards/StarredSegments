package com.alex.strava.starred.strava;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SegmentExport implements Iterable<SegmentExport.Point> {

    private static final Pattern PATTERN_SAFE = Pattern.compile("[^a-zA-Z0-9]");

    private final int id;
    private final String name;

    private final String athleteName;
    private final long totalTime;
    private final float totalDistance;

    private final List<Point> points;
    private final Point start;
    private final Point stop;

    public SegmentExport(final int id, final String name, final String athleteName, long totalTime, float totalDistance, final List<Point> points) {
        this.id = id;
        this.name = name;

        this.athleteName = athleteName;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;

        this.points = points;
        start = points.get(0);
        stop = points.get(points.size() - 1);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        if (name != null && name.trim().length() > 0) {
            return name;
        } else {
            return null;
        }
    }

    public String getSafeName() {
        final String name = getName();
        if (name != null) {
            final String safeName = PATTERN_SAFE.matcher(name).replaceAll("");
            if (safeName.length() > 0) {
                return safeName;
            } else {
                return String.format(Locale.US, "segment_%d", getId());
            }
        } else {
            return null;
        }
    }

    public String getAthleteName() {
        return athleteName;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getStart() {
        return start;
    }

    public Point getStop() {
        return stop;
    }

    @Override
    public Iterator<Point> iterator() {
        return getPoints().iterator();
    }

    public static class Point {

        private final ZonedDateTime time;
        private final double latitude;
        private final double longitude;
        private final float distance;
        private final float altitude;

        public Point(final ZonedDateTime time, final double latitude, final double longitude, final float distance, final float altitude) {
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;
            this.altitude = altitude;
        }

        public ZonedDateTime getTime() {
            return time;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public float getDistance() {
            return distance;
        }

        public float getAltitude() {
            return altitude;
        }
    }
}
