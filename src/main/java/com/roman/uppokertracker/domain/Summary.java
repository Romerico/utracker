package com.roman.uppokertracker.domain;

import java.util.List;

public class Summary {

    private final double pot;
    private final double rake;
    private final List<SummaryEntry> entries;

    public Summary(List<SummaryEntry> entries) {
        this(0, 0, entries);
    }

    public Summary(double pot, double rake, List<SummaryEntry> entries) {
        this.pot = pot;
        this.rake = rake;
        this.entries = entries;
    }

    public double pot() {
        return pot;
    }

    public double rake() {
        return rake;
    }

    public List<SummaryEntry> entries() {
        return entries;
    }
}
