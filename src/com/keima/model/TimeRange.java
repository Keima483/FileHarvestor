package com.keima.model;

import java.time.LocalTime;

public class TimeRange {

    public final LocalTime start;
    public final LocalTime end;

    public TimeRange(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(LocalTime time) {
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public boolean isNull() {
        return start == null || end == null;
    }
}
