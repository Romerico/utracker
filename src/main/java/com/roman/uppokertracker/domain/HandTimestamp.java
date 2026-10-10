package com.roman.uppokertracker.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class HandTimestamp {

    private final LocalDateTime dateTime;
    private final ZoneId zone;

    public HandTimestamp(LocalDateTime dateTime, ZoneId zone) {
        this.dateTime = dateTime;
        this.zone = zone;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    public ZoneId zone() {
        return zone;
    }
}
